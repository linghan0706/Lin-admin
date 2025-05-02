package org.lin.lin_admin.module.message.service;

import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.module.message.dto.MessageDTO;
import org.lin.lin_admin.module.message.dto.MessageVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 留言服务接口
 */
public interface MessageService {
    
    /**
     * 创建留言
     * @param dto 留言DTO
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean create(MessageDTO dto, HttpServletRequest request);
    
    /**
     * 分页获取留言
     * @param page 页码
     * @param size 每页数量
     * @param status 状态
     * @return 分页结果
     */
    PageResult<MessageVO> page(int page, int size, String status);
    
    /**
     * 删除留言
     * @param id 留言ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 回复留言
     * @param id 留言ID
     * @param replyContent 回复内容
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean reply(Long id, String replyContent, Long adminId);
    
    /**
     * 更新留言状态
     * @param id 留言ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 根据ID获取留言
     * @param id 留言ID
     * @return 留言VO
     */
    MessageVO getById(Long id);
} 