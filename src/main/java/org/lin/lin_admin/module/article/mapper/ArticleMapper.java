package org.lin.lin_admin.module.article.mapper;

import org.apache.ibatis.annotations.*;
import org.lin.lin_admin.module.article.model.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ArticleMapper {
    
    @Insert("INSERT INTO articles(title, summary, content, author_id, status, cover_image, created_at, updated_at, published_at) " +
            "VALUES(#{title}, #{summary}, #{content}, #{authorId}, #{status}, #{coverImage}, #{createdAt}, #{updatedAt}, #{publishedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);
    
    @Update("UPDATE articles SET title = #{title}, summary = #{summary}, content = #{content}, " +
            "status = #{status}, cover_image = #{coverImage}, updated_at = #{updatedAt}, published_at = #{publishedAt} WHERE id = #{id}")
    int update(Article article);
    
    @Delete("DELETE FROM articles WHERE id = #{id}")
    int delete(Long id);
    
    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article findById(Long id);
    
    @Select("SELECT * FROM articles ORDER BY created_at DESC")
    List<Article> findAll();
    
    @Select("SELECT * FROM articles WHERE status = 'published' ORDER BY published_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Article> findAllPublished(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT * FROM articles WHERE status = #{status} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Article> findAllByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM articles")
    int count();
    
    @Select("SELECT COUNT(*) FROM articles WHERE status = #{status}")
    int countByStatus(String status);
} 