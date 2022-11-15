package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.model.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author rickardoberg
 */
public class ResourceDocument
        implements JsonElement {

    private final ObjectNode json;

    public ResourceDocument(ObjectNode json) {
        this.json = json;
    }

    @Override
    public ObjectNode json() {
        return json;
    }

    public static class Builder
            implements With<ResourceDocument.Builder>
    {
        private final ObjectNode builder;

        public Builder(ObjectNode builder) {
            this.builder = builder;
        }

        public Builder() {
            this(JsonNodeFactory.instance.objectNode());
        }

        public Builder data(ResourceObject.Builder value) {
            return data(value.build());
        }

        public Builder data(ResourceObject value) {
            if (value == null) {
                builder.putNull("data");
            } else {
                builder.set("data", value.json());
            }
            return this;
        }

        public Builder data(ResourceObjects.Builder value) {
            return data(value.build());
        }

        public Builder data(ResourceObjects value) {
            builder.set("data", value.json());
            return this;
        }

        public Builder errors(Errors.Builder value) {
            return errors(value.build());
        }

        public Builder errors(Errors value) {
            builder.set("errors", value.json());
            return this;
        }

        public Builder meta(Meta value) {
            if (!value.object().isEmpty()) {
                builder.set("meta", value.json());
            }
            return this;
        }

        public Builder jsonapi(JsonApi value) {
            if (!value.object().isEmpty()) {
                builder.set("jsonapi", value.json());
            }
            return this;
        }

        public Builder links(Links.Builder value) {
            return links(value.build());
        }

        public Builder links(Links value) {
            if (!value.object().isEmpty()) {
                builder.set("links", value.json());
            }
            return this;
        }

        public Builder included(Included.Builder value) {
            return included(value.build());
        }

        public Builder included(Included value) {
            if (!value.array().isEmpty()) {
                builder.set("included", value.json());
            }
            return this;
        }

        public ResourceDocument build() {
            return new ResourceDocument(builder);
        }

    }

    public boolean isCollection() {
        return object().get("data") instanceof ArrayNode;
    }

    public Optional<ResourceObject> getResource() {
        JsonNode data = object().path("data");
        if (!data.isObject()) {
            return Optional.empty();
        }

        return Optional.of(new ResourceObject((ObjectNode) data));
    }


    public Optional<ResourceObjects> getResources() {
        JsonNode data = object().path("data");
        if (!data.isArray()) {
            return Optional.empty();
        }

        return Optional.of(new ResourceObjects(((ArrayNode) data)));
    }

    public Errors getErrors() {
        JsonNode errors = object().path("errors");
        return new Errors(errors instanceof ArrayNode ? (ArrayNode) errors :
                JsonNodeFactory.instance.arrayNode());
    }

    public Meta getMeta() {
        JsonNode meta = object().path("meta");
        return new Meta(meta instanceof ObjectNode ? (ObjectNode) meta :
                JsonNodeFactory.instance.objectNode());
    }

    public JsonApi getJsonapi() {
        JsonNode jsonapi = object().path("jsonapi");
        return new JsonApi(jsonapi instanceof ObjectNode ? (ObjectNode) jsonapi :
                JsonNodeFactory.instance.objectNode());
    }

    public Links getLinks() {
        JsonNode links = object().path("links");
        return new Links(links instanceof ObjectNode ? (ObjectNode) links :
                JsonNodeFactory.instance.objectNode());
    }

    public Included getIncluded() {
        JsonNode included = object().path("included");
        return new Included(included instanceof ArrayNode ? (ArrayNode) included :
                JsonNodeFactory.instance.arrayNode());
    }

    /**
     * If this ResourceDocument is a collection of ResourceObjects, then split into a set of ResourceDocuments that are
     * single-resource. Referenced included ResourceObjects are included in the split documents, and duplicated if necessary.
     * <p>
     * If this ResourceDocument is a single ResourceObject, then just return it.
     *
     * @return
     */
    public Stream<ResourceDocument> split() {
        return getResources().map(ros -> ros.stream().map(ro ->
                        new Builder()
                                .data(ro)
                                .links(getLinks())
                                .meta(getMeta())
                                .jsonapi(getJsonapi())
                                .included(new Included.Builder()
                                        .includeRelated(ro, getIncluded().getIncluded())
                                        .build())
                                .build()))
                .orElse(Stream.of(this));
    }

    /**
     * Return copy of ResourceDocument, but with all links having their host and port replaced
     * @param baseUri URI with host and port to use
     * @return
     */
    public ResourceDocument resolve(URI baseUri)
    {
        ResourceDocument resolved = new ResourceDocument(json.deepCopy());

        resolve(baseUri, resolved.getLinks());
        resolved.getResource().ifPresent(ro -> resolve(baseUri, ro.getLinks()));
        resolved.getResources().ifPresent(ros -> ros.forEach(ro -> resolve(baseUri, ro.getLinks())));
        resolved.getIncluded().getIncluded().forEach(ro -> resolve(baseUri, ro.getLinks()));

        return resolved;
    }

    private void resolve(URI baseUri, Links links)
    {
        ObjectNode linksJson = links.json();
        Iterator<Map.Entry<String, JsonNode>> fields = linksJson.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            JsonNode value = next.getValue();
            String key = next.getKey();
            if (value instanceof TextNode)
            {
                TextNode textNode = (TextNode) value;
                /*
                String resolvedUri = UriBuilder.fromUri(textNode.textValue())
                        .host(baseUri.getHost())
                        .port(baseUri.getPort())
                        .toTemplate();
                 */
                String resolvedUri = resolveUri(baseUri, textNode.textValue());
                linksJson.set(key, linksJson.textNode(resolvedUri));
            } else if (value instanceof ObjectNode)
            {
                ObjectNode objectNode = (ObjectNode) value;
                /*
                String resolvedUri = UriBuilder.fromUri(objectNode.get("href").textValue())
                        .host(baseUri.getHost())
                        .port(baseUri.getPort())
                        .toTemplate();
                 */
                String resolvedUri = resolveUri(baseUri, objectNode.get("href").textValue());
                objectNode.set(key, linksJson.textNode(resolvedUri));
            }
        }
    }

    private static String resolveUri(URI baseUri, String relativeUriStr) {
        URI relativeUri = URI.create(relativeUriStr);
        String resolvedUri;
        try {
            resolvedUri = new URI(
                    baseUri.getScheme(),
                    baseUri.getUserInfo(),
                    baseUri.getHost(),
                    baseUri.getPort(),
                    relativeUri.getPath(),
                    relativeUri.getQuery(),
                    relativeUri.getFragment()
            ).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return resolvedUri;
    }
}
