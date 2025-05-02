package org.lin.lin_admin.module.tool.service;

import org.lin.lin_admin.module.tool.dto.ToolDTO;

import java.util.List;

/**
 * 工具服务接口
 */
public interface ToolService {
    
    /**
     * 获取所有工具
     * @return 工具列表
     */
    List<ToolDTO> findAll();
    
    /**
     * 根据ID获取工具
     * @param id 工具ID
     * @return 工具DTO
     */
    ToolDTO findById(Long id);
    
    /**
     * 创建工具
     * @param toolDTO 工具数据
     * @return 创建的工具
     */
    ToolDTO create(ToolDTO toolDTO);
    
    /**
     * 更新工具
     * @param id 工具ID
     * @param toolDTO 工具数据
     * @return 更新后的工具
     */
    ToolDTO update(Long id, ToolDTO toolDTO);
    
    /**
     * 删除工具
     * @param id 工具ID
     */
    void delete(Long id);
} 