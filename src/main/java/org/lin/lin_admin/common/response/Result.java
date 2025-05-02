package org.lin.lin_admin.common.response;

/**
 * 统一响应结果封装
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;
    
    public Result() {
    }
    
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // 成功返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    // 失败返回
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }
    
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
} 