package org.lin.lin_admin.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import net.coobird.thumbnailator.Thumbnails;
import org.lin.lin_admin.module.file.dto.ImageInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片处理工具类
 */
public class ImageUtils {

    /**
     * 提取图片元数据
     * @param file 图片文件
     * @return 包含图片元数据的DTO对象
     */
    public static ImageInfoDTO extractMetadata(MultipartFile file) throws IOException {
        ImageInfoDTO imageInfo = new ImageInfoDTO();
        
        // 设置基本信息
        imageInfo.setOriginalFilename(file.getOriginalFilename());
        imageInfo.setFileSize(file.getSize());
        imageInfo.setContentType(file.getContentType());
        
        try {
            // 获取图片尺寸
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image != null) {
                imageInfo.setWidth(image.getWidth());
                imageInfo.setHeight(image.getHeight());
            }
            
            // 获取EXIF信息
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            
            // 相机信息
            ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0 != null) {
                if (exifIFD0.containsTag(ExifIFD0Directory.TAG_MAKE)) {
                    imageInfo.setCameraMake(exifIFD0.getString(ExifIFD0Directory.TAG_MAKE));
                }
                if (exifIFD0.containsTag(ExifIFD0Directory.TAG_MODEL)) {
                    imageInfo.setCameraModel(exifIFD0.getString(ExifIFD0Directory.TAG_MODEL));
                }
                if (exifIFD0.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
                    try {
                        String dateString = exifIFD0.getString(ExifIFD0Directory.TAG_DATETIME);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                        imageInfo.setShootTime(format.parse(dateString));
                    } catch (ParseException e) {
                        // 解析日期失败，忽略
                    }
                }
            }
            
            // 相机参数
            ExifSubIFDDirectory exifSubIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifSubIFD != null) {
                if (exifSubIFD.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)) {
                    imageInfo.setExposureTime(exifSubIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
                }
                if (exifSubIFD.containsTag(ExifSubIFDDirectory.TAG_FNUMBER)) {
                    imageInfo.setAperture(exifSubIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER));
                }
                if (exifSubIFD.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)) {
                    try {
                        int iso = exifSubIFD.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                        imageInfo.setIso(iso);
                    } catch (Exception e) {
                        // 解析ISO失败，忽略
                    }
                }
                if (exifSubIFD.containsTag(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)) {
                    imageInfo.setFocalLength(exifSubIFD.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
                }
            }
            
            // GPS信息
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    imageInfo.setLatitude(geoLocation.getLatitude());
                    imageInfo.setLongitude(geoLocation.getLongitude());
                }
                
                if (gpsDirectory.containsTag(GpsDirectory.TAG_ALTITUDE)) {
                    try {
                        double altitude = gpsDirectory.getDouble(GpsDirectory.TAG_ALTITUDE);
                        imageInfo.setAltitude(altitude);
                    } catch (Exception e) {
                        // 解析海拔失败，忽略
                    }
                }
            }
            
        } catch (ImageProcessingException e) {
            // 元数据提取失败，只保留基本信息
            System.err.println("提取图片元数据失败: " + e.getMessage());
        }
        
        return imageInfo;
    }
    
    /**
     * 提取所有图片元数据并返回Map
     * @param file 图片文件
     * @return 元数据Map
     */
    public static Map<String, Object> extractAllMetadata(MultipartFile file) throws IOException {
        Map<String, Object> metadataMap = new HashMap<>();
        
        try {
            // 提取EXIF信息
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    metadataMap.put(tag.getTagName(), tag.getDescription());
                }
            }
            
        } catch (ImageProcessingException e) {
            System.err.println("提取图片元数据失败: " + e.getMessage());
        }
        
        return metadataMap;
    }
    
    /**
     * 压缩图片
     * @param imageBytes 原图片字节数据
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @param quality 质量(0.0-1.0)
     * @return 压缩后的图片字节数据
     */
    public static byte[] compressImage(byte[] imageBytes, int maxWidth, int maxHeight, float quality) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        BufferedImage originalImage = ImageIO.read(inputStream);
        
        // 如果图片尺寸小于最大限制，不做缩放处理
        if (originalImage.getWidth() <= maxWidth && originalImage.getHeight() <= maxHeight) {
            Thumbnails.of(originalImage)
                    .scale(1.0)
                    .outputQuality(quality)
                    .toOutputStream(outputStream);
        } else {
            Thumbnails.of(originalImage)
                    .size(maxWidth, maxHeight)
                    .keepAspectRatio(true)
                    .outputQuality(quality)
                    .toOutputStream(outputStream);
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * 从MultipartFile创建备份输入流
     * 因为InputStream只能读取一次，需要备份流以便多次使用
     */
    public static InputStream cloneInputStream(MultipartFile file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try (InputStream is = file.getInputStream()) {
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
} 