package com.exoreaction.xorcery.jsonapi.model;

import com.exoreaction.xorcery.json.JsonElement;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rickardoberg
 */
public class JsonApi
        implements JsonElement {

    private final ObjectNode json;

    public JsonApi(ObjectNode json) {
        this.json = json;
    }

    @Override
    public ObjectNode json() {
        return json;
    }
}
