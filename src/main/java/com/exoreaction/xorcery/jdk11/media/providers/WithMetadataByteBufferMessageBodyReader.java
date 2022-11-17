package com.exoreaction.xorcery.jdk11.media.providers;

import com.exoreaction.xorcery.jdk11.metadata.Metadata;
import com.exoreaction.xorcery.jdk11.reactivestreams.client.api.WithMetadata;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class WithMetadataByteBufferMessageBodyReader implements XorceryClientMediaReader<WithMetadata<ByteBuffer>> {

    private final ObjectMapper objectMapper;

    public WithMetadataByteBufferMessageBodyReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public WithMetadata<ByteBuffer> readFrom(InputStream entityStream) throws IOException {
        entityStream.mark(entityStream.available());
        JsonFactory jf = new JsonFactory(objectMapper);
        JsonParser jp = jf.createParser(entityStream);
//            JsonToken metadataToken = jp.nextToken();
        Metadata metadata = jp.readValueAs(Metadata.class);
        long offset = jp.getCurrentLocation().getByteOffset();

        entityStream.reset();
        entityStream.skip(offset);

        ByteBuffer payload = ByteBuffer.wrap(entityStream.readAllBytes());
        return new WithMetadata<>(metadata, payload);
    }
}
