package org.lin.lin_admin.module.message.mapper;

import org.apache.ibatis.annotations.*;
import org.lin.lin_admin.module.message.model.Message;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言数据访问接口
 */
@Mapper
@Repository
public interface MessageMapper {

    @Insert("INSERT INTO message(nickname, content, contact_info, ip_address, user_agent, status) " +
            "VALUES(#{nickname}, #{content}, #{contactInfo}, #{ipAddress}, #{userAgent}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Delete("DELETE FROM message WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM message WHERE id = #{id}")
    Message findById(Long id);

    @Select("SELECT * FROM message ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Message> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT * FROM message WHERE status = #{status} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Message> findByStatus(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);
    
    @Update("UPDATE message SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Update("UPDATE message SET reply_content = #{replyContent}, reply_at = #{replyAt}, " +
            "admin_id = #{adminId}, status = 'replied' WHERE id = #{id}")
    int updateReply(@Param("id") Long id, 
                    @Param("replyContent") String replyContent, 
                    @Param("replyAt") LocalDateTime replyAt, 
                    @Param("adminId") Long adminId);
    
    @Select("SELECT COUNT(*) FROM message")
    int count();
    
    @Select("SELECT COUNT(*) FROM message WHERE status = #{status}")
    int countByStatus(String status);
} 