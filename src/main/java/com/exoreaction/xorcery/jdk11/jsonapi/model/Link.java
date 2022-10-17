package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.Optional;

/**
 * @author rickardoberg
 */
public class Link {
    private final String rel;
    private final JsonNode value;

    public Link(String rel, JsonNode value) {
        this.rel = rel;
        this.value = value;
    }

    public Link(String rel, String uri) {
        this(rel, JsonNodeFactory.instance.textNode(uri));
    }

    public String rel() {
        return rel;
    }

    public String getHref() {
        return value.isTextual() ? value.textValue() : value.path("href").textValue();
    }

    public URI getHrefAsUri() {
        return URI.create(getHref());
    }

    /*
    public UriBuilder getHrefAsUriBuilder() {
        return UriBuilder.fromUri(getHref());
    }

    public UriTemplate getHrefAsUriTemplate() {
        return new UriTemplate(getHref());
    }

    public Link createURI(String... values)
    {
        return new Link(rel, new UriTemplate(getHref()).createURI(values));
    }

    public boolean isTemplate() {
        return !new UriTemplate(getHref()).getTemplateVariables().isEmpty();
    }
     */

    public boolean isWebsocket() {
        return getHrefAsUri().getScheme().startsWith("ws");
    }

    public Optional<Meta> getMeta() {
        if (value.isTextual()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(value.path("meta")).map(ObjectNode.class::cast).map(Meta::new);
        }
    }
}
