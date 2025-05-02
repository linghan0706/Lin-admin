package org.lin.lin_admin.module.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 留言回复数据传输对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyDTO {
    private String replyContent;

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
} 