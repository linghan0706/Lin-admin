# 博客管理后台API

## 项目概述

本项目是一个基于Spring Boot、Spring Security、JWT和MyBatis的博客管理后台API，提供安全的用户认证和文章管理功能。

## 技术栈

- Java 17
- Spring Boot 3.4.5
- Spring Security
- MyBatis
- MySQL
- JWT (JSON Web Token)

## 功能

- 管理员登录认证
- JWT令牌生成与验证
- 受保护API资源访问控制

## 数据库设计

项目包含三个主要表：

- `users`: 管理员用户表
- `articles`: 文章表
- `comments`: 评论表

详细的数据库设计请查看 `src/main/resources/schema.sql`。

## API接口

### 登录接口

- **URL**: `/admin/login`
- **方法**: POST
- **请求体**:
  ```json
  {
    "username": "管理员用户名",
    "password": "管理员密码"
  }
  ```
- **成功响应** (200 OK):
  ```json
  {
    "token": "JWT令牌"
  }
  ```
- **失败响应** (401 Unauthorized):
  ```json
  {
    "error": "invalid credentials"
  }
  ```

### 访问受保护资源

对于所有受保护的API端点，需要在请求头中携带JWT令牌：

```
Authorization: Bearer 你的JWT令牌
```

## 快速开始

### 前置条件

- Java 17或更高版本
- Maven 3.6.0或更高版本
- MySQL 8.0或更高版本

### 设置数据库

1. 创建MySQL数据库：
   ```sql
   CREATE DATABASE blog_db;
   ```
2. 更新 `application.yml`中的数据库连接信息。

### 构建与运行

1. 克隆项目
2. 构建项目：
   ```bash
   mvn clean package
   ```
3. 运行应用：
   ```bash
   java -jar target/lin_admin-0.0.1-SNAPSHOT.jar
   ```

### 测试API

使用Postman或curl测试登录API:

```bash
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 安全注意事项

- 在生产环境中，请更改JWT密钥(`jwt.secret`)
- 使用HTTPS确保数据传输安全
- 定期轮换JWT密钥
- 实现JWT黑名单机制以支持注销功能

## 许可证

[MIT License](LICENSE)
