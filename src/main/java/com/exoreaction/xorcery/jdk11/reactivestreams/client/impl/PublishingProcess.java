package com.exoreaction.xorcery.jdk11.reactivestreams.client.impl;

import com.exoreaction.xorcery.jdk11.configuration.Configuration;
import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaReader;
import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaWriter;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.websocket.PublishWebSocketEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

public final class PublishingProcess {
    private static final Logger logger = LoggerFactory.getLogger(PublishingProcess.class);

    private final WebSocketClient webSocketClient;
    private final ObjectMapper objectMapper;
    private final Timer timer;
    private final ByteBufferPool byteBufferPool;
    private final XorceryClientMediaReader<Object> resultReader;
    private final XorceryClientMediaWriter<Object> eventWriter;
    private final Type eventType;
    private final Type resultType;
    private final URI subscriberWebsocketUri;
    private final Configuration subscriberConfiguration;
    private final Flow.Publisher<Object> publisher;
    private final CompletableFuture<Void> result;

    public PublishingProcess(WebSocketClient webSocketClient, ObjectMapper objectMapper, Timer timer,
                             ByteBufferPool byteBufferPool,
                             XorceryClientMediaReader<Object> resultReader,
                             XorceryClientMediaWriter<Object> eventWriter,
                             Type eventType,
                             Type resultType,
                             URI subscriberWebsocketUri,
                             Configuration subscriberConfiguration,
                             Flow.Publisher<Object> publisher, CompletableFuture<Void> result
    ) {
        this.webSocketClient = webSocketClient;
        this.objectMapper = objectMapper;
        this.timer = timer;
        this.byteBufferPool = byteBufferPool;
        this.resultReader = resultReader;
        this.eventWriter = eventWriter;
        this.eventType = eventType;
        this.resultType = resultType;
        this.subscriberWebsocketUri = subscriberWebsocketUri;
        this.subscriberConfiguration = subscriberConfiguration;
        this.publisher = publisher;
        this.result = result;
    }

    public void start() {
        if (result.isDone()) {
            return;
        }

        if (!webSocketClient.isStarted()) {
            retry();
        }

        ForkJoinPool.commonPool().execute(() ->
        {
            try {
                webSocketClient.connect(new PublishWebSocketEndpoint(
                                subscriberWebsocketUri.toASCIIString(),
                                publisher,
                                eventWriter,
                                resultReader,
                                eventType,
                                resultType,
                                subscriberConfiguration,
                                objectMapper,
                                byteBufferPool,
                                this), subscriberWebsocketUri)
                        .whenComplete(this::complete);
            } catch (IOException e) {
                logger.error("Could not subscribe to " + subscriberWebsocketUri.toASCIIString(), e);

                retry();
            }
        });
    }

    private void complete(Session session, Throwable throwable) {
        if (throwable != null) {
            logger.error("Could not subscribe to " + subscriberWebsocketUri.toASCIIString(), throwable);
            retry();
        }
    }

    public void retry() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                start();
            }
        }, 10000);
    }

    public WebSocketClient webSocketClient() {
        return webSocketClient;
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public Timer timer() {
        return timer;
    }

    public ByteBufferPool byteBufferPool() {
        return byteBufferPool;
    }

    public XorceryClientMediaReader<Object> resultReader() {
        return resultReader;
    }

    public XorceryClientMediaWriter<Object> eventWriter() {
        return eventWriter;
    }

    public Type eventType() {
        return eventType;
    }

    public Type resultType() {
        return resultType;
    }

    public URI subscriberWebsocketUri() {
        return subscriberWebsocketUri;
    }

    public Configuration subscriberConfiguration() {
        return subscriberConfiguration;
    }

    public Flow.Publisher<Object> publisher() {
        return publisher;
    }

    public CompletableFuture<Void> result() {
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PublishingProcess) obj;
        return Objects.equals(this.webSocketClient, that.webSocketClient) &&
                Objects.equals(this.objectMapper, that.objectMapper) &&
                Objects.equals(this.timer, that.timer) &&
                Objects.equals(this.byteBufferPool, that.byteBufferPool) &&
                Objects.equals(this.resultReader, that.resultReader) &&
                Objects.equals(this.eventWriter, that.eventWriter) &&
                Objects.equals(this.eventType, that.eventType) &&
                Objects.equals(this.resultType, that.resultType) &&
                Objects.equals(this.subscriberWebsocketUri, that.subscriberWebsocketUri) &&
                Objects.equals(this.subscriberConfiguration, that.subscriberConfiguration) &&
                Objects.equals(this.publisher, that.publisher) &&
                Objects.equals(this.result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webSocketClient, objectMapper, timer, byteBufferPool, resultReader, eventWriter, eventType, resultType, subscriberWebsocketUri, subscriberConfiguration, publisher, result);
    }

    @Override
    public String toString() {
        return "PublishingProcess[" +
                "webSocketClient=" + webSocketClient + ", " +
                "objectMapper=" + objectMapper + ", " +
                "timer=" + timer + ", " +
                "logger=" + logger + ", " +
                "byteBufferPool=" + byteBufferPool + ", " +
                "resultReader=" + resultReader + ", " +
                "eventWriter=" + eventWriter + ", " +
                "eventType=" + eventType + ", " +
                "resultType=" + resultType + ", " +
                "subscriberWebsocketUri=" + subscriberWebsocketUri + ", " +
                "subscriberConfiguration=" + subscriberConfiguration + ", " +
                "publisher=" + publisher + ", " +
                "result=" + result + ']';
    }

}
