package org.lin.lin_admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 博客管理后台应用程序入口类
 */
@SpringBootApplication
public class LinAdminApplication {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    private final Environment environment;
    
    public LinAdminApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(LinAdminApplication.class, args);
        System.out.println("LinAdminApplication started,spring启动");
    }
    
    /**
     * 应用程序启动后初始化上传目录
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initUploadDirectory() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            System.out.println("上传目录初始化完成: " + uploadPath);
            
            // 获取并输出服务器端口
            String port = environment.getProperty("server.port", "8080");
            System.out.println("服务器启动成功，运行端口: " + port);
        } catch (IOException e) {
            System.err.println("无法创建上传目录: " + e.getMessage());
        }
    }
} 