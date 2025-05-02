package org.lin.lin_admin.module.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 */
@Data
public class CommentDTO {
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 关联文章ID
     */
    private Long articleId;
    
    /**
     * 评论者ID
     */
    private Long userId;
    
    /**
     * 评论者昵称
     */
    private String username;
    
    /**
     * 评论者头像
     */
    private String userAvatar;
    
    /**
     * 评论状态（0待审核，1已批准，2已拒绝）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 