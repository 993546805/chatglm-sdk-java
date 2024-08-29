# 智谱AI SDK

```java
public OpenAiSession test_OpenAiSessionFactory() {
    Configuration configuration = new Configuration();
    configuration.setApiHost("https://open.bigmodel.cn/");
    configuration.setApiSecretKey("9cd449f61025da13714********.KWGLV6******zSK");
    configuration.setLevel(HttpLoggingInterceptor.Level.BODY);

    OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

    return factory.openSession();
}
```