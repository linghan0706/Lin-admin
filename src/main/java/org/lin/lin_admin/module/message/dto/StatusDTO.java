package org.lin.lin_admin.module.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 留言状态数据传输对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusDTO {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 