package com.tu.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author tu
 * @date 2024-07-04 21:23
 */
@Getter
@RequiredArgsConstructor
public enum EventType {
    add("add","增量"),
    finish("finish", "结束"),
    error("error", "错误"),
    interrupted("interrupted", "中断");

    private final String code;
    private final String info;
}
