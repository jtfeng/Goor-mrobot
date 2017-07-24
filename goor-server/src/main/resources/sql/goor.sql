/*
Navicat MySQL Data Transfer

Source Server         : 172.16.0.15
Source Server Version : 50624
Source Host           : 172.16.0.15:3306
Source Database       : goor

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-07-12 16:29:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for A_MAP_INFO
-- ----------------------------
DROP TABLE IF EXISTS `A_MAP_INFO`;
CREATE TABLE `A_MAP_INFO` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `MAP_NAME` varchar(50) DEFAULT NULL COMMENT '地图名',
  `SCENE_NAME` varchar(50) DEFAULT NULL COMMENT '场景名',
  `PNG_IMAGE_LOCAL_PATH` varchar(100) DEFAULT NULL COMMENT '地图png文件本地地址',
  `ROS` varchar(400) DEFAULT NULL,
  `MAP_ZIP_ID` INT DEFAULT NULL,
  `MAP_ALIAS` varchar(50) DEFAULT NULL,
  `DEVICE_ID` varchar(200) DEFAULT NULL,
  `PNG_DESIGNED` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for A_MAP_POINT
-- ----------------------------
DROP TABLE IF EXISTS `A_MAP_POINT`;
CREATE TABLE `A_MAP_POINT` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `POINT_NAME` varchar(256) NOT NULL COMMENT '唯一标识符',
  `POINT_ALIAS` varchar(256) NOT NULL COMMENT '显示名称',
  `SCENE_NAME` varchar(256) DEFAULT NULL COMMENT '地图场景名',
  `MAP_NAME` varchar(256) DEFAULT NULL COMMENT '地图名',
  `POINT_LEVEL` int(11) DEFAULT NULL COMMENT '导航点等级',
  `X` double(20,15) DEFAULT NULL COMMENT '坐标X',
  `Y` double(20,15) DEFAULT NULL COMMENT '坐标Y',
  `TH` double(20,15) DEFAULT NULL COMMENT '坐标旋转角度',
  `MAP_POINT_TYPE_ID` int(11) DEFAULT NULL COMMENT '点类型索引',
  `IC_POINT_TYPE` varchar(20) DEFAULT NULL COMMENT '工控点类型',
  `CLOUD_POINT_TYPE_ID` int(11) DEFAULT NULL COMMENT '云端点类型索引',


  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for A_MAP_ZIP
-- ----------------------------
DROP TABLE IF EXISTS `A_MAP_ZIP`;
CREATE TABLE `A_MAP_ZIP` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `MAP_NAME` varchar(50) DEFAULT NULL COMMENT '地图名称',
  `SCENE_NAME` varchar(50) NOT NULL COMMENT '场景名称',
  `FILE_PATH` varchar(256) DEFAULT NULL COMMENT '文件路径',
  `VERSION` varchar(50) DEFAULT NULL COMMENT '版本号',
  `FILE_NAME` varchar(50) DEFAULT NULL COMMENT '文件名',
  `MD5` varchar(256) DEFAULT NULL COMMENT 'MD5',
  `DEVICE_ID` varchar(256) DEFAULT NULL COMMENT '上传地图的设备编号',
  `FILE_UPLOAD_ID` bigint(20) DEFAULT NULL COMMENT '文件上传编号',
  `ROBOT_PATH` varchar(256) DEFAULT NULL COMMENT '机器人上存储路径',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_MAP_ZIP
-- ----------------------------
INSERT INTO `A_MAP_ZIP` VALUES ('75', '100', null, '2017-07-06 09:34:25', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-34-24.zip', null, 'maps_2017-07-06_09-34-24.zip', null, 'cookyPlus1301', '82', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('76', '100', null, '2017-07-06 09:34:43', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-34-43.zip', null, 'maps_2017-07-06_09-34-43.zip', null, 'cookyPlus1301', '83', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('77', '100', null, '2017-07-06 09:45:00', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-44-59.zip', null, 'maps_2017-07-06_09-44-59.zip', null, 'cookyPlus1301', '84', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('78', '100', null, '2017-07-06 10:43:25', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_10-43-25.zip', null, 'maps_2017-07-06_10-43-25.zip', null, 'cookyPlus1301', '85', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('79', '100', null, '2017-07-08 11:34:55', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-08_11-34-51.zip', null, 'maps_2017-07-08_11-34-51.zip', null, 'cookyPlus1301', '86', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('80', '100', null, '2017-07-08 11:35:21', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-08_11-35-20.zip', null, 'maps_2017-07-08_11-35-20.zip', null, 'cookyPlus1301', '87', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('81', '100', null, '2017-07-12 09:51:23', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-51-22.zip', null, 'maps_2017-07-12_09-51-22.zip', null, 'SNabc0010', '88', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('82', '100', null, '2017-07-12 09:53:35', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-53-34.zip', null, 'maps_2017-07-12_09-53-34.zip', null, 'SNabc0010', '89', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('83', '100', null, '2017-07-12 09:55:05', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-55-03.zip', null, 'maps_2017-07-12_09-55-03.zip', null, 'SNabc0010', '90', 'E:\\share\\map_server');
INSERT INTO `A_MAP_ZIP` VALUES ('84', '100', null, '2017-07-12 09:55:51', null, 'agv,cloud,example,', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-55-50.zip', null, 'maps_2017-07-12_09-55-50.zip', null, 'SNabc0010', '91', 'E:\\share\\map_server');

-- ----------------------------
-- Table structure for A_STATION
-- ----------------------------
DROP TABLE IF EXISTS `A_STATION`;
CREATE TABLE `A_STATION` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `NAME` varchar(256) NOT NULL COMMENT '站名',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `STATION_TYPE_ID` int(11) DEFAULT NULL COMMENT '站类型索引',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT 'ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT 'ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_STATION
-- ----------------------------
INSERT INTO `A_STATION` VALUES ('1', '二楼202病房', '', '1', '101', '100', '2017-07-05 14:08:14');
INSERT INTO `A_STATION` VALUES ('2', '四楼牙科a', '', '3', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('4', '三楼牙科', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('5', '二楼牙科', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('6', '一楼牙科', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('7', '四楼污物发送处', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('8', '三楼洁物发送处', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('9', '二楼核磁共振室', '', '3', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('13', '一楼心电图室', '', '1', '102', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('14', '四楼401病房a', null, '1', '102', '100', '2017-06-30 15:41:51');
INSERT INTO `A_STATION` VALUES ('15', '三楼301病房a', null, '1', '101', '100', '2017-07-01 18:09:56');
INSERT INTO `A_STATION` VALUES ('16', '二楼201病房', null, '1', '101', '100', '2017-07-05 14:08:14');
INSERT INTO `A_STATION` VALUES ('17', 'ceshi1', '', '1', '100', '100', '2017-07-06 16:10:49');
INSERT INTO `A_STATION` VALUES ('18', 'ceshi2', 'ceshi2备注', '2', '100', '100', '2017-07-06 16:22:36');
INSERT INTO `A_STATION` VALUES ('19', 'ceshi3', '', '1', '100', '100', '2017-07-06 16:26:03');
INSERT INTO `A_STATION` VALUES ('20', 'ceshi4', 'ss', '1', '100', '100', '2017-07-06 16:30:36');

-- ----------------------------
-- Table structure for A_STATION_MAP_POINT_XREF
-- ----------------------------
DROP TABLE IF EXISTS `A_STATION_MAP_POINT_XREF`;
CREATE TABLE `A_STATION_MAP_POINT_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `MAP_POINT_ID` bigint(20) NOT NULL COMMENT '点索引',
  `STATION_ID` bigint(20) NOT NULL COMMENT '站索引',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_STATION_MAP_POINT_XREF
-- ----------------------------
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('26', '19', '17');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('27', '33', '17');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('28', '34', '17');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('29', '22', '18');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('30', '27', '18');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('31', '25', '19');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('33', '24', '20');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('34', '25', '20');

-- ----------------------------
-- Table structure for AC_MENU
-- ----------------------------
DROP TABLE IF EXISTS `AC_MENU`;
CREATE TABLE `AC_MENU` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `KEY` varchar(30) DEFAULT NULL COMMENT '英文名',
  `NAME` varchar(30) DEFAULT NULL COMMENT '名称',
  `ICON` varchar(30) DEFAULT NULL COMMENT '图标样式',
  `PARENT_ID` int(11) DEFAULT NULL COMMENT '父ID',
  `URL` varchar(50) DEFAULT NULL COMMENT '菜单跳转地址',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_MENU
-- ----------------------------
INSERT INTO `AC_MENU` VALUES ('18', 'dashboard', '控制台', 'laptop', null, '');
INSERT INTO `AC_MENU` VALUES ('19', 'dispatch', '调度任务', 'laptop', null, 'dispatch');
INSERT INTO `AC_MENU` VALUES ('20', 'assets', '资产管理', 'laptop', null, null);
INSERT INTO `AC_MENU` VALUES ('21', 'robot', '机器人', 'laptop', '20', 'assets/robot');
INSERT INTO `AC_MENU` VALUES ('22', 'shelf', '货架', 'laptop', '20', 'assets/shelf');
INSERT INTO `AC_MENU` VALUES ('23', 'remote', '远程监控', 'laptop', null, null);
INSERT INTO `AC_MENU` VALUES ('24', 'monitor', '监测', '', '23', 'remote/monitor');
INSERT INTO `AC_MENU` VALUES ('25', 'upgrade', '升级', null, '23', 'remote/update');
INSERT INTO `AC_MENU` VALUES ('26', 'resource', '资源', null, '23', 'remote/resource');
INSERT INTO `AC_MENU` VALUES ('27', 'map', '地图管理', 'laptop', null, 'map');
INSERT INTO `AC_MENU` VALUES ('28', 'account', '用户中心', null, null, null);
INSERT INTO `AC_MENU` VALUES ('29', 'user', '用户列表', null, '28', 'account/user');
INSERT INTO `AC_MENU` VALUES ('30', 'group', '用户组', null, '28', 'account/group');
INSERT INTO `AC_MENU` VALUES ('31', 'area', '区域', 'laptop', null, null);
INSERT INTO `AC_MENU` VALUES ('32', 'station', '站管理', null, '31', 'area/station');
INSERT INTO `AC_MENU` VALUES ('33', 'point', '导航目标点管理', null, '31', 'area/point');
INSERT INTO `AC_MENU` VALUES ('34', 'log', '日志收集', 'laptop', null, 'log');

-- ----------------------------
-- Table structure for AC_PERMISSION
-- ----------------------------
DROP TABLE IF EXISTS `AC_PERMISSION`;
CREATE TABLE `AC_PERMISSION` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MENU_ID` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`ID`),
  KEY `MENU_ID` (`MENU_ID`),
  CONSTRAINT `AC_PERMISSION_ibfk_1` FOREIGN KEY (`MENU_ID`) REFERENCES `AC_MENU` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_PERMISSION
-- ----------------------------
INSERT INTO `AC_PERMISSION` VALUES ('7', '18');
INSERT INTO `AC_PERMISSION` VALUES ('1', '19');
INSERT INTO `AC_PERMISSION` VALUES ('2', '21');
INSERT INTO `AC_PERMISSION` VALUES ('3', '22');
INSERT INTO `AC_PERMISSION` VALUES ('4', '24');
INSERT INTO `AC_PERMISSION` VALUES ('5', '25');
INSERT INTO `AC_PERMISSION` VALUES ('6', '26');
INSERT INTO `AC_PERMISSION` VALUES ('8', '27');
INSERT INTO `AC_PERMISSION` VALUES ('9', '29');
INSERT INTO `AC_PERMISSION` VALUES ('10', '30');
INSERT INTO `AC_PERMISSION` VALUES ('11', '32');
INSERT INTO `AC_PERMISSION` VALUES ('12', '33');
INSERT INTO `AC_PERMISSION` VALUES ('13', '34');

-- ----------------------------
-- Table structure for AC_ROLE
-- ----------------------------
DROP TABLE IF EXISTS `AC_ROLE`;
CREATE TABLE `AC_ROLE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CN_NAME` varchar(10) DEFAULT NULL COMMENT '角色中文名称',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT 'ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_ROLE
-- ----------------------------
INSERT INTO `AC_ROLE` VALUES ('1', '超级管理员', '100', '1', '2017-06-29 14:38:37');
INSERT INTO `AC_ROLE` VALUES ('2', '医院管理员', '100', '1', '2017-06-29 14:39:10');
INSERT INTO `AC_ROLE` VALUES ('3', '站管理员', '100', '1', '2017-06-29 14:39:19');

-- ----------------------------
-- Table structure for AC_ROLE_PERMISSION_XREF
-- ----------------------------
DROP TABLE IF EXISTS `AC_ROLE_PERMISSION_XREF`;
CREATE TABLE `AC_ROLE_PERMISSION_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_ID` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `PERMISSION_ID` bigint(20) DEFAULT NULL COMMENT '权限ID',
  PRIMARY KEY (`ID`),
  KEY `AC_ROLE_PERMISSION_XREF_ibfk_2` (`PERMISSION_ID`),
  KEY `AC_ROLE_PERMISSION_XREF_ibfk_1` (`ROLE_ID`),
  CONSTRAINT `AC_ROLE_PERMISSION_XREF_ibfk_1` FOREIGN KEY (`ROLE_ID`) REFERENCES `AC_ROLE` (`ID`),
  CONSTRAINT `AC_ROLE_PERMISSION_XREF_ibfk_2` FOREIGN KEY (`PERMISSION_ID`) REFERENCES `AC_PERMISSION` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_ROLE_PERMISSION_XREF
-- ----------------------------

-- ----------------------------
-- Table structure for AC_STATION_ROBOT_XREF
-- ----------------------------
DROP TABLE IF EXISTS `AC_STATION_ROBOT_XREF`;
CREATE TABLE `AC_STATION_ROBOT_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `STATION_ID` bigint(20) DEFAULT NULL COMMENT '站ID',
  `ROBOT_ID` bigint(20) DEFAULT NULL COMMENT '机器人ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_STATION_ROBOT_XREF
-- ----------------------------
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('13', '1', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('14', '2', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('15', '4', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('16', '5', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('17', '6', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('18', '7', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('19', '8', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('20', '9', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('21', '13', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('22', '14', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('23', '15', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('24', '16', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('25', '17', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('26', '18', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('27', '19', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('28', '20', '318');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('29', '20', '314');
INSERT INTO `AC_STATION_ROBOT_XREF` VALUES ('30', '20', '315');

-- ----------------------------
-- Table structure for AC_USER
-- ----------------------------
DROP TABLE IF EXISTS `AC_USER`;
CREATE TABLE `AC_USER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_NAME` varchar(30) DEFAULT NULL COMMENT '用户名',
  `PASSWORD` varchar(250) DEFAULT NULL COMMENT '密码',
  `ACTIVATED` bit(1) DEFAULT b'1' COMMENT '是否有效',
  `DIRECT_LOGIN_KEY` int(4) DEFAULT NULL COMMENT '快捷登陆口令',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT 'ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_USER
-- ----------------------------
INSERT INTO `AC_USER` VALUES ('53', 'ray', '123456', '', null, '100', '1', '2017-06-27 10:59:28');
INSERT INTO `AC_USER` VALUES ('54', 'test', '123456', '\0', '1234', '101', '1', '2017-06-27 10:59:31');
INSERT INTO `AC_USER` VALUES ('55', 'test1', '123456', '\0', null, '101', null, null);
INSERT INTO `AC_USER` VALUES ('56', 'test2', '123456', '', null, '100', null, '2017-06-27 17:14:57');
INSERT INTO `AC_USER` VALUES ('57', 'test3', '123456', '', null, '100', null, '2017-06-27 17:14:59');
INSERT INTO `AC_USER` VALUES ('58', 'test4', '123456', '', null, '100', null, '2017-06-27 17:15:00');
INSERT INTO `AC_USER` VALUES ('59', 'test5', '123456', '', null, '100', null, '2017-06-27 17:15:00');
INSERT INTO `AC_USER` VALUES ('60', 'test6', '123456', '', null, '100', null, '2017-06-27 17:15:00');
INSERT INTO `AC_USER` VALUES ('61', 'test7', '123456', '', null, '100', null, '2017-06-27 17:15:01');
INSERT INTO `AC_USER` VALUES ('62', 'test8', '123456', '', null, '100', null, '2017-06-27 17:15:01');
INSERT INTO `AC_USER` VALUES ('63', 'test9', '123456', '', null, '100', null, '2017-06-27 17:15:01');
INSERT INTO `AC_USER` VALUES ('64', 'test10', '123456', '', null, '100', null, '2017-06-27 17:15:01');
INSERT INTO `AC_USER` VALUES ('65', 'test11', '123456', '', null, '100', null, '2017-06-27 17:15:01');
INSERT INTO `AC_USER` VALUES ('66', 'test12', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('67', 'test13', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('68', 'test14', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('69', 'test15', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('70', 'test16', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('71', 'test17', '123456', '', null, '100', null, '2017-06-27 17:15:02');
INSERT INTO `AC_USER` VALUES ('72', 'test18', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('73', 'test19', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('74', 'test20', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('75', 'test21', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('76', 'test22', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('77', 'test23', '123456', '', null, '100', null, '2017-06-27 17:15:03');
INSERT INTO `AC_USER` VALUES ('78', 'test24', '123456', '', null, '100', null, '2017-06-27 17:15:04');
INSERT INTO `AC_USER` VALUES ('79', 'test25', '123456', '', null, '100', null, '2017-06-27 18:03:17');
INSERT INTO `AC_USER` VALUES ('80', 'test26', '123456', '', null, '100', null, '2017-06-27 18:03:34');
INSERT INTO `AC_USER` VALUES ('81', 'test30', '123456', '', '4321', '102', null, '2017-06-28 11:49:45');
INSERT INTO `AC_USER` VALUES ('82', 'test39', '123456', '', null, '100', null, '2017-06-28 17:02:30');
INSERT INTO `AC_USER` VALUES ('86', 'ray123', '123456', '\0', null, '100', null, '2017-06-29 14:41:34');
INSERT INTO `AC_USER` VALUES ('87', 'ray1234', '123456', '', null, '100', null, '2017-06-29 14:42:56');
INSERT INTO `AC_USER` VALUES ('88', 'ray12345', '123456', '', null, '100', null, '2017-06-29 14:43:42');
INSERT INTO `AC_USER` VALUES ('90', 'ray12', '123456', '', null, '100', null, '2017-06-29 14:48:04');
INSERT INTO `AC_USER` VALUES ('92', 'ray2', '123456', '', null, '100', null, '2017-06-29 14:55:04');
INSERT INTO `AC_USER` VALUES ('93', 'ray3', '123456', '', null, '100', '1', '2017-06-29 15:03:01');
INSERT INTO `AC_USER` VALUES ('94', 'bianxingjinggang', '123456', '', null, '100', '1', '2017-06-30 14:01:44');
INSERT INTO `AC_USER` VALUES ('95', 'bianxingjinggang1', '123456', '\0', null, '100', '1', '2017-06-30 15:01:29');
INSERT INTO `AC_USER` VALUES ('96', 'hero1', '123456', '', null, '100', '1', '2017-06-30 16:14:38');
INSERT INTO `AC_USER` VALUES ('97', 'hero2', '123456', '', '2569', '100', '1', '2017-06-30 16:15:56');
INSERT INTO `AC_USER` VALUES ('98', 'ray20170701', '123456', '', '9875', '100', '1', '2017-07-01 14:06:43');
INSERT INTO `AC_USER` VALUES ('99', 'jack', '123456', '', '2598', '100', '1', '2017-07-01 16:20:33');
INSERT INTO `AC_USER` VALUES ('100', 'iverson', '123456', '', null, '100', '1', '2017-07-01 16:24:49');
INSERT INTO `AC_USER` VALUES ('101', 'iverson1', '123456', '', '6987', '100', '1', '2017-07-01 16:28:45');
INSERT INTO `AC_USER` VALUES ('102', 'iverson4', '123456', '', null, '100', '1', '2017-07-03 21:07:26');
INSERT INTO `AC_USER` VALUES ('103', 'iverson3', '123456', '', null, '100', '1', '2017-07-03 21:39:19');
INSERT INTO `AC_USER` VALUES ('104', 'iverson9', '123456', '', null, '102', '1', '2017-07-04 15:28:08');
INSERT INTO `AC_USER` VALUES ('105', 'cehi1', '123456', '', '1245', '101', '1', '2017-07-05 16:39:24');
INSERT INTO `AC_USER` VALUES ('106', 'number', '123456', '', null, '100', '1', '2017-07-05 18:32:07');
INSERT INTO `AC_USER` VALUES ('107', 'ceshi1', '123456', '', '1111', '100', '1', '2017-07-05 18:53:41');
INSERT INTO `AC_USER` VALUES ('108', 'fsdf', 'sdfsfsd', '', '1212', '100', '1', '2017-07-05 20:43:58');
INSERT INTO `AC_USER` VALUES ('109', 'ceshi2', '123456', '', '9998', '100', '1', '2017-07-05 22:05:30');
INSERT INTO `AC_USER` VALUES ('110', 'ceshi3', '123456', '', '8787', '100', '1', '2017-07-05 22:09:57');
INSERT INTO `AC_USER` VALUES ('111', 'bigman', '123456', '', '1234', '100', '1', '2017-07-06 19:59:20');
INSERT INTO `AC_USER` VALUES ('112', 'fall', '123456', '', '5987', '100', '1', '2017-07-11 16:32:13');
INSERT INTO `AC_USER` VALUES ('113', 'fallout3', '123456', '', null, '100', '1', '2017-07-11 16:46:40');
INSERT INTO `AC_USER` VALUES ('114', 'fallout4', '123456', '', null, '100', '1', '2017-07-11 16:51:37');

-- ----------------------------
-- Table structure for AC_USER_ROLE_XREF
-- ----------------------------
DROP TABLE IF EXISTS `AC_USER_ROLE_XREF`;
CREATE TABLE `AC_USER_ROLE_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `ROLE_ID` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`ID`),
  KEY `AC_USER_ROLE_ibfk_1` (`USER_ID`),
  KEY `AC_USER_ROLE_ibfk_2` (`ROLE_ID`),
  CONSTRAINT `AC_USER_ROLE_XREF_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `AC_USER` (`ID`),
  CONSTRAINT `AC_USER_ROLE_XREF_ibfk_2` FOREIGN KEY (`ROLE_ID`) REFERENCES `AC_ROLE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_USER_ROLE_XREF
-- ----------------------------
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('18', '93', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('19', '94', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('20', '95', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('21', '96', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('22', '97', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('23', '98', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('24', '99', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('25', '100', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('26', '101', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('27', '102', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('28', '103', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('29', '104', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('30', '53', '1');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('31', '81', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('32', '105', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('33', '106', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('34', '107', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('35', '108', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('36', '109', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('37', '110', '3');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('38', '111', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('39', '112', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('40', '113', '2');
INSERT INTO `AC_USER_ROLE_XREF` VALUES ('41', '114', '2');

-- ----------------------------
-- Table structure for AC_USER_STATION_XREF
-- ----------------------------
DROP TABLE IF EXISTS `AC_USER_STATION_XREF`;
CREATE TABLE `AC_USER_STATION_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `STATION_ID` bigint(20) DEFAULT NULL COMMENT '站ID',
  PRIMARY KEY (`ID`),
  KEY `STATION_ID` (`STATION_ID`),
  KEY `USER_ID` (`USER_ID`),
  CONSTRAINT `AC_USER_STATION_XREF_ibfk_2` FOREIGN KEY (`STATION_ID`) REFERENCES `A_STATION` (`ID`),
  CONSTRAINT `AC_USER_STATION_XREF_ibfk_3` FOREIGN KEY (`USER_ID`) REFERENCES `AC_USER` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_USER_STATION_XREF
-- ----------------------------
INSERT INTO `AC_USER_STATION_XREF` VALUES ('4', '93', '2');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('25', '95', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('26', '95', '13');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('27', '95', '14');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('28', '96', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('29', '96', '13');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('30', '96', '14');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('31', '97', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('32', '97', '13');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('33', '97', '14');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('34', '101', '7');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('35', '101', '8');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('36', '101', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('45', '102', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('46', '102', '13');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('47', '102', '14');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('53', '104', '14');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('54', '104', '13');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('70', '105', '15');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('71', '105', '16');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('74', '108', '8');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('75', '108', '5');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('76', '108', '6');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('78', '109', '5');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('79', '109', '7');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('80', '94', '8');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('81', '94', '4');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('82', '94', '7');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('83', '94', '9');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('92', '110', '6');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('93', '114', '2');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('94', '53', '2');

-- ----------------------------
-- Table structure for APP_CONFIG
-- ----------------------------
DROP TABLE IF EXISTS `APP_CONFIG`;
CREATE TABLE `APP_CONFIG` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MPUSH_PUBLICKEY` varchar(256) NOT NULL,
  `MPUSH_ALLOCSERVER` varchar(256) NOT NULL,
  `MPUSH_PUSHSERVER` varchar(256) NOT NULL,
  `MPUSH_DEVICEID` varchar(256) NOT NULL,
  `MPUSH_OSNAME` varchar(256) NOT NULL,
  `MPUSH_OSVERSION` varchar(256) NOT NULL,
  `MPUSH_CLIENTVERSION` varchar(256) NOT NULL,
  `MPUSH_USERID` varchar(256) NOT NULL,
  `MPUSH_TAGS` varchar(256) NOT NULL,
  `MPUSH_SESSIONSTORAGEDIR` varchar(256) NOT NULL,
  `ROS_PATH` varchar(256) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of APP_CONFIG
-- ----------------------------
INSERT INTO `APP_CONFIG` VALUES ('1', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB', 'http://push.myee7.com/allocServer/', 'http://push.myee7.com/pushServer/api/admin/push.json', 'goor-server', 'ubuntu_1', 'goor-server', 'goor-server', 'goor-server', 'goor-server', 'goor-server', '192.168.3.51');

-- ----------------------------
-- Table structure for AS_GOODS_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `AS_GOODS_TYPE`;
CREATE TABLE `AS_GOODS_TYPE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '创建人ID',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_GOODS_TYPE
-- ----------------------------
INSERT INTO `AS_GOODS_TYPE` VALUES ('1', '药物', '2017-07-07 11:47:02', null, '1', '100');
INSERT INTO `AS_GOODS_TYPE` VALUES ('2', '垃圾', '2017-07-07 11:47:05', null, '1', '100');
INSERT INTO `AS_GOODS_TYPE` VALUES ('3', '被草', '2017-07-07 11:47:09', null, '1', '100');
INSERT INTO `AS_GOODS_TYPE` VALUES ('4', '医疗器械', '2017-07-07 11:47:12', null, '1', '100');
INSERT INTO `AS_GOODS_TYPE` VALUES ('5', '餐饮', '2017-07-07 11:47:14', null, '1', '100');

-- ----------------------------
-- Table structure for AS_RFIDBRACELET
-- ----------------------------
DROP TABLE IF EXISTS `AS_RFIDBRACELET`;
CREATE TABLE `AS_RFIDBRACELET` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `STORE_ID` bigint(20) DEFAULT '100',
  `CREATED_BY` bigint(20) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `BRACELET_ID` varchar(36) DEFAULT NULL,
  `BRACELET_USERID` varchar(36) DEFAULT NULL,
  `BRACELET_NAME` varchar(50) DEFAULT NULL,
  `BRACELET_USERNAME` varchar(50) DEFAULT NULL,
  `BRACELET_AUTH` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_RFIDBRACELET
-- ----------------------------
INSERT INTO `AS_RFIDBRACELET` VALUES ('10', '100', '345', '2017-07-07 12:05:36', '345', '345', '345', 'guest9', '0');
INSERT INTO `AS_RFIDBRACELET` VALUES ('11', '100', '567', null, '567', '567', '567', 'guest3', '1');
INSERT INTO `AS_RFIDBRACELET` VALUES ('12', '100', '678', null, '6666', '678', '678', 'guest2', '0');

-- ----------------------------
-- Table structure for AS_ROBOT
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT`;
CREATE TABLE `AS_ROBOT` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `NAME` varchar(50) DEFAULT NULL COMMENT '机器人名称',
  `CODE` varchar(50) DEFAULT NULL COMMENT '机器人编号',
  `TYPE_ID` int(11) DEFAULT NULL COMMENT '类型ID',
  `DESCRIPTION` varchar(200) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL,
  `STORE_ID` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `BOX_ACTIVATED` bit(1) DEFAULT b'1' COMMENT '是否启用',
  `IS_BUSY` bit(1) DEFAULT b'0' COMMENT '状态(0-空闲， 1-占用)',
  `IS_ONLINE` bit(1) DEFAULT b'1' COMMENT '是否在线(0-不在线， 1-在线)',
  PRIMARY KEY (`ID`),
  KEY `TYPE` (`TYPE_ID`),
  CONSTRAINT `AS_ROBOT_ibfk_1` FOREIGN KEY (`TYPE_ID`) REFERENCES `AS_ROBOT_TYPE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for AS_ROBOT_CONFIG
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT_CONFIG`;
CREATE TABLE `AS_ROBOT_CONFIG` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROBOT_ID` int(11) NOT NULL COMMENT '机器人ID',
  `BATTERY_THRESHOLD` int(3) DEFAULT NULL COMMENT '电量阈值',
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATED_BY` bigint(20) DEFAULT NULL,
  `STORE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_ROBOT_CONFIG
-- ----------------------------
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('16', '314', '50', '2017-07-03 17:00:50', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('17', '315', '30', '2017-07-03 17:00:26', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('18', '316', '30', '2017-07-03 17:02:09', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('19', '317', '30', '2017-07-03 17:02:14', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('20', '318', '30', '2017-07-03 17:02:19', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('21', '319', '30', '2017-07-03 17:02:27', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('22', '320', '30', '2017-07-03 17:02:32', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('23', '321', '30', '2017-07-03 17:02:40', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('24', '322', '30', '2017-07-03 17:02:47', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('25', '323', '30', '2017-07-03 17:02:56', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('26', '324', '30', '2017-07-03 17:27:52', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('27', '325', '30', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('28', '326', '30', '2017-07-08 16:45:50', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('29', '327', '30', '2017-07-08 16:48:34', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('30', '328', '30', '2017-07-08 16:52:00', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('31', '329', '30', '2017-07-11 20:02:47', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('32', '330', '30', '2017-07-11 20:04:23', '1', '100');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('33', '331', '30', '2017-07-11 20:19:35', '1', '100');

-- ----------------------------
-- Table structure for AS_ROBOT_PASSWORD
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT_PASSWORD`;
CREATE TABLE `AS_ROBOT_PASSWORD` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROBOT_ID` bigint(20) DEFAULT NULL COMMENT '对应机器人编号',
  `BOX_NUM` int(12) DEFAULT NULL COMMENT '对应箱子编号',
  `PASSWORD` varchar(20) DEFAULT NULL COMMENT '密码',
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATED_BY` bigint(20) DEFAULT NULL,
  `STORE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_ROBOT_PASSWORD
-- ----------------------------
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('1', '43', '1', '1234', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('2', null, null, '1235', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('3', null, null, '7777', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('4', null, null, '6666', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('5', '45', '3', '420366', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('6', '45', '4', '863415', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('7', '45', '5', '214441', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('8', '45', '6', '721413', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('9', '45', '7', '227614', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('10', '45', '8', '108242', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('11', '45', '9', '754442', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('12', '45', '10', '768888', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('13', '46', '1', '318660', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('14', '47', '1', '244704', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('16', '49', '1', '644322', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('17', '50', '1', '614871', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('18', '50', '2', '714136', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('19', '52', '1', '661424', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('20', '52', '2', '151053', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('21', null, null, '1235', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('22', null, null, '1235', null, null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('23', '54', '1', '743105', '2017-06-27 10:28:01', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('24', '201', '1', '325641', '2017-06-27 10:55:50', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('25', null, '1', '223634', '2017-06-27 11:18:35', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('26', '301', '1', '863354', '2017-06-29 11:34:07', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('27', '302', '1', '474407', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('28', '302', '2', '260141', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('29', '302', '3', '116036', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('30', '302', '4', '685687', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('31', '302', '5', '482852', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('32', '302', '6', '068647', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('33', '302', '7', '848265', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('34', '302', '8', '051547', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('35', '302', '9', '074646', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('36', '302', '10', '604317', '2017-06-29 11:34:30', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('37', '303', '1', '6412', '2017-06-29 11:37:02', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('38', '303', '2', '5555', '2017-06-29 11:37:02', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('39', '304', '1', '352751', '2017-06-30 16:57:13', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('40', '305', '1', '878446', '2017-07-03 15:03:39', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('41', '307', '1', '125162', '2017-07-03 15:39:53', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('42', '308', '1', '304400', '2017-07-03 15:43:05', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('43', '309', '1', '662456', '2017-07-03 15:43:14', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('44', '309', '2', '101878', '2017-07-03 15:43:14', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('45', '310', '1', '621134', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('46', '310', '2', '072868', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('47', '310', '3', '756050', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('48', '310', '4', '370170', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('49', '310', '5', '243776', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('50', '310', '6', '442678', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('51', '310', '7', '847101', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('52', '310', '8', '264153', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('53', '310', '9', '163807', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('54', '310', '10', '574320', '2017-07-03 15:43:24', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('55', '314', '1', '546633', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('56', '314', '2', '515376', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('57', '314', '3', '048223', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('58', '314', '4', '811217', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('59', '314', '5', '518855', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('60', '314', '6', '123412', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('61', '314', '7', '483604', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('62', '314', '8', '082561', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('63', '314', '9', '142032', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('64', '314', '10', '776243', '2017-07-03 16:58:20', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('65', '315', '1', '115703', '2017-07-03 17:00:26', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('66', '316', '1', '142357', '2017-07-03 17:02:09', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('67', '317', '1', '534331', '2017-07-03 17:02:14', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('68', '318', '1', '653726', '2017-07-03 17:02:19', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('69', '319', '1', '666434', '2017-07-03 17:02:27', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('70', '320', '1', '242837', '2017-07-03 17:02:32', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('71', '321', '1', '420332', '2017-07-03 17:02:40', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('72', '322', '1', '301260', '2017-07-03 17:02:47', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('73', '322', '2', '727330', '2017-07-03 17:02:47', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('74', '323', '1', '571103', '2017-07-03 17:02:56', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('75', '323', '2', '845517', '2017-07-03 17:02:56', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('76', '324', '1', '134221', '2017-07-03 17:27:52', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('77', '324', '2', '431461', '2017-07-03 17:27:52', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('78', '325', '1', '225755', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('79', '325', '2', '451621', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('80', '325', '3', '655137', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('81', '325', '4', '654030', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('82', '325', '5', '258752', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('83', '325', '6', '772582', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('84', '325', '7', '404065', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('85', '325', '8', '630188', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('86', '325', '9', '881165', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('87', '325', '10', '565854', '2017-07-03 17:27:59', '1', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('88', '326', '1', '412228', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('89', '326', '2', '465033', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('90', '326', '3', '441446', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('91', '326', '4', '372410', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('92', '326', '5', '827302', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('93', '326', '6', '356428', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('94', '326', '7', '188537', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('95', '326', '8', '874087', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('96', '326', '9', '606427', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('97', '326', '10', '732571', '2017-07-08 16:45:50', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('98', '327', '1', '300041', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('99', '327', '2', '711323', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('100', '327', '3', '783701', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('101', '327', '4', '658613', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('102', '327', '5', '346445', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('103', '327', '6', '204100', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('104', '327', '7', '142080', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('105', '327', '8', '710721', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('106', '327', '9', '758088', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('107', '327', '10', '267733', '2017-07-08 16:48:34', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('108', '328', '1', '446124', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('109', '328', '2', '824683', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('110', '328', '3', '826168', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('111', '328', '4', '275660', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('112', '328', '5', '166721', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('113', '328', '6', '564047', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('114', '328', '7', '154071', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('115', '328', '8', '307853', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('116', '328', '9', '605623', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('117', '328', '10', '163757', '2017-07-08 16:50:53', '111', '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('118', '329', '1', '172266', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('119', '329', '2', '621777', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('120', '329', '3', '256882', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('121', '329', '4', '133041', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('122', '329', '5', '102083', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('123', '329', '6', '301786', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('124', '329', '7', '717484', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('125', '329', '8', '443153', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('126', '329', '9', '301274', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('127', '329', '10', '155735', '2017-07-11 20:02:47', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('128', '330', '1', '646583', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('129', '330', '2', '603430', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('130', '330', '3', '774627', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('131', '330', '4', '846886', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('132', '330', '5', '786015', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('133', '330', '6', '233376', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('134', '330', '7', '741254', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('135', '330', '8', '558157', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('136', '330', '9', '774208', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('137', '330', '10', '833600', '2017-07-11 20:04:23', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('138', '331', '1', '786864', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('139', '331', '2', '854743', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('140', '331', '3', '110705', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('141', '331', '4', '741770', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('142', '331', '5', '586033', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('143', '331', '6', '862076', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('144', '331', '7', '662683', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('145', '331', '8', '127134', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('146', '331', '9', '576375', '2017-07-11 20:19:35', null, '100');
INSERT INTO `AS_ROBOT_PASSWORD` VALUES ('147', '331', '10', '570058', '2017-07-11 20:19:35', null, '100');

-- ----------------------------
-- Table structure for AS_ROBOT_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT_TYPE`;
CREATE TABLE `AS_ROBOT_TYPE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `NAME` varchar(50) DEFAULT NULL COMMENT '机器人类型名称',
  `BOX_COUNT` int(11) DEFAULT NULL COMMENT '格子数量',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_ROBOT_TYPE
-- ----------------------------
INSERT INTO `AS_ROBOT_TYPE` VALUES ('1', '拖车式', '1');
INSERT INTO `AS_ROBOT_TYPE` VALUES ('2', '柜式', '2');
INSERT INTO `AS_ROBOT_TYPE` VALUES ('3', '抽屉式', '10');

-- ----------------------------
-- Table structure for AS_SHELF
-- ----------------------------
DROP TABLE IF EXISTS `AS_SHELF`;
CREATE TABLE `AS_SHELF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CODE` varchar(50) DEFAULT NULL COMMENT '编号',
  `NAME` varchar(50) DEFAULT NULL COMMENT '名称',
  `RFID` varchar(50) DEFAULT NULL COMMENT 'RFID',
  `TYPE` varchar(50) DEFAULT NULL COMMENT '类型',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '备注',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_SHELF
-- ----------------------------
INSERT INTO `AS_SHELF` VALUES ('1', 'test0', 'test0', 'tes0', 'test0', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('2', 'test1', 'test1', 'test1', 'test1', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('3', 'test2', 'test2', 'tes2', 'test2', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('4', 'test3', 'test3', 'tes3', 'test3', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('5', 'test4', 'test4', 'test4', 'test4', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('9', 'test8', 'test8', 'test8', 'test8', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('10', 'test9', 'test9', 'tes9', 'test9', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('11', 'test10', 'test10', 'tes10', 'test10', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('12', 'test11', 'test11', 'test11', 'test11', null, '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('13', 'test12', 'test12', 'test12', 'test12', 'c', '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('14', 'huojia007', 'afadsf', '1234', '88888', '99999', '100', '1', '2017-07-03 20:30:31');
INSERT INTO `AS_SHELF` VALUES ('16', 'huojia360', '货架360', '1234', '185485', 'huojia360', '100', '1', '2017-07-10 09:30:39');
INSERT INTO `AS_SHELF` VALUES ('17', '55', '55', '55', '55', '55', '100', '1', '2017-07-10 14:03:47');
INSERT INTO `AS_SHELF` VALUES ('18', '345', '345', '345', '345', '345', '100', '1', '2017-07-10 14:22:49');
INSERT INTO `AS_SHELF` VALUES ('19', '567', '567', '567', '456', '456', '100', '1', '2017-07-10 14:27:03');
INSERT INTO `AS_SHELF` VALUES ('20', '456456', '456456', '345345345', '345345', '345345', '100', '1', '2017-07-10 14:29:24');
INSERT INTO `AS_SHELF` VALUES ('21', '987', '987', '987', '987', '987', '100', '1', '2017-07-10 15:04:51');
INSERT INTO `AS_SHELF` VALUES ('22', '1111', '1111', '1111', '1111', '1111', '100', '1', '2017-07-10 15:58:32');
INSERT INTO `AS_SHELF` VALUES ('23', '34676757', '4356456', '345345', '34545', '5555', '100', '1', '2017-07-10 16:33:23');
INSERT INTO `AS_SHELF` VALUES ('25', '测试货架1_detail', '测试货架1_detail', '测试货架1_detail', '测试货架1_detail', '测试货架1_detail', '100', '1', '2017-07-10 18:03:50');
INSERT INTO `AS_SHELF` VALUES ('27', '测试货架3_detail', '测试货架3_detail', '测试货架3_detail', 'XXXXXXXX', '测试货架3_detail', '100', '1', '2017-07-10 20:39:03');
INSERT INTO `AS_SHELF` VALUES ('28', 'uuuu', 'uuuu', 'uuuu', 'uuuu', 'uuuu', '100', '1', '2017-07-11 11:06:42');

-- ----------------------------
-- Table structure for C_FILE_UPLOAD
-- ----------------------------
DROP TABLE IF EXISTS `C_FILE_UPLOAD`;
CREATE TABLE `C_FILE_UPLOAD` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `NAME` varchar(50) NOT NULL COMMENT '名称',
  `LENGTH` bigint(20) DEFAULT NULL COMMENT '长度',
  `PATH` varchar(256) DEFAULT NULL COMMENT '路径',
  `MD5` varchar(256) DEFAULT NULL COMMENT 'MD5',
  `STATUS` int(11) DEFAULT NULL COMMENT '状态（0传输成功 1传输失败）',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of C_FILE_UPLOAD
-- ----------------------------
INSERT INTO `C_FILE_UPLOAD` VALUES ('1', 'upload_2017-06-23.zip', '1234632', 'e:\\download_home\\upload\\upload_2017-06-23.zip', null, '0', '2017-06-23 17:29:47', '2017-06-23 17:29:47');
INSERT INTO `C_FILE_UPLOAD` VALUES ('2', 'upload_2017-07-03_03-05-23.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_03-05-23.zip', null, '0', '2017-07-03 15:06:28', '2017-07-03 15:06:28');
INSERT INTO `C_FILE_UPLOAD` VALUES ('3', 'upload_2017-07-03_03-25-54.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_03-25-54.zip', null, '0', '2017-07-03 15:26:16', '2017-07-03 15:26:16');
INSERT INTO `C_FILE_UPLOAD` VALUES ('4', 'upload_2017-07-03_03-34-12.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_03-34-12.zip', null, '0', '2017-07-03 15:34:24', '2017-07-03 15:34:24');
INSERT INTO `C_FILE_UPLOAD` VALUES ('5', 'upload_2017-07-03_03-34-50.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_03-34-50.zip', null, '0', '2017-07-03 15:38:09', '2017-07-03 15:38:09');
INSERT INTO `C_FILE_UPLOAD` VALUES ('6', 'upload_2017-07-03_04-41-12.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_04-41-12.zip', null, '0', '2017-07-03 16:41:25', '2017-07-03 16:41:25');
INSERT INTO `C_FILE_UPLOAD` VALUES ('7', 'upload_2017-07-03_04-44-53.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_04-44-53.zip', null, '0', '2017-07-03 16:45:11', '2017-07-03 16:45:11');
INSERT INTO `C_FILE_UPLOAD` VALUES ('8', 'upload_2017-07-03_04-46-01.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_04-46-01.zip', null, '0', '2017-07-03 16:46:05', '2017-07-03 16:46:05');
INSERT INTO `C_FILE_UPLOAD` VALUES ('9', 'upload_2017-07-03_04-48-26.zip', '1234632', 'e:\\download_home\\upload\\map\\upload_2017-07-03_04-48-26.zip', null, '0', '2017-07-03 16:48:27', '2017-07-03 16:48:27');
INSERT INTO `C_FILE_UPLOAD` VALUES ('10', 'upload_2017-07-03_04-54-03.zip', '1234632', 'e:\\download_home\\featureItemId\\upload\\map\\upload_2017-07-03_04-54-03.zip', null, '0', '2017-07-03 16:54:04', '2017-07-03 16:54:04');
INSERT INTO `C_FILE_UPLOAD` VALUES ('11', 'upload_2017-07-04_11-27-20.zip', '1234632', 'e:\\download_home\\100\\upload\\map\\upload_2017-07-04_11-27-20.zip', null, '0', '2017-07-04 11:27:22', '2017-07-04 11:27:22');
INSERT INTO `C_FILE_UPLOAD` VALUES ('12', 'example_2017-07-04_11-46-56.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_11-46-56.zip', null, '0', '2017-07-04 11:46:58', '2017-07-04 11:46:58');
INSERT INTO `C_FILE_UPLOAD` VALUES ('13', 'example_2017-07-04_11-47-30.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_11-47-30.zip', null, '0', '2017-07-04 11:47:31', '2017-07-04 11:47:31');
INSERT INTO `C_FILE_UPLOAD` VALUES ('14', 'example_2017-07-04_11-51-32.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_11-51-32.zip', null, '0', '2017-07-04 11:52:02', '2017-07-04 11:52:02');
INSERT INTO `C_FILE_UPLOAD` VALUES ('15', 'example_2017-07-04_11-53-23.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_11-53-23.zip', null, '0', '2017-07-04 11:54:08', '2017-07-04 11:54:08');
INSERT INTO `C_FILE_UPLOAD` VALUES ('16', 'example_2017-07-04_11-57-05.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_11-57-05.zip', null, '0', '2017-07-04 11:57:47', '2017-07-04 11:57:47');
INSERT INTO `C_FILE_UPLOAD` VALUES ('17', 'example_2017-07-04_01-42-52.zip', '1301', 'e:\\download_home\\upload\\map\\example_2017-07-04_01-42-52.zip', null, '0', '2017-07-04 13:42:52', '2017-07-04 13:42:52');
INSERT INTO `C_FILE_UPLOAD` VALUES ('18', 'example_2017-07-04_01-45-46.zip', '1301', 'e:\\download_home\\upload\\map\\example_2017-07-04_01-45-46.zip', null, '0', '2017-07-04 13:46:24', '2017-07-04 13:46:24');
INSERT INTO `C_FILE_UPLOAD` VALUES ('19', 'example_2017-07-04_01-49-11.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_01-49-11.zip', null, '0', '2017-07-04 13:49:39', '2017-07-04 13:49:39');
INSERT INTO `C_FILE_UPLOAD` VALUES ('20', 'example_2017-07-04_01-51-29.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_01-51-29.zip', null, '0', '2017-07-04 13:51:29', '2017-07-04 13:51:29');
INSERT INTO `C_FILE_UPLOAD` VALUES ('21', 'example_2017-07-04_01-56-45.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_01-56-45.zip', null, '0', '2017-07-04 13:56:46', '2017-07-04 13:56:46');
INSERT INTO `C_FILE_UPLOAD` VALUES ('22', 'example_2017-07-04_01-57-26.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_01-57-26.zip', null, '0', '2017-07-04 13:57:26', '2017-07-04 13:57:26');
INSERT INTO `C_FILE_UPLOAD` VALUES ('23', 'example_2017-07-04_02-06-49.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-06-49.zip', null, '0', '2017-07-04 14:06:50', '2017-07-04 14:06:50');
INSERT INTO `C_FILE_UPLOAD` VALUES ('24', 'example_2017-07-04_02-08-37.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-08-37.zip', null, '0', '2017-07-04 14:08:38', '2017-07-04 14:08:38');
INSERT INTO `C_FILE_UPLOAD` VALUES ('25', 'example_2017-07-04_02-12-08.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-12-08.zip', null, '0', '2017-07-04 14:12:09', '2017-07-04 14:12:09');
INSERT INTO `C_FILE_UPLOAD` VALUES ('26', 'example_2017-07-04_02-12-32.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-12-32.zip', null, '0', '2017-07-04 14:12:33', '2017-07-04 14:12:33');
INSERT INTO `C_FILE_UPLOAD` VALUES ('27', 'example_2017-07-04_02-14-24.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-14-24.zip', null, '0', '2017-07-04 14:14:25', '2017-07-04 14:14:25');
INSERT INTO `C_FILE_UPLOAD` VALUES ('28', 'example_2017-07-04_02-24-56.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-24-56.zip', null, '0', '2017-07-04 14:24:57', '2017-07-04 14:24:57');
INSERT INTO `C_FILE_UPLOAD` VALUES ('29', 'example_2017-07-04_02-32-45.zip', '1301', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-32-45.zip', null, '0', '2017-07-04 14:32:46', '2017-07-04 14:32:46');
INSERT INTO `C_FILE_UPLOAD` VALUES ('30', 'example_2017-07-04_02-35-31.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-35-31.zip', null, '0', '2017-07-04 14:35:32', '2017-07-04 14:35:32');
INSERT INTO `C_FILE_UPLOAD` VALUES ('31', 'example_2017-07-04_02-37-04.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-37-04.zip', null, '0', '2017-07-04 14:37:04', '2017-07-04 14:37:04');
INSERT INTO `C_FILE_UPLOAD` VALUES ('32', 'example_2017-07-04_02-37-33.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-37-33.zip', null, '0', '2017-07-04 14:37:33', '2017-07-04 14:37:33');
INSERT INTO `C_FILE_UPLOAD` VALUES ('33', 'example_2017-07-04_02-37-56.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-37-56.zip', null, '0', '2017-07-04 14:37:56', '2017-07-04 14:37:56');
INSERT INTO `C_FILE_UPLOAD` VALUES ('34', 'example_2017-07-04_02-45-58.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-45-58.zip', null, '0', '2017-07-04 14:45:58', '2017-07-04 14:45:58');
INSERT INTO `C_FILE_UPLOAD` VALUES ('35', 'example_2017-07-04_02-55-03.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_02-55-03.zip', null, '0', '2017-07-04 14:55:04', '2017-07-04 14:55:04');
INSERT INTO `C_FILE_UPLOAD` VALUES ('36', 'example_2017-07-04_03-07-29.zip', '15842', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_03-07-29.zip', null, '0', '2017-07-04 15:07:30', '2017-07-04 15:07:30');
INSERT INTO `C_FILE_UPLOAD` VALUES ('37', 'example_2017-07-04_03-17-11.zip', '15684', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_03-17-11.zip', null, '0', '2017-07-04 15:17:12', '2017-07-04 15:17:12');
INSERT INTO `C_FILE_UPLOAD` VALUES ('38', 'example_2017-07-04_04-04-07.zip', '71191', 'e:\\download_home\\100\\upload\\map\\example_2017-07-04_04-04-07.zip', null, '0', '2017-07-04 16:04:07', '2017-07-04 16:04:07');
INSERT INTO `C_FILE_UPLOAD` VALUES ('39', 'maps_2017-07-04_04-40-21.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-40-21.zip', null, '0', '2017-07-04 16:40:23', '2017-07-04 16:40:23');
INSERT INTO `C_FILE_UPLOAD` VALUES ('40', 'maps_2017-07-04_04-42-52.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-42-52.zip', null, '0', '2017-07-04 16:42:53', '2017-07-04 16:42:53');
INSERT INTO `C_FILE_UPLOAD` VALUES ('41', 'maps_2017-07-04_04-46-46.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-46-46.zip', null, '0', '2017-07-04 16:46:47', '2017-07-04 16:46:47');
INSERT INTO `C_FILE_UPLOAD` VALUES ('42', 'maps_2017-07-04_04-49-40.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-49-40.zip', null, '0', '2017-07-04 16:49:41', '2017-07-04 16:49:41');
INSERT INTO `C_FILE_UPLOAD` VALUES ('43', 'maps_2017-07-04_04-53-19.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-53-19.zip', null, '0', '2017-07-04 16:53:20', '2017-07-04 16:53:20');
INSERT INTO `C_FILE_UPLOAD` VALUES ('44', 'maps_2017-07-04_04-59-19.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_04-59-19.zip', null, '0', '2017-07-04 16:59:19', '2017-07-04 16:59:19');
INSERT INTO `C_FILE_UPLOAD` VALUES ('45', 'maps_2017-07-04_05-17-58.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_05-17-58.zip', null, '0', '2017-07-04 17:17:58', '2017-07-04 17:17:58');
INSERT INTO `C_FILE_UPLOAD` VALUES ('46', 'maps_2017-07-04_05-24-57.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_05-24-57.zip', null, '0', '2017-07-04 17:24:58', '2017-07-04 17:24:58');
INSERT INTO `C_FILE_UPLOAD` VALUES ('47', 'maps_2017-07-04_05-25-06.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-04_05-25-06.zip', null, '0', '2017-07-04 17:25:07', '2017-07-04 17:25:07');
INSERT INTO `C_FILE_UPLOAD` VALUES ('48', 'maps_2017-07-05_11-13-44.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_11-13-44.zip', null, '0', '2017-07-05 11:13:46', '2017-07-05 11:13:46');
INSERT INTO `C_FILE_UPLOAD` VALUES ('49', 'maps_2017-07-05_11-48-30.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_11-48-30.zip', null, '0', '2017-07-05 11:48:31', '2017-07-05 11:48:31');
INSERT INTO `C_FILE_UPLOAD` VALUES ('50', 'maps_2017-07-05_11-50-37.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_11-50-37.zip', null, '0', '2017-07-05 11:50:38', '2017-07-05 11:50:38');
INSERT INTO `C_FILE_UPLOAD` VALUES ('51', 'maps_2017-07-05_11-51-50.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_11-51-50.zip', null, '0', '2017-07-05 11:51:51', '2017-07-05 11:51:51');
INSERT INTO `C_FILE_UPLOAD` VALUES ('52', 'maps_2017-07-05_02-10-04.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-10-04.zip', null, '0', '2017-07-05 14:10:05', '2017-07-05 14:10:05');
INSERT INTO `C_FILE_UPLOAD` VALUES ('53', 'maps_2017-07-05_02-11-53.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-11-53.zip', null, '0', '2017-07-05 14:11:54', '2017-07-05 14:11:54');
INSERT INTO `C_FILE_UPLOAD` VALUES ('54', 'maps_2017-07-05_02-17-33.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-17-33.zip', null, '0', '2017-07-05 14:17:34', '2017-07-05 14:17:34');
INSERT INTO `C_FILE_UPLOAD` VALUES ('55', 'maps_2017-07-05_02-23-23.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-23-23.zip', null, '0', '2017-07-05 14:23:24', '2017-07-05 14:23:24');
INSERT INTO `C_FILE_UPLOAD` VALUES ('56', 'maps_2017-07-05_02-39-39.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-39-39.zip', null, '0', '2017-07-05 14:39:40', '2017-07-05 14:39:40');
INSERT INTO `C_FILE_UPLOAD` VALUES ('57', 'maps_2017-07-05_02-40-44.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_02-40-44.zip', null, '0', '2017-07-05 14:40:45', '2017-07-05 14:40:45');
INSERT INTO `C_FILE_UPLOAD` VALUES ('58', 'maps_2017-07-05_03-17-39.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_03-17-39.zip', null, '0', '2017-07-05 15:17:40', '2017-07-05 15:17:40');
INSERT INTO `C_FILE_UPLOAD` VALUES ('59', 'maps_2017-07-05_03-23-53.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_03-23-53.zip', null, '0', '2017-07-05 15:23:54', '2017-07-05 15:23:54');
INSERT INTO `C_FILE_UPLOAD` VALUES ('60', 'maps_2017-07-05_03-29-41.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_03-29-41.zip', null, '0', '2017-07-05 15:29:42', '2017-07-05 15:29:42');
INSERT INTO `C_FILE_UPLOAD` VALUES ('61', 'maps_2017-07-05_03-34-34.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_03-34-34.zip', null, '0', '2017-07-05 15:34:35', '2017-07-05 15:34:35');
INSERT INTO `C_FILE_UPLOAD` VALUES ('62', 'maps_2017-07-05_03-35-10.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_03-35-10.zip', null, '0', '2017-07-05 15:35:10', '2017-07-05 15:35:10');
INSERT INTO `C_FILE_UPLOAD` VALUES ('63', 'maps_2017-07-05_04-03-57.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_04-03-57.zip', null, '0', '2017-07-05 16:03:59', '2017-07-05 16:03:59');
INSERT INTO `C_FILE_UPLOAD` VALUES ('64', 'maps_2017-07-05_04-05-45.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_04-05-45.zip', null, '0', '2017-07-05 16:05:46', '2017-07-05 16:05:46');
INSERT INTO `C_FILE_UPLOAD` VALUES ('65', 'maps_2017-07-05_05-13-41.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-13-41.zip', null, '0', '2017-07-05 17:13:42', '2017-07-05 17:13:42');
INSERT INTO `C_FILE_UPLOAD` VALUES ('66', 'maps_2017-07-05_05-15-20.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-15-20.zip', null, '0', '2017-07-05 17:15:20', '2017-07-05 17:15:20');
INSERT INTO `C_FILE_UPLOAD` VALUES ('67', 'maps_2017-07-05_05-17-35.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-17-35.zip', null, '0', '2017-07-05 17:17:36', '2017-07-05 17:17:36');
INSERT INTO `C_FILE_UPLOAD` VALUES ('68', 'maps_2017-07-05_05-20-04.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-20-04.zip', null, '0', '2017-07-05 17:20:05', '2017-07-05 17:20:05');
INSERT INTO `C_FILE_UPLOAD` VALUES ('69', 'maps_2017-07-05_05-26-35.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-26-35.zip', null, '0', '2017-07-05 17:26:36', '2017-07-05 17:26:36');
INSERT INTO `C_FILE_UPLOAD` VALUES ('70', 'maps_2017-07-05_05-27-32.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-27-32.zip', null, '0', '2017-07-05 17:27:33', '2017-07-05 17:27:33');
INSERT INTO `C_FILE_UPLOAD` VALUES ('71', 'maps_2017-07-05_05-28-07.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-28-07.zip', null, '0', '2017-07-05 17:28:08', '2017-07-05 17:28:08');
INSERT INTO `C_FILE_UPLOAD` VALUES ('72', 'maps_2017-07-05_05-31-58.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-31-58.zip', null, '0', '2017-07-05 17:31:59', '2017-07-05 17:31:59');
INSERT INTO `C_FILE_UPLOAD` VALUES ('73', 'maps_2017-07-05_05-37-03.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-37-03.zip', null, '0', '2017-07-05 17:37:03', '2017-07-05 17:37:03');
INSERT INTO `C_FILE_UPLOAD` VALUES ('74', 'maps_2017-07-05_05-39-38.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_05-39-38.zip', null, '0', '2017-07-05 17:39:39', '2017-07-05 17:39:39');
INSERT INTO `C_FILE_UPLOAD` VALUES ('75', 'maps_2017-07-05_06-23-13.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_06-23-13.zip', null, '0', '2017-07-05 18:23:14', '2017-07-05 18:23:14');
INSERT INTO `C_FILE_UPLOAD` VALUES ('76', 'maps_2017-07-05_07-03-35.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_07-03-35.zip', null, '0', '2017-07-05 19:03:36', '2017-07-05 19:03:36');
INSERT INTO `C_FILE_UPLOAD` VALUES ('77', 'maps_2017-07-05_07-05-48.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_07-05-48.zip', null, '0', '2017-07-05 19:05:48', '2017-07-05 19:05:48');
INSERT INTO `C_FILE_UPLOAD` VALUES ('78', 'maps_2017-07-05_07-38-27.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_07-38-27.zip', null, '0', '2017-07-05 19:38:28', '2017-07-05 19:38:28');
INSERT INTO `C_FILE_UPLOAD` VALUES ('79', 'maps_2017-07-05_07-40-29.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_07-40-29.zip', null, '0', '2017-07-05 19:40:30', '2017-07-05 19:40:30');
INSERT INTO `C_FILE_UPLOAD` VALUES ('80', 'maps_2017-07-05_07-44-32.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-05_07-44-32.zip', null, '0', '2017-07-05 19:44:32', '2017-07-05 19:44:32');
INSERT INTO `C_FILE_UPLOAD` VALUES ('81', 'maps_2017-07-06_09-32-24.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-32-24.zip', null, '0', '2017-07-06 09:32:25', '2017-07-06 09:32:25');
INSERT INTO `C_FILE_UPLOAD` VALUES ('82', 'maps_2017-07-06_09-34-24.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-34-24.zip', null, '0', '2017-07-06 09:34:25', '2017-07-06 09:34:25');
INSERT INTO `C_FILE_UPLOAD` VALUES ('83', 'maps_2017-07-06_09-34-43.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-34-43.zip', null, '0', '2017-07-06 09:34:43', '2017-07-06 09:34:43');
INSERT INTO `C_FILE_UPLOAD` VALUES ('84', 'maps_2017-07-06_09-44-59.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_09-44-59.zip', null, '0', '2017-07-06 09:45:00', '2017-07-06 09:45:00');
INSERT INTO `C_FILE_UPLOAD` VALUES ('85', 'maps_2017-07-06_10-43-25.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-06_10-43-25.zip', null, '0', '2017-07-06 10:43:25', '2017-07-06 10:43:25');
INSERT INTO `C_FILE_UPLOAD` VALUES ('86', 'maps_2017-07-08_11-34-51.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-08_11-34-51.zip', null, '0', '2017-07-08 11:34:55', '2017-07-08 11:34:55');
INSERT INTO `C_FILE_UPLOAD` VALUES ('87', 'maps_2017-07-08_11-35-20.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-08_11-35-20.zip', null, '0', '2017-07-08 11:35:21', '2017-07-08 11:35:21');
INSERT INTO `C_FILE_UPLOAD` VALUES ('88', 'maps_2017-07-12_09-51-22.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-51-22.zip', null, '0', '2017-07-12 09:51:23', '2017-07-12 09:51:23');
INSERT INTO `C_FILE_UPLOAD` VALUES ('89', 'maps_2017-07-12_09-53-34.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-53-34.zip', null, '0', '2017-07-12 09:53:35', '2017-07-12 09:53:35');
INSERT INTO `C_FILE_UPLOAD` VALUES ('90', 'maps_2017-07-12_09-55-03.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-55-03.zip', null, '0', '2017-07-12 09:55:05', '2017-07-12 09:55:05');
INSERT INTO `C_FILE_UPLOAD` VALUES ('91', 'maps_2017-07-12_09-55-50.zip', '92943', 'e:\\download_home\\100\\upload\\map\\maps_2017-07-12_09-55-50.zip', null, '0', '2017-07-12 09:55:51', '2017-07-12 09:55:51');

-- ----------------------------
-- Table structure for LOG_CHARGE_INFO
-- ----------------------------
DROP TABLE IF EXISTS `LOG_CHARGE_INFO`;
CREATE TABLE `LOG_CHARGE_INFO` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATED_BY` bigint(20) DEFAULT NULL,
  `DEVICE_ID` varchar(256) DEFAULT NULL,
  `CHARGING_STATUS` bit(1) DEFAULT NULL,
  `PLUGIN_STATUS` bit(1) DEFAULT NULL,
  `POWER_PERCENT` int(11) DEFAULT NULL,
  `AUTO_CHARGING` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for D_FEATURE_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `D_FEATURE_ITEM`;
CREATE TABLE `D_FEATURE_ITEM` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `VALUE` varchar(256) DEFAULT NULL COMMENT '值',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `DATA_MODEL` varchar(256) DEFAULT NULL COMMENT '数据模板，方便前端用户输入',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_FEATURE_ITEM
-- ----------------------------
INSERT INTO `D_FEATURE_ITEM` VALUES ('1', '单点导航', 'navigation_point', '单点导航命令', '{\"id\":0,\"x\":0,\"y\":0,\"z\":0,\"sceneName\":\"场景名\",\"mapName\":\"地图名\"}');
INSERT INTO `D_FEATURE_ITEM` VALUES ('2', 'TTS语音', 'voice_tts', 'TTS语音命令', '{\"voiceContent\":\"要说的话\"}');
INSERT INTO `D_FEATURE_ITEM` VALUES ('3', 'MP3语音', 'voice_mp3', 'MP3语音命令', '{\"fileName\":\"要播放的语音文件名\"}');
INSERT INTO `D_FEATURE_ITEM` VALUES ('4', '自动充电', 'charge_auto', '自动回充', '{}');

-- ----------------------------
-- Table structure for D_FEATURE_ITEM_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `D_FEATURE_ITEM_TYPE`;
CREATE TABLE `D_FEATURE_ITEM_TYPE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `VALUE` varchar(256) DEFAULT NULL COMMENT '值',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `DATA_MODEL` varchar(256) DEFAULT NULL COMMENT '数据模板，方便前端用户输入',
  `FEATURE_ITEM_ID` bigint(20) DEFAULT NULL COMMENT '功能ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_FEATURE_ITEM_TYPE
-- ----------------------------
INSERT INTO `D_FEATURE_ITEM_TYPE` VALUES ('1', '测试1', 'hahahah啊', '你猜啊1', 'sadfsdf', '1');
INSERT INTO `D_FEATURE_ITEM_TYPE` VALUES ('2', '测试12', 'hahahah312', '你猜啊1', 'sadfsdf', '1');

-- ----------------------------
-- Table structure for D_MISSION
-- ----------------------------

DROP TABLE IF EXISTS `D_MISSION`;
CREATE TABLE `D_MISSION` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `REPEAT_COUNT` int(11) DEFAULT NULL COMMENT '重复次数',
  `INTERVAL_TIME` bigint(20) DEFAULT NULL COMMENT '间隔时间',
  `SCENE_NAME` varchar(255) DEFAULT NULL COMMENT '场景名',
  `TYPE_ID` int(2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION
-- ----------------------------

-- ----------------------------
-- Table structure for D_MISSION_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_ITEM`;
CREATE TABLE `D_MISSION_ITEM` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `DATA` varchar(256) DEFAULT NULL COMMENT '数据',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `FEATURE_ITEM_ID` bigint(20) DEFAULT NULL COMMENT '功能ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_ITEM
-- ----------------------------

-- ----------------------------
-- Table structure for D_MISSION_LIST
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_LIST`;
CREATE TABLE `D_MISSION_LIST` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `MISSION_LIST_TYPE` varchar(256) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `INTERVAL_TIME` bigint(20) DEFAULT NULL COMMENT '间隔时间',
  `REPEAT_COUNT` int(11) DEFAULT NULL COMMENT '重复次数',
  `START_TIME` bigint(20) DEFAULT NULL COMMENT '开始时间',
  `PRIORITY` int(11) DEFAULT NULL COMMENT '优先等级',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_LIST
-- ----------------------------

-- ----------------------------
-- Table structure for D_MISSION_LIST_MISSION_XREF
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_LIST_MISSION_XREF`;
CREATE TABLE `D_MISSION_LIST_MISSION_XREF` (
  `MISSION_LIST_ID` bigint(20) DEFAULT NULL,
  `MISSION_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_LIST_MISSION_XREF
-- ----------------------------
INSERT INTO `D_MISSION_LIST_MISSION_XREF` VALUES ('9', '2');
INSERT INTO `D_MISSION_LIST_MISSION_XREF` VALUES ('9', '4');

-- ----------------------------
-- Table structure for D_MISSION_MISSION_ITEM_XREF
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_MISSION_ITEM_XREF`;
CREATE TABLE `D_MISSION_MISSION_ITEM_XREF` (
  `MISSION_ID` bigint(20) DEFAULT NULL,
  `MISSION_ITEM_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_MISSION_ITEM_XREF
-- ----------------------------
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES (null, '17');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES (null, '18');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('6', '19');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('6', '20');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('7', '25');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('7', '26');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('2', '4');
INSERT INTO `D_MISSION_MISSION_ITEM_XREF` VALUES ('2', '5');

-- ----------------------------
-- Table structure for LOG_INFO
-- ----------------------------
DROP TABLE IF EXISTS `LOG_INFO`;
CREATE TABLE `LOG_INFO` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DEVICE_ID` varchar(256) NOT NULL,
  `MESSAGE` varchar(256) DEFAULT NULL,
  `LOG_LEVEL` varchar(50) NOT NULL,
  `LOG_TYPE` varchar(50) NOT NULL,
  `CREATE_DATE` datetime NOT NULL,
  `HANDLE_PERSON` varchar(50) DEFAULT NULL,
  `HANDLE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_INFO
-- ----------------------------
INSERT INTO `LOG_INFO` VALUES ('1', 'dasdf', 'asdfasdf', 'INFO', 'INFO_EXECUTE_TASK', '2017-06-08 15:43:54', null, null);
INSERT INTO `LOG_INFO` VALUES ('2', 'dasdf', 'asdfasdf', 'WARNING', 'INFO_EXECUTE_TASK', '2017-06-08 15:43:54', null, null);
INSERT INTO `LOG_INFO` VALUES ('3', 'dasdf', 'asdfasdf', 'ERROR', 'INFO_EXECUTE_TASK', '2017-06-08 15:43:54', null, null);
INSERT INTO `LOG_INFO` VALUES ('4', 'dasdf', 'asdfasdf', 'INFO', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:19', null, null);
INSERT INTO `LOG_INFO` VALUES ('5', 'dasdf', 'asdfasdf', 'WARNING', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:19', null, null);
INSERT INTO `LOG_INFO` VALUES ('6', 'dasdf', 'asdfasdf', 'ERROR', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:20', null, null);
INSERT INTO `LOG_INFO` VALUES ('7', 'dasdf', 'asdfasdf', 'INFO', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:20', null, null);
INSERT INTO `LOG_INFO` VALUES ('8', 'dasdf', 'asdfasdf', 'WARNING', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:20', null, null);
INSERT INTO `LOG_INFO` VALUES ('9', 'dasdf', 'asdfasdf', 'ERROR', 'INFO_EXECUTE_TASK', '2017-06-08 15:44:20', null, null);
INSERT INTO `LOG_INFO` VALUES ('10', 'dasdf', 'asdfasdf', 'INFO', 'INFO_EXECUTE_TASK', '2017-06-09 15:44:20', 'null', '2017-06-20 14:23:37');

-- ----------------------------
-- Table structure for LOG_MISSION
-- ----------------------------
DROP TABLE IF EXISTS `LOG_MISSION`;
CREATE TABLE `LOG_MISSION` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `ROBOT_CODE` varchar(50) DEFAULT NULL COMMENT '机器人编号',
  `MISSION_TYPE` int(11) NOT NULL COMMENT '任务日志类型：0-任务列表日志，1-任务日志，2-任务节点日志',
  `MISSION_LIST_ID` bigint(20) NOT NULL COMMENT '任务列表ID',
  `MISSION_ID` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `MISSION_ITEM_ID` bigint(20) DEFAULT NULL COMMENT '任务节点ID',
  `MISSION_LIST_REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '任务列表重复',
  `MISSION_REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '任务重复',
  `MISSION_EVENT` varchar(255) NOT NULL COMMENT 'event 目前包括（后续可能增加）：\r\n    start_success：开始成功\r\n    start_fail：开始失败\r\n    pause_success：暂停成功\r\n    pause_fail：暂停失败\r\n    resume_success：恢复成功\r\n    resume_fail：恢复失败\r\n    cancel_success：取消成功\r\n    cancel_fail：取消失败\r\n    finish：完成',
  `MISSION_ITEM_NAME` varchar(255) DEFAULT NULL COMMENT '任务item名称',
  `MISSION_DESCRIPTION` text NOT NULL COMMENT '事件描述，对于特殊的事件加以说明，若无说明则为空字符串',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  `CHARGING_STATUS` bit(1) DEFAULT b'0' COMMENT '充电状态  1：正在充电  0：未充电',
  `PLUGIN_STATUS` bit(1) DEFAULT b'0' COMMENT '1：插入充电桩   0：未插入充电桩',
  `POWER_PERCENT` int(11) DEFAULT '0' COMMENT '电量  范围  0-100',
  `ROS` varchar(200) DEFAULT NULL COMMENT 'ros当前位置信息',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_MISSION
-- ----------------------------

-- ----------------------------
-- Table structure for LOG_ELEVATOR
-- ----------------------------
DROP TABLE IF EXISTS `LOG_ELEVATOR`;
CREATE TABLE `LOG_ELEVATOR` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `ADDR` varchar(255) NOT NULL COMMENT '远程电梯地址',
  `VALUE` varchar(255) NOT NULL COMMENT '消息值',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_ELEVATOR
-- ----------------------------

-- ----------------------------
-- Table structure for TASK_MISSION_LIST
-- ----------------------------
DROP TABLE IF EXISTS `TASK_MISSION_LIST`;
CREATE TABLE `TASK_MISSION_LIST` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `SCENE_ID` bigint(20) DEFAULT NULL COMMENT '场景ID',
  `STATE` varchar(50) DEFAULT NULL COMMENT '任务执行状态',
  `REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '重复执行次数',
  `REPEAT_TIMES_REAL` int(11) DEFAULT NULL COMMENT '重复执行次数实时状态，查询状态时候的repeat_times值放到该字段',
  `ROBOT_CODE` varchar(50) DEFAULT NULL COMMENT '机器人编号',
  `ORDER_ID` bigint(20) NOT NULL COMMENT '订单编号',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `DESCRIPTION` varchar(255) NOT NULL COMMENT '描述',
  `MISSION_LIST_TYPE` varchar(255) NOT NULL COMMENT '任务类型',
  `INTERVAL_TIME` bigint(20) NOT NULL COMMENT '间隔时间',
  `START_TIME` bigint(20) NOT NULL COMMENT '开始时间',
  `STOP_TIME` bigint(20) NOT NULL COMMENT '结束时间',
  `PRIORITY` int(11) NOT NULL COMMENT '优先级',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of TASK_MISSION_LIST
-- ----------------------------

-- ----------------------------
-- Table structure for TASK_MISSION
-- ----------------------------
DROP TABLE IF EXISTS `TASK_MISSION`;
CREATE TABLE `TASK_MISSION` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `SCENE_ID` bigint(20) DEFAULT NULL COMMENT '场景ID',
  `MISSION_LIST_ID` bigint(20) NOT NULL COMMENT '任务列表ID',
  `STATE` varchar(50) DEFAULT NULL COMMENT '任务执行状态',
  `REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '重复执行次数',
  `REPEAT_TIMES_REAL` int(11) DEFAULT NULL COMMENT '重复执行次数实时状态，查询状态时候的repeat_times值放到该字段',
  `PRESET_MISSION_CODE` varchar(255) DEFAULT NULL COMMENT '预置任务编号',
  `ORDER_DETAIL_MISSION` varchar(255) DEFAULT NULL COMMENT '是否是order detail对应的任务，1:是; 0:不是',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `DESCRIPTION` varchar(255) NOT NULL COMMENT '描述',
  `INTERVAL_TIME` bigint(20) NOT NULL COMMENT '间隔时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of TASK_MISSION
-- ----------------------------

-- ----------------------------
-- Table structure for TASK_MISSION_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `TASK_MISSION_ITEM`;
CREATE TABLE `TASK_MISSION_ITEM` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `SCENE_ID` bigint(20) DEFAULT NULL COMMENT '场景ID',
  `MISSION_LIST_ID` bigint(20) NOT NULL COMMENT '任务列表ID',
  `MISSION_ID` bigint(20) NOT NULL COMMENT '任务ID',
  `STATE` varchar(50) DEFAULT NULL COMMENT '任务执行状态',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `DESCRIPTION` varchar(255) NOT NULL COMMENT '描述',
  `DATA` text NOT NULL COMMENT '任务详细/功能数据',
  `FEATURE_VALUE` varchar(255) NOT NULL COMMENT 'data对应子功能的唯一命令字串',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of TASK_MISSION_ITEM
-- ----------------------------

-- ----------------------------
-- Table structure for M_MERCHANT_STORE
-- ----------------------------
DROP TABLE IF EXISTS `M_MERCHANT_STORE`;
CREATE TABLE `M_MERCHANT_STORE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `NAME` varchar(256) NOT NULL COMMENT '门店名',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of M_MERCHANT_STORE
-- ----------------------------
INSERT INTO `M_MERCHANT_STORE` VALUES ('3', 'aaa3', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('4', 'aaa4', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('5', 'aaa5', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('6', 'aaa6', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('7', 'aaa7', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('8', 'aaa8', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('9', 'aaa9', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('10', 'aaa', '');
INSERT INTO `M_MERCHANT_STORE` VALUES ('11', 'aaa2', null);
INSERT INTO `M_MERCHANT_STORE` VALUES ('100', 'AGV测试门店', '');

-- ----------------------------
-- Table structure for oauth_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token` (
  `TOKEN_ID` varchar(255) DEFAULT NULL,
  `TOKEN` blob,
  `AUTHENTICATION_ID` varchar(255) DEFAULT NULL,
  `USER_NAME` varchar(255) DEFAULT NULL,
  `CLIENT_ID` varchar(255) DEFAULT NULL,
  `AUTHENTICATION` blob,
  `REFRESH_TOKEN` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_access_token
-- ----------------------------
INSERT INTO `oauth_access_token` VALUES ('2f2846855ef2e372b9711689b2be9473', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D36E902FD7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002464343732616133312D666432372D343135382D386339342D646132343934643737306166, '87a6a71dc7b7587f007a7f193023fbdc', 'ray', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000372617974000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F7870740003726179, null);
INSERT INTO `oauth_access_token` VALUES ('d26bb901a00038f82b456922f7d2a0a8', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D3386B63E7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002466316234353435652D343265342D346435312D393364322D326632623735646338316632, 'ea854ac2c137cb3f6ab46c05e7f85e0c', 'ray3', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D657400047261793374000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000472617933, null);
INSERT INTO `oauth_access_token` VALUES ('0b0252bea9f453538641fc1fb00c11a1', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D33A67A007870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002437333431316232652D356435342D343638662D396565612D346563643233366437363966, '818df0a0de81abf74f8e81de682d8477', 'fallout4', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000866616C6C6F75743474000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000866616C6C6F757434, null);
INSERT INTO `oauth_access_token` VALUES ('abd8c913bff6c8597b58ac3566be9bc3', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D33A6B3527870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002464616366373261392D393430362D343263612D613334312D363365313531616531303061, '50c651af1c87cd1abe97719e854c46b4', 'ceshi3', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000663657368693374000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F7870740006636573686933, null);
INSERT INTO `oauth_access_token` VALUES ('3b440006e8ab3cf3d04bc5e9258c2384', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D33A6CC1F7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002466363235393562322D653931372D343934312D616130332D336438646362633439646630, '3954a7491ec7e864a1e65bde2c6b3b6e', 'fall', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000466616C6C74000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000466616C6C, null);
INSERT INTO `oauth_access_token` VALUES ('28d066aaff494fb5f76a8e21ec30df8a', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D33A7750E7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002465306334313761322D316662382D343938332D386437372D626164396534316331666638, '15b185c340b9b3dfa73fcd1785866e2e', 'iverson', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000769766572736F6E74000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000769766572736F6E, null);
INSERT INTO `oauth_access_token` VALUES ('cca81f1ec86d2638f09b3a69050a02b3', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D33A7ACF37870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002438376161393338322D303939372D346130392D626531652D326438343666383362366335, 'e8c4588eb6469f5f5792e171cd446d0a', 'iverson1', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000869766572736F6E3174000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000869766572736F6E31, null);
INSERT INTO `oauth_access_token` VALUES ('90dfa5f1ddce05fdc7393252fbc39c50', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D370812AA7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002463626464393763342D653966652D346430622D626561302D343337626433373335643935, '659e9b4b4c0e7585d654983fa949e935', 'ceshi2', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D6574000663657368693274000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F7870740006636573686932, null);
INSERT INTO `oauth_access_token` VALUES ('e3eb3a4a815ec7d7c43599580e7e0c8a', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015D3746D2B97870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002432376433306235352D343539632D343132392D613831312D336166303735396538623266, 'd63d43c80725fd6235213b52f06205fc', 'bigman', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D657400066269676D616E74000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F78707400066269676D616E, null);

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `CLIENT_ID` varchar(255) NOT NULL,
  `RESOURCE_IDS` varchar(255) DEFAULT NULL,
  `CLIENT_SECRET` varchar(255) DEFAULT NULL,
  `SCOPE` varchar(255) DEFAULT NULL,
  `AUTHORIZED_GRANT_TYPES` varchar(255) DEFAULT NULL,
  `WEB_SERVER_REDIRECT_URI` varchar(255) DEFAULT NULL,
  `AUTHORITIES` varchar(255) DEFAULT NULL,
  `ACCESS_TOKEN_VALIDITY` int(11) DEFAULT NULL,
  `REFRESH_TOKEN_VALIDITY` int(11) DEFAULT NULL,
  `ADDITIONAL_INFORMATION` varchar(4096) DEFAULT NULL,
  `AUTOAPPROVE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`CLIENT_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('web', 'user,auth', 'web_secret', 'user_read,user_write', 'password', '', 'read,write', null, null, '{}', 'user_read,user_write');

-- ----------------------------
-- Table structure for OFFLINE_MESSAGE
-- ----------------------------
DROP TABLE IF EXISTS `OFFLINE_MESSAGE`;
CREATE TABLE IF NOT EXISTS OFFLINE_MESSAGE
(
  UUID VARCHAR(228) PRIMARY KEY NOT NULL,
  SENDER_ID VARCHAR(128) NOT NULL COMMENT '发送者ID',
  RECEIVER_ID VARCHAR(128) NOT NULL COMMENT '接收者ID',
  MESSAGE_KIND SMALLINT DEFAULT 0 COMMENT '消息种类，默认为0，0：文本消息，1：二进制消息',
  MESSAGE_TYPE VARCHAR(256) NOT NULL COMMENT '消息类型',
  MESSAGE_STATUS_TYPE SMALLINT DEFAULT 0 COMMENT '消息状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成',
  RELY_MESSAGE VARCHAR(256) COMMENT '回执消息',
  MESSAGE_TEXT TEXT COMMENT '文本消息',
  MESSAGE_BINARY BLOB COMMENT '二进制消息',
  SEND_COUNT INTEGER DEFAULT 1 COMMENT '发送次数',
  SEND_TIME DATETIME NOT NULL COMMENT '发送时间',
  UPDATE_TIME DATETIME COMMENT '更新时间',
  SUCCESS BIT COMMENT '是否发送成功'
);

-- ----------------------------
-- Table structure for OR_GOODS
-- ----------------------------
DROP TABLE IF EXISTS `OR_GOODS`;
CREATE TABLE `OR_GOODS` (
  `ID` bigint(12) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(64) DEFAULT NULL COMMENT '名称',
  `UNIT` varchar(12) DEFAULT NULL COMMENT '单位',
  `DELETE_STATUS` tinyint(1) DEFAULT NULL,
  `GOOD_TYPE_ID` bigint(12) DEFAULT NULL,
  `STORE_ID` bigint(12) DEFAULT NULL,
  `CREATED_BY` bigint(12) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OR_GOODS
-- ----------------------------
INSERT INTO `OR_GOODS` VALUES ('1', 'iphone7', '个', '0', '1', '100', '53', '2017-07-07 17:23:34');

-- ----------------------------
-- Table structure for OR_GOODS_INFO
-- ----------------------------
DROP TABLE IF EXISTS `OR_GOODS_INFO`;
CREATE TABLE `OR_GOODS_INFO` (
  `ID` bigint(12) NOT NULL AUTO_INCREMENT,
  `ORDER_DETAIL_ID` bigint(12) DEFAULT NULL,
  `GOODS_ID` bigint(12) DEFAULT NULL,
  `NUM` int(12) DEFAULT NULL,
  `BOX_NUM` int(12) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of OR_GOODS_INFO
-- ----------------------------


-- ----------------------------
-- Table structure for OR_ORDER
-- ----------------------------
DROP TABLE IF EXISTS `OR_ORDER`;
CREATE TABLE `OR_ORDER` (
  `ID` bigint(12) NOT NULL AUTO_INCREMENT,
  `ORDER_SETTING_ID` bigint(12) DEFAULT NULL COMMENT '关联基本设置',
  `ROBOT_ID` bigint(12) DEFAULT NULL COMMENT '启用机器人',
  `START_STATION_ID` bigint(12) DEFAULT NULL COMMENT '下单站',
  `NEED_SHELF` tinyint(1) DEFAULT NULL,
  `SHELF_ID` bigint(12) DEFAULT NULL,
  `SCENE_ID` bigint(12) DEFAULT NULL COMMENT '场景id',
  `STATUS` int(12) DEFAULT NULL COMMENT '状态',
  `STORE_ID` bigint(12) DEFAULT NULL,
  `CREATED_BY` bigint(12) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OR_ORDER
-- ----------------------------


-- ----------------------------
-- Table structure for OR_ORDER_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `OR_ORDER_DETAIL`;
CREATE TABLE `OR_ORDER_DETAIL` (
  `ID` bigint(12) NOT NULL AUTO_INCREMENT,
  `ORDER_ID` bigint(12) DEFAULT NULL,
  `STATION_ID` bigint(12) DEFAULT NULL,
  `STATUS` int(12) DEFAULT NULL,
  `FINISH_DATE` datetime DEFAULT NULL,
  `STORE_ID` bigint(12) DEFAULT NULL,
  `CREATED_BY` bigint(12) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OR_ORDER_DETAIL
-- ----------------------------


-- ----------------------------
-- Table structure for OR_ORDER_SETTING
-- ----------------------------
DROP TABLE IF EXISTS `OR_ORDER_SETTING`;
CREATE TABLE `OR_ORDER_SETTING` (
  `ID` bigint(12) NOT NULL AUTO_INCREMENT,
  `NICK_NAME` varchar(64) DEFAULT NULL,
  `STATION_ID` bigint(12) DEFAULT NULL,
  `START_POINT_ID` bigint(12) DEFAULT NULL,
  `END_POINT_ID` bigint(12) DEFAULT NULL,
  `NEED_SIGN` tinyint(1) DEFAULT NULL,
  `GOODS_TYPE_ID` bigint(20) DEFAULT NULL,
  `PACKAGE_TYPE` int(12) DEFAULT NULL,
  `ROBOT_TYPE_ID` bigint(12) DEFAULT NULL,
  `DEFAULT_SETTING` tinyint(1) DEFAULT NULL,
  `DELETE_STATUS` tinyint(1) DEFAULT NULL,
  `STORE_ID` bigint(12) DEFAULT NULL,
  `CREATED_BY` bigint(12) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records of OR_ORDER_SETTING
-- ----------------------------

-- ----------------------------
-- Table structure for RE_RESOURCE
-- ----------------------------
DROP TABLE IF EXISTS `RE_RESOURCE`;
CREATE TABLE `RE_RESOURCE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RESOURCE_TYPE` int(12) DEFAULT NULL COMMENT '资源类型',
  `ORIGIN_NAME` varchar(64) DEFAULT NULL COMMENT '原始名',
  `GENERATE_NAME` varchar(255) DEFAULT NULL COMMENT '生成名',
  `FILE_TYPE` varchar(255) DEFAULT NULL COMMENT '文件类型',
  `FILE_SIZE` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `MD5` varchar(255) DEFAULT NULL COMMENT 'md5',
  `PATH` varchar(255) DEFAULT NULL COMMENT '路径',
  `CONTENT` varchar(255) DEFAULT NULL COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATED_BY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of RE_RESOURCE
-- ----------------------------
INSERT INTO `RE_RESOURCE` VALUES ('1', '1', '5.png', 'd4a4cd9a-bed2-4733-9d94-ab7e68ca68de', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/5.png', null, '2017-06-19 09:18:36', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('2', '1', 'app.js', '7100045e-c7df-4879-b659-5f394c09c914', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 09:28:23', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('3', '1', 'app.js', '250d38d0-b641-4781-a302-ac4a2bf0dbae', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 09:29:10', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('4', '1', 'app.js', '7732995d-9784-465b-ae0e-787c53e18e67', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 09:31:06', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('5', '1', 'app.js', 'b37b513d-b2a9-4b97-9a99-63b1c982c2b5', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 09:31:13', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('6', '1', '5.png', '310564b0-7705-4614-95ce-2941c899f4e6.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/5.png', null, '2017-06-19 09:44:06', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('7', '1', '5.png', '36a0ac63-bd16-412b-8bba-e4b7c689993f.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/36a0ac63-bd16-412b-8bba-e4b7c689993f.png', null, '2017-06-19 09:47:57', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('8', '1', '5.png', '802f656a-e208-45c9-83ce-a79761b7eb8f.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/802f656a-e208-45c9-83ce-a79761b7eb8f.png', null, '2017-06-19 09:49:23', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('9', '1', '5.png', 'fa318d69-c128-4f6c-a81d-963a699a9f78.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '1/fa318d69-c128-4f6c-a81d-963a699a9f78.png', null, '2017-06-19 09:50:45', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('10', '1', 'angular.min.js', 'b0ae5188-4251-4f31-a939-1280b4b21a10', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:02:10', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('11', '1', 'angular.min.js', '3e2bb33b-ccc8-4f96-9cb7-4bc48e9c68a7', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:03:07', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('12', '1', 'angular.min.js', 'd2a19910-789f-4b57-8648-8594c42c37dd', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:04:11', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('13', '1', 'app.js', '7237df11-3b0a-4bf3-a53d-892b4a5e8210', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 10:06:31', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('14', '1', 'app.js', '68c08365-01ee-4fcd-ab9a-0b89cc0c9c79', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 10:07:04', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('15', '1', 'app.js', '31b30c94-dfd6-413f-8424-3fe8bc87ecfc', 'application/javascript', '151894', '297a37f15879612184747164886a9a83', '/app.js', null, '2017-06-19 10:07:07', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('16', '1', 'angular.min.js', '00432c85-36be-44d9-a6e0-01fcb103542b', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:08:27', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('17', '1', 'angular.min.js', '4c832c94-f835-46ab-8043-e984aa939a82', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:53:43', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('18', '1', 'angular.min.js', 'e62181f8-3335-4919-b1c6-0063f2150f55', 'application/javascript', '154641', '3af021f96f77b434753d8b1e7fa90186', '/angular.min.js', null, '2017-06-19 10:54:16', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('19', '1', 'bc.html', 'd8b214e1-7c38-404c-86bb-003d86da62b0.html', 'text/html', '3559', '6d449739def1cde0ac23608021f8970f', '/1/d8b214e1-7c38-404c-86bb-003d86da62b0.html', null, '2017-06-19 14:37:30', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('20', '1', 'FuMJj5jpAK8_wd2c0KvdwEmCaATt.jpg', 'e930a761-ac3f-44c8-845f-137abe0eec72.jpg', 'image/jpeg', '3074', '467e9d2c9f54d1e69917481b45f674a9', '/1/e930a761-ac3f-44c8-845f-137abe0eec72.jpg', null, '2017-06-20 14:58:14', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('21', '1', 'nginx.conf', '5da1d0e3-1a7f-4046-993c-22bb6f2fb3d8.conf', 'application/octet-stream', '9313', 'fa5d5a2d933e98b1204d0eba05db5212', '/1/5da1d0e3-1a7f-4046-993c-22bb6f2fb3d8.conf', null, '2017-06-20 16:58:46', null, null);
INSERT INTO `RE_RESOURCE` VALUES ('22', '1', '2017_01_13_17_12.jpeg', '14578b49-b734-4ad0-9092-7a8223302e1a.jpeg', 'image/jpeg', '308352', '60184e4e624a0be01626426359806d54', '/1/14578b49-b734-4ad0-9092-7a8223302e1a.jpeg', null, '2017-06-23 14:39:10', '1', '1');
INSERT INTO `RE_RESOURCE` VALUES ('23', '1', '2017_01_13_17_12.jpeg', 'd9fe3f48-9e7a-457e-a9cd-f349cad9a4a7.jpeg', 'image/jpeg', '308352', '60184e4e624a0be01626426359806d54', '/1/d9fe3f48-9e7a-457e-a9cd-f349cad9a4a7.jpeg', null, '2017-06-26 14:12:49', '100', '1');
INSERT INTO `RE_RESOURCE` VALUES ('24', '1', '5.png', '51043f50-4d03-44d5-8e51-6c8d54f74c6f.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/1/51043f50-4d03-44d5-8e51-6c8d54f74c6f.png', null, '2017-06-29 12:29:25', '100', '1');
INSERT INTO `RE_RESOURCE` VALUES ('25', '1', '[~V9T9V4%VWH9{(IRRD1QO4.jpg', 'e2f57a83-2741-40b3-bf80-07e467770b99.jpg', 'image/jpeg', '113328', 'ed4ded98d2b131009714631e752ff620', '/1/e2f57a83-2741-40b3-bf80-07e467770b99.jpg', null, '2017-06-29 12:55:40', '100', '1');
INSERT INTO `RE_RESOURCE` VALUES ('26', '1', '[~V9T9V4%VWH9{(IRRD1QO4.jpg', '29f781f7-dc0e-4f67-b7e2-959d16957bf6.jpg', 'image/jpeg', '113328', 'ed4ded98d2b131009714631e752ff620', '/1/29f781f7-dc0e-4f67-b7e2-959d16957bf6.jpg', null, '2017-06-29 13:04:44', '100', '1');
INSERT INTO `RE_RESOURCE` VALUES ('27', '1', '[~V9T9V4%VWH9{(IRRD1QO4.jpg', '7ea13eb4-8ee3-4628-9d3a-7567db1c876b.jpg', 'image/jpeg', '113328', 'ed4ded98d2b131009714631e752ff620', '/1', null, '2017-06-29 14:13:07', '100', '1');
INSERT INTO `RE_RESOURCE` VALUES ('28', '1', '1.png', '2b2cf9f1-1d47-486e-855a-5286880c5666.png', 'image/png', '121088', '346d486458a9cb0f6c7e175b98188b8a', '/1/2b2cf9f1-1d47-486e-855a-5286880c5666.png', null, '2017-07-04 15:57:50', '100', '93');
INSERT INTO `RE_RESOURCE` VALUES ('29', '1', '5.png', '56716065-2b1e-4e0f-b541-8b0d465e21b1.png', 'image/png', '202830', '0322070d337dc723c65028fcad2f4b2d', '/1/56716065-2b1e-4e0f-b541-8b0d465e21b1.png', null, '2017-07-04 15:58:36', '100', '53');
INSERT INTO `RE_RESOURCE` VALUES ('30', '1', 'Gerrit服务器使用指南.docx', 'a09ad55b-43c7-4a20-a85a-2ab03c682f9c.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '1328508', '2d55c730f07321410cf7d92c60985247', '/1/a09ad55b-43c7-4a20-a85a-2ab03c682f9c.docx', null, '2017-07-06 20:31:46', '100', '53');

-- ----------------------------
-- Table structure for RECEIVE_MESSAGE
-- ----------------------------
DROP TABLE IF EXISTS `RECEIVE_MESSAGE`;
CREATE TABLE `RECEIVE_MESSAGE` (
  `ID` bigint(20) NOT NULL,
  `SENDER_ID` varchar(128) NOT NULL,
  `SEND_DEVICE_TYPE` varchar(128) NOT NULL,
  `RECEIVER_ID` varchar(128) NOT NULL,
  `RECEIVER_DEVICE_TYPE` varchar(128) NOT NULL,
  `WEBSOCKET_ID` varchar(128) DEFAULT NULL,
  `RECEIPT_WEBSOCKET` bit(1) NOT NULL,
  `FINISH` bit(1) NOT NULL,
  `MESSAGE_KIND` smallint(6) NOT NULL DEFAULT '0',
  `MESSAGE_TYPE` varchar(256) NOT NULL,
  `FAIL_RESEND` bit(1) NOT NULL,
  `SESSION_ID` int(11) DEFAULT NULL,
  `MESSAGE_STATUS_TYPE` smallint(6) NOT NULL DEFAULT '0',
  `VERSION` varchar(256) DEFAULT NULL,
  `RELY_MESSAGE` varchar(256) DEFAULT NULL,
  `MESSAGE_TEXT` text,
  `MESSAGE_BINARY` blob,
  `SEND_COUNT` int(11) NOT NULL DEFAULT '0',
  `SEND_TIME` datetime NOT NULL,
  `UPDATE_TIME` datetime NOT NULL,
  `SUCCESS` bit(1) NOT NULL,
  PRIMARY KEY (`ID`,`SENDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of RECEIVE_MESSAGE
-- ----------------------------
INSERT INTO `RECEIVE_MESSAGE` VALUES ('34', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '65', '2017-06-14 13:45:50', '2017-06-14 13:49:51', '');
INSERT INTO `RECEIVE_MESSAGE` VALUES ('35', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '4', '2017-06-14 17:42:32', '2017-06-14 17:45:29', '');
INSERT INTO `RECEIVE_MESSAGE` VALUES ('36', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '3', '2017-06-14 17:47:54', '2017-06-14 17:48:01', '');

-- ----------------------------
-- Table structure for SHELFS_GOOD_TYPES_RELATIONS
-- ----------------------------
DROP TABLE IF EXISTS `SHELFS_GOOD_TYPES_RELATIONS`;
CREATE TABLE `SHELFS_GOOD_TYPES_RELATIONS` (
  ` ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系表 ID 序号',
  `SHELF_ID` bigint(20) DEFAULT NULL COMMENT '货架 ID 编号',
  `GOOD_TYPEID` bigint(20) DEFAULT NULL COMMENT '可装备货物的类型 ID 编号',
  PRIMARY KEY (` ID`),
  KEY `SHELFS_GOOD_TYPES_RELATIONS_AS_SHELF_ID_fk` (`SHELF_ID`),
  KEY `SHELFS_GOOD_TYPES_RELATIONS_AS_GOOD_TYPE_ID_fk` (`GOOD_TYPEID`),
  CONSTRAINT `SHELFS_GOOD_TYPES_RELATIONS_AS_GOOD_TYPE_ID_fk` FOREIGN KEY (`GOOD_TYPEID`) REFERENCES `AS_GOODS_TYPE` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SHELFS_GOOD_TYPES_RELATIONS_AS_SHELF_ID_fk` FOREIGN KEY (`SHELF_ID`) REFERENCES `AS_SHELF` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 COMMENT='货架与可装配货物类型关系表';

-- ----------------------------
-- Records of SHELFS_GOOD_TYPES_RELATIONS
-- ----------------------------
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('12', '27', '1');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('13', '27', '2');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('14', '27', '3');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('15', '27', '4');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('16', '27', '5');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('17', '25', '2');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('18', '25', '4');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('28', '28', '1');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('29', '28', '5');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('30', '28', '4');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('31', '28', '3');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('32', '28', '2');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('33', '23', '2');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('34', '23', '1');
INSERT INTO `SHELFS_GOOD_TYPES_RELATIONS` VALUES ('35', '19', null);

-- ----------------------------
-- Table structure for AS_ROBOT_CHARGER_MAP_POINT_XREF【机器人充电桩关联表】
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT_CHARGER_MAP_POINT_XREF`;
CREATE TABLE `AS_ROBOT_CHARGER_MAP_POINT_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROBOT_ID` bigint(20) DEFAULT NULL COMMENT '机器人ID',
  `CHARGER_MAP_POINT_ID` bigint(20) DEFAULT NULL COMMENT '充电桩点ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;



-- ----------------------------
DROP TABLE IF EXISTS `LOG_BASE_STATE`;
CREATE TABLE `LOG_BASE_STATE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATED_BY` bigint(20) DEFAULT NULL,
  `DEVICE_CODE` varchar(256) DEFAULT NULL,
  `RES` bit DEFAULT 0,
  `POWER_ON` bit DEFAULT 0  COMMENT '开关机',
  `NORMAL` bit DEFAULT 0 COMMENT '正常',
  `IO_EMERGENCY_STOP` bit DEFAULT 0 COMMENT 'IO急停',
  `SWITCH_EMERGENCY_STOP` bit DEFAULT 0 COMMENT '开关急停',
  `UNDER_VOLTAGE_EMERGENCY_STOP` bit DEFAULT 0 COMMENT '欠压停机',
  `OVER_SPEED_SMERGENCY_STOP` bit DEFAULT 0 COMMENT '过速停机',
  `LEFT_DRIVER_FLOW` bit DEFAULT 0 COMMENT '驱动器过流',
  `LEFT_DRIVER_ERROR` bit DEFAULT 0 COMMENT '编码器错误',
  `LEFT_POOR_POSITION` bit DEFAULT 0 COMMENT '位置超差',
  `LEFT_DRIVER_OVERLOAD` bit DEFAULT 0 COMMENT '驱动器过载',
  `LEFT_MOTOR_HIGH_TEMPERATURE` bit DEFAULT 0 COMMENT '电机过温',
  `LEFT_MOTOR_COMMUNICATION_BREAK` bit DEFAULT 0 COMMENT '电机通信断线',
  `LEFT_PWM_CONTROLL_BREAK` bit DEFAULT 0 COMMENT 'PWM控制断线',
  `RIGHT_DRIVER_FLOW` bit DEFAULT 0 COMMENT '驱动器过流',
  `RIGHT_DRIVER_ERROR` bit DEFAULT 0 COMMENT '编码器错误',
  `RIGHT_POOR_POSITION` bit DEFAULT 0 COMMENT '位置超差',
  `RIGHT_DRIVER_OVERLOAD` bit DEFAULT 0 COMMENT '驱动器过载',
  `RIGHT_MOTOR_HIGH_TEMPERATURE` bit DEFAULT 0 COMMENT '电机过温',
  `RIGHT_MOTOR_COMMUNICATION_BREAK` bit DEFAULT 0 COMMENT '电机通信断线',
  `RIGHT_PWM_CONTROLL_BREAK` bit DEFAULT 0 COMMENT 'PWM控制断线',
  `LEFT_ANTI_DROPPING` bit DEFAULT 0 COMMENT '防跌落左传感器',
  `MIDDLE_ANTI_DROPPING` bit DEFAULT 0 COMMENT '防跌落中传感器',
  `RIGHT_ANTI_DROPPING` bit DEFAULT 0 COMMENT '防跌落右传感器',
  `LEFT_BASE_MICRO_SWITCH` bit DEFAULT 0 COMMENT '防碰撞左开关',
  `MIDDLE_BASE_MICRO_SWITCH` bit DEFAULT 0 COMMENT '防碰撞中开关',
  `RIGHT_BASE_MICRO_SWITCH` bit DEFAULT 0 COMMENT '防碰撞右开关',
    PRIMARY KEY (`ID`)
) ;