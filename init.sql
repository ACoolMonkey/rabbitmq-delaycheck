/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 23/04/2020 23:48:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for message_content
-- ----------------------------
DROP TABLE IF EXISTS `message_content`;
CREATE TABLE `message_content`  (
  `msg_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_no` bigint(32) NOT NULL,
  `product_no` int(10) NOT NULL,
  `msg_status` int(10) NOT NULL COMMENT '(0,\"发送中\"),(1,\"mq的broker确认接受到消息\"),(2,\"没有对应交换机\"),(3,\"没有对应的路由\"),(4,\"消费端成功消费消息\")',
  `exchange` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `routing_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `err_cause` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `max_retry` int(10) NOT NULL,
  `current_retry` int(10) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `order_no` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `money` double(10, 2) NOT NULL,
  `product_no` int(10) NOT NULL,
  `order_status` int(10) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1572167801312 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info`  (
  `product_no` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `product_num` int(10) NOT NULL,
  PRIMARY KEY (`product_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_info
-- ----------------------------
INSERT INTO `product_info` VALUES (1, 'iPhone 8 Plus', 100);

SET FOREIGN_KEY_CHECKS = 1;
