package org.lin.lin_admin.module.article.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.common.exception.ResourceNotFoundException;
import org.lin.lin_admin.module.article.dto.ArticleDTO;
import org.lin.lin_admin.module.article.service.ArticleService;
import org.lin.lin_admin.module.article.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章管理控制器 - 提供管理端的文章API
 */
@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminArticleController.class);

    @Autowired
    private ArticleService articleService;
    
    /**
     * 获取所有文章(包括草稿)
     * @return 文章列表
     */
    @GetMapping
    public ApiResponse<List<ArticleVO>> getAllArticles() {
        try {
            logger.info("获取所有文章");
        List<ArticleVO> articles = articleService.findAll();
        return ApiResponse.success(articles);
        } catch (Exception e) {
            logger.error("获取文章列表失败", e);
            return ApiResponse.serverError("获取文章列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页获取文章
     * @param page 页码
     * @param size 每页数量
     * @param status 状态(可选)
     * @return 文章分页列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<ArticleVO>> getArticlePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            logger.info("分页获取文章，页码：{}，每页数量：{}，状态：{}", page, size, status);
        PageResult<ArticleVO> result = articleService.page(page, size, status);
        return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("分页获取文章失败", e);
            return ApiResponse.serverError("分页获取文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文章统计数据
     * @return 文章统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getArticleStats() {
        try {
            logger.info("获取文章统计数据");
            Map<String, Object> stats = new HashMap<>();
            
            // 这里可以根据需求添加各种统计数据
            List<ArticleVO> allArticles = articleService.findAll();
            int totalCount = allArticles.size();
            
            long publishedCount = allArticles.stream()
                .filter(a -> "published".equals(a.getStatus()))
                .count();
                
            long draftCount = allArticles.stream()
                .filter(a -> "draft".equals(a.getStatus()))
                .count();
                
            long archivedCount = allArticles.stream()
                .filter(a -> "archived".equals(a.getStatus()))
                .count();
            
            stats.put("totalCount", totalCount);
            stats.put("publishedCount", publishedCount);
            stats.put("draftCount", draftCount);
            stats.put("archivedCount", archivedCount);
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            logger.error("获取文章统计数据失败", e);
            return ApiResponse.serverError("获取文章统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取文章
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleVO> getArticleById(@PathVariable Long id) {
        try {
            logger.info("根据ID获取文章，ID：{}", id);
            if (id == null) {
                return ApiResponse.error(400, "文章ID不能为空");
            }
            
        ArticleVO article = articleService.getById(id);
            
            // 检查文章是否存在
            if (article == null) {
                return ApiResponse.error(404, "文章不存在");
            }
            
        return ApiResponse.success(article);
        } catch (Exception e) {
            logger.error("获取文章失败，ID：{}", id, e);
            return ApiResponse.serverError("获取文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建文章
     * @param dto 创建文章请求
     * @return 创建的文章
     */
    @PostMapping
    public ApiResponse<ArticleVO> createArticle(@RequestBody ArticleDTO dto) {
        try {
            logger.info("创建文章请求: {}", dto);
            // 基本参数验证
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return ApiResponse.error(400, "文章标题不能为空");
            }
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return ApiResponse.error(400, "文章内容不能为空");
            }
            
        Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
        ArticleVO article = articleService.create(dto, userId);
            logger.info("文章创建成功，ID: {}", article.getId());
        return ApiResponse.success(article);
        } catch (Exception e) {
            logger.error("创建文章失败", e);
            return ApiResponse.serverError("创建文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新文章
     * @param id 文章ID
     * @param dto 更新文章请求
     * @return 更新后的文章
     */
    @PutMapping("/{id}")
    public ApiResponse<ArticleVO> updateArticle(@PathVariable Long id, @RequestBody ArticleDTO dto) {
        try {
            logger.info("更新文章请求，ID: {}，数据: {}", id, dto);
            // 基本参数验证
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return ApiResponse.error(400, "文章标题不能为空");
            }
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return ApiResponse.error(400, "文章内容不能为空");
            }
            
        dto.setId(id);
        Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
        ArticleVO article = articleService.update(dto, userId);
            logger.info("文章更新成功，ID: {}", id);
        return ApiResponse.success(article);
        } catch (ResourceNotFoundException e) {
            logger.error("文章不存在，ID: {}", id);
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            logger.error("更新文章失败，ID: {}", id, e);
            return ApiResponse.serverError("更新文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除文章
     * @param request 包含要删除的文章ID列表
     * @return 成功响应
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteArticles(@RequestBody BatchDeleteRequest request) {
        try {
            logger.info("批量删除文章请求，ID列表: {}", request.getIds());
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return ApiResponse.error(400, "文章ID列表不能为空");
            }
            
            Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
            int successCount = 0;
            StringBuilder errorMsg = new StringBuilder();
            
            for (Long id : request.getIds()) {
                try {
                    articleService.delete(id, userId);
                    successCount++;
                } catch (ResourceNotFoundException e) {
                    errorMsg.append("文章ID ").append(id).append(" 不存在; ");
                } catch (Exception e) {
                    errorMsg.append("删除文章ID ").append(id).append(" 失败: ").append(e.getMessage()).append("; ");
                }
            }
            
            if (successCount == request.getIds().size()) {
                logger.info("所有文章删除成功，共 {} 篇", successCount);
                return ApiResponse.success(null);
            } else {
                logger.warn("部分文章删除成功，成功: {}, 总数: {}, 错误: {}", 
                    successCount, request.getIds().size(), errorMsg.toString());
                return ApiResponse.error(207, "部分文章删除成功: " + errorMsg.toString());
            }
        } catch (Exception e) {
            logger.error("批量删除文章失败", e);
            return ApiResponse.serverError("批量删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文章
     * @param id 文章ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long id) {
        try {
            logger.info("删除文章请求，ID: {}", id);
        Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
        articleService.delete(id, userId);
            logger.info("文章删除成功，ID: {}", id);
        return ApiResponse.success(null);
        } catch (ResourceNotFoundException e) {
            logger.error("文章不存在，ID: {}", id);
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            logger.error("删除文章失败，ID: {}", id, e);
            return ApiResponse.serverError("删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 发布文章
     * @param id 文章ID
     * @return 成功响应
     */
    @PutMapping("/{id}/publish")
    public ApiResponse<ArticleVO> publishArticle(@PathVariable Long id) {
        try {
            logger.info("发布文章请求，ID: {}", id);
            Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
            ArticleDTO dto = new ArticleDTO();
            dto.setId(id);
            dto.setStatus("published");
            
            ArticleVO article = articleService.update(dto, userId);
            logger.info("文章发布成功，ID: {}", id);
            return ApiResponse.success(article);
        } catch (ResourceNotFoundException e) {
            logger.error("文章不存在，ID: {}", id);
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            logger.error("发布文章失败，ID: {}", id, e);
            return ApiResponse.serverError("发布文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 归档文章
     * @param id 文章ID
     * @return 成功响应
     */
    @PutMapping("/{id}/archive")
    public ApiResponse<ArticleVO> archiveArticle(@PathVariable Long id) {
        try {
            logger.info("归档文章请求，ID: {}", id);
            Long userId = getCurrentUserId();
            logger.info("当前用户ID: {}", userId);
            
            ArticleDTO dto = new ArticleDTO();
            dto.setId(id);
            dto.setStatus("archived");
            
            ArticleVO article = articleService.update(dto, userId);
            logger.info("文章归档成功，ID: {}", id);
            return ApiResponse.success(article);
        } catch (ResourceNotFoundException e) {
            logger.error("文章不存在，ID: {}", id);
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            logger.error("归档文章失败，ID: {}", id, e);
            return ApiResponse.serverError("归档文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                // 如果不是数字格式，可能是用户名，需要查询用户ID
                // 这里简化处理，默认返回1
                logger.warn("无法解析用户ID，使用默认ID: 1");
                return 1L;
            }
        }
        logger.warn("未找到认证信息，使用默认ID: 1");
        return 1L; // 默认用户ID
    }
    
    /**
     * 批量删除请求类
     */
    public static class BatchDeleteRequest {
        private List<Long> ids;
        
        public List<Long> getIds() {
            return ids;
        }
        
        public void setIds(List<Long> ids) {
            this.ids = ids;
        }
    }
} 