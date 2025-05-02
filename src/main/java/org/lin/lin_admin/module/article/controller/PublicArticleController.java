package org.lin.lin_admin.module.article.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.module.article.service.ArticleService;
import org.lin.lin_admin.module.article.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开文章控制器 - 提供前台展示的文章API
 */
@RestController
@RequestMapping("/articles")
public class PublicArticleController {

    @Autowired
    private ArticleService articleService;
    
    /**
     * 获取所有已发布的文章(分页)
     * @param page 页码
     * @param size 每页数量
     * @return 文章列表
     */
    @GetMapping
    public ApiResponse<List<ArticleVO>> getPublishedArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ArticleVO> articles = articleService.findAllPublished(page, size);
        return ApiResponse.success(articles);
    }
    
    /**
     * 分页获取已发布文章
     * @param page 页码
     * @param size 每页数量 
     * @return 文章分页列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<ArticleVO>> getArticlePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<ArticleVO> result = articleService.page(page, size, "published");
        return ApiResponse.success(result);
    }
    
    /**
     * 根据ID获取已发布的文章
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleVO> getArticleById(@PathVariable Long id) {
        try {
            if (id == null) {
                return ApiResponse.error(400, "文章ID不能为空");
            }
            
            ArticleVO article = articleService.getById(id);
            
            // 检查文章是否存在
            if (article == null) {
                return ApiResponse.error(404, "文章不存在");
            }
            
            // 验证文章是否已发布
            if (!"published".equals(article.getStatus())) {
                return ApiResponse.error(404, "文章不存在或未发布");
            }
            
            return ApiResponse.success(article);
        } catch (Exception e) {
            return ApiResponse.serverError("获取文章失败: " + e.getMessage());
        }
    }
} 