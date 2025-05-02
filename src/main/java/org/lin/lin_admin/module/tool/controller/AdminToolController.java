package org.lin.lin_admin.module.tool.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.tool.dto.ToolDTO;
import org.lin.lin_admin.module.tool.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工具管理控制器 - 提供管理端的工具API
 */
@RestController
@RequestMapping("/admin/tools")
public class AdminToolController {

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
    
    /**
     * 根据ID获取工具
     * @param id 工具ID
     * @return 工具详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ToolDTO> getToolById(@PathVariable Long id) {
        ToolDTO tool = toolService.findById(id);
        return ApiResponse.success(tool);
    }
    
    /**
     * 创建工具
     * @param toolDTO 工具数据
     * @return 创建的工具
     */
    @PostMapping
    public ApiResponse<ToolDTO> createTool(@RequestBody ToolDTO toolDTO) {
        ToolDTO tool = toolService.create(toolDTO);
        return ApiResponse.success(tool);
    }
    
    /**
     * 更新工具
     * @param id 工具ID
     * @param toolDTO 工具数据
     * @return 更新后的工具
     */
    @PutMapping("/{id}")
    public ApiResponse<ToolDTO> updateTool(@PathVariable Long id, @RequestBody ToolDTO toolDTO) {
        ToolDTO tool = toolService.update(id, toolDTO);
        return ApiResponse.success(tool);
    }
    
    /**
     * 删除工具
     * @param id 工具ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTool(@PathVariable Long id) {
        toolService.delete(id);
        return ApiResponse.success(null);
    }
} 