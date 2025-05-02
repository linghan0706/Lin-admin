# Lin-API公共接口文档

## 基本信息

- **基础URL**: `http://localhost:8080`
- **内容类型**: `application/json`
- **认证方式**: 无需认证

## API接口列表

### 1. 公共访问API (无需认证)

#### 1.1 获取已发布文章列表

- **接口**: `/articles`
- **方法**: `GET`
- **认证**: 不需要
- **请求参数**:
  - `page`: 页码（默认值为1）
  - `size`: 每页条数（默认值为10）
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/articles', {
    params: { page: 1, size: 10 }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": 1,
        "title": "文章标题",
        "summary": "文章摘要...",
        "content": "文章内容",
        "authorName": "管理员",
        "status": "published",
        "coverImage": "http://localhost:8080/uploads/image1.jpg",
        "createdAt": "2025-01-01T12:00:00",
        "updatedAt": "2025-01-01T12:00:00",
        "publishedAt": "2025-01-01T12:00:00",
        "tags": ["标签1", "标签2"]
      }
    ]
  }
  ```

#### 1.2 获取文章详情

- **接口**: `/articles/{id}`
- **方法**: `GET`
- **认证**: 不需要
- **调用示例**:
  ```javascript
  axios.get(`http://localhost:8080/articles/1`);
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "title": "文章标题",
      "summary": "文章摘要...",
      "content": "文章完整内容...",
      "authorName": "管理员",
      "status": "published",
      "coverImage": "http://localhost:8080/uploads/image1.jpg",
      "createdAt": "2025-01-01T12:00:00",
      "updatedAt": "2025-01-01T12:00:00",
      "publishedAt": "2025-01-01T12:00:00",
      "tags": ["标签1", "标签2"]
    }
  }
  ```

#### 1.3 获取工具列表

- **接口**: `/tools`
- **方法**: `GET`
- **认证**: 不需要
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/tools');
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": 1,
        "name": "工具名称",
        "description": "工具描述",
        "link": "https://example.com"
      }
    ]
  }
  ```

## 错误处理

所有API在发生错误时返回适当的HTTP状态码和统一格式的JSON响应:

| 状态码 | 说明         |
| ------ | ------------ |
| 200    | 请求成功     |
| 400    | 请求参数错误 |
| 404    | 资源不存在   |
| 500    | 服务器错误   |

错误响应格式:

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
``` 