-- 创建数据库
CREATE DATABASE IF NOT EXISTS study DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE study;

-- 创建用户表
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(255) NOT NULL COMMENT '密码（BCrypt 加密）',
                        `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                        `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                        `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
                        `role` varchar(20) DEFAULT 'user' COMMENT '角色: user/admin',
                        `status` tinyint DEFAULT '1' COMMENT '状态: 0-禁用, 1-启用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据（密码: 123456）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
    ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin');