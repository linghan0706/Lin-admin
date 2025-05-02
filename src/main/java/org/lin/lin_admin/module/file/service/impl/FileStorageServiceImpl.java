package org.lin.lin_admin.module.file.service.impl;

import org.lin.lin_admin.module.file.dto.ImageInfoDTO;
import org.lin.lin_admin.module.file.mapper.ImageInfoMapper;
import org.lin.lin_admin.module.file.service.FileStorageService;
import org.lin.lin_admin.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${file.compress-quality:0.8}")
    private float compressQuality;
    
    @Value("${file.max-width:1920}")
    private int maxWidth;
    
    @Value("${file.max-height:1080}")
    private int maxHeight;
    
    @Value("${file.compress-enabled:true}")
    private boolean compressEnabled;
    
    @Autowired
    private ImageInfoMapper imageInfoMapper;

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // 获取文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 生成唯一文件名，避免文件覆盖
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 创建上传目录（如果不存在）
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        // 保存文件到上传目录
        Path targetLocation = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // 生成访问URL - 简化直接使用相对路径，匹配WebConfig中的静态资源映射
        String fileDownloadUri = "/uploads/" + newFilename;
        
        System.out.println("文件已保存到: " + targetLocation);
        System.out.println("文件访问URL: " + fileDownloadUri);
                
        return fileDownloadUri;
    }
    
    @Override
    public String storeImageWithMetadata(MultipartFile file) throws IOException {
        // 获取文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 生成唯一文件名，避免文件覆盖
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 创建上传目录（如果不存在）
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        // 提取图片元数据
        ImageInfoDTO imageInfo = ImageUtils.extractMetadata(file);
        
        // 目标文件位置
        Path targetLocation = uploadPath.resolve(newFilename);
        
        // 如果启用压缩，对图片进行压缩处理
        if (compressEnabled && isImageFile(file.getContentType())) {
            byte[] fileBytes = file.getBytes();
            byte[] compressedBytes = ImageUtils.compressImage(fileBytes, maxWidth, maxHeight, compressQuality);
            Files.write(targetLocation, compressedBytes);
        } else {
            // 不压缩，直接保存原文件
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 生成访问URL
        String fileDownloadUri = "/uploads/" + newFilename;
        
        // 设置图片路径并保存到数据库
        imageInfo.setPath(fileDownloadUri);
        imageInfoMapper.insert(imageInfo);
        
        System.out.println("图片已保存到: " + targetLocation);
        System.out.println("图片访问URL: " + fileDownloadUri);
                
        return fileDownloadUri;
    }
    
    @Override
    public ImageInfoDTO getImageInfo(Long id) {
        return imageInfoMapper.findById(id);
    }
    
    @Override
    public ImageInfoDTO getImageInfoByPath(String path) {
        return imageInfoMapper.findByPath(path);
    }
    
    @Override
    public List<ImageInfoDTO> listImageInfos(int page, int size) {
        int offset = (page - 1) * size;
        return imageInfoMapper.findPage(offset, size);
    }
    
    @Override
    public int countImageInfos() {
        return imageInfoMapper.count();
    }
    
    @Override
    public boolean deleteImage(Long id) {
        // 1. 获取图片信息
        ImageInfoDTO imageInfo = imageInfoMapper.findById(id);
        if (imageInfo == null) {
            return false;
        }
        
        // 2. 删除物理文件
        try {
            String relativePath = imageInfo.getPath();
            String filename = relativePath.substring(relativePath.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("删除图片文件失败: " + e.getMessage());
            return false;
        }
        
        // 3. 删除数据库记录
        return imageInfoMapper.delete(id) > 0;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String contentType) {
        return contentType != null && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/gif") || 
                contentType.equals("image/bmp"));
    }
} 