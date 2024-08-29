package com.tu.chatglm.session.defaults;

import cn.hutool.http.ContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tu.chatglm.IOpenAiApi;
import com.tu.chatglm.common.Constants;
import com.tu.chatglm.executor.Executor;
import com.tu.chatglm.model.*;
import com.tu.chatglm.session.Configuration;
import com.tu.chatglm.session.OpenAiSession;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 会话服务
 * @author tu
 * @date 2024-07-04 22:03
 */
public class DefaultOpenAiSession implements OpenAiSession {
    private final Configuration configuration;
    private final Map<Model, Executor> executorGroup;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;

    public DefaultOpenAiSession(Configuration configuration, Map<Model, Executor> executorGroup) {
        this.configuration = configuration;
        this.executorGroup = executorGroup;
        this.factory = configuration.createRequestFactory();
    }
    @Override
    public EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception {
        Executor executor = executorGroup.get(chatCompletionRequest.getModel());
        if (null == executor) {
            throw new RuntimeException(chatCompletionRequest.getModel() + " 模型执行器尚未实现！");
        }
        return executor.completions(chatCompletionRequest, eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        // 动态设置 Host Key 便于用户传递自己的信息
        String apiHost = Constants.NULL.equals(apiHostByUser) ? configuration.getApiHost() : apiHostByUser;
        String apiKey = Constants.NULL.equals(apiKeyByUser) ? configuration.getApiKey() : apiKeyByUser;

        // 构建请求参数
        Request request = new Request.Builder()
                .url(apiHost.concat(IOpenAiApi.v3_completions))
                .addHeader("apiKey", apiKey)
                .addHeader("userName", "LIYIZE")
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), new ObjectMapper().writeValueAsString(chatCompletionRequest)))
                .build();
        // 返回结果细腻系
        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception {
        Executor executor = executorGroup.get(chatCompletionRequest.getModel());
        if (null == executor) {
            throw new RuntimeException(chatCompletionRequest.getModel() + " 模型执行器尚未实现！");
        }
        return executor.completions(chatCompletionRequest);

    }

    @Override
    public ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception {
        Executor executor = executorGroup.get(chatCompletionRequest.getModel());
        if (null == executor) {
            throw new RuntimeException(chatCompletionRequest.getModel() + " 模型执行器尚未实现！");
        }
        return executor.completionsSync(chatCompletionRequest);

    }

    @Override
    public ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception {
        Executor executor = executorGroup.get(imageCompletionRequest.getModelEnum());
        if (null == executor) {
            throw new RuntimeException(imageCompletionRequest.getModel() + " 模型执行器尚未实现！");
        }
        return executor.genImages(imageCompletionRequest);

    }

    @Override
    public Configuration configuration() {
        return configuration;
    }
}
