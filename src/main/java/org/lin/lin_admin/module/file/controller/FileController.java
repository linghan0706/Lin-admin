package org.lin.lin_admin.module.file.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.file.dto.FileUploadResponseDTO;
import org.lin.lin_admin.module.file.dto.ImageInfoDTO;
import org.lin.lin_admin.module.file.service.FileStorageService;
import org.lin.lin_admin.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/admin/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传图片
     * @param file 图片文件（支持jpg/png格式）
     * @return 上传结果，包含图片URL
     */
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponseDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                return ApiResponse.badRequest("只支持jpg/png格式的图片");
            }
            
            // 检查文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ApiResponse.badRequest("图片大小不能超过5MB");
            }
            
            // 保存文件并获取URL
            String fileUrl = fileStorageService.storeFile(file);
            
            // 返回结果
            FileUploadResponseDTO responseDTO = new FileUploadResponseDTO();
            responseDTO.setUrl(fileUrl);
            responseDTO.setFilename(file.getOriginalFilename());
            responseDTO.setSize(file.getSize());
            responseDTO.setContentType(file.getContentType());
            
            return ApiResponse.success(responseDTO);
        } catch (IOException e) {
            return ApiResponse.serverError("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传图片并提取元数据
     * @param file 图片文件（支持jpg/png格式）
     * @return 上传结果，包含图片URL和元数据
     */
    @PostMapping(value = "/upload/image/with-metadata", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponseDTO> uploadImageWithMetadata(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                return ApiResponse.badRequest("只支持jpg/png格式的图片");
            }
            
            // 检查文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ApiResponse.badRequest("图片大小不能超过10MB");
            }
            
            // 提取元数据
            ImageInfoDTO imageInfo = ImageUtils.extractMetadata(file);
            Map<String, Object> metadata = ImageUtils.extractAllMetadata(file);
            
            // 保存文件、元数据并获取URL
            String fileUrl = fileStorageService.storeImageWithMetadata(file);
            
            // 返回结果
            FileUploadResponseDTO responseDTO = new FileUploadResponseDTO();
            responseDTO.setUrl(fileUrl);
            responseDTO.setFilename(file.getOriginalFilename());
            responseDTO.setSize(file.getSize());
            responseDTO.setContentType(file.getContentType());
            responseDTO.setWidth(imageInfo.getWidth());
            responseDTO.setHeight(imageInfo.getHeight());
            responseDTO.setCameraMake(imageInfo.getCameraMake());
            responseDTO.setCameraModel(imageInfo.getCameraModel());
            responseDTO.setShootTime(imageInfo.getShootTime());
            responseDTO.setMetadata(metadata);
            
            return ApiResponse.success(responseDTO);
        } catch (IOException e) {
            return ApiResponse.serverError("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取图片信息
     * @param id 图片ID
     * @return 图片信息
     */
    @GetMapping("/image/{id}")
    public ApiResponse<ImageInfoDTO> getImageInfo(@PathVariable Long id) {
        ImageInfoDTO imageInfo = fileStorageService.getImageInfo(id);
        if (imageInfo == null) {
            return ApiResponse.notFound("图片不存在");
        }
        return ApiResponse.success(imageInfo);
    }
    
    /**
     * 分页获取图片列表
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 图片列表
     */
    @GetMapping("/images")
    public ApiResponse<Map<String, Object>> listImages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<ImageInfoDTO> imageList = fileStorageService.listImageInfos(page, size);
        int total = fileStorageService.countImageInfos();
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", imageList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return ApiResponse.success(result);
    }
    
    /**
     * 删除图片
     * @param id 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/image/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable Long id) {
        boolean success = fileStorageService.deleteImage(id);
        if (!success) {
            return ApiResponse.notFound("图片不存在或删除失败");
        }
        return ApiResponse.success(null);
    }
} 