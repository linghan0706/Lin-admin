package org.lin.lin_admin.module.article.service.impl;

import org.lin.lin_admin.module.article.mapper.TagMapper;
import org.lin.lin_admin.module.article.model.Tag;
import org.lin.lin_admin.module.article.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> findAll() {
        logger.debug("获取所有标签");
        return tagMapper.findAll();
    }

    @Override
    public Tag findById(Long id) {
        logger.debug("根据ID获取标签：{}", id);
        if (id == null) {
            logger.warn("标签ID为空");
            return null;
        }
        
        return tagMapper.findById(id);
    }

    @Override
    public Tag findByName(String name) {
        logger.debug("根据名称获取标签：{}", name);
        if (name == null || name.trim().isEmpty()) {
            logger.warn("标签名称为空");
            return null;
        }
        
        return tagMapper.findByName(name);
    }

    @Override
    @Transactional
    public Tag create(Tag tag) {
        logger.info("创建标签：{}", tag.getName());
        
        // 验证标签名称
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            logger.warn("标签名称为空");
            throw new IllegalArgumentException("标签名称不能为空");
        }
        
        // 检查标签名称是否已存在
        Tag existingTag = tagMapper.findByName(tag.getName().trim());
        if (existingTag != null) {
            logger.warn("标签名称已存在：{}", tag.getName());
            throw new IllegalArgumentException("标签名称已存在");
        }
        
        // 清理并设置标签名称
        tag.setName(tag.getName().trim());
        
        // 插入数据库
        int rows = tagMapper.insert(tag);
        logger.info("标签创建成功，ID：{}", tag.getId());
        
        return tag;
    }

    @Override
    @Transactional
    public Tag update(Long id, Tag tag) {
        logger.info("更新标签，ID：{}，新名称：{}", id, tag.getName());
        
        // 验证标签ID
        if (id == null) {
            logger.warn("标签ID为空");
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 验证标签名称
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            logger.warn("标签名称为空");
            throw new IllegalArgumentException("标签名称不能为空");
        }
        
        // 检查标签是否存在
        Tag existingTag = tagMapper.findById(id);
        if (existingTag == null) {
            logger.warn("标签不存在，ID：{}", id);
            throw new IllegalArgumentException("标签不存在");
        }
        
        // 清理名称
        String newName = tag.getName().trim();
        
        // 如果名称没有变化，直接返回
        if (existingTag.getName().equals(newName)) {
            logger.info("标签名称未变化，跳过更新");
            return existingTag;
        }
        
        // 检查新名称是否已存在
        Tag nameExistsTag = tagMapper.findByName(newName);
        if (nameExistsTag != null && !nameExistsTag.getId().equals(id)) {
            logger.warn("标签名称已存在：{}", newName);
            throw new IllegalArgumentException("标签名称已存在");
        }
        
        // 设置更新数据
        existingTag.setName(newName);
        
        // 更新数据库
        int rows = tagMapper.update(existingTag);
        logger.info("标签更新成功，ID：{}", id);
        
        return existingTag;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("删除标签，ID：{}", id);
        
        // 验证标签ID
        if (id == null) {
            logger.warn("标签ID为空");
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 检查标签是否存在
        Tag existingTag = tagMapper.findById(id);
        if (existingTag == null) {
            logger.warn("标签不存在，ID：{}", id);
            return false;
        }
        
        // 检查标签是否被使用
        if (isTagInUse(id)) {
            logger.warn("标签正在被使用，无法删除，ID：{}", id);
            throw new IllegalStateException("标签正在被使用，无法删除");
        }
        
        // 删除标签
        int rows = tagMapper.delete(id);
        logger.info("标签删除成功，ID：{}", id);
        
        return rows > 0;
    }

    @Override
    public boolean isTagInUse(Long id) {
        logger.debug("检查标签是否被使用，ID：{}", id);
        if (id == null) {
            return false;
        }
        
        int count = tagMapper.countArticlesByTagId(id);
        return count > 0;
    }
} 