package org.lin.lin_admin.module.article.service;

import org.lin.lin_admin.module.article.dto.ArticleDTO;
import org.lin.lin_admin.module.article.vo.ArticleVO;
import org.lin.lin_admin.common.paging.PageResult;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {
    
    /**
     * 获取所有已发布的文章(分页)
     * @param page 页码
     * @param size 每页数量
     * @return 文章列表
     */
    List<ArticleVO> findAllPublished(int page, int size);
    
    /**
     * 获取所有文章(包括草稿)
     * @return 文章列表
     */
    List<ArticleVO> findAll();
    
    /**
     * 分页获取文章
     * @param page 页码
     * @param size 每页数量
     * @param status 状态(可选)
     * @return 分页结果
     */
    PageResult<ArticleVO> page(int page, int size, String status);
    
    /**
     * 根据ID获取文章
     * @param id 文章ID
     * @return 文章VO
     */
    ArticleVO getById(Long id);
    
    /**
     * 创建文章
     * @param dto 文章DTO
     * @param operatorId 操作者ID
     * @return 创建的文章
     */
    ArticleVO create(ArticleDTO dto, Long operatorId);
    
    /**
     * 更新文章
     * @param dto 文章DTO
     * @param operatorId 操作者ID
     * @return 更新后的文章
     */
    ArticleVO update(ArticleDTO dto, Long operatorId);
    
    /**
     * 删除文章
     * @param id 文章ID
     * @param operatorId 操作者ID
     */
    void delete(Long id, Long operatorId);
} 