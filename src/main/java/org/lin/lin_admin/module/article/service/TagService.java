package org.lin.lin_admin.module.article.service;

import org.lin.lin_admin.module.article.model.Tag;
import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> findAll();
    
    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签对象，如果不存在则返回null
     */
    Tag findById(Long id);
    
    /**
     * 根据名称获取标签
     * @param name 标签名称
     * @return 标签对象，如果不存在则返回null
     */
    Tag findByName(String name);
    
    /**
     * 创建标签
     * @param tag 标签对象
     * @return 创建后的标签对象
     * @throws IllegalArgumentException 如果标签名为空或已存在
     */
    Tag create(Tag tag);
    
    /**
     * 更新标签
     * @param id 标签ID
     * @param tag 标签对象
     * @return 更新后的标签对象
     * @throws IllegalArgumentException 如果标签名为空或已存在
     */
    Tag update(Long id, Tag tag);
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否删除成功
     * @throws IllegalStateException 如果标签正在被使用
     */
    boolean delete(Long id);
    
    /**
     * 检查标签是否被文章使用
     * @param id 标签ID
     * @return 是否被使用
     */
    boolean isTagInUse(Long id);
} 