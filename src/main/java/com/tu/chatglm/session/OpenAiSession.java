package com.tu.chatglm.session;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    /**
     * 问答模型 GPT-3.5/4.0 & 流式反馈
     * @param apiHostByUser 自定义 Host
     * @param apiKeyByUser 自定义 Key
     * @param chatCompletionRequest 请求信息
     * @param eventSourceListener 实现监听;通过监听的 onEvent 方法接收数据
     * @return 应答结果
     */
    EventSource completions(String apiHostByUser, String apiKeyByUser, ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception;

    Configuration configuration();

}
