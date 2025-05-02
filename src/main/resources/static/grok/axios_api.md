# Lin-API接口设计文档

## 基本信息

- **基础URL**: `http://localhost:8080`
- **内容类型**: `application/json`
- **认证方式**: JWT令牌（Bearer Authentication）

## JWT认证说明

系统使用JWT（JSON Web Token）进行身份验证。登录成功后会获取一个令牌（token），后续的受保护API请求都需要在请求头中携带此令牌。

- **认证格式**: 在HTTP请求头中添加 `Authorization: Bearer {token}`
- **令牌有效期**: 1小时（可在application.properties中配置）
- **注意事项**:
  - 确保Bearer和token之间有一个空格
  - 令牌过期后需要重新登录获取新令牌
  - 跨域请求时确保预检请求(OPTIONS)正确传递Authorization头
  - 403错误通常表示令牌无效或已过期

## API接口列表

### 1. 用户认证

#### 1.1 管理员登录

- **接口**: `/admin/login`
- **方法**: `POST`
- **认证**: 不需要
- **请求参数**:
  ```json
  {
    "username": "管理员用户名",
    "password": "管理员密码"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "username": "admin"
    }
  }
  ```

#### 1.2 检查认证状态

- **接口**: `/admin/check-auth`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/admin/check-auth', {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "authenticated": true,
      "principal": "admin"
    }
  }
  ```

### 2. 公共访问API

公共访问API已移至单独的文档中，请参考 [公共API文档](./public_api.md)。

### 3. 管理员文章API (需要认证)

#### 3.1 获取所有文章列表(包括草稿)

- **接口**: `/admin/articles`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/admin/articles', {
    headers: { Authorization: `Bearer ${token}` }
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

#### 3.2 获取文章详情

- **接口**: `/admin/articles/{id}`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get(`http://localhost:8080/admin/articles/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
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

#### 3.3 创建文章

- **接口**: `/admin/articles`
- **方法**: `POST`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "title": "新文章标题",
    "summary": "文章摘要...", 
    "content": "文章内容...",
    "status": "draft",
    "coverImage": "http://localhost:8080/uploads/image1.jpg",
    "tagIds": [1, 2]
  }
  ```
- **调用示例**:
  ```javascript
  axios.post('http://localhost:8080/admin/articles', {
    title: '新文章标题',
    summary: '文章摘要...',
    content: '文章内容...',
    status: 'draft',
    coverImage: 'http://localhost:8080/uploads/image1.jpg',
    tagIds: [1, 2]
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 2,
      "title": "新文章标题",
      "summary": "文章摘要...",
      "content": "文章内容...",
      "authorName": "管理员",
      "status": "draft",
      "coverImage": "http://localhost:8080/uploads/image1.jpg",
      "createdAt": "2025-01-02T14:30:00",
      "updatedAt": "2025-01-02T14:30:00",
      "publishedAt": null,
      "tags": ["标签1", "标签2"]
    }
  }
  ```

#### 3.4 更新文章

- **接口**: `/admin/articles/{id}`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "title": "更新后的标题",
    "summary": "更新后的摘要...",
    "content": "更新后的内容...",
    "status": "published",
    "coverImage": "http://localhost:8080/uploads/image2.jpg",
    "tagIds": [2, 3]
  }
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/articles/1`, {
    title: '更新后的标题',
    summary: '更新后的摘要...',
    content: '更新后的内容...',
    status: 'published',
    coverImage: 'http://localhost:8080/uploads/image2.jpg',
    tagIds: [2, 3]
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "title": "更新后的标题",
      "summary": "更新后的摘要...",
      "content": "更新后的内容...",
      "authorName": "管理员",
      "status": "published",
      "coverImage": "http://localhost:8080/uploads/image2.jpg",
      "createdAt": "2025-01-01T12:00:00",
      "updatedAt": "2025-01-02T15:45:00",
      "publishedAt": "2025-01-02T15:45:00",
      "tags": ["标签2", "标签3"]
    }
  }
  ```

#### 3.5 删除文章

- **接口**: `/admin/articles/{id}`
- **方法**: `DELETE`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.delete(`http://localhost:8080/admin/articles/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 3.6 发布文章

- **接口**: `/admin/articles/{id}/publish`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/articles/1/publish`, {}, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "title": "文章标题",
      "summary": "",
      "content": "文章内容...",
      "authorName": "管理员",
      "status": "published",
      "coverImage": "http://localhost:8080/uploads/image1.jpg",
      "createdAt": "2025-01-01T12:00:00",
      "updatedAt": "2025-01-02T16:20:00",
      "publishedAt": "2025-01-02T16:20:00",
      "tags": ["标签1", "标签2"]
    }
  }
  ```

#### 3.7 归档文章

- **接口**: `/admin/articles/{id}/archive`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/articles/1/archive`, {}, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "title": "文章标题",
      "summary": "",
      "content": "文章内容...",
      "authorName": "管理员",
      "status": "archived",
      "coverImage": "http://localhost:8080/uploads/image1.jpg",
      "createdAt": "2025-01-01T12:00:00",
      "updatedAt": "2025-01-02T16:25:00",
      "publishedAt": "2025-01-01T14:30:00",
      "tags": ["标签1", "标签2"]
    }
  }
  ```

### 4. 管理员工具API (需要认证)

#### 4.1 获取工具列表

- **接口**: `/admin/tools`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/admin/tools', {
    headers: { Authorization: `Bearer ${token}` }
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
        "name": "工具名称",
        "description": "工具描述",
        "link": "https://example.com"
      }
    ]
  }
  ```

#### 4.2 获取工具详情

- **接口**: `/admin/tools/{id}`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get(`http://localhost:8080/admin/tools/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "name": "工具名称",
      "description": "工具描述",
      "link": "https://example.com"
    }
  }
  ```

#### 4.3 创建工具

- **接口**: `/admin/tools`
- **方法**: `POST`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "name": "工具名称",
    "description": "工具描述",
    "link": "https://example.com"
  }
  ```
- **调用示例**:
  ```javascript
  axios.post('http://localhost:8080/admin/tools', {
    name: '工具名称',
    description: '工具描述',
    link: 'https://example.com'
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 2,
      "name": "工具名称",
      "description": "工具描述",
      "link": "https://example.com"
    }
  }
  ```

#### 4.4 更新工具

- **接口**: `/admin/tools/{id}`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "name": "更新后的名称",
    "description": "更新后的描述",
    "link": "https://example.com/new"
  }
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/tools/1`, {
    name: '更新后的名称',
    description: '更新后的描述',
    link: 'https://example.com/new'
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "name": "更新后的名称",
      "description": "更新后的描述",
      "link": "https://example.com/new"
    }
  }
  ```

#### 4.5 删除工具

- **接口**: `/admin/tools/{id}`
- **方法**: `DELETE`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.delete(`http://localhost:8080/admin/tools/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

### 5. 留言管理API

#### 5.1 前台留言提交 (无需认证)

- **接口**: `/api/message`
- **方法**: `POST`
- **认证**: 不需要
- **请求头**:
  ```
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "nickname": "访客",
    "content": "这是一条留言内容",
    "contactInfo": "example@email.com"
  }
  ```
- **调用示例**:
  ```javascript
  axios.post('http://localhost:8080/api/message', {
    nickname: '访客',
    content: '这是一条留言内容',
    contactInfo: 'example@email.com'
  }, {
    headers: { 'Content-Type': 'application/json' }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "留言提交成功",
    "data": null
  }
  ```

#### 5.2 管理员留言列表 (需要认证)

- **接口**: `/admin/message/page`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **请求参数**:
  - `page`: 页码（默认值为1）
  - `size`: 每页条数（默认值为10）
  - `status`: 状态筛选，可选值：pending(待处理)、replied(已回复)、hidden(已隐藏)
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/admin/message/page', {
    params: { page: 1, size: 10, status: 'pending' },
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "content": [
        {
          "id": 1,
          "nickname": "访客",
          "content": "留言内容",
          "contactInfo": "exa***@email.com",
          "ipAddress": "127.0.0.1",
          "status": "pending",
          "createdAt": "2023-06-01T12:34:56",
          "replyContent": null,
          "replyAt": null,
          "adminName": null
        }
      ],
      "total": 30,
      "page": 1,
      "size": 10,
      "totalPages": 3
    }
  }
  ```

#### 5.3 获取留言详情 (需要认证)

- **接口**: `/admin/message/{id}`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get(`http://localhost:8080/admin/message/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "nickname": "访客",
      "content": "留言内容",
      "contactInfo": "exa***@email.com",
      "ipAddress": "127.0.0.1",
      "status": "pending",
      "createdAt": "2023-06-01T12:34:56",
      "replyContent": null,
      "replyAt": null,
      "adminName": null
    }
  }
  ```

#### 5.4 回复留言 (需要认证)

- **接口**: `/admin/message/{id}/reply`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "replyContent": "感谢您的留言，我们会尽快处理"
  }
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/message/1/reply`, {
    replyContent: '感谢您的留言，我们会尽快处理'
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "回复成功",
    "data": null
  }
  ```

#### 5.5 更新留言状态 (需要认证)

- **接口**: `/admin/message/{id}/status`
- **方法**: `PUT`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: application/json
  ```
- **请求参数**:
  ```json
  {
    "status": "hidden"  // pending, replied, hidden
  }
  ```
- **调用示例**:
  ```javascript
  axios.put(`http://localhost:8080/admin/message/1/status`, {
    status: 'hidden'
  }, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "状态更新成功",
    "data": null
  }
  ```

#### 5.6 删除留言 (需要认证)

- **接口**: `/admin/message/{id}`
- **方法**: `DELETE`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.delete(`http://localhost:8080/admin/message/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": null
  }
  ```

### 6. 文件上传API (需要认证)

#### 6.1 上传图片

- **接口**: `/admin/files/upload/image`
- **方法**: `POST`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: multipart/form-data
  ```
- **请求参数**:
  - `file`: 图片文件（支持jpg/png格式，大小限制5MB）
- **调用示例**:
  ```javascript
  // 使用FormData上传
  const formData = new FormData();
  formData.append('file', fileObject); // fileObject是从input[type="file"]获取的文件对象

  axios.post('http://localhost:8080/admin/files/upload/image', formData, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "url": "http://localhost:8080/uploads/images/202507011230_example.jpg",
      "filename": "example.jpg",
      "size": 1048576,
      "contentType": "image/jpeg"
    }
  }
  ```

#### 6.2 上传图片并提取元数据

- **接口**: `/admin/files/upload/image/with-metadata`
- **方法**: `POST`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  Content-Type: multipart/form-data
  ```
- **请求参数**:
  - `file`: 图片文件（支持jpg/png格式，大小限制10MB）
- **调用示例**:
  ```javascript
  // 使用FormData上传
  const formData = new FormData();
  formData.append('file', fileObject); // fileObject是从input[type="file"]获取的文件对象

  axios.post('http://localhost:8080/admin/files/upload/image/with-metadata', formData, {
    headers: { 
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "url": "http://localhost:8080/uploads/images/202507011230_example.jpg",
      "filename": "example.jpg",
      "size": 1048576,
      "contentType": "image/jpeg",
      "width": 1920,
      "height": 1080,
      "cameraMake": "Canon",
      "cameraModel": "EOS 5D Mark IV",
      "shootTime": "2023-06-15T14:30:45",
      "metadata": {
        "Make": "Canon",
        "Model": "EOS 5D Mark IV",
        "ExposureTime": "1/125 sec",
        "FNumber": "f/2.8",
        "ISO": "100",
        "FocalLength": "50.0 mm",
        "GPS Latitude": "40° 41' 21.5\" N",
        "GPS Longitude": "74° 2' 40.3\" W",
        "GPS Altitude": "10.5 m"
      }
    }
  }
  ```

#### 6.3 获取图片信息

- **接口**: `/admin/files/image/{id}`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.get(`http://localhost:8080/admin/files/image/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "path": "/uploads/abc123.jpg",
      "originalFilename": "example.jpg",
      "fileSize": 1048576,
      "width": 1920,
      "height": 1080,
      "contentType": "image/jpeg",
      "createTime": "2023-06-15T14:30:45",
      "cameraMake": "Canon",
      "cameraModel": "EOS 5D Mark IV",
      "shootTime": "2023-06-15T14:30:45",
      "exposureTime": "1/125 sec",
      "aperture": "f/2.8",
      "iso": 100,
      "focalLength": "50.0 mm",
      "latitude": 40.689306,
      "longitude": -74.044528,
      "altitude": 10.5
    }
  }
  ```

#### 6.4 分页获取图片列表

- **接口**: `/admin/files/images`
- **方法**: `GET`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **请求参数**:
  - `page`: 页码（默认值为1）
  - `size`: 每页条数（默认值为10）
- **调用示例**:
  ```javascript
  axios.get('http://localhost:8080/admin/files/images', {
    params: { page: 1, size: 10 },
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "list": [
        {
          "id": 1,
          "path": "/uploads/abc123.jpg",
          "originalFilename": "example.jpg",
          "fileSize": 1048576,
          "width": 1920,
          "height": 1080,
          "contentType": "image/jpeg",
          "createTime": "2023-06-15T14:30:45",
          "cameraMake": "Canon",
          "cameraModel": "EOS 5D Mark IV"
        }
      ],
      "total": 30,
      "page": 1,
      "size": 10
    }
  }
  ```

#### 6.5 删除图片

- **接口**: `/admin/files/image/{id}`
- **方法**: `DELETE`
- **认证**: 需要
- **请求头**:
  ```
  Authorization: Bearer {token}
  ```
- **调用示例**:
  ```javascript
  axios.delete(`http://localhost:8080/admin/files/image/1`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

## 注意事项

### 关于文章的summary字段

文章的summary字段（摘要）现已添加到数据库中，您可以在创建或更新文章时提供此字段。如果不提供，系统会自动从文章内容中生成摘要。自动生成的摘要会：

- 移除Markdown格式标记
- 提取文章前150个字符
- 在截断处添加省略号（...）

当更新文章内容时，如果不同时提供新的摘要，系统将自动基于更新后的内容重新生成摘要。

### 文件上传限制

- 图片上传仅支持JPG和PNG格式
- 文件大小不能超过5MB
- 上传成功后会返回文件的访问URL，可直接用于文章的coverImage字段

## 错误处理

所有API在发生错误时返回适当的HTTP状态码和统一格式的JSON响应:

| 状态码 | 说明                 |
| ------ | -------------------- |
| 200    | 请求成功             |
| 400    | 请求参数错误         |
| 401    | 未授权访问或令牌过期 |
| 403    | 禁止访问（无权限）   |
| 404    | 资源不存在           |
| 500    | 服务器错误           |

错误响应格式:

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
```

## 解决403错误的常见问题

如果您在调用需要认证的API时遇到403错误，请检查以下几点：

1. 确保在请求头中正确设置了Authorization: `Authorization: Bearer ${token}`（注意Bearer后有一个空格）
2. 确保token未过期（默认有效期为1小时）
3. 确保使用了正确格式的token（从登录接口获取的完整token）
4. 跨域请求时，确保OPTIONS预检请求能够正确处理

前端调用示例：

```javascript
// 登录获取token
async function login(username, password) {
  try {
    const response = await axios.post('http://localhost:8080/admin/login', {
      username,
      password
    });
  
    // 保存token到localStorage
    const token = response.data.data.token;
    localStorage.setItem('token', token);
    return token;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
}

// 使用token调用需要认证的API
async function fetchProtectedResource(url) {
  try {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
  
    // 在请求头中添加token
    const response = await axios.get(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  
    return response.data;
  } catch (error) {
    // 如果返回401或403，可能是token过期或无效
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // 重定向到登录页或提示用户重新登录
      console.error('认证失败，请重新登录');
    }
    throw error;
  }
}

// 检查认证状态
async function checkAuthStatus() {
  try {
    const result = await fetchProtectedResource('http://localhost:8080/admin/check-auth');
    return result.data.authenticated;
  } catch (error) {
    return false;
  }
}
```

## 数据模型

项目包含四个主要表：

- `users`: 管理员用户表
- `articles`: 文章表
- `comments`: 评论表
- `tools`: 工具链接表

详细结构参考 `schema.sql`文件。
