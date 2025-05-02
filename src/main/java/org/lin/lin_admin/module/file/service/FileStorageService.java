package org.lin.lin_admin.module.file.service;

import org.lin.lin_admin.module.file.dto.ImageInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 存储文件并返回可访问的URL
     * @param file 要存储的文件
     * @return 文件URL
     * @throws IOException 如果存储过程中发生I/O错误
     */
    String storeFile(MultipartFile file) throws IOException;
    
    /**
     * 存储图片文件，提取元数据，并返回可访问的URL
     * @param file 要存储的图片文件
     * @return 文件URL
     * @throws IOException 如果存储过程中发生I/O错误
     */
    String storeImageWithMetadata(MultipartFile file) throws IOException;
    
    /**
     * 根据图片ID查询图片信息
     * @param id 图片ID
     * @return 图片信息
     */
    ImageInfoDTO getImageInfo(Long id);
    
    /**
     * 根据图片路径查询图片信息
     * @param path 图片路径
     * @return 图片信息
     */
    ImageInfoDTO getImageInfoByPath(String path);
    
    /**
     * 分页查询图片信息列表
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 图片信息列表
     */
    List<ImageInfoDTO> listImageInfos(int page, int size);
    
    /**
     * 获取图片总数
     * @return 图片总数
     */
    int countImageInfos();
    
    /**
     * 删除图片
     * @param id 图片ID
     * @return 是否删除成功
     */
    boolean deleteImage(Long id);
} 