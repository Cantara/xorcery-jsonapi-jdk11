package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.model.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * @author rickardoberg
 */
public class Links
        implements JsonElement {

    private final ObjectNode json;

    public Links(ObjectNode json) {
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

        public Builder link(Consumer<Builder> consumer) {
            consumer.accept(this);
            return this;
        }

        public Builder link(String rel, String href) {
            builder.set(rel, builder.textNode(href));
            return this;
        }

        public Builder link(String rel, URI href) {
            return link(rel, href.toASCIIString());
        }

        public Builder link(Enum<?> rel, URI href) {
            return link(rel.name(), href);
        }

        /*
        public Builder link(String rel, UriBuilder href) {
            return link(rel, href.build());
        }
         */

        public Builder link(String rel, String href, Meta meta) {
            builder.set(rel, builder.objectNode()
                    .<ObjectNode>set("href", builder.textNode(href))
                    .set("meta", meta.json()));
            return this;
        }

        public Builder link(String rel, URI href, Meta meta) {
            return link(rel, href.toASCIIString(), meta);
        }

        /*
        public Builder link(String rel, UriBuilder href, Meta meta) {
            return link(rel, href.build().toASCIIString(), meta);
        }
         */

        public Links build() {
            return new Links(builder);
        }
    }

    public boolean isEmpty() {
        return object().isEmpty();
    }

    public Optional<Link> getSelf() {
        return getByRel("self");
    }

    public Optional<Link> getByRel(String name) {
        return Optional.ofNullable(object().get(name)).map(v -> new Link(name, v));
    }

    public List<Link> getLinks() {

        Iterator<Map.Entry<String, JsonNode>> fields = object().fields();
        List<Link> links = new ArrayList<>(object().size());
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            links.add(new Link(next.getKey(), next.getValue()));
        }
        return links;
    }
}
