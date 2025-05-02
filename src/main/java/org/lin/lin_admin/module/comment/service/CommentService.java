package org.lin.lin_admin.module.comment.service;

import org.lin.lin_admin.module.comment.dto.CommentDTO;
import org.lin.lin_admin.module.comment.dto.CreateCommentRequest;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 根据文章ID获取所有评论
     * @param articleId 文章ID
     * @return 评论列表
     */
    List<CommentDTO> findByArticleId(Long articleId);
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论DTO
     */
    CommentDTO findById(Long id);
    
    /**
     * 创建评论
     * @param request 创建评论请求
     * @param userId 用户ID
     * @return 创建的评论
     */
    CommentDTO create(CreateCommentRequest request, Long userId);
    
    /**
     * 审核评论
     * @param id 评论ID
     * @param approved 是否通过
     * @return 更新后的评论
     */
    CommentDTO review(Long id, boolean approved);
    
    /**
     * 删除评论
     * @param id 评论ID
     */
    void delete(Long id);
    
    /**
     * 删除文章下的所有评论
     * @param articleId 文章ID
     */
    void deleteByArticleId(Long articleId);
} 