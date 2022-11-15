package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.model.JsonElement;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author rickardoberg
 */
public class Errors
        implements JsonElement, Iterable<Error> {

    private final ArrayNode json;

    public Errors(ArrayNode json) {
        this.json = json;
    }

    @Override
    public ArrayNode json() {
        return json;
    }

    public static class Builder
            implements With<Builder>
    {
        private final ArrayNode builder;

        public Builder(ArrayNode builder) {
            this.builder = builder;
        }

        public Builder() {
            this(JsonNodeFactory.instance.arrayNode());
        }

        public Builder error(Error error) {
            builder.add(error.json());
            return this;
        }

        public Errors build() {
            return new Errors(builder);
        }
    }

    public boolean hasErrors() {
        return !array().isEmpty();
    }

    public List<Error> getErrors() {
        return JsonElement.getValuesAs(array(),Error::new);
    }

    @Override
    public Iterator<Error> iterator() {
        return getErrors().iterator();
    }

    public Map<String, Error> getErrorMap() {
        Map<String, Error> map = new HashMap<>();
        for (Error error : getErrors()) {
            String pointer = error.getSource().getPointer();
            if (pointer != null) {
                pointer = pointer.substring(pointer.lastIndexOf('/') + 1);
            }
            map.put(pointer, error);
        }
        return map;
    }

    public String getError() {
        for (Error error : getErrors()) {
            if (error.getSource().getPointer() == null)
                return error.getTitle();
        }

        return null;
    }
}
