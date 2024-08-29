package com.tu.chatglm.test;

import com.alibaba.fastjson.JSON;
import com.tu.chatglm.model.*;
import com.tu.chatglm.session.Configuration;
import com.tu.chatglm.session.OpenAiSession;
import com.tu.chatglm.session.OpenAiSessionFactory;
import com.tu.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("9cd449f61025da137146bdd171f7fb92.KWGLV6urTrzMPzSK");
        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);

        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        this.openAiSession = factory.openSession();
    }

    @Test
    public void openSession() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_3_5_TURBO);
        request.setIncremental(false);
        request.setIsCompatible(true);

        request.setTools(new ArrayList<ChatCompletionRequest.Tool>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("小傅哥").build())
                        .build());
            }
        });
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("小傅哥的是谁")
                        .build());
            }
        });

        openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果 onEvent：{}", response.getData());
                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.finish.getCode().equals(type)) {
                    ChatCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatCompletionResponse.Meta.class);
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(meta));
                }

            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }



    /**
     * 同步请求
     */
    @Test
    public void test_completions_future() throws Exception {
        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_4_FLASH); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("1+1")
                        .build());
            }
        });

        CompletableFuture<String> future = openAiSession.completions(request);
        String response = future.get();

        log.info("测试结果：{}", response);
    }




    /**
     * 用于测试:同步
     */
    @Test
    public void test_completions_sync() throws Exception {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(Model.GLM_4_FLASH)
                .messages(Collections.singletonList(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("1+1=")
                        .build()))
                .build();

        ChatCompletionSyncResponse chatCompletionSyncResponse = openAiSession.completionsSync(request);
        log.info("测试结果：{}", JSON.toJSONString(chatCompletionSyncResponse));
    }




}