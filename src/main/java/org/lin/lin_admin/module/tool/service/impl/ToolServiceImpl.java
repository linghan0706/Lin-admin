package org.lin.lin_admin.module.tool.service.impl;

import org.lin.lin_admin.module.tool.dto.ToolDTO;
import org.lin.lin_admin.module.tool.mapper.ToolMapper;
import org.lin.lin_admin.module.tool.model.Tool;
import org.lin.lin_admin.module.tool.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具服务实现类
 */
@Service
public class ToolServiceImpl implements ToolService {

    @Autowired
    private ToolMapper toolMapper;

    @Override
    public List<ToolDTO> findAll() {
        List<Tool> tools = toolMapper.findAll();
        return tools.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ToolDTO findById(Long id) {
        Tool tool = toolMapper.findById(id);
        return tool != null ? convertToDTO(tool) : null;
    }

    @Override
    public ToolDTO create(ToolDTO toolDTO) {
        Tool tool = new Tool();
        tool.setName(toolDTO.getName());
        tool.setDescription(toolDTO.getDescription());
        
        // 检查link是否为null，若为null则设置默认值
        String link = toolDTO.getLink();
        if (link == null || link.trim().isEmpty()) {
            link = "#"; // 设置默认值为"#"或其他适合业务的默认值
        }
        tool.setLink(link);
        
        toolMapper.insert(tool);
        return convertToDTO(tool);
    }

    @Override
    public ToolDTO update(Long id, ToolDTO toolDTO) {
        Tool tool = toolMapper.findById(id);
        if (tool != null) {
            tool.setName(toolDTO.getName());
            tool.setDescription(toolDTO.getDescription());
            tool.setLink(toolDTO.getLink());
            
            toolMapper.update(tool);
            return convertToDTO(tool);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        toolMapper.deleteById(id);
    }
    
    /**
     * 将实体对象转换为DTO对象
     * @param tool 工具实体
     * @return 工具DTO
     */
    private ToolDTO convertToDTO(Tool tool) {
        ToolDTO dto = new ToolDTO();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setDescription(tool.getDescription());
        dto.setLink(tool.getLink());
        return dto;
    }
} 