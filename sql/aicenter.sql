/*
 Navicat Premium Data Transfer

 Source Server         : 5.79_mysql
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : 35.46.5.79:3306
 Source Schema         : yulo

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 21/10/2025 14:12:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aicenter
-- ----------------------------
DROP TABLE IF EXISTS `aicenter`;
CREATE TABLE `aicenter` (
  `camera_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `camera_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `camera_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lanes` text COLLATE utf8mb4_unicode_ci,
  `ai_function` text COLLATE utf8mb4_unicode_ci,
  `regions` text COLLATE utf8mb4_unicode_ci,
  `radar_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attributes` text COLLATE utf8mb4_unicode_ci,
  `src_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `media_worker` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `background_snap` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `camera_offset` int DEFAULT '0',
  PRIMARY KEY (`camera_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
