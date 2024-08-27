package com.tu.chatglm.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tu
 * @date 2024-07-04 21:27
 */
@Slf4j
public class BearerTokenUtils {

    /**
     * 过期时间:默认 30 分钟
     */
    private static final long expireMillis = 30 * 60 * 1000L;

    // 缓存服务
    public static Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(expireMillis - (60 * 1000L), TimeUnit.MILLISECONDS)
            .build();

    /**
     * 对 ApiKey 签名
     * @param apiKey 登录创建 ApiKey
     * @param apiSecret apiKey的后半部分 9cd449f61025da137146bdd171f7fb92.KWGLV6urTrzMPzSK 取 KWGLV6urTrzMPzSK 使用
     * @return
     */
    public static String getToken(String apiKey, String apiSecret){
        // 缓存 Token
        String token = cache.getIfPresent(apiKey);
        if (null != token) {
            return token;
        }
        // 创建 token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        Map<String,Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        payload.put("exp", System.currentTimeMillis() + expireMillis);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String,Object> headerCliaims = new HashMap<>();
        headerCliaims.put("alg", "HS256");
        headerCliaims.put("sign_type", "SIGN");
        token = JWT.create().withPayload(payload).withHeader(headerCliaims).sign(algorithm);
        cache.put(apiKey, token);
        return token;
    }
}
