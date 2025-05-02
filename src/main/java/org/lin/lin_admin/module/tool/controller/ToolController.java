package org.lin.lin_admin.module.tool.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.tool.dto.ToolDTO;
import org.lin.lin_admin.module.tool.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工具控制器 - 提供工具相关的API
 */
@RestController
@RequestMapping("/tools")
public class ToolController {
    
    @Autowired
    private ToolService toolService;
    
    /**
     * 获取所有工具
     * @return 工具列表
     */
    @GetMapping
    public ApiResponse<List<ToolDTO>> getAllTools() {
        List<ToolDTO> tools = toolService.findAll();
        return ApiResponse.success(tools);
    }
} 