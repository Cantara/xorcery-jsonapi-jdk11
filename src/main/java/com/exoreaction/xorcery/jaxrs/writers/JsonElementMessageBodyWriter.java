package com.exoreaction.xorcery.jaxrs.writers;

import com.exoreaction.xorcery.jaxrs.MediaTypes;
import com.exoreaction.xorcery.json.JsonElement;
import com.exoreaction.xorcery.jsonapi.model.ResourceDocument;
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

import static com.exoreaction.xorcery.jsonapi.model.JsonApiRels.describedby;

@Provider
@Produces({MediaType.WILDCARD})
public class JsonElementMessageBodyWriter
        implements MessageBodyWriter<JsonElement> {
    private final ObjectMapper objectMapper;
    private final ObjectMapper yamlObjectMapper;

    public JsonElementMessageBodyWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.yamlObjectMapper = new ObjectMapper();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonElement.class.isAssignableFrom(type) &&
                (mediaType.isCompatible(MediaTypes.APPLICATION_JSON_API_TYPE) ||
                        mediaType.isCompatible(MediaTypes.APPLICATION_JSON_SCHEMA_TYPE) ||
                        mediaType.isCompatible(MediaTypes.APPLICATION_YAML_TYPE) ||
                        mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE));
    }

    @Override
    public void writeTo(JsonElement jsonElement, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        if (jsonElement instanceof ResourceDocument) {
            ((ResourceDocument) jsonElement).getLinks().getByRel(describedby).ifPresent(link ->
            {
                httpHeaders.add("Link", String.format("<%s>; rel=\"%s\"; title=\"%s\"", link.getHref(), link.rel(), "JSON Schema"));
            });
        }

        if (mediaType.isCompatible(MediaTypes.APPLICATION_YAML_TYPE)) {
            yamlObjectMapper.writer().writeValue(entityStream, jsonElement.json());
        } else {
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(entityStream, jsonElement.json());
        }
    }
}
