package com.master.chat.api.openai.entity.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @since 2023-03-02
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatChoice implements Serializable {

    private long index;
    /**
     * 请求参数stream为true返回是delta
     */
    @JsonProperty("delta")
    private OpenAiMessage delta;
    /**
     * 请求参数stream为false返回是message
     */
    @JsonProperty("message")
    private OpenAiMessage message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
