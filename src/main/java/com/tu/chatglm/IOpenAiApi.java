package com.tu.chatglm;

import com.tu.chatglm.model.ChatCompletionRequest;
import com.tu.chatglm.model.ChatCompletionResponse;
import com.tu.chatglm.model.ImageCompletionRequest;
import com.tu.chatglm.model.ImageCompletionResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * OpenAi 接口,用于扩展通用类服务
 *
 * @author tu
 * @date 2024-07-04 21:02
 */
public interface IOpenAiApi {

    String v3_completions = "api/paas/v3/model-api/{model}/sse-invoke";
    String v3_completions_sync = "api/pass/v3/model0api/{model}/invoke";


    @POST(v3_completions)
    Single<ChatCompletionResponse> completions(@Path("model") String model, @Body ChatCompletionRequest chatCompletionRequest);

    @POST(v3_completions_sync)
    Single<ChatCompletionResponse> completions(@Body ChatCompletionRequest chatCompletionRequest);

    String v4 = "api/paas/v4/chat/completions";

    String cogview3 = "api/paas/v4/images/generations";

    @POST(cogview3)
    Single<ImageCompletionResponse> genImages(@Body ImageCompletionRequest imageCompletionRequest);
}
