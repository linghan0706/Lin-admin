package org.lin.lin_admin.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义跨域过滤器，确保在所有其他过滤器之前运行
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomCorsFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 记录请求信息，便于调试
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String origin = request.getHeader("Origin");
        
        logger.info("CORS过滤器处理请求: {} {} 来源: {}", requestMethod, requestURI, origin != null ? origin : "未知");
        
        // 当请求头中有Origin时设置允许的来源，否则使用*
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        
        // 允许所有请求头
        response.setHeader("Access-Control-Allow-Headers", 
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token");
        // 允许所有HTTP方法
        response.setHeader("Access-Control-Allow-Methods", 
                "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        // 允许发送凭证
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 预检请求缓存时间
        response.setHeader("Access-Control-Max-Age", "3600");
        // 暴露响应头
        response.setHeader("Access-Control-Expose-Headers", "Authorization, X-Auth-Token");

        // 对于预检请求，直接返回200 OK
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            logger.info("处理OPTIONS预检请求: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        } else {
            logger.debug("继续处理非预检请求: {} {}", requestMethod, requestURI);
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("CustomCorsFilter 初始化");
    }

    @Override
    public void destroy() {
        logger.info("CustomCorsFilter 销毁");
    }
} 