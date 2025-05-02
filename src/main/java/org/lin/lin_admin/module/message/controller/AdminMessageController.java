package org.lin.lin_admin.module.message.controller;

import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.message.dto.MessageVO;
import org.lin.lin_admin.module.message.dto.ReplyDTO;
import org.lin.lin_admin.module.message.dto.StatusDTO;
import org.lin.lin_admin.module.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 后台留言管理控制器
 */
@RestController
@RequestMapping("/admin/message")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;
    
    /**
     * 分页获取留言
     * @param page 页码
     * @param size 每页数量
     * @param status 状态（可选）
     * @return 留言分页列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<MessageVO>> getMessagePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        PageResult<MessageVO> result = messageService.page(page, size, status);
        return ApiResponse.success(result);
    }
    
    /**
     * 根据ID获取留言
     * @param id 留言ID
     * @return 留言详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MessageVO> getMessageById(@PathVariable Long id) {
        MessageVO messageVO = messageService.getById(id);
        if (messageVO == null) {
            return ApiResponse.error(404, "留言不存在");
        }
        return ApiResponse.success(messageVO);
    }
    
    /**
     * 回复留言
     * @param id 留言ID
     * @param dto 回复信息
     * @return 操作结果
     */
    @PutMapping("/{id}/reply")
    public ApiResponse<Void> replyMessage(@PathVariable Long id, @RequestBody ReplyDTO dto) {
        if (dto.getReplyContent() == null || dto.getReplyContent().trim().isEmpty()) {
            return ApiResponse.badRequest("回复内容不能为空");
        }
        
        Long adminId = getCurrentAdminId();
        boolean success = messageService.reply(id, dto.getReplyContent(), adminId);
        
        return success ? 
               ApiResponse.success("回复成功", null) : 
               ApiResponse.error(500, "回复失败，请确认留言是否存在");
    }
    
    /**
     * 更新留言状态
     * @param id 留言ID
     * @param dto 状态信息
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateMessageStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
            return ApiResponse.badRequest("状态不能为空");
        }
        
        try {
            boolean success = messageService.updateStatus(id, dto.getStatus());
            return success ? 
                   ApiResponse.success("状态更新成功", null) : 
                   ApiResponse.error(500, "状态更新失败，请确认留言是否存在");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }
    
    /**
     * 删除留言
     * @param id 留言ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        boolean success = messageService.delete(id);
        return success ? 
               ApiResponse.success("删除成功", null) : 
               ApiResponse.error(404, "留言不存在或已被删除");
    }
    
    /**
     * 获取当前管理员ID
     * @return 管理员ID
     */
    private Long getCurrentAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                // 默认返回1
                return 1L;
            }
        }
        return 1L; // 默认管理员ID
    }
} 