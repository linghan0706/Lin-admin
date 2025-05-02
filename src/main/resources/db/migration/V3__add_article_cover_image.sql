-- 添加文章表的封面图片字段
ALTER TABLE articles ADD COLUMN cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL'; 