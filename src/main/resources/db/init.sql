-- 创建数据库
CREATE DATABASE IF NOT EXISTS infomanagement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE infomanagement;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    gender ENUM('male', 'female', 'other') DEFAULT 'other' COMMENT '性别',
    user_type ENUM('user', 'admin') DEFAULT 'user' COMMENT '用户类型',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 论坛帖子表
CREATE TABLE forum_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    views INT DEFAULT 0 COMMENT '浏览量',
    image_url VARCHAR(500) COMMENT '图片URL',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES users(id)
) COMMENT '论坛帖子表';

-- 学习活动表
CREATE TABLE learning_activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    views INT DEFAULT 0 COMMENT '浏览量',
    image_url VARCHAR(500) COMMENT '图片URL',
    reserved_count INT DEFAULT 0 COMMENT '已预约数量',
    max_capacity INT DEFAULT 100 COMMENT '最大容量',
    status ENUM('active', 'closed') DEFAULT 'active' COMMENT '状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES users(id)
) COMMENT '学习活动表';

-- 收藏表
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子/活动ID',
    post_type ENUM('forum', 'learning') NOT NULL COMMENT '类型',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_favorite (user_id, post_id, post_type)
) COMMENT '收藏表';

-- 预约表
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (activity_id) REFERENCES learning_activities(id),
    UNIQUE KEY unique_reservation (user_id, activity_id)
) COMMENT '预约表';

-- 商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    category VARCHAR(50) COMMENT '分类',
    image_url VARCHAR(500) COMMENT '图片URL',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '商品表';

-- 购物车表
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY unique_cart_item (user_id, product_id)
) COMMENT '购物车表';

-- 插入测试数据
-- 用户数据 - 使用明文密码
INSERT INTO users (username, password, nickname, email, user_type) VALUES 
('admin', 'admin123', '管理员', 'admin@example.com', 'admin'),
('test_user', 'test123', '测试用户', 'test@example.com', 'user');

-- 论坛帖子数据
INSERT INTO forum_posts (title, content, author_id, views, image_url) VALUES 
('这是第一个测试帖子', '这是测试帖子的内容，欢迎大家参与讨论。', 2, 120, 'https://picsum.photos/400/400?random=1'),
('技术分享：Spring Boot最佳实践', '在这篇文章中，我将分享一些Spring Boot开发的最佳实践...', 1, 89, 'https://picsum.photos/400/400?random=2'),
('前端开发心得', 'Vue.js开发过程中遇到的一些问题和解决方案...', 2, 156, 'https://picsum.photos/400/400?random=3');

-- 学习活动数据
INSERT INTO learning_activities (title, content, author_id, views, image_url, reserved_count, max_capacity) VALUES 
('摄影活动预约', '本次摄影活动将带你领略美丽的风景，学习专业的摄影技巧...', 1, 120, 'https://picsum.photos/400/400?random=1', 10, 50),
('编程技术分享会', 'Java开发技术分享，包括Spring Boot、MyBatis等技术栈...', 1, 89, 'https://picsum.photos/400/400?random=2', 25, 100),
('设计思维工作坊', '学习设计思维方法，提升产品设计能力...', 2, 67, 'https://picsum.photos/400/400?random=3', 15, 30);

-- 商品数据
INSERT INTO products (name, description, price, original_price, stock, category, image_url) VALUES 
('高级智能手表', '功能强大的智能手表，支持健康监测、运动追踪等功能', 1299.00, 1599.00, 100, 'electronics', 'https://picsum.photos/300/300?random=21'),
('无线蓝牙耳机', '高品质音效，降噪功能，长续航', 299.00, 399.00, 200, 'electronics', 'https://picsum.photos/300/300?random=22'),
('时尚背包', '大容量，多功能分层设计', 199.00, 299.00, 150, 'fashion', 'https://picsum.photos/300/300?random=23'),
('运动鞋', '舒适透气，适合各种运动', 399.00, 499.00, 80, 'sports', 'https://picsum.photos/300/300?random=24');