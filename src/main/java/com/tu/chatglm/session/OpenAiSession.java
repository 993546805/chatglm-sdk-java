package com.tu.chatglm.session;

import com.tu.chatglm.model.ChatCompletionRequest;
import com.tu.chatglm.model.ChatCompletionSyncResponse;
import com.tu.chatglm.model.ImageCompletionRequest;
import com.tu.chatglm.model.ImageCompletionResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author tu
 * @date 2024-07-04 21:35
 */
public interface OpenAiSession {

    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception;

    Configuration configuration();

}
