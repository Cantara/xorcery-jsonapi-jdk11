package com.exoreaction.xorcery.jdk11.reactivestreams;

import com.exoreaction.xorcery.jdk11.configuration.Configuration;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.ReactiveStreamsClient;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.impl.JettyAndJerseyBasedReactiveStreamsClient;
import com.exoreaction.xorcery.jdk11.reactivestreams.fibonacci.FibonacciPublisher;
import com.exoreaction.xorcery.jdk11.reactivestreams.media.LongMessageBodyReader;
import com.exoreaction.xorcery.jdk11.reactivestreams.media.LongMessageBodyWriter;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReactiveStreamsTest {

    @Test
    public void thatSequenceCanBePublished() throws InterruptedException, ExecutionException, TimeoutException {
        Configuration configuration = new Configuration.Builder()
                .build();
        ReactiveStreamsClient reactiveStreamsClient = new JettyAndJerseyBasedReactiveStreamsClient(
                configuration,
                List.of(LongMessageBodyWriter.class),
                List.of(LongMessageBodyReader.class)
        );
        CompletableFuture<Void> future = reactiveStreamsClient.publish(URI.create("ws://localhost:60797/fibonacci"), configuration, new FibonacciPublisher(12), FibonacciPublisher.class)
                .thenAccept(v -> {
                    System.out.printf("reactive-streams publisher complete signal received%n");
                });
        future.get(30, TimeUnit.SECONDS);
    }
}
