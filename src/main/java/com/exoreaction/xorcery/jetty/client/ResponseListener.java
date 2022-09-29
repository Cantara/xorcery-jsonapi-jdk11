package com.exoreaction.xorcery.jetty.client;

import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class ResponseListener extends BufferingResponseListener
{
    private final CompletableFuture<Result> future = new CompletableFuture<>();

    @Override
    public void onComplete(Result result)
    {
        if (result.isFailed())
        {
            Throwable cause = result.getFailure();
            future.completeExceptionally(cause);
            return;
        }

        result = new Result(result.getRequest(), new HttpContentResponse(result.getResponse(), getContent(), getMediaType(), getEncoding()));

        future.complete(result);
    }

    /**
     * @return the server response
     */
    public CompletionStage<Result> getResult()
    {
        return future;
    }
}