package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rickardoberg
 */

public class Error
        implements JsonElement {

    private final ObjectNode json;

    public Error(ObjectNode json) {
        this.json = json;
    }

    @Override
    public ObjectNode json() {
        return json;
    }

    public static class Builder
            implements With<Builder>
    {
        private final ObjectNode builder;

        public Builder(ObjectNode builder) {
            this.builder = builder;
        }

        public Builder() {
            this(JsonNodeFactory.instance.objectNode());
        }

        public Builder status(int value) {
            builder.set("status", builder.textNode(Integer.toString(value)));
            return this;
        }

        public Builder source(Source source) {
            builder.set("source", source.object());
            return this;
        }

        public Builder title(String value) {
            builder.set("title", builder.textNode(value));
            return this;
        }

        public Builder detail(String value) {
            builder.set("detail", builder.textNode(value));
            return this;
        }

        public Error build() {
            return new Error(builder);
        }
    }

    public String getStatus() {
        return getString("status").orElse(null);
    }

    public Source getSource() {
        JsonNode source = object().path("source");
        return new Source(source instanceof ObjectNode ? (ObjectNode) source :
                JsonNodeFactory.instance.objectNode());
    }

    public String getTitle() {
        return getString("title").orElse("");
    }

    public String getDetail() {
        return getString("detail").orElse("");
    }
}
