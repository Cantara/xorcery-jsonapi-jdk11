package com.exoreaction.xorcery.jdk11.service.domainevents.api.event;

import com.exoreaction.xorcery.jdk11.json.model.JsonElement;
import com.exoreaction.xorcery.jdk11.service.domainevents.api.entity.DomainEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING;

public final class JsonDomainEvent
        implements JsonElement, DomainEvent {
    private final ObjectNode json;


    public static Builder event(String eventName) {
        return new Builder(eventName);
    }

    public static final class Builder {
        private final ObjectNode builder;

        public Builder(ObjectNode builder) {
            this.builder = builder;
        }

        public Builder(String eventName) {
            this(JsonNodeFactory.instance.objectNode());
            builder.set("event", builder.textNode(eventName));
        }

        public StateBuilder created(String type, String id) {
            builder.set("created", builder.objectNode()
                    .<ObjectNode>set("type", builder.textNode(type))
                    .set("id", builder.textNode(id)));

            return new StateBuilder(builder);
        }

        public StateBuilder created(Enum<?> type, String id) {
            return created(type.name(), id);
        }

        public StateBuilder updated(String type, String id) {
            builder.set("updated", builder.objectNode()
                    .<ObjectNode>set("type", builder.textNode(type))
                    .set("id", builder.textNode(id)));

            return new StateBuilder(builder);
        }

        public StateBuilder updated(Enum<?> type, String id) {
            return updated(type.name(), id);
        }

        public JsonDomainEvent deleted(String type, String id) {
            builder.set("deleted", builder.objectNode()
                    .<ObjectNode>set("type", builder.textNode(type))
                    .set("id", builder.textNode(id)));

            return new JsonDomainEvent(builder);
        }

        public JsonDomainEvent deleted(Enum<?> type, String id) {
            return deleted(type.name(), id);
        }

        public ObjectNode builder() {
            return builder;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Builder) obj;
            return Objects.equals(this.builder, that.builder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(builder);
        }

        @Override
        public String toString() {
            return "Builder[" +
                    "builder=" + builder + ']';
        }

    }

    public static final class StateBuilder {
            private static final Map<Class<?>, Function<Object, JsonNode>> TO_JSON =
                    Map.of(
                            String.class, v -> JsonNodeFactory.instance.textNode(v.toString()),
                            Integer.class, v -> JsonNodeFactory.instance.numberNode((Integer) v)
                    );
        private final ObjectNode builder;

        public StateBuilder(ObjectNode builder) {
            this.builder = builder;
        }

            public StateBuilder attribute(String name, JsonNode value) {
                JsonNode attributes = builder.get("attributes");
                if (attributes == null) {
                    attributes = builder.objectNode();
                    builder.set("attributes", attributes);
                }

                ObjectNode objectNode = (ObjectNode) attributes;
                objectNode.set(name, value);

                return this;
            }

            public StateBuilder attribute(Enum<?> name, JsonNode value) {
                return attribute(name.name(), value);
            }

            public StateBuilder attribute(String name, Object value) {
                if (value == null) {
                    attribute(name, NullNode.getInstance());
                } else {
                    attribute(name, TO_JSON
                            .getOrDefault(value.getClass(), (v) -> JsonNodeFactory.instance.textNode(v.toString()))
                            .apply(value));
                }

                return this;
            }

            public StateBuilder attribute(Enum<?> name, Object value) {
                return attribute(name.name(), value);
            }

            public StateBuilder addedRelationship(String relationship, String type, String id) {

                if (id == null)
                    return this;

                JsonNode relationships = builder.get("addedrelationships");
                if (relationships == null) {
                    relationships = builder.arrayNode();
                    builder.set("addedrelationships", relationships);
                }

                ArrayNode arrayNode = (ArrayNode) relationships;
                JsonNode relationshipNode = arrayNode.objectNode()
                        .<ObjectNode>set("type", arrayNode.textNode(type))
                        .<ObjectNode>set("id", arrayNode.textNode(id))
                        .set("relationship", arrayNode.textNode(relationship));
                arrayNode.add(relationshipNode);

                return this;
            }

            public StateBuilder addedRelationship(Enum<?> relationship, Enum<?> type, String id) {
                return addedRelationship(relationship.name(), type.name(), id);
            }

            public StateBuilder removedRelationship(String relationship, String type, String id) {

                JsonNode relationships = builder.get("removedrelationships");
                if (relationships == null) {
                    relationships = builder.arrayNode();
                    builder.set("removedrelationships", relationships);
                }

                ArrayNode arrayNode = (ArrayNode) relationships;
                JsonNode relationshipNode = arrayNode.objectNode()
                        .<ObjectNode>set("type", arrayNode.textNode(type))
                        .<ObjectNode>set("id", id == null ? arrayNode.nullNode() : arrayNode.textNode(id))
                        .set("relationship", arrayNode.textNode(relationship));
                arrayNode.add(relationshipNode);

                return this;
            }

            public StateBuilder removedRelationship(Enum<?> relationship, Enum<?> type, String id) {
                return removedRelationship(relationship.name(), type.name(), id);
            }

            public JsonDomainEvent build() {
                return new JsonDomainEvent(builder);
            }

        public ObjectNode builder() {
            return builder;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (StateBuilder) obj;
            return Objects.equals(this.builder, that.builder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(builder);
        }

        @Override
        public String toString() {
            return "StateBuilder[" +
                    "builder=" + builder + ']';
        }

        }

    @JsonCreator(mode = DELEGATING)
    public JsonDomainEvent(ObjectNode json) {
        this.json = json;
    }

    @JsonValue
    public ObjectNode event() {
        return json;
    }

    @Override
    public ObjectNode json() {
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JsonDomainEvent) obj;
        return Objects.equals(this.json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }

    @Override
    public String toString() {
        return "JsonDomainEvent[" +
                "json=" + json + ']';
    }

}
