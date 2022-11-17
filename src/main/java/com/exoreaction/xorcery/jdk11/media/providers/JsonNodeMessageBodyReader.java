package com.exoreaction.xorcery.jdk11.media.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonNodeMessageBodyReader implements XorceryClientMediaReader<JsonNode> {

    private ObjectMapper objectMapper;

    public JsonNodeMessageBodyReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode readFrom(InputStream entityStream) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(entityStream);
        return jsonNode;
    }
}
