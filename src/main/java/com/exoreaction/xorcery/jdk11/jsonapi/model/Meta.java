package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rickardoberg
 */
public class Meta implements JsonElement {

    private final ObjectNode json;

    public Meta(ObjectNode json) {
        this.json = json;
    }

    @Override
    public ObjectNode json() {
        return json;
    }

    public static class Builder
            implements With<Builder> {
        private final ObjectNode builder;

        public Builder(ObjectNode builder) {
            this.builder = builder;
        }

        public Builder() {
            this(JsonNodeFactory.instance.objectNode());
        }

        public ObjectNode builder() {
            return builder;
        }

        public Builder meta(String name, JsonNode value) {
            builder.set(name, value);
            return this;
        }

        public Builder meta(String name, long value) {
            builder.set(name, builder.numberNode(value));
            return this;
        }

        public Meta build() {
            return new Meta(builder);
        }
    }

    public ObjectNode getMeta() {
        return object();
    }
}
