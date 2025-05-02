# 博客管理系统前端 API 使用指南

本指南提供关于如何在前端应用中调用博客管理系统API的详细说明和最佳实践。

## 目录

1. [环境配置](#环境配置)
2. [身份验证](#身份验证)
3. [API调用示例](#api调用示例)
4. [错误处理](#错误处理)
5. [最佳实践](#最佳实践)

## 环境配置

### API基础URL配置

为了便于管理API请求，建议创建一个API配置文件，如下所示：

```javascript
// api-config.js
const config = {
  baseURL: 'http://localhost:8080',
  timeout: 10000, // 请求超时时间（毫秒）
  headers: {
    'Content-Type': 'application/json'
  }
};

export default config;
```

### Axios实例配置

创建一个预配置的Axios实例以简化API调用：

```javascript
// api-client.js
import axios from 'axios';
import config from './api-config';

// 创建Axios实例
const apiClient = axios.create({
  baseURL: config.baseURL,
  timeout: config.timeout,
  headers: config.headers
});

// 请求拦截器 - 添加认证令牌
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器 - 处理常见错误
apiClient.interceptors.response.use(response => {
  return response;
}, error => {
  // 处理401错误 - 未授权访问
  if (error.response && error.response.status === 401) {
    // 清除本地存储的令牌
    localStorage.removeItem('auth_token');
    // 重定向到登录页
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

export default apiClient;
```

## 身份验证

### 登录处理

```javascript
// auth-service.js
import apiClient from './api-client';

export const AuthService = {
  login: async (username, password) => {
    try {
      const response = await apiClient.post('/admin/login', { username, password });
      if (response.data && response.data.token) {
        // 存储令牌到本地存储
        localStorage.setItem('auth_token', response.data.token);
        // 存储用户名
        localStorage.setItem('username', response.data.username);
        return response.data;
      }
    } catch (error) {
      console.error('登录失败:', error);
      throw error;
    }
  },
  
  logout: () => {
    // 清除本地存储中的身份验证数据
    localStorage.removeItem('auth_token');
    localStorage.removeItem('username');
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('auth_token');
  }
};
```

### 令牌验证

```javascript
// token-validator.js
import jwt_decode from 'jwt-decode';

export const TokenValidator = {
  isTokenValid: (token) => {
    if (!token) return false;
    
    try {
      const decoded = jwt_decode(token);
      const currentTime = Date.now() / 1000; // 转换为秒
      
      // 检查令牌是否已过期
      if (decoded.exp && decoded.exp < currentTime) {
        return false;
      }
      
      return true;
    } catch (error) {
      console.error('令牌验证失败:', error);
      return false;
    }
  }
};
```

## API调用示例

### 获取文章列表

```javascript
// article-service.js
import apiClient from './api-client';

export const ArticleService = {
  // 获取文章列表，支持分页
  getArticles: async (page = 1, size = 10, sort = 'createTime,desc') => {
    try {
      const response = await apiClient.get('/admin/articles', {
        params: { page, size, sort }
      });
      return response.data;
    } catch (error) {
      console.error('获取文章列表失败:', error);
      throw error;
    }
  },
  
  // 获取单篇文章详情
  getArticleById: async (id) => {
    try {
      const response = await apiClient.get(`/admin/articles/${id}`);
      return response.data;
    } catch (error) {
      console.error(`获取文章ID=${id}失败:`, error);
      throw error;
    }
  },
  
  // 创建新文章
  createArticle: async (articleData) => {
    try {
      const response = await apiClient.post('/admin/articles', articleData);
      return response.data;
    } catch (error) {
      console.error('创建文章失败:', error);
      throw error;
    }
  },
  
  // 更新文章
  updateArticle: async (id, articleData) => {
    try {
      const response = await apiClient.put(`/admin/articles/${id}`, articleData);
      return response.data;
    } catch (error) {
      console.error(`更新文章ID=${id}失败:`, error);
      throw error;
    }
  },
  
  // 删除文章
  deleteArticle: async (id) => {
    try {
      await apiClient.delete(`/admin/articles/${id}`);
      return true;
    } catch (error) {
      console.error(`删除文章ID=${id}失败:`, error);
      throw error;
    }
  }
};
```

### 在React组件中使用

```jsx
// ArticleList.jsx
import React, { useState, useEffect } from 'react';
import { ArticleService } from '../services/article-service';

const ArticleList = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  
  useEffect(() => {
    const fetchArticles = async () => {
      try {
        setLoading(true);
        const data = await ArticleService.getArticles(currentPage);
        setArticles(data.articles);
        setLoading(false);
      } catch (err) {
        setError('获取文章列表失败');
        setLoading(false);
      }
    };
    
    fetchArticles();
  }, [currentPage]);
  
  // 删除文章的处理函数
  const handleDelete = async (id) => {
    if (window.confirm('确定要删除这篇文章吗？')) {
      try {
        await ArticleService.deleteArticle(id);
        // 在本地更新文章列表
        setArticles(articles.filter(article => article.id !== id));
      } catch (err) {
        alert('删除文章失败');
      }
    }
  };
  
  if (loading) return <div>加载中...</div>;
  if (error) return <div>错误: {error}</div>;
  
  return (
    <div>
      <h2>文章列表</h2>
      {articles.length === 0 ? (
        <p>暂无文章</p>
      ) : (
        <ul>
          {articles.map(article => (
            <li key={article.id}>
              <h3>{article.title}</h3>
              <p>{article.summary}</p>
              <div>
                <button onClick={() => handleEdit(article.id)}>编辑</button>
                <button onClick={() => handleDelete(article.id)}>删除</button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ArticleList;
```

## 错误处理

建议为API调用实现统一的错误处理逻辑：

```javascript
// error-handler.js
export const handleApiError = (error) => {
  if (error.response) {
    // 服务器响应了错误状态码
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return { type: 'VALIDATION_ERROR', message: data.error || '请求参数错误' };
      case 401:
        return { type: 'UNAUTHORIZED', message: '未授权，请重新登录' };
      case 403:
        return { type: 'FORBIDDEN', message: '权限不足，无法访问' };
      case 404:
        return { type: 'NOT_FOUND', message: data.error || '请求的资源不存在' };
      case 500:
        return { type: 'SERVER_ERROR', message: '服务器错误，请稍后重试' };
      default:
        return { type: 'UNKNOWN_ERROR', message: '未知错误' };
    }
  } else if (error.request) {
    // 请求发送但没收到响应
    return { type: 'NETWORK_ERROR', message: '网络错误，无法连接到服务器' };
  } else {
    // 请求设置过程中发生错误
    return { type: 'REQUEST_ERROR', message: error.message || '请求错误' };
  }
};
```

## 最佳实践

1. **集中管理API调用**：将所有API调用封装在专门的服务模块中
2. **处理认证令牌**：使用请求拦截器自动添加认证令牌
3. **错误处理**：实现统一的错误处理逻辑
4. **避免硬编码URL**：使用配置文件管理API基础URL
5. **处理加载状态**：在UI中显示加载状态以提高用户体验
6. **数据缓存**：考虑使用本地缓存减少不必要的API调用
7. **响应式更新**：在数据变化时更新UI
8. **定期检查令牌有效性**：定期验证JWT令牌是否过期

通过遵循这些最佳实践，您可以创建一个高效、可维护的前端应用，简化与后端API的交互。 