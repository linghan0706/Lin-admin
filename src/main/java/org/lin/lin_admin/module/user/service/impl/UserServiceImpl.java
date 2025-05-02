package org.lin.lin_admin.module.user.service.impl;

import org.lin.lin_admin.module.user.dto.LoginRequest;
import org.lin.lin_admin.module.user.dto.LoginResponse;
import org.lin.lin_admin.module.user.mapper.UserMapper;
import org.lin.lin_admin.module.user.model.User;
import org.lin.lin_admin.module.user.service.UserService;
import org.lin.lin_admin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 查找用户
        User user = findByUsername(loginRequest.getUsername());
        
        // 用户不存在
        if (user == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        
        // 明文密码验证（不使用哈希）
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 返回登录响应
        return new LoginResponse(token, user.getUsername());
    }
} 