package org.lin.lin_admin.module.article.mapper;

import org.apache.ibatis.annotations.*;
import org.lin.lin_admin.module.article.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagMapper {

    @Insert("INSERT INTO tags(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Update("UPDATE tags SET name = #{name} WHERE id = #{id}")
    int update(Tag tag);

    @Delete("DELETE FROM tags WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM tags WHERE id = #{id}")
    Tag findById(Long id);

    @Select("SELECT * FROM tags WHERE name = #{name}")
    Tag findByName(String name);

    @Select("SELECT * FROM tags ORDER BY id")
    List<Tag> findAll();
    
    @Select("SELECT t.* FROM tags t JOIN article_tags at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
    List<Tag> findByArticleId(Long articleId);
    
    @Insert("INSERT INTO article_tags(article_id, tag_id) VALUES(#{articleId}, #{tagId})")
    int insertArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);
    
    @Delete("DELETE FROM article_tags WHERE article_id = #{articleId}")
    int deleteArticleTagsByArticleId(Long articleId);
    
    @Select("SELECT tag_id FROM article_tags WHERE article_id = #{articleId}")
    List<Long> findTagIdsByArticleId(Long articleId);
    
    /**
     * 检查标签是否被文章使用
     * @param tagId 标签ID
     * @return 使用该标签的文章数量
     */
    @Select("SELECT COUNT(*) FROM article_tags WHERE tag_id = #{tagId}")
    int countArticlesByTagId(Long tagId);
} 