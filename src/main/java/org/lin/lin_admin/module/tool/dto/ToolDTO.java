package org.lin.lin_admin.module.tool.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 工具数据传输对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolDTO {
    private Long id;
    private String name;
    private String description;
    private String link;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
} 