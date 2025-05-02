package org.lin.lin_admin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.JwtException;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey secretKey;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:86400000}")  // 默认24小时
    private Long refreshExpiration;
    
    public JwtUtil(@Value("${jwt.secret:}") String secret) {
        // 如果配置了密钥，使用配置的密钥（确保至少256位），否则生成新密钥
        if (secret != null && !secret.isEmpty()) {
            // 确保密钥长度至少为32字节（256位）
            if (secret.length() < 32) {
                // 如果不够长，扩展密钥到32字节
                StringBuilder paddedSecret = new StringBuilder(secret);
                while (paddedSecret.length() < 32) {
                    paddedSecret.append(secret);
                }
                secret = paddedSecret.substring(0, 32);
            }
            // 使用提供的密钥
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            logger.info("使用配置的JWT密钥");
        } else {
            // 生成新的安全密钥
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            logger.warn("未配置JWT密钥，使用自动生成的密钥（应用重启后将失效）");
        }
    }

    /**
     * 从令牌中获取用户名
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     * @param token 令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取声明
     * @param token 令牌
     * @param claimsResolver 声明解析器
     * @param <T> 声明类型
     * @return 声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * @param token 令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            // 使用新版Jwts API
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("解析JWT令牌失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 检查令牌是否过期
     * @param token 令牌
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            boolean isExpired = expiration.before(new Date());
            if (isExpired) {
                logger.debug("令牌已过期，过期时间: {}", expiration);
            }
            return isExpired;
        } catch (Exception e) {
            logger.error("检查令牌过期时出错: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 生成令牌
     * @param username 用户名
     * @return 令牌
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        String token = doGenerateToken(claims, username);
        logger.info("为用户 '{}' 生成了新的JWT令牌", username);
        return token;
    }

    /**
     * 生成刷新令牌
     * @param username 用户名
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();
        logger.info("为用户 '{}' 生成了新的刷新令牌", username);
        return token;
    }
    
    /**
     * 使用刷新令牌生成新的访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public String refreshToken(String refreshToken) {
        try {
            final Claims claims = getAllClaimsFromToken(refreshToken);
            final String username = claims.getSubject();
            Object tokenType = claims.get("type");
            
            if (!"refresh".equals(tokenType)) {
                logger.warn("尝试使用非刷新令牌生成新令牌");
                throw new JwtException("无效的刷新令牌");
            }
            
            if (!isTokenExpired(refreshToken)) {
                logger.info("刷新令牌有效，为用户 '{}' 生成新的访问令牌", username);
                return generateToken(username);
            } else {
                logger.warn("刷新令牌已过期");
                throw new JwtException("刷新令牌已过期");
            }
        } catch (Exception e) {
            logger.error("刷新令牌失败: {}", e.getMessage());
            throw new JwtException("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 生成令牌的具体实现
     * @param claims 声明
     * @param subject 主题
     * @return 令牌
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(issuedAt.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 验证令牌
     * @param token 令牌
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            boolean isValid = (tokenUsername.equals(username) && !isTokenExpired(token));
            if (isValid) {
                logger.debug("令牌验证成功，用户: {}", username);
            } else {
                logger.warn("令牌验证失败，Token用户: {}, 请求用户: {}", tokenUsername, username);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("验证令牌时出错: {}", e.getMessage());
            return false;
        }
    }
} 