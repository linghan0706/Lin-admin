package org.lin.lin_admin.common.exception;

/**
 * 资源不存在异常
 * 用于表示请求的资源（如文章、用户、评论等）不存在的情况
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + "不存在，ID: " + id);
    }
} 