package org.lin.lin_admin.module.message.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.message.dto.MessageDTO;
import org.lin.lin_admin.module.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 前台留言控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    /**
     * 提交留言
     * @param dto 留言信息
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping
    public ApiResponse<Void> create(@RequestBody MessageDTO dto, HttpServletRequest request) {
        // 参数校验
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            return ApiResponse.badRequest("昵称不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return ApiResponse.badRequest("留言内容不能为空");
        }
        
        boolean success = messageService.create(dto, request);
        return success ? 
               ApiResponse.success("留言提交成功", null) : 
               ApiResponse.serverError("留言提交失败，请稍后重试");
    }
} 