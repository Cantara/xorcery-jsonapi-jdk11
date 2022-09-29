package com.exoreaction.xorcery.jsonapi.client;

import com.exoreaction.xorcery.jaxrs.MediaTypes;
import com.exoreaction.xorcery.jsonapi.model.Link;
import com.exoreaction.xorcery.jsonapi.model.ResourceDocument;
import com.exoreaction.xorcery.jsonapi.model.ResourceObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class JsonApiClient {
    private final Client client;

    public JsonApiClient(Client client) {
        this.client = client;
    }

    public CompletionStage<ResourceDocument> get(Link link) {
        CompletableFuture<ResourceDocument> future = new CompletableFuture<>();
        client.target(link.getHrefAsUri())
                .request(MediaTypes.APPLICATION_JSON_API_TYPE)
                .buildGet()
                .submit(new ResourceDocumentCallback(future, link));
        return future;
    }


    public CompletionStage<ResourceObject> submit(Link link, ResourceObject resourceObject) {
        CompletableFuture<ResourceObject> future = new CompletableFuture<>();
        client.target(link.getHrefAsUri())
                .request(MediaTypes.APPLICATION_JSON_API_TYPE)
                .buildPost(Entity.entity(resourceObject, MediaTypes.APPLICATION_JSON_API_TYPE))
                .submit(new ResourceObjectCallback(future, link));
        return future;
    }

    /**
     * For batch uploads
     *
     * @param link
     * @param resourceDocument
     * @return
     */
    public CompletionStage<ResourceObject> submit(Link link, ResourceDocument resourceDocument) {
        CompletableFuture<ResourceObject> future = new CompletableFuture<>();
        client.target(link.getHrefAsUri())
                .request(MediaTypes.APPLICATION_JSON_API_TYPE)
                .buildPost(Entity.entity(resourceDocument, MediaTypes.APPLICATION_JSON_API_TYPE))
                .submit(new ResourceObjectCallback(future, link));
        return future;
    }

    public Client client() {
        return client;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JsonApiClient) obj;
        return Objects.equals(this.client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client);
    }

    @Override
    public String toString() {
        return "JsonApiClient[" +
                "client=" + client + ']';
    }


    private static class Callback<T>
            implements InvocationCallback<T> {

        private final CompletableFuture<T> future;
        private final Link link;

        public Callback(CompletableFuture<T> future, Link link) {
            this.future = future;
            this.link = link;
        }

        @Override
        public void completed(T result) {
            future.complete(result);
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof ProcessingException) {
                future.completeExceptionally(new ProcessingException(link.getHref(), throwable.getCause()));
            } else {
                future.completeExceptionally(throwable);
            }
        }
    }

    private static class ResourceObjectCallback
            extends Callback<ResourceObject> {
        public ResourceObjectCallback(CompletableFuture<ResourceObject> future, Link link) {
            super(future, link);
        }
    }

    private static class ResourceDocumentCallback
            extends Callback<ResourceDocument> {
        public ResourceDocumentCallback(CompletableFuture<ResourceDocument> future, Link link) {
            super(future, link);
        }
    }

}