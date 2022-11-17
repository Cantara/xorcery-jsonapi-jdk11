package com.exoreaction.xorcery.jdk11.media.providers;

import java.io.IOException;
import java.io.OutputStream;

public interface XorceryClientMediaWriter<ITEM> {

    void writeTo(ITEM item, OutputStream entityStream) throws IOException;
}
