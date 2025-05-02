package org.lin.lin_admin.module.user.controller;

import org.lin.lin_admin.common.response.ApiResponse;
import org.lin.lin_admin.module.user.dto.LoginRequest;
import org.lin.lin_admin.module.user.dto.LoginResponse;
import org.lin.lin_admin.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ApiResponse.success(response);
    }
    
    /**
     * 检查认证状态
     * @return 认证状态
     */
    @GetMapping("/check-auth")
    public ApiResponse<Map<String, Object>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", authentication != null && authentication.isAuthenticated());
        result.put("principal", authentication != null ? authentication.getPrincipal() : null);
        return ApiResponse.success(result);
    }
}
