package org.lin.lin_admin.module.user.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    private String token;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
} 