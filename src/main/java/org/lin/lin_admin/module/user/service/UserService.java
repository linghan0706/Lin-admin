package org.lin.lin_admin.module.user.service;

import org.lin.lin_admin.module.user.dto.LoginRequest;
import org.lin.lin_admin.module.user.dto.LoginResponse;
import org.lin.lin_admin.module.user.model.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体，如不存在返回null
     */
    User findByUsername(String username);
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应（包含token）
     */
    LoginResponse login(LoginRequest loginRequest);
} 