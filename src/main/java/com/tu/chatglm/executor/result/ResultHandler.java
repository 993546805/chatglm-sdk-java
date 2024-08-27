package com.tu.chatglm.executor.result;

import okhttp3.sse.EventSourceListener;

/**
 * @author tu
 * @date 2024-07-04 21:39
 */
public interface ResultHandler {
    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);

}
