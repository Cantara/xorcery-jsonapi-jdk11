package com.exoreaction.xorcery.jsonapi.model;

import com.exoreaction.xorcery.builders.With;
import com.exoreaction.xorcery.json.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rickardoberg
 */
public class ResourceObjectIdentifier
        implements JsonElement {

    private final ObjectNode json;

    public ResourceObjectIdentifier(ObjectNode json) {
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

        public Builder(String type, String id) {
            this(JsonNodeFactory.instance.objectNode());
            builder.<ObjectNode>set("type", builder.textNode(type))
                    .set("id", builder.textNode(id));
        }

        public Builder(Enum<?> type, String id) {
            this(type.name(), id);
        }

        public Builder meta(Meta meta) {
            builder.set("meta", meta.json());
            return this;
        }

        public ResourceObjectIdentifier build() {
            return new ResourceObjectIdentifier(builder);
        }
    }

    public String getId() {
        return getString("id").orElse("");
    }

    public String getType() {
        return getString("type").orElseThrow();
    }

    public Meta getMeta() {
        JsonNode meta = object().path("meta");
        return new Meta(meta instanceof ObjectNode ? (ObjectNode) meta :
                JsonNodeFactory.instance.objectNode());
    }
}
