package com.exoreaction.xorcery.jdk11.reactivestreams;

import com.exoreaction.xorcery.jdk11.configuration.Configuration;
import com.exoreaction.xorcery.jdk11.media.providers.WithMetadataByteBufferMessageBodyReader;
import com.exoreaction.xorcery.jdk11.media.providers.WithMetadataByteBufferMessageBodyWriter;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.ReactiveStreamsClient;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.impl.JettyBasedReactiveStreamsClient;
import com.exoreaction.xorcery.jdk11.reactivestreams.domainevents.DomainEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DomainEventsReactiveStreamsTest {

    @Test
    @Disabled
    public void thatSequenceCanBePublished() throws InterruptedException, ExecutionException, TimeoutException {
        Configuration configuration = new Configuration.Builder()
                .build();
        ObjectMapper mapper = new ObjectMapper();
        ReactiveStreamsClient reactiveStreamsClient = new JettyBasedReactiveStreamsClient(
                configuration,
                List.of(new WithMetadataByteBufferMessageBodyWriter(mapper)),
                List.of(new WithMetadataByteBufferMessageBodyReader(mapper))
        );
        CompletableFuture<Void> future = reactiveStreamsClient.publish(URI.create("ws://localhost:60797/jsonevents"), configuration, new DomainEventPublisher(mapper, 12), DomainEventPublisher.class)
                .thenAccept(v -> {
                    System.out.printf("reactive-streams publisher complete signal received%n");
                });
        future.get(30, TimeUnit.SECONDS);
    }
}
