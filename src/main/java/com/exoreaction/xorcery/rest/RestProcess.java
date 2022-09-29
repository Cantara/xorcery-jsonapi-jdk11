package com.exoreaction.xorcery.rest;

import jakarta.ws.rs.ServerErrorException;

import java.util.concurrent.CompletionStage;

public interface RestProcess<T> {
    void start();

    default void stop() {
        result().toCompletableFuture().cancel(true);
    }

    CompletionStage<T> result();

    default void complete(T value, Throwable t) {

        if (result().toCompletableFuture().isCancelled())
            return;

        if (t != null) {
            try {
                throw unwrap(t);
            } catch (ServerErrorException e) {
                retry();
            } catch (Throwable e) {
                result().toCompletableFuture().completeExceptionally(e);
            }
        } else {
            result().toCompletableFuture().complete(value);
        }
    }

    default void retry()
    {
        start();
    }

    default Throwable unwrap(Throwable t)
    {
        while (t.getCause() != null)
        {
            t = t.getCause();
        }
        return t;
    }
}
