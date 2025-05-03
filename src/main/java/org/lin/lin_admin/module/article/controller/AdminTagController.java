package org.lin.lin_admin.module.article.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.article.model.Tag;
import org.lin.lin_admin.module.article.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器 - 提供管理端的标签API
 */
@RestController
@RequestMapping("/admin/tags")
public class AdminTagController {

    private static final Logger logger = LoggerFactory.getLogger(AdminTagController.class);

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping
    public ApiResponse<List<Tag>> getAllTags() {
        try {
            logger.info("获取所有标签");
            List<Tag> tags = tagService.findAll();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            logger.error("获取标签列表失败", e);
            return ApiResponse.serverError("获取标签列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Tag> getTagById(@PathVariable Long id) {
        try {
            logger.info("获取标签，ID：{}", id);
            Tag tag = tagService.findById(id);
            if (tag == null) {
                logger.warn("标签不存在，ID：{}", id);
                return ApiResponse.error(404, "标签不存在");
            }
            return ApiResponse.success(tag);
        } catch (Exception e) {
            logger.error("获取标签失败，ID：{}", id, e);
            return ApiResponse.serverError("获取标签失败: " + e.getMessage());
        }
    }

    /**
     * 创建标签
     * @param tag 标签对象
     * @return 创建的标签
     */
    @PostMapping
    public ApiResponse<Tag> createTag(@RequestBody Tag tag) {
        try {
            logger.info("创建标签请求：{}", tag.getName());
            if (tag.getName() == null || tag.getName().trim().isEmpty()) {
                logger.warn("标签名称为空");
                return ApiResponse.badRequest("标签名称不能为空");
            }
            
            Tag createdTag = tagService.create(tag);
            logger.info("标签创建成功，ID：{}", createdTag.getId());
            return ApiResponse.success(createdTag);
        } catch (IllegalArgumentException e) {
            logger.warn("创建标签失败：{}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("创建标签失败", e);
            return ApiResponse.serverError("创建标签失败: " + e.getMessage());
        }
    }

    /**
     * 更新标签
     * @param id 标签ID
     * @param tag 标签对象
     * @return 更新后的标签
     */
    @PutMapping("/{id}")
    public ApiResponse<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            logger.info("更新标签请求，ID：{}，新名称：{}", id, tag.getName());
            if (tag.getName() == null || tag.getName().trim().isEmpty()) {
                logger.warn("标签名称为空");
                return ApiResponse.badRequest("标签名称不能为空");
            }
            
            Tag updatedTag = tagService.update(id, tag);
            logger.info("标签更新成功，ID：{}", updatedTag.getId());
            return ApiResponse.success(updatedTag);
        } catch (IllegalArgumentException e) {
            logger.warn("更新标签失败：{}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("更新标签失败，ID：{}", id, e);
            return ApiResponse.serverError("更新标签失败: " + e.getMessage());
        }
    }

    /**
     * 删除标签
     * @param id 标签ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        try {
            logger.info("删除标签请求，ID：{}", id);
            boolean success = tagService.delete(id);
            if (!success) {
                logger.warn("标签不存在，ID：{}", id);
                return ApiResponse.error(404, "标签不存在");
            }
            logger.info("标签删除成功，ID：{}", id);
            return ApiResponse.success(null);
        } catch (IllegalStateException e) {
            logger.warn("删除标签失败：{}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("删除标签失败：{}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("删除标签失败，ID：{}", id, e);
            return ApiResponse.serverError("删除标签失败: " + e.getMessage());
        }
    }
} 