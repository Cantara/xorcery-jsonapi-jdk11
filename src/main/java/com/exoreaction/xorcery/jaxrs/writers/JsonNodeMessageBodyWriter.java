package com.exoreaction.xorcery.jaxrs.writers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.WILDCARD)
public class JsonNodeMessageBodyWriter
        implements MessageBodyWriter<JsonNode> {
    private ObjectMapper objectMapper;

    public JsonNodeMessageBodyWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean result = JsonNode.class.isAssignableFrom(type) && (mediaType.isWildcardType() || mediaType.getSubtype().endsWith("json"));
        return result;
    }

    @Override
    public void writeTo(JsonNode jsonObject, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        objectMapper.writeValue(entityStream, jsonObject);
    }
}