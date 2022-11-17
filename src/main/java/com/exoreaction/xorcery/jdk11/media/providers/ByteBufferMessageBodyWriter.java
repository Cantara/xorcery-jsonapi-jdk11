package com.exoreaction.xorcery.jdk11.media.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferMessageBodyWriter implements XorceryClientMediaWriter<ByteBuffer> {

    public ByteBufferMessageBodyWriter() {
    }

    public void writeTo(ByteBuffer byteBuffer, OutputStream entityStream) throws IOException {
        entityStream.write(byteBuffer.array());
    }
}
