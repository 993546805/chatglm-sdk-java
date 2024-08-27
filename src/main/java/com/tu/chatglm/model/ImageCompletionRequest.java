package com.tu.chatglm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tu
 * @date 2024-07-04 21:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageCompletionRequest {

    /**
     * 模型；24年1月发布了 cogview-3 生成图片模型
     */
    private Model model = Model.COGVIEW_3;


    /**
     * 所需图像的文本描述
     */
    private String prompt;

    public String getModel() {
        return model.getCode();
    }

    public Model getModelEnum() {
        return model;
    }

    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("model", model.getCode());
        paramsMap.put("prompt", prompt);
        try {
            return new ObjectMapper().writeValueAsString(paramsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
