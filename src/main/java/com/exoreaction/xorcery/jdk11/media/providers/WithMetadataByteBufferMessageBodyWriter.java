package com.exoreaction.xorcery.jdk11.media.providers;

import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.WithMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WithMetadataByteBufferMessageBodyWriter implements XorceryClientMediaWriter<WithMetadata<ByteBuffer>> {

    private final ObjectMapper objectMapper;

    public WithMetadataByteBufferMessageBodyWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeTo(WithMetadata<ByteBuffer> item, OutputStream entityStream) throws IOException {
        byte[] metadataBytes = objectMapper.writeValueAsBytes(item.metadata().json());
        entityStream.write(metadataBytes);
        byte[] payloadBytes = item.event().array();
        entityStream.write(payloadBytes);
    }
}
