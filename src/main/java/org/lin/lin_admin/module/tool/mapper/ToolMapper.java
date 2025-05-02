package org.lin.lin_admin.module.tool.mapper;

import org.apache.ibatis.annotations.*;
import org.lin.lin_admin.module.tool.model.Tool;

import java.util.List;

@Mapper
public interface ToolMapper {
    
    @Select("SELECT * FROM tools WHERE id = #{id}")
    Tool findById(Long id);
    
    @Select("SELECT * FROM tools")
    List<Tool> findAll();
    
    @Insert("INSERT INTO tools (name, description, link) VALUES (#{name}, #{description}, #{link})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tool tool);
    
    @Update("UPDATE tools SET name = #{name}, description = #{description}, link = #{link} WHERE id = #{id}")
    int update(Tool tool);
    
    @Delete("DELETE FROM tools WHERE id = #{id}")
    int deleteById(Long id);
} 