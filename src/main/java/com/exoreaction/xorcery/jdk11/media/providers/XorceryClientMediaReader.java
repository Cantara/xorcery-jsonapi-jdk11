package com.exoreaction.xorcery.jdk11.media.providers;

import java.io.IOException;
import java.io.InputStream;

public interface XorceryClientMediaReader<ITEM> {

    ITEM readFrom(InputStream entityStream) throws IOException;
}
