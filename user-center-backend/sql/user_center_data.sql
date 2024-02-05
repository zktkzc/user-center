/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3306
 Source Schema         : user_center

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 05/02/2024 20:06:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tagName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名称',
  `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
  `parentId` bigint NULL DEFAULT NULL COMMENT '父标签id',
  `isParent` tinyint NULL DEFAULT NULL COMMENT '是否为父标签，0-不是，1-是',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniIdx_tagName`(`tagName` ASC) USING BTREE,
  INDEX `idx_userId`(`userId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '队伍名称',
  `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `maxNum` int NOT NULL DEFAULT 1 COMMENT '最大人数',
  `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `userId` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '0-公开，1-私有，2-加密',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '队伍表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, '测试队伍', '测试队伍', 6, '2024-02-07 12:49:00', 3, 2, '1234', '2024-02-04 12:45:52', '2024-02-04 12:45:52', 0);
INSERT INTO `team` VALUES (2, '测试公开队伍', '测试公开队伍', 3, '2024-02-11 06:22:00', 3, 0, '', '2024-02-04 13:11:20', '2024-02-04 16:31:21', 1);
INSERT INTO `team` VALUES (4, 'tkzc00的队伍', 'tkzc00的队伍', 6, '2024-02-11 16:41:00', 2, 0, '', '2024-02-04 16:34:13', '2024-02-04 16:34:13', 0);
INSERT INTO `team` VALUES (5, 'tkzc的队伍', 'tkzc的队伍', 5, '2024-05-04 20:06:00', 3, 0, '', '2024-02-05 16:06:41', '2024-02-05 16:06:41', 0);
INSERT INTO `team` VALUES (6, 'tkzc的公开队伍', 'tkzc的公开队伍', 3, '2024-12-05 23:27:00', 3, 0, '', '2024-02-05 18:15:46', '2024-02-05 18:15:46', 0);
INSERT INTO `team` VALUES (7, 'tkzc00的加密队伍', 'tkzc00的加密队伍', 6, '2024-08-05 20:34:00', 2, 2, '1234', '2024-02-05 18:34:32', '2024-02-05 18:34:32', 0);
INSERT INTO `team` VALUES (8, 'tkzc00的私密队伍', 'tkzc00的私密队伍', 3, '2024-12-05 23:00:00', 2, 1, '', '2024-02-05 19:01:14', '2024-02-05 19:01:14', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` int NOT NULL DEFAULT 0 COMMENT '用户状态，0-正常',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  `userRole` int NOT NULL DEFAULT 0 COMMENT '角色，0-普通用户，1-管理员',
  `planetCode` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '星球编号',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签列表',
  `profile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 500009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'abc', '123', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '123456', '123', '456', 0, '2024-01-29 10:56:50', '2024-02-04 18:24:28', 0, 0, NULL, '[\"男\",\"Java\"]', NULL);
INSERT INTO `user` VALUES (2, 'tkzc00', 'tkzc00', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, 'd339e2a27c596c289461c5916709c1db', NULL, NULL, 0, '2024-01-29 15:51:22', '2024-02-04 18:24:28', 0, 1, NULL, '[\"Java\",\"C++\",\"Python\"]', NULL);
INSERT INTO `user` VALUES (3, 'tkzc1', 'tkzc', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, 'd339e2a27c596c289461c5916709c1db', '15912345678', 'tkzc@qq.com', 0, '2024-01-29 15:53:31', '2024-02-04 19:56:20', 0, 0, NULL, '[\"男\",\"Python\",\"Java\",\"C\",\"C++\",\"前端\",\"竞赛\",\"大一\",\"求职\",\"单身\"]', NULL);
INSERT INTO `user` VALUES (4, 'ysqg', 'ysqg', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 1, 'd339e2a27c596c289461c5916709c1db', NULL, NULL, 0, '2024-01-29 17:48:33', '2024-02-01 15:13:31', 0, 0, NULL, '[\"女\",\"大一\"]', NULL);
INSERT INTO `user` VALUES (5, 'ysqf', 'ysqf', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, 'd339e2a27c596c289461c5916709c1db', NULL, NULL, 0, '2024-01-30 12:14:51', '2024-02-04 18:24:28', 0, 0, NULL, '[\"Java\", \"男\", \"乒乓球\"]', NULL);
INSERT INTO `user` VALUES (7, 'admin', 'admin', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, 'd339e2a27c596c289461c5916709c1db', NULL, NULL, 0, '2024-01-30 14:37:25', '2024-02-04 18:24:28', 0, 1, '1', '[\"大一\", \"Java\", \"男\", \"羽毛球\"]', NULL);
INSERT INTO `user` VALUES (9, '假用户', 'fake1', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake1@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:24:28', 0, 0, '111111', '[\"Java\", \"女\", \"大二\"]', '这是一个假用户');
INSERT INTO `user` VALUES (10, '假用户', 'fake2', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake2@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:24:28', 0, 0, '111111', '[\"Python\", \"女\"]', '这是一个假用户');
INSERT INTO `user` VALUES (11, '假用户', 'fake3', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake3@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:25:37', 0, 0, '111111', '[\"女\", \"乒乓球\"]', '这是一个假用户');
INSERT INTO `user` VALUES (12, '假用户', 'fake4', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake4@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:25:37', 0, 0, '111111', '[\"C++\", \"男\", \"研一\"]', '这是一个假用户');
INSERT INTO `user` VALUES (13, '假用户', 'fake5', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake5@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:25:37', 0, 0, '111111', '[\"男\", \"大三\"]', '这是一个假用户');
INSERT INTO `user` VALUES (14, '假用户', 'fake6', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake6@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:25:37', 0, 0, '111111', '[\"单身\", \"求职\", \"Java\"]', '这是一个假用户');
INSERT INTO `user` VALUES (15, '假用户', 'fake7', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake7@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:26:11', 0, 0, '111111', '[\"竞赛\", \"女\", \"Python\"]', '这是一个假用户');
INSERT INTO `user` VALUES (16, '假用户', 'fake8', 'https://p.qqan.com/up/2020-12/16070652272519101.jpg', 0, '12345678', '13512345678', 'fake8@qq.com', 0, '2024-02-02 11:31:43', '2024-02-04 18:26:11', 0, 0, '111111', '[\"竞赛\", \"男\"]', '这是一个假用户');

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `teamId` bigint NULL DEFAULT NULL COMMENT '队伍ID',
  `joinTime` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户队伍关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_team
-- ----------------------------
INSERT INTO `user_team` VALUES (1, 3, 1, '2024-02-04 12:45:52', '2024-02-04 12:45:52', '2024-02-04 12:45:52', 0);
INSERT INTO `user_team` VALUES (2, 3, 2, '2024-02-04 13:11:20', '2024-02-04 13:11:20', '2024-02-04 16:31:21', 1);
INSERT INTO `user_team` VALUES (3, 3, 3, '2024-02-04 15:21:07', '2024-02-04 15:21:06', '2024-02-04 15:21:06', 0);
INSERT INTO `user_team` VALUES (4, 2, 4, '2024-02-04 16:34:14', '2024-02-04 16:34:13', '2024-02-04 16:34:13', 0);
INSERT INTO `user_team` VALUES (5, 3, 4, '2024-02-04 16:34:52', '2024-02-04 16:34:52', '2024-02-04 16:35:50', 1);
INSERT INTO `user_team` VALUES (6, 3, 5, '2024-02-05 16:06:42', '2024-02-05 16:06:41', '2024-02-05 16:06:41', 0);
INSERT INTO `user_team` VALUES (7, 3, 4, '2024-02-05 16:35:14', '2024-02-05 16:35:14', '2024-02-05 16:35:14', 0);
INSERT INTO `user_team` VALUES (8, 3, 6, '2024-02-05 18:15:47', '2024-02-05 18:15:46', '2024-02-05 18:15:46', 0);
INSERT INTO `user_team` VALUES (9, 2, 1, '2024-02-05 18:33:37', '2024-02-05 18:33:37', '2024-02-05 18:33:37', 0);
INSERT INTO `user_team` VALUES (10, 2, 7, '2024-02-05 18:34:32', '2024-02-05 18:34:32', '2024-02-05 18:34:32', 0);
INSERT INTO `user_team` VALUES (11, 2, 8, '2024-02-05 19:01:14', '2024-02-05 19:01:14', '2024-02-05 19:01:14', 0);

SET FOREIGN_KEY_CHECKS = 1;
