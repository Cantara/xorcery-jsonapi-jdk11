package com.exoreaction.xorcery.jdk11.reactivestreams.domainevents;

import com.exoreaction.xorcery.jdk11.metadata.Metadata;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.WithMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Random;

public class DomainEventGenerator implements Iterable<WithMetadata<ByteBuffer>> {

    private final ObjectMapper mapper;
    private final int maxItems;
    private final Random random = new Random(473849L);

    public DomainEventGenerator(ObjectMapper mapper, int maxItems) {
        this.mapper = mapper;
        this.maxItems = maxItems;
    }

    @Override
    public Iterator<WithMetadata<ByteBuffer>> iterator() {
        return new Iterator<>() {
            private int count;

            @Override
            public boolean hasNext() {
                return count < maxItems;
            }

            @Override
            public WithMetadata<ByteBuffer> next() {
                count++;
                Metadata metadata = new Metadata(mapper.createObjectNode()
                        .put("timestamp", ZonedDateTime.now().toString())
                );
                ArrayNode eventJsonArray = mapper.createArrayNode();
                ObjectNode event1 = eventJsonArray.addObject();
                event1.put("eventName", "The first event");
                event1.put("randomdata", random.nextInt());
                ObjectNode event2 = eventJsonArray.addObject();
                event2.put("eventName", "The second event");
                event2.put("randomdata", random.nextInt());
                ByteBuffer byteBuffer = null;
                try {
                    byteBuffer = ByteBuffer.wrap(mapper.writeValueAsBytes(eventJsonArray));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return new WithMetadata<>(metadata, byteBuffer);
            }
        };
    }
}
