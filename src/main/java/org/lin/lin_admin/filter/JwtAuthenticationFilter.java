package org.lin.lin_admin.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lin.lin_admin.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 用于处理包含JWT令牌的认证请求
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 设置响应头，允许跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // 不拦截OPTIONS请求
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpStatus.OK.value());
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            
            String username = null;
            String token = null;

            // 记录请求信息
            logger.info("请求URI: " + request.getRequestURI() + ", 方法: " + request.getMethod());
            if (authorizationHeader != null) {
                logger.info("Authorization头: " + (authorizationHeader.startsWith("Bearer ") ? 
                           "Bearer " + authorizationHeader.substring(7, Math.min(15, authorizationHeader.length())) + "..." : 
                           "非Bearer格式"));
            } else {
                logger.info("未提供Authorization头");
            }
            
            // 检查请求头是否包含Authorization和Bearer token
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                    logger.info("成功从Token中提取用户名: " + username);
                } catch (ExpiredJwtException e) {
                    logger.warn("Token已过期: " + e.getMessage());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token已过期，请重新登录\",\"data\":null}");
                    return;
                } catch (JwtException e) {
                    logger.warn("Token无效: " + e.getMessage());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token无效，请重新登录\",\"data\":null}");
                    return;
                } catch (Exception e) {
                    logger.error("无法解析Token: " + e.getMessage(), e);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token验证失败，请重新登录\",\"data\":null}");
                    return;
                }
            } else {
                // 仅针对需要认证的路径记录日志
                String requestURI = request.getRequestURI();
                if (shouldAuthenticate(requestURI)) {
                    logger.warn("访问受保护资源 " + requestURI + " 但未提供有效Token");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"未授权访问，请先登录\",\"data\":null}");
                    return;
                }
            }
            
            // 如果成功提取用户名并且当前没有认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 创建认证对象
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置认证信息到Spring Security上下文
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                logger.info("用户 '" + username + "' 已通过认证");
            }
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JWT过滤器发生错误: " + e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"message\":\"服务器内部错误\",\"data\":null}");
        }
    }
    
    /**
     * 判断是否为需要认证的请求路径
     */
    private boolean shouldAuthenticate(String uri) {
        // 不需要认证的路径
        return uri.startsWith("/admin/") && 
                !uri.equals("/admin/login") && 
                !isStaticResource(uri);
    }
    
    /**
     * 判断是否为静态资源
     */
    private boolean isStaticResource(String uri) {
        return uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || 
               uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg") || 
               uri.endsWith(".gif") || uri.endsWith(".svg");
    }
} 