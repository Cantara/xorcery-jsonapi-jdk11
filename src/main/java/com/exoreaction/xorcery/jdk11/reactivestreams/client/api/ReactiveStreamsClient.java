package com.exoreaction.xorcery.jdk11.reactivestreams.client.api;

import com.exoreaction.xorcery.jdk11.configuration.Configuration;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public interface ReactiveStreamsClient {

    CompletableFuture<Void> publish(URI subscriberWebsocketUri, Configuration subscriberConfiguration, Flow.Publisher<?> publisher, Class<? extends Flow.Publisher<?>> publisherType);
}
