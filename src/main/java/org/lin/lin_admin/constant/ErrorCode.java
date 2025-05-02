package org.lin.lin_admin.constant;

/**
 * 系统错误代码常量类
 */
public class ErrorCode {
    // 通用错误代码 (1000-1999)
    public static final int UNKNOWN_ERROR = 1000;
    public static final int VALIDATION_ERROR = 1001;
    public static final int RESOURCE_NOT_FOUND = 1002;
    public static final int ILLEGAL_ARGUMENT = 1003;
    
    // 认证相关错误代码 (2000-2999)
    public static final int AUTH_INVALID_CREDENTIALS = 2000;
    public static final int AUTH_EXPIRED_TOKEN = 2001;
    public static final int AUTH_INVALID_TOKEN = 2002;
    public static final int AUTH_INSUFFICIENT_PERMISSIONS = 2003;
    public static final int AUTH_USER_NOT_FOUND = 2004;
    public static final int AUTH_WRONG_PASSWORD = 2005;
    
    // 用户相关错误代码 (3000-3999)
    public static final int USER_ALREADY_EXISTS = 3000;
    public static final int USER_NOT_ACTIVATED = 3001;
    
    // 业务操作错误代码 (4000-4999)
    public static final int OPERATION_FAILED = 4000;
    public static final int DUPLICATE_OPERATION = 4001;
} 