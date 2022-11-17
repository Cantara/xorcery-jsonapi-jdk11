package com.exoreaction.xorcery.jdk11.reactivestreams.media;

import com.exoreaction.xorcery.jdk11.media.providers.XorceryClientMediaWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class LongMessageBodyWriter
        implements XorceryClientMediaWriter<Long> {

    public LongMessageBodyWriter() {
    }

    @Override
    public void writeTo(Long item, OutputStream entityStream) throws IOException {
        byte[] buf = new byte[8];
        ByteBuffer.wrap(buf)
                .putLong(item);
        entityStream.write(buf);
    }
}
