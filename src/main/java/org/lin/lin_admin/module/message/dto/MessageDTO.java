package org.lin.lin_admin.module.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 留言数据传输对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {
    private String nickname;
    private String content;
    private String contactInfo;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
} 