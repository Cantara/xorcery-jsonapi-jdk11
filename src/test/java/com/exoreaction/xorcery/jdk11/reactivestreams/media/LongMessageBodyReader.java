package com.exoreaction.xorcery.jdk11.reactivestreams.media;

import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class LongMessageBodyReader
        implements XorceryClientMediaReader<Long> {

    public LongMessageBodyReader() {
    }

    @Override
    public Long readFrom(InputStream entityStream) throws IOException {
        byte[] buf = new byte[8];
        int read = entityStream.read(buf);
        if (read != 8) {
            throw new RuntimeException("Only " + read + " bytes read, should have been 8");
        }
        Long item = ByteBuffer.wrap(buf)
                .getLong();
        return item;
    }
}
