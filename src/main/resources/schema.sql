-- 按照正确顺序删除表（先删除有外键引用的表）
DROP TABLE IF EXISTS article_tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS tools;
DROP TABLE IF EXISTS image_info;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- 创建文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '文章摘要',
    content TEXT NOT NULL COMMENT '正文（Markdown）',
    author_id BIGINT NOT NULL COMMENT '作者用户ID',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态：draft|published',
    cover_image VARCHAR(255) COMMENT '封面图片URL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL COMMENT '发布时间，发布时填写'
);

-- 创建标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名'
);

-- 创建文章-标签关联表
CREATE TABLE IF NOT EXISTS article_tags (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY(article_id, tag_id),
    FOREIGN KEY(article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 创建评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT,
    name VARCHAR(50),
    email VARCHAR(100),
    content TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, approved, spam
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(article_id) REFERENCES articles(id) ON DELETE CASCADE
);

-- 创建工具链接表
CREATE TABLE IF NOT EXISTS tools (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    link VARCHAR(255) NOT NULL
);

-- 创建留言表
CREATE TABLE IF NOT EXISTS messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nickname VARCHAR(50) NOT NULL COMMENT '留言者昵称',
  content TEXT NOT NULL COMMENT '留言内容',
  contact_info VARCHAR(100) COMMENT '联系方式（邮箱/电话）',
  ip_address VARCHAR(50) COMMENT '留言者IP',
  user_agent VARCHAR(255) COMMENT '浏览器信息',
  status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending|replied|hidden',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  reply_content TEXT COMMENT '回复内容',
  reply_at TIMESTAMP COMMENT '回复时间',
  admin_id BIGINT COMMENT '回复管理员ID'
);

-- 创建图片信息表
CREATE TABLE IF NOT EXISTS image_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  path VARCHAR(255) NOT NULL COMMENT '图片存储路径',
  original_filename VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  file_size BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  width INT DEFAULT NULL COMMENT '图片宽度(像素)',
  height INT DEFAULT NULL COMMENT '图片高度(像素)',
  content_type VARCHAR(100) DEFAULT NULL COMMENT '文件类型',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  camera_make VARCHAR(100) DEFAULT NULL COMMENT '相机品牌',
  camera_model VARCHAR(100) DEFAULT NULL COMMENT '相机型号',
  shoot_time DATETIME DEFAULT NULL COMMENT '拍摄时间',
  exposure_time VARCHAR(50) DEFAULT NULL COMMENT '曝光时间',
  aperture VARCHAR(50) DEFAULT NULL COMMENT '光圈值',
  iso INT DEFAULT NULL COMMENT 'ISO感光度',
  focal_length VARCHAR(50) DEFAULT NULL COMMENT '焦距',
  latitude DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
  longitude DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
  altitude DECIMAL(10,2) DEFAULT NULL COMMENT '海拔',
  INDEX idx_path (path)
); 