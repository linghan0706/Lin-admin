package org.lin.lin_admin.module.comment.mapper;

import org.apache.ibatis.annotations.*;
import org.lin.lin_admin.module.comment.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    @Select("SELECT * FROM comments WHERE id = #{id}")
    Comment findById(Long id);
    
    @Select("SELECT * FROM comments WHERE article_id = #{articleId} ORDER BY created_at DESC")
    List<Comment> findByArticleId(Long articleId);
    
    @Insert("INSERT INTO comments (article_id, name, email, content, status, created_at) " +
            "VALUES (#{articleId}, #{name}, #{email}, #{content}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    @Update("UPDATE comments SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Delete("DELETE FROM comments WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("DELETE FROM comments WHERE article_id = #{articleId}")
    int deleteByArticleId(Long articleId);
} 