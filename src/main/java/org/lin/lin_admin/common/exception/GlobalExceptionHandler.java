package org.lin.lin_admin.common.exception;

import org.lin.lin_admin.common.response.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常
     * @param e 运行时异常
     * @return API响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        return ApiResponse.serverError(e.getMessage());
    }

    /**
     * 处理认证异常
     * @param e 认证异常
     * @return API响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleBadCredentialsException(BadCredentialsException e) {
        return ApiResponse.unauthorized();
    }

    /**
     * 处理资源不存在异常
     * @param e 资源不存在异常
     * @return API响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponse.error(404, e.getMessage());
    }

    /**
     * 处理通用异常
     * @param e 异常
     * @return API响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.serverError("服务器内部错误");
    }

    /**
     * 处理数据访问异常
     * @param e 数据访问异常
     * @return API响应
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleDataAccessException(DataAccessException e) {
        return ApiResponse.serverError("数据库访问异常: " + e.getMessage());
    }
    
    /**
     * 处理空指针异常
     * @param e 空指针异常
     * @return API响应
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleNullPointerException(NullPointerException e) {
        return ApiResponse.serverError("服务器处理错误: 空值引用");
    }
} 