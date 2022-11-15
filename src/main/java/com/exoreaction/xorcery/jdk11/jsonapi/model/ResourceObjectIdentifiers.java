package com.exoreaction.xorcery.jdk11.jsonapi.model;

import com.exoreaction.xorcery.jdk11.builders.With;
import com.exoreaction.xorcery.jdk11.json.model.JsonElement;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.List;

/**
 * @author rickardoberg
 */
public class ResourceObjectIdentifiers
        implements JsonElement {

    private final ArrayNode json;

    public ResourceObjectIdentifiers(ArrayNode json) {
        this.json = json;
    }

    @Override
    public ArrayNode json() {
        return json;
    }

    public static class Builder
            implements With<Builder>
    {
        private final ArrayNode builder;

        public Builder(ArrayNode builder) {
            this.builder = builder;
        }

        public Builder() {
            this(JsonNodeFactory.instance.arrayNode());
        }

        public Builder resource(ResourceObjectIdentifier resourceObjectIdentifier) {
            builder.add(resourceObjectIdentifier.json());
            return this;
        }

        public Builder resource(ResourceObject resourceObject) {
            builder.add(resourceObject.getResourceObjectIdentifier().json());
            return this;
        }

        public Builder resources(ResourceObjectIdentifiers resourceObjectIdentifiers) {
            resourceObjectIdentifiers.getResources().forEach(this::resource);
            return this;
        }

        public Builder resources(ResourceObjects resourceObjects) {
            for (ResourceObject resource : resourceObjects) {
                builder.add(resource.getResourceObjectIdentifier().json());
            }
            return this;
        }

        public ResourceObjectIdentifiers build() {
            return new ResourceObjectIdentifiers(builder);
        }
    }

    public List<ResourceObjectIdentifier> getResources() {
        return JsonElement.getValuesAs(array(), ResourceObjectIdentifier::new);
    }

    public boolean contains(ResourceObjectIdentifier resourceObjectIdentifier) {
        return getResources().stream().anyMatch(ro -> ro.equals(resourceObjectIdentifier));
    }

    public boolean contains(ResourceObject resourceObject) {
        return contains(resourceObject.getResourceObjectIdentifier());
    }
}
