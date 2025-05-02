package org.lin.lin_admin.module.comment.dto;

import org.lin.lin_admin.common.paging.PageParam;

/**
 * 评论查询条件
 */
public class CommentQuery extends PageParam {
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评论状态
     */
    private Integer status;
    
    /**
     * 关键词搜索
     */
    private String keyword;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
} 