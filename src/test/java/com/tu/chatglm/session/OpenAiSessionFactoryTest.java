package com.tu.chatglm.session;

import com.alibaba.fastjson.JSON;
import com.tu.chatglm.model.*;
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
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

@Slf4j
public class OpenAiSessionFactoryTest {

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

    /**
     * 流式对话；
     * 1. 默认 isCompatible = true 会兼容新旧版数据格式
     * 2. GLM_3_5_TURBO、GLM_4 支持联网等插件
     */
    @Test
    public void test_completions() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_3_5_TURBO); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
        request.setIncremental(false);
        request.setIsCompatible(true); // 是否对返回结果数据做兼容，24年1月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容。
        // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
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

        // 请求
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

        // 等待
        countDownLatch.await();
    }



}