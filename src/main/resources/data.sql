-- 插入默认管理员用户，密码为明文"123456"
-- 注意：在生产环境中，不应使用明文密码，这仅用于开发测试
INSERT INTO users (username, password) 
VALUES ('admin', '123456')
ON DUPLICATE KEY UPDATE username=username; 

-- 初始化标签
INSERT INTO tags (name) VALUES ('技术') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO tags (name) VALUES ('生活') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO tags (name) VALUES ('旅行') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO tags (name) VALUES ('学习') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO tags (name) VALUES ('编程') ON DUPLICATE KEY UPDATE name = name;

-- 初始化工具
INSERT INTO tools (name, description, link) 
VALUES ('GitHub', 'GitHub是世界上最大的代码托管平台', 'https://github.com')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO tools (name, description, link) 
VALUES ('VS Code', 'Visual Studio Code是一款轻量级但功能强大的源代码编辑器', 'https://code.visualstudio.com')
ON DUPLICATE KEY UPDATE name = name; 