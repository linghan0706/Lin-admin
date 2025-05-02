package org.lin.lin_admin.module.file.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lin.lin_admin.module.file.dto.ImageInfoDTO;

import java.util.List;

/**
 * 图片信息Mapper接口
 */
@Mapper
public interface ImageInfoMapper {
    
    /**
     * 插入图片信息
     * @param imageInfo 图片信息
     * @return 影响的行数
     */
    int insert(ImageInfoDTO imageInfo);
    
    /**
     * 根据ID查询图片信息
     * @param id 图片ID
     * @return 图片信息
     */
    ImageInfoDTO findById(Long id);
    
    /**
     * 根据路径查询图片信息
     * @param path 图片路径
     * @return 图片信息
     */
    ImageInfoDTO findByPath(String path);
    
    /**
     * 查询所有图片信息
     * @return 图片信息列表
     */
    List<ImageInfoDTO> findAll();
    
    /**
     * 分页查询图片信息
     * @param offset 起始位置
     * @param limit 每页大小
     * @return 图片信息列表
     */
    List<ImageInfoDTO> findPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 计算总记录数
     * @return 总记录数
     */
    int count();
    
    /**
     * 删除图片信息
     * @param id 图片ID
     * @return 影响的行数
     */
    int delete(Long id);
} 