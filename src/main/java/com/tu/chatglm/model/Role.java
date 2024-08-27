package com.tu.chatglm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author tu
 * @date 2024-07-04 21:26
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /**
     * user 用户输入的内容，role位user
     */
    user("user"),
    /**
     * 模型生成的内容，role位assistant
     */
    assistant("assistant"),

    /**
     * 系统
     */
    system("system"),

    ;
    private final String code;
}
