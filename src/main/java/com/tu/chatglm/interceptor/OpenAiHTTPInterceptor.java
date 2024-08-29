package com.tu.chatglm.interceptor;

import com.tu.chatglm.common.Constants;
import com.tu.chatglm.session.Configuration;
import com.tu.chatglm.utils.BearerTokenUtils;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import retrofit2.http.Header;

import java.io.IOException;

/**
 * @author tu
 * @date 2024-07-04 22:08
 */
public class OpenAiHTTPInterceptor implements Interceptor {

    private final Configuration configuration;
    public OpenAiHTTPInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @NotNull
    public  Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();

        String apiKeyByUser = original.header("apiKey");
        if (apiKeyByUser == null) {
            apiKeyByUser = Constants.NULL;
        }
        String apiKey = Constants.NULL.equals(apiKeyByUser) ? configuration.getApiKey() : apiKeyByUser;

        Request request = original.newBuilder()
                .url(original.url())
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(apiKey, configuration.getApiSecret()))
                .header("Content-Type", Configuration.JSON_CONTENT_TYPE)
                .header("User-Agent", Configuration.DEFAULT_USER_AGENT)
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
