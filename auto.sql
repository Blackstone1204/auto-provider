/*
Navicat MySQL Data Transfer

Source Server         : 我的库
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : auto

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-03-23 11:45:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `device`
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `serial` varchar(255) NOT NULL,
  `resolution` varchar(50) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `bp_port` int(11) DEFAULT '4722',
  `view_port` int(11) DEFAULT NULL,
  `mini_port` int(11) DEFAULT NULL,
  `touch_port` int(11) DEFAULT NULL,
  `node` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL,
  `sdk` varchar(20) DEFAULT NULL,
  `imsi` varchar(255) DEFAULT NULL,
  `imei` varchar(255) DEFAULT NULL,
  `abi` varchar(50) DEFAULT NULL,
  `has_get_root` int(11) DEFAULT NULL COMMENT '0:否；1：是',
  `insert_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `md5` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`serial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device
-- ----------------------------

-- ----------------------------
-- Table structure for `record`
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT,
  `record_name` varchar(50) DEFAULT NULL,
  `tip` varchar(200) DEFAULT NULL,
  `command_str` varchar(5000) DEFAULT NULL,
  `user_name` varchar(200) DEFAULT NULL,
  `serial` varchar(200) DEFAULT NULL,
  `screenshot_location` varchar(500) DEFAULT NULL,
  `method` int(11) NOT NULL COMMENT '录制方式 1 坐标 ',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of record
-- ----------------------------
INSERT INTO `record` VALUES ('154', '静态页面验证点录制', '', 'touch;230;163,touch;124;348,touch;126;351,touch;126;352,touch;26;149,touch;30;143,touch;34;151,touch;125;236,touch;223;34,touch;236;27,', '971406187@qq.com', null, 'E:/auto-provider/user-data\\971406187@qq.com\\pointer\\record\\2018-03-21_09-49-24', '0', '2018-03-21 09:51:08');
INSERT INTO `record` VALUES ('161', '汤姆猫测试', '购买页面右上角退出11111111111111购买页面右上角退出11111111111111', 'touch;237;159,touch;219;32,touch;242;27,', '971406187@qq.com', null, 'E:/auto-provider/user-data\\971406187@qq.com\\pointer\\record\\2018-03-21_11-09-08', '0', '2018-03-21 11:09:38');
INSERT INTO `record` VALUES ('166', '汤姆猫测试', '购买页面右上角退出22222购买页面右上角退出22222购买页面右上角退出22222购买页面右上角退出22222', 'touch;36;219,touch;235;18,touch;241;25,touch;105;60,touch;135;140,touch;118;238,touch;229;26,', '971406187@qq.com', null, 'E:/auto-provider/user-data\\971406187@qq.com\\pointer\\record\\2018-03-22_10-37-45', '0', '2018-03-22 10:38:43');
INSERT INTO `record` VALUES ('167', '汤姆猫测试', '测试计费 支付失败', 'touch;109;63,touch;126;128,touch;130;233,', '971406187@qq.com', null, 'E:/auto-provider/user-data\\971406187@qq.com\\pointer\\record\\2018-03-22_15-31-55', '0', '2018-03-22 15:32:51');

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '系统管理员');
INSERT INTO `role` VALUES ('2', '会员');
INSERT INTO `role` VALUES ('3', '游客');

-- ----------------------------
-- Table structure for `sub_task`
-- ----------------------------
DROP TABLE IF EXISTS `sub_task`;
CREATE TABLE `sub_task` (
  `sub_tag` varchar(255) NOT NULL,
  `task_tag` varchar(255) NOT NULL,
  `serial` varchar(255) NOT NULL,
  `model` varchar(255) DEFAULT NULL,
  `script_name` varchar(255) DEFAULT NULL,
  `result` varchar(20) DEFAULT NULL,
  `device_log` varchar(255) DEFAULT NULL,
  `cpu_data` varchar(255) DEFAULT NULL,
  `memory_data` varchar(255) DEFAULT NULL,
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sub_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sub_task
-- ----------------------------
INSERT INTO `sub_task` VALUES ('2018-01-09_11-49-29_15', '2018-01-09_11-49-29', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-09 11:49:29', '2018-01-09 11:52:38');
INSERT INTO `sub_task` VALUES ('2018-01-09_11-49-29_49', '2018-01-09_11-49-29', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-09 11:49:29', '2018-01-09 11:52:03');
INSERT INTO `sub_task` VALUES ('2018-01-09_11-54-11_82', '2018-01-09_11-54-11', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-09 11:54:11', '2018-01-09 11:54:42');
INSERT INTO `sub_task` VALUES ('2018-01-09_11-54-11_86', '2018-01-09_11-54-11', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-09 11:54:11', '2018-01-09 11:55:10');
INSERT INTO `sub_task` VALUES ('2018-01-09_13-23-40_48', '2018-01-09_13-23-40', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-09 13:23:40', '2018-01-09 13:24:15');
INSERT INTO `sub_task` VALUES ('2018-01-09_13-23-40_87', '2018-01-09_13-23-40', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-09 13:23:40', '2018-01-09 13:24:45');
INSERT INTO `sub_task` VALUES ('2018-01-10_11-52-46_57', '2018-01-10_11-52-46', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 11:52:46', '2018-01-10 11:54:17');
INSERT INTO `sub_task` VALUES ('2018-01-10_11-52-46_62', '2018-01-10_11-52-46', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 11:52:46', '2018-01-10 11:53:45');
INSERT INTO `sub_task` VALUES ('2018-01-10_13-52-02_41', '2018-01-10_13-52-02', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 13:52:02', '2018-01-10 13:53:06');
INSERT INTO `sub_task` VALUES ('2018-01-10_13-52-02_73', '2018-01-10_13-52-02', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 13:52:02', '2018-01-10 13:53:39');
INSERT INTO `sub_task` VALUES ('2018-01-10_13-59-03_34', '2018-01-10_13-59-03', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 13:59:03', '2018-01-10 13:59:28');
INSERT INTO `sub_task` VALUES ('2018-01-10_13-59-03_67', '2018-01-10_13-59-03', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 13:59:03', '2018-01-10 13:59:49');
INSERT INTO `sub_task` VALUES ('2018-01-10_14-04-09_47', '2018-01-10_14-04-09', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 14:04:09', '2018-01-10 14:04:32');
INSERT INTO `sub_task` VALUES ('2018-01-10_14-04-09_53', '2018-01-10_14-04-09', '216a84c4', 'MI 4LTE', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 14:04:09', '2018-01-10 14:04:45');
INSERT INTO `sub_task` VALUES ('2018-01-10_14-04-09_86', '2018-01-10_14-04-09', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 14:04:09', '2018-01-10 14:04:55');
INSERT INTO `sub_task` VALUES ('2018-01-10_14-04-09_99', '2018-01-10_14-04-09', '216a84c4', 'MI 4LTE', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 14:04:09', '2018-01-10 14:05:28');
INSERT INTO `sub_task` VALUES ('2018-01-10_16-06-30_25', '2018-01-10_16-06-30', '216a84c4', 'MI 4LTE', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 16:06:30', '2018-01-10 16:08:14');
INSERT INTO `sub_task` VALUES ('2018-01-10_16-06-30_77', '2018-01-10_16-06-30', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class', 'pass', null, null, null, '2018-01-10 16:06:30', '2018-01-10 16:07:24');
INSERT INTO `sub_task` VALUES ('2018-01-10_16-06-30_78', '2018-01-10_16-06-30', '6eb3d6d5', 'SM-G5308W', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 16:06:30', '2018-01-10 16:07:52');
INSERT INTO `sub_task` VALUES ('2018-01-10_16-06-30_97', '2018-01-10_16-06-30', '216a84c4', 'MI 4LTE', 'E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\SingleClick.class', 'pass', null, null, null, '2018-01-10 16:06:30', '2018-01-10 16:08:39');

-- ----------------------------
-- Table structure for `task`
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `task_tag` varchar(255) NOT NULL,
  `task_name` varchar(500) NOT NULL COMMENT '功能简介',
  `user_name` varchar(255) NOT NULL COMMENT '用户名',
  `task_dir` varchar(255) NOT NULL COMMENT '任务对应后台目录',
  `device_serial` varchar(255) NOT NULL COMMENT '测试手机 多个用分号隔开',
  `upload_name` varchar(255) NOT NULL COMMENT '上传包名',
  `script_name` varchar(500) NOT NULL COMMENT '脚本路径 多个分号隔开',
  `result` varchar(255) NOT NULL COMMENT '执行结果',
  `apk_name` varchar(255) NOT NULL COMMENT '使用的apk',
  `apk_name2` varchar(255) NOT NULL COMMENT '上传时apk名称',
  `model` varchar(255) NOT NULL,
  `pkg_name` varchar(255) NOT NULL,
  `method` varchar(20) NOT NULL COMMENT '测试类型',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES ('2018-01-09_11-49-29', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-09_11-49-19', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-09_11-39-39.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-09 11:52:40');
INSERT INTO `task` VALUES ('2018-01-09_11-54-11', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-09_11-49-19', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-09_11-54-10.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-09 11:55:12');
INSERT INTO `task` VALUES ('2018-01-09_13-23-40', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-09_11-49-19', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-09_11-49-19\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-09_13-23-39.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-09 13:24:46');
INSERT INTO `task` VALUES ('2018-01-10_11-52-46', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-10_11-52-32', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-10_11-52-43.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-10 11:54:18');
INSERT INTO `task` VALUES ('2018-01-10_13-52-02', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-10_11-52-32', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-10_13-51-59.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-10 13:53:42');
INSERT INTO `task` VALUES ('2018-01-10_13-59-03', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-10_11-52-32', ';6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class', 'pass', '2018-01-10_13-59-02.apk', 'DemoTest.apk', ';SM-G5308W', 'unknown', 'appium', '2018-01-10 13:59:50');
INSERT INTO `task` VALUES ('2018-01-10_14-04-09', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-10_11-52-32', ';216a84c4;6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\SingleClick.class', 'pass', '2018-01-10_14-04-05.apk', 'DemoTest.apk', ';MI 4LTE;SM-G5308W', 'unknown', 'appium', '2018-01-10 14:05:30');
INSERT INTO `task` VALUES ('2018-01-10_16-06-30', 'appium测试', '971406187@qq.com', 'E:/auto-web/user-data\\971406187@qq.com\\2018-01-10_11-52-32', ';216a84c4;6eb3d6d5', 'unknown', ';E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\LongClickTest.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\6eb3d6d5\\test\\script\\SingleClick.class;E:\\auto-web\\user-data\\971406187@qq.com\\appium\\2018-01-10_11-52-32\\216a84c4\\test\\script\\SingleClick.class', 'pass', '2018-01-10_16-06-24.apk', 'DemoTest.apk', ';MI 4LTE;SM-G5308W', 'unknown', 'appium', '2018-01-10 16:08:44');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` varchar(11) NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('971406187@qq.com', 'admin', '1');
