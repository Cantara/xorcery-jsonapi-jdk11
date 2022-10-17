package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

/**
 * @author rickardoberg
 */
public class ResourceObject
        implements JsonElement {

    private final ObjectNode json;

    public ResourceObject(ObjectNode json) {
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

            if (id != null)
                builder.set("id", builder.textNode(id));

            builder.set("type", builder.textNode(type));
        }

        public Builder(Enum<?> type, String id) {
            this(type.name(), id);
        }

        public Builder(String type) {
            this(JsonNodeFactory.instance.objectNode());
            builder.set("type", builder.textNode(type));
        }

        public Builder(ResourceObject resourceObject) {
            this(resourceObject.json().deepCopy());
        }

        public Builder attributes(Attributes attributes) {
            ObjectNode object = attributes.object();
            if (!object.isEmpty())
                builder.set("attributes", object);
            return this;
        }

        public Builder attributes(Attributes.Builder attributes) {
            return attributes(attributes.build());
        }

        public Builder relationships(Relationships relationships) {
            ObjectNode object = relationships.object();
            if (!object.isEmpty())
                builder.set("relationships", object);
            return this;
        }

        public Builder relationships(Relationships.Builder relationships) {
            return relationships(relationships.build());
        }

        public Builder links(Links links) {
            builder.set("links", links.json());
            return this;
        }

        public Builder links(Links.Builder links) {
            return links(links.build());
        }

        public Builder meta(Meta meta) {
            builder.set("meta", meta.json());
            return this;
        }

        public ResourceObject build() {
            return new ResourceObject(builder);
        }
    }

    public String getId() {
        return object().path("id").textValue();
    }

    public String getType() {
        return object().path("type").textValue();
    }

    public ResourceObjectIdentifier getResourceObjectIdentifier() {
        return new ResourceObjectIdentifier.Builder(getType(), getId()).build();
    }

    public Attributes getAttributes() {
        JsonNode attributes = object().path("attributes");
        return new Attributes(attributes instanceof ObjectNode ? (ObjectNode) attributes :
                JsonNodeFactory.instance.objectNode());
    }

    public Relationships getRelationships() {
        JsonNode relationships = object().path("relationships");
        return new Relationships(relationships instanceof ObjectNode ? (ObjectNode) relationships :
                JsonNodeFactory.instance.objectNode());
    }

    public Links getLinks() {
        JsonNode links = object().path("links");
        return new Links(links instanceof ObjectNode ? (ObjectNode) links :
                JsonNodeFactory.instance.objectNode());
    }

    public Meta getMeta() {
        JsonNode meta = object().path("meta");
        return new Meta(meta instanceof ObjectNode ? (ObjectNode) meta :
                JsonNodeFactory.instance.objectNode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceObject that = (ResourceObject) o;
        return getType().equals(that.getType()) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }
}
