package com.exoreaction.xorcery.jdk11.media.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public class JsonNodeMessageBodyWriter implements XorceryClientMediaWriter<JsonNode> {
    private ObjectMapper objectMapper;

    public JsonNodeMessageBodyWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void writeTo(JsonNode jsonObject, OutputStream entityStream) throws IOException {
        objectMapper.writeValue(entityStream, jsonObject);
    }
}
