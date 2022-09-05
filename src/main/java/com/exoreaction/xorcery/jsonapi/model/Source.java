package com.exoreaction.xorcery.jsonapi.model;

import com.exoreaction.xorcery.builders.With;
import com.exoreaction.xorcery.json.JsonElement;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rickardoberg
 */
public class Source
        implements JsonElement {

    private final ObjectNode json;

    public Source(ObjectNode json) {
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

        public Builder pointer(String value) {
            builder.set("pointer", builder.textNode(value));
            return this;
        }

        public Builder parameter(String value) {
            builder.set("detail", builder.textNode(value));
            return this;
        }

        public Source build() {
            return new Source(builder);
        }
    }

    public String getPointer() {
        return getString("pointer").orElse(null);
    }

    public String getParameter() {
        return getString("parameter").orElse(null);
    }
}
