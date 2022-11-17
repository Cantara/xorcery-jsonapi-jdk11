package com.exoreaction.xorcery.jdk11.reactivestreams.client.impl;

import com.exoreaction.xorcery.jdk11.configuration.Configuration;
import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaReader;
import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaWriter;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.ReactiveStreamsClient;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.WithResult;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public class JettyBasedReactiveStreamsClient implements ReactiveStreamsClient {

    private final HttpClient httpClient;
    private final WebSocketClient webSocketClient;

    private final ObjectMapper objectMapper;
    private final Timer timer;

    private final ByteBufferPool byteBufferPool;

    private final List<XorceryClientMediaWriter> writers;
    private final List<XorceryClientMediaReader> readers;

    public JettyBasedReactiveStreamsClient(Configuration configuration, List<XorceryClientMediaWriter> writers, List<XorceryClientMediaReader> readers) {
        this.httpClient = new JettyClientInitializer().createClient(configuration);
        this.webSocketClient = new WebSocketClient(httpClient);
        this.webSocketClient.setIdleTimeout(Duration.ofSeconds(httpClient.getIdleTimeout()));
        try {
            this.webSocketClient.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false); // TODO figure out whether this will affect anything here?
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.timer = new Timer();
        this.byteBufferPool = new ArrayByteBufferPool();
        this.writers = writers;
        this.readers = readers;
    }

    @Override
    public CompletableFuture<Void> publish(URI subscriberWebsocketUri, Configuration subscriberConfiguration, Flow.Publisher<?> publisher, Class<? extends Flow.Publisher<?>> publisherType) {
        if (publisherType == null)
            publisherType = (Class<? extends Flow.Publisher<?>>) publisher.getClass();

        CompletableFuture<Void> result = new CompletableFuture<>();


        Type type = resolveActualTypeArgs(publisherType, Flow.Publisher.class)[0];
        Type eventType = getEventType(type);
        Optional<Type> resultType = getResultType(type);

        XorceryClientMediaWriter<Object> eventWriter = getWriter(eventType);
        XorceryClientMediaReader<Object> resultReader = resultType.map(this::getReader).orElse(null);

        // Start publishing process
        new PublishingProcess(
                webSocketClient,
                objectMapper,
                timer,
                byteBufferPool,
                resultReader,
                eventWriter,
                eventType,
                resultType.orElse(null),
                subscriberWebsocketUri,
                subscriberConfiguration,
                (Flow.Publisher<Object>) publisher,
                result).start();
        return result;
    }

    public static <T> Type[] resolveActualTypeArgs(Class<? extends T> offspring, Class<T> base, Type... actualArgs) {

        assert offspring != null;
        assert base != null;
        assert actualArgs.length == 0 || actualArgs.length == offspring.getTypeParameters().length;

        //  If actual types are omitted, the type parameters will be used instead.
        if (actualArgs.length == 0) {
            actualArgs = offspring.getTypeParameters();
        }
        // map type parameters into the actual types
        Map<String, Type> typeVariables = new HashMap<String, Type>();
        for (int i = 0; i < actualArgs.length; i++) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) offspring.getTypeParameters()[i];
            typeVariables.put(typeVariable.getName(), actualArgs[i]);
        }

        // Find direct ancestors (superclass, interfaces)
        List<Type> ancestors = new LinkedList<Type>();
        if (offspring.getGenericSuperclass() != null) {
            ancestors.add(offspring.getGenericSuperclass());
        }
        for (Type t : offspring.getGenericInterfaces()) {
            ancestors.add(t);
        }

        // Recurse into ancestors (superclass, interfaces)
        for (Type type : ancestors) {
            if (type instanceof Class<?>) {
                // ancestor is non-parameterized. Recurse only if it matches the base class.
                Class<?> ancestorClass = (Class<?>) type;
                if (base.isAssignableFrom(ancestorClass)) {
                    Type[] result = resolveActualTypeArgs((Class<? extends T>) ancestorClass, base);
                    if (result != null) {
                        return result;
                    }
                }
            }
            if (type instanceof ParameterizedType) {
                // ancestor is parameterized. Recurse only if the raw type matches the base class.
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class<?>) {
                    Class<?> rawTypeClass = (Class<?>) rawType;
                    if (base.isAssignableFrom(rawTypeClass)) {

                        // loop through all type arguments and replace type variables with the actually known types
                        List<Type> resolvedTypes = new LinkedList<Type>();
                        for (Type t : parameterizedType.getActualTypeArguments()) {
                            if (t instanceof TypeVariable<?>) {
                                Type resolvedType = typeVariables.get(((TypeVariable<?>) t).getName());
                                resolvedTypes.add(resolvedType != null ? resolvedType : t);
                            } else {
                                resolvedTypes.add(t);
                            }
                        }

                        Type[] result = resolveActualTypeArgs((Class<? extends T>) rawTypeClass, base, resolvedTypes.toArray(new Type[]{}));
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }

        // we have a result if we reached the base class.
        return offspring.equals(base) ? actualArgs : null;
    }

    private Type getEventType(Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(WithResult.class) ? ((ParameterizedType) type).getActualTypeArguments()[0] : type;
    }

    private Optional<Type> getResultType(Type type) {
        return Optional.ofNullable(type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(WithResult.class) ? ((ParameterizedType) type).getActualTypeArguments()[1] : null);
    }

    private XorceryClientMediaWriter<Object> getWriter(Type type) {
        return writers.stream()
                .filter(writer -> {
                    for (Type genericInterface : writer.getClass().getGenericInterfaces()) {
                        if (genericInterface.getTypeName().startsWith(XorceryClientMediaWriter.class.getTypeName() + "<")) {
                            Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                            if (type.getTypeName().equals(actualTypeArgument.getTypeName())) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    private XorceryClientMediaReader<Object> getReader(Type type) {
        return readers.stream()
                .filter(reader -> {
                    for (Type genericInterface : reader.getClass().getGenericInterfaces()) {
                        if (genericInterface.getTypeName().startsWith(XorceryClientMediaReader.class.getTypeName() + "<")) {
                            Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                            if (type.getTypeName().equals(actualTypeArgument.getTypeName())) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }
}
