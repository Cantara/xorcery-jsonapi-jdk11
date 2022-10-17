package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author rickardoberg
 */
public class Relationships
        implements JsonElement {

    private final ObjectNode json;

    public Relationships(ObjectNode json) {
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

        public Builder(Relationships relationships) {
            this(relationships.object().deepCopy());
        }

        public ObjectNode builder() {
            return builder;
        }

        public Builder relationship(Enum<?> name, Relationship relationship) {
            return relationship(name.name(), relationship);
        }

        public Builder relationship(String name, Relationship relationship) {
            builder.set(name, relationship.json());
            return this;
        }

        public Builder relationship(String name, Relationship.Builder relationship) {
            return relationship(name, relationship.build());
        }

        public Relationships build() {
            return new Relationships(builder);
        }
    }

    public boolean isEmpty()
    {
        return object().isEmpty();
    }

    public List<Relationship> getRelationshipList() {

        return JsonElement.getValuesAs(object(), Relationship::new);
    }

    public Map<String, Relationship> getRelationships() {
        Map<String, Relationship> rels = new HashMap<>(object().size());
        Iterator<Map.Entry<String, JsonNode>> fields = object().fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            rels.put(next.getKey(), new Relationship((ObjectNode)next.getValue()));
        }
        return rels;
    }

    public Optional<Relationship> getRelationship(String name) {
        return Optional.ofNullable(object().get(name)).map(v -> new Relationship((ObjectNode) v));
    }

    public Optional<Relationship> getRelationship(Enum<?> name) {
        return getRelationship(name.name());
    }
}
