package com.exoreaction.xorcery.jdk11.media.providers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

public class ByteBufferMessageBodyReader implements XorceryClientMediaReader<ByteBuffer> {

    public ByteBufferMessageBodyReader() {
    }

    @Override
    public ByteBuffer readFrom(InputStream entityStream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(entityStream.available()); // TODO not safe allocation, see documentation of stream
        Channels.newChannel(entityStream).read(byteBuffer);
        return byteBuffer;
    }
}
