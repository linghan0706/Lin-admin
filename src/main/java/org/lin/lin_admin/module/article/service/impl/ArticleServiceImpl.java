package org.lin.lin_admin.module.article.service.impl;

import org.lin.lin_admin.common.exception.ResourceNotFoundException;
import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.module.article.dto.ArticleDTO;
import org.lin.lin_admin.module.article.dto.CreateArticleRequest;
import org.lin.lin_admin.module.article.mapper.ArticleMapper;
import org.lin.lin_admin.module.article.mapper.TagMapper;
import org.lin.lin_admin.module.article.model.Article;
import org.lin.lin_admin.module.article.model.Tag;
import org.lin.lin_admin.module.article.service.ArticleService;
import org.lin.lin_admin.module.article.vo.ArticleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<ArticleVO> findAllPublished(int page, int size) {
        logger.info("查询所有已发布文章，页码：{}，每页数量：{}", page, size);
        int offset = (page - 1) * size;
        List<Article> articles = articleMapper.findAllPublished(offset, size);
        logger.info("查询到{}篇已发布文章", articles.size());
        return articles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> findAll() {
        logger.info("查询所有文章");
        List<Article> articles = articleMapper.findAll();
        logger.info("查询到{}篇文章", articles.size());
        return articles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ArticleVO> page(int page, int size, String status) {
        logger.info("分页查询文章，页码：{}，每页数量：{}，状态：{}", page, size, status);
        int offset = (page - 1) * size;
        List<Article> articles;
        long total;
        
        if (status != null && !status.isEmpty()) {
            logger.info("按状态查询：{}", status);
            articles = articleMapper.findAllByStatus(status, offset, size);
            total = articleMapper.countByStatus(status);
        } else {
            logger.info("查询所有状态");
            articles = articleMapper.findAll();
            total = articleMapper.count();
        }
        
        logger.info("查询到{}篇文章，总数：{}", articles.size(), total);
        List<ArticleVO> voList = articles.stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(voList, total, page, size);
    }

    @Override
    public ArticleVO getById(Long id) {
        try {
            logger.info("根据ID查询文章：{}", id);
            if (id == null) {
                logger.warn("文章ID为空");
                return null;
            }
            
            Article article = articleMapper.findById(id);
            if (article == null) {
                logger.warn("未找到ID为{}的文章", id);
                return null;
            }
            
            logger.info("查找到文章：{}", article.getTitle());
            return convertToVO(article);
        } catch (Exception e) {
            logger.error("获取文章失败，ID：{}", id, e);
            throw new RuntimeException("获取文章失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ArticleVO create(ArticleDTO dto, Long operatorId) {
        try {
            logger.info("创建文章：{}，操作者ID：{}", dto.getTitle(), operatorId);
            Article article = new Article();
            
            // 逐个设置属性而不是使用BeanUtils.copyProperties(dto, article)
            article.setTitle(dto.getTitle());
            article.setContent(dto.getContent());
            
            // 设置摘要，如果没有提供则从内容自动生成
            if (dto.getSummary() != null && !dto.getSummary().trim().isEmpty()) {
                article.setSummary(dto.getSummary());
            } else {
                article.setSummary(generateSummary(dto.getContent()));
            }
            
            article.setAuthorId(operatorId);
            
            // 设置状态，默认为草稿
            if (dto.getStatus() != null) {
                article.setStatus(dto.getStatus());
            } else {
                article.setStatus("draft");
            }
            
            // 设置封面图片
            article.setCoverImage(dto.getCoverImage());
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            article.setCreatedAt(now);
            article.setUpdatedAt(now);
            
            // 如果状态是已发布，设置发布时间
            if ("published".equals(article.getStatus())) {
                article.setPublishedAt(now);
            }
            
            logger.info("插入文章记录");
            int rows = articleMapper.insert(article);
            logger.info("插入文章记录完成，受影响行数：{}，新文章ID：{}", rows, article.getId());
            
            // 处理标签 - 安全地处理tagIds可能为null的情况
            if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
                logger.info("处理文章标签，标签数量：{}", dto.getTagIds().size());
                handleTags(article.getId(), dto.getTagIds());
            } else {
                logger.info("文章没有标签");
            }
            
            logger.info("文章创建成功，ID：{}", article.getId());
            return convertToVO(article);
        } catch (Exception e) {
            logger.error("创建文章失败", e);
            throw new RuntimeException("创建文章失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ArticleVO update(ArticleDTO dto, Long operatorId) {
        try {
            logger.info("更新文章，ID：{}，操作者ID：{}", dto.getId(), operatorId);
            Article existingArticle = articleMapper.findById(dto.getId());
            if (existingArticle == null) {
                logger.warn("文章不存在，ID：{}", dto.getId());
                throw new ResourceNotFoundException("文章", dto.getId());
            }
            
            // 验证操作者是否为作者
            if (!existingArticle.getAuthorId().equals(operatorId)) {
                logger.warn("操作者ID（{}）与文章作者ID（{}）不匹配", operatorId, existingArticle.getAuthorId());
                throw new RuntimeException("只有作者可以编辑文章");
            }
            
            logger.info("更新前：title={}，status={}", existingArticle.getTitle(), existingArticle.getStatus());
            
            // 只复制非null属性，逐个设置而不是使用BeanUtils.copyProperties
            if (dto.getTitle() != null) {
                logger.debug("更新标题：{}", dto.getTitle());
                existingArticle.setTitle(dto.getTitle());
            }
            if (dto.getContent() != null) {
                logger.debug("更新内容：{}", (dto.getContent().length() > 50) ? dto.getContent().substring(0, 50) + "..." : dto.getContent());
                existingArticle.setContent(dto.getContent());
                // 如果内容更新了但没有提供新的摘要，则自动更新摘要
                if (dto.getSummary() == null || dto.getSummary().trim().isEmpty()) {
                    existingArticle.setSummary(generateSummary(dto.getContent()));
                    logger.debug("自动更新摘要：{}", existingArticle.getSummary());
                }
            }
            if (dto.getSummary() != null) {
                logger.debug("更新摘要：{}", dto.getSummary());
                existingArticle.setSummary(dto.getSummary());
            }
            if (dto.getStatus() != null) {
                logger.debug("更新状态：{}", dto.getStatus());
                existingArticle.setStatus(dto.getStatus());
            }
            if (dto.getCoverImage() != null) {
                logger.debug("更新封面图片：{}", dto.getCoverImage());
                existingArticle.setCoverImage(dto.getCoverImage());
            }
            
            // 设置更新时间
            existingArticle.setUpdatedAt(LocalDateTime.now());
            logger.debug("设置更新时间：{}", existingArticle.getUpdatedAt());
            
            // 如果是首次发布设置发布时间
            if ("published".equals(dto.getStatus()) && existingArticle.getPublishedAt() == null) {
                logger.info("首次发布文章，设置发布时间");
                existingArticle.setPublishedAt(LocalDateTime.now());
            }
            
            logger.info("执行数据库更新");
            int rows = articleMapper.update(existingArticle);
            logger.info("更新文章完成，受影响行数：{}", rows);
            
            // 处理标签 - 安全地处理tagIds可能为null的情况
            if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
                logger.info("更新文章标签，标签数量：{}", dto.getTagIds().size());
                handleTags(existingArticle.getId(), dto.getTagIds());
            }
            
            logger.info("文章更新成功，ID：{}", existingArticle.getId());
            return convertToVO(existingArticle);
        } catch (ResourceNotFoundException e) {
            logger.error("文章不存在：{}", dto.getId());
            throw e; // 直接抛出资源未找到异常
        } catch (Exception e) {
            logger.error("更新文章失败，ID：{}", dto.getId(), e);
            throw new RuntimeException("更新文章失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id, Long operatorId) {
        try {
            logger.info("删除文章，ID：{}，操作者ID：{}", id, operatorId);
            Article article = articleMapper.findById(id);
            if (article == null) {
                logger.warn("文章不存在，ID：{}", id);
                throw new ResourceNotFoundException("文章", id);
            }
            
            // 验证操作者是否为作者
            if (!article.getAuthorId().equals(operatorId)) {
                logger.warn("操作者ID（{}）与文章作者ID（{}）不匹配", operatorId, article.getAuthorId());
                throw new RuntimeException("只有作者可以删除文章");
            }
            
            // 删除文章标签关联
            logger.info("删除文章-标签关联");
            int tagRows = tagMapper.deleteArticleTagsByArticleId(id);
            logger.info("删除文章-标签关联完成，受影响行数：{}", tagRows);
            
            // 删除文章
            logger.info("删除文章记录");
            int rows = articleMapper.delete(id);
            logger.info("删除文章记录完成，受影响行数：{}", rows);
            
            logger.info("文章删除成功，ID：{}", id);
        } catch (Exception e) {
            logger.error("删除文章失败，ID：{}", id, e);
            throw e;
        }
    }
    
    /**
     * 处理文章标签
     */
    private void handleTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            logger.info("标签列表为空，无需处理");
            return;
        }
        
        try {
            // 验证标签ID是否存在
            List<Long> existingTagIds = new ArrayList<>();
            for (Long tagId : tagIds) {
                Tag tag = tagMapper.findById(tagId);
                if (tag != null) {
                    existingTagIds.add(tagId);
                } else {
                    logger.warn("标签ID不存在: {}, 将被跳过", tagId);
                }
            }
            
            if (existingTagIds.isEmpty()) {
                logger.warn("没有有效的标签ID，跳过标签处理");
                return;
            }
            
            // 删除旧的标签关联
            logger.info("删除文章{}的旧标签关联", articleId);
            int deleteRows = tagMapper.deleteArticleTagsByArticleId(articleId);
            logger.info("删除旧标签关联完成，受影响行数：{}", deleteRows);
            
            // 添加新的标签关联
            logger.info("添加新标签关联，有效标签数量：{}", existingTagIds.size());
            for (Long tagId : existingTagIds) {
                logger.debug("添加标签关联：文章ID={}，标签ID={}", articleId, tagId);
                try {
                    int insertRows = tagMapper.insertArticleTag(articleId, tagId);
                    logger.debug("添加标签关联完成，受影响行数：{}", insertRows);
                } catch (Exception e) {
                    logger.error("添加标签关联失败，文章ID={}，标签ID={}", articleId, tagId, e);
                    throw e;
                }
            }
            logger.info("文章标签处理完成");
        } catch (Exception e) {
            logger.error("处理文章标签失败，文章ID={}", articleId, e);
            throw new RuntimeException("处理文章标签失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将实体转换为VO
     */
    private ArticleVO convertToVO(Article article) {
        logger.debug("将文章实体转换为VO，ID：{}", article.getId());
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        
        try {
            // 获取标签
            List<Tag> tags = tagMapper.findByArticleId(article.getId());
            if (tags != null && !tags.isEmpty()) {
                logger.debug("文章有{}个标签", tags.size());
                vo.setTags(tags.stream().map(Tag::getName).collect(Collectors.toList()));
            } else {
                logger.debug("文章没有标签");
                vo.setTags(new ArrayList<>());
            }
            
            // TODO: 获取作者名称（需要用户服务）
            vo.setAuthorName("管理员");
            
            return vo;
        } catch (Exception e) {
            logger.error("转换文章VO失败，ID：{}", article.getId(), e);
            throw new RuntimeException("转换文章VO失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从文章内容自动生成摘要
     * @param content 文章内容
     * @return 生成的摘要
     */
    private String generateSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        
        // 移除Markdown格式
        String plainText = content.replaceAll("\\*\\*|\\*|#+|\\[.*?\\]\\(.*?\\)|```[\\s\\S]*?```|`.*?`|>|\\|", "")
            .replaceAll("\\n\\s*\\n", "\n") // 移除多余空行
            .trim();
        
        // 截取前150个字符作为摘要
        int summaryLength = Math.min(plainText.length(), 150);
        String summary = plainText.substring(0, summaryLength);
        
        // 如果截断了句子，添加省略号
        if (summaryLength < plainText.length()) {
            summary += "...";
        }
        
        return summary;
    }
} 