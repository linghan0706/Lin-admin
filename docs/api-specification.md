# 博客管理系统 API 接口规范文档

## 基本信息

- **基础URL**: `http://101.126.146.84:8080`
- **内容类型**: `application/json`
- **认证方式**: JWT令牌（Bearer Authentication）

## 认证方式

除了登录和初始化接口外，所有接口都需要通过JWT进行认证。认证方式如下：

1. 首先通过登录接口获取JWT令牌
2. 在后续请求中，在请求头中添加`Authorization: Bearer {token}`

## API接口列表

### 1. 用户认证相关

#### 1.1 管理员登录

- **URL**: `/admin/login`
- **方法**: `POST`
- **认证**: 不需要

**请求参数**:

```json
{
  "username": "管理员用户名",
  "password": "管理员密码"
}
```

**成功响应** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin"
}
```

**错误响应** (401 Unauthorized):

```json
{
  "error": "用户名或密码错误"
}
```

### 2. 文章管理相关

#### 2.1 获取文章列表

- **URL**: `/admin/articles`
- **方法**: `GET`
- **认证**: 需要

**请求参数** (可选查询参数):

```
?page=1&size=10&sort=createTime,desc
```

**请求头**:

```
Authorization: Bearer {token}
```

**成功响应** (200 OK):

```json
{
  "articles": [
    {
      "id": 1,
      "title": "文章标题",
      "summary": "文章摘要",
      "content": "文章内容",
      "createTime": "2025-01-01T12:00:00",
      "updateTime": "2025-01-02T14:30:00"
    }
    // 更多文章...
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 5,
    "totalItems": 42,
    "itemsPerPage": 10
  }
}
```

#### 2.2 获取单篇文章详情

- **URL**: `/admin/articles/{id}`
- **方法**: `GET`
- **认证**: 需要

**URL参数**:
- `id`: 文章ID

**请求头**:

```
Authorization: Bearer {token}
```

**成功响应** (200 OK):

```json
{
  "id": 1,
  "title": "文章标题",
  "summary": "文章摘要",
  "content": "文章完整内容...",
  "createTime": "2025-01-01T12:00:00",
  "updateTime": "2025-01-02T14:30:00",
  "tags": ["标签1", "标签2"]
}
```

**错误响应** (404 Not Found):

```json
{
  "error": "文章不存在"
}
```

#### 2.3 创建新文章

- **URL**: `/admin/articles`
- **方法**: `POST`
- **认证**: 需要

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

```json
{
  "title": "新文章标题",
  "summary": "文章摘要",
  "content": "文章完整内容...",
  "tags": ["标签1", "标签2"]
}
```

**成功响应** (201 Created):

```json
{
  "id": 43,
  "title": "新文章标题",
  "summary": "文章摘要",
  "content": "文章完整内容...",
  "createTime": "2025-04-28T10:15:30",
  "updateTime": "2025-04-28T10:15:30",
  "tags": ["标签1", "标签2"]
}
```

#### 2.4 更新文章

- **URL**: `/admin/articles/{id}`
- **方法**: `PUT`
- **认证**: 需要

**URL参数**:
- `id`: 文章ID

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

```json
{
  "title": "更新后的标题",
  "summary": "更新后的摘要",
  "content": "更新后的内容...",
  "tags": ["标签1", "标签3"]
}
```

**成功响应** (200 OK):

```json
{
  "id": 1,
  "title": "更新后的标题",
  "summary": "更新后的摘要",
  "content": "更新后的内容...",
  "createTime": "2025-01-01T12:00:00",
  "updateTime": "2025-04-28T11:20:45",
  "tags": ["标签1", "标签3"]
}
```

#### 2.5 删除文章

- **URL**: `/admin/articles/{id}`
- **方法**: `DELETE`
- **认证**: 需要

**URL参数**:
- `id`: 文章ID

**请求头**:

```
Authorization: Bearer {token}
```

**成功响应** (204 No Content)

**错误响应** (404 Not Found):

```json
{
  "error": "文章不存在"
}
```

### 3. 系统管理相关

#### 3.1 系统初始化

- **URL**: `/setup`
- **方法**: `GET`
- **认证**: 不需要

**成功响应** (200 OK):

```json
{
  "success": true,
  "message": "管理员账户初始化成功"
}
```

## 错误处理

所有API在发生错误时会返回适当的HTTP状态码和JSON格式的错误信息:

| 状态码 | 说明 |
| ------ | ---- |
| 200    | 请求成功 |
| 201    | 创建成功 |
| 204    | 删除成功 |
| 400    | 请求参数错误 |
| 401    | 未授权访问（缺少令牌或令牌无效） |
| 403    | 权限不足 |
| 404    | 请求的资源不存在 |
| 500    | 服务器内部错误 |

错误响应格式:

```json
{
  "error": "错误描述信息"
}
```

## 前端调用示例

### 使用Axios调用登录接口

```javascript
axios.post('http://localhost:8080/admin/login', {
  username: 'admin',
  password: 'password'
})
.then(response => {
  // 保存JWT令牌
  const token = response.data.token;
  // 处理成功响应
})
.catch(error => {
  // 处理错误响应
});
```

### 使用Axios调用需要认证的接口

```javascript
axios.get('http://localhost:8080/admin/articles', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => {
  // 处理获取的文章数据
})
.catch(error => {
  // 处理错误响应
});
```

### 创建新文章示例

```javascript
axios.post('http://localhost:8080/admin/articles', {
  title: '新文章标题',
  summary: '文章摘要',
  content: '文章完整内容...',
  tags: ['标签1', '标签2']
}, {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => {
  console.log('文章创建成功:', response.data);
})
.catch(error => {
  console.error('创建文章失败:', error);
});
```

## CORS 支持

该API支持跨域资源共享(CORS)，允许从不同源的前端应用进行访问。服务器配置了以下CORS头：

- `Access-Control-Allow-Origin`: 根据请求中的Origin决定
- `Access-Control-Allow-Headers`: `Origin, X-Requested-With, Content-Type, Accept, Authorization`
- `Access-Control-Allow-Methods`: `GET, POST, PUT, DELETE, OPTIONS`
- `Access-Control-Allow-Credentials`: `true`
- `Access-Control-Max-Age`: `3600`
- `Access-Control-Expose-Headers`: `Authorization` 