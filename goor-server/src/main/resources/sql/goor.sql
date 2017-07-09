/*
Navicat MySQL Data Transfer

Source Server         : 172.16.0.15
Source Server Version : 50624
Source Host           : 172.16.0.15:3306
Source Database       : goor

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-07-01 19:58:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for A_MAP_POINT
-- ----------------------------
DROP TABLE IF EXISTS `A_MAP_POINT`;
CREATE TABLE `A_MAP_POINT` (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  STORE_ID BIGINT COMMENT '店铺ID',
  CREATED_BY BIGINT COMMENT '创建人',
  CREATE_TIME datetime COMMENT '创建时间' DEFAULT NOW(),
  POINT_NAME VARCHAR(256) NOT NULL COMMENT '唯一标识符',
  POINT_ALIAS VARCHAR(256) NOT NULL COMMENT '显示名称',
  SCENE_NAME VARCHAR(256) COMMENT '地图场景名',
  MAP_NAME VARCHAR(256) COMMENT '地图名',
  POINT_LEVEL INT  COMMENT '导航点等级',
  X DOUBLE(20,15) COMMENT '坐标X',
  Y DOUBLE(20,15) COMMENT '坐标Y',
  TH DOUBLE(20,15) COMMENT '坐标旋转角度',
  MAP_POINT_TYPE_ID INT COMMENT '点类型索引',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_MAP_POINT
-- ----------------------------
INSERT INTO `A_MAP_POINT` VALUES ('10', 'G1', '2', 'example', 'F001', '2.000', '3.000', '2.000', '2', null);
INSERT INTO `A_MAP_POINT` VALUES ('11', 'G2', '2', 'example', 'F001', '2.000', '3.000', '2.000', '2', null);
INSERT INTO `A_MAP_POINT` VALUES ('13', '测试as', '别名ad', 'asdfasdf', 'asdfasdf', '1.200', '2.600', '12.000', '4', null);
INSERT INTO `A_MAP_POINT` VALUES ('14', 'sadfasdfqwe', '撒的发生', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '5', '0');
INSERT INTO `A_MAP_POINT` VALUES ('15', 'sadfasdfqwe撒地方', '撒的发生', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '6', '0');
INSERT INTO `A_MAP_POINT` VALUES ('16', '爱迪生 qwe撒地方', '撒的发生', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '7', '0');
INSERT INTO `A_MAP_POINT` VALUES ('17', '爱迪asd生 asd', '撒的发生', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '8', '0');
INSERT INTO `A_MAP_POINT` VALUES ('18', 'asasdasdasd', '撒的发生asd', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '1', '0');
INSERT INTO `A_MAP_POINT` VALUES ('19', '爱迪asdasdasd生 asdasd', '撒的发生', 'asdfasd', 'asdfasdfasdf', '10.200', '15.200', '15.800', '8', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_STATION
-- ----------------------------
INSERT INTO `A_STATION` VALUES ('2', 'aaab2', '', '3', '100', null, null);
INSERT INTO `A_STATION` VALUES ('3', 'aaa3', '', '1', '100', null, null);
INSERT INTO `A_STATION` VALUES ('4', 'aaa4', '', '1', '100', null, null);
INSERT INTO `A_STATION` VALUES ('5', 'aaa5', '', '1', '100', null, null);
INSERT INTO `A_STATION` VALUES ('6', 'aaa6', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('7', 'aaa7', '', '1', '100', null, null);
INSERT INTO `A_STATION` VALUES ('8', 'aaa8', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('9', 'aaa9', '', '3', '100', null, null);
INSERT INTO `A_STATION` VALUES ('13', 'aaa10', '', '1', '100', '100', '2017-06-23 19:23:24');
INSERT INTO `A_STATION` VALUES ('14', 'aaa2', null, '1', '100', '100', '2017-06-30 15:41:51');
INSERT INTO `A_STATION` VALUES ('15', 'aaa2', null, '1', '100', '100', '2017-07-01 18:09:56');

-- ----------------------------
-- Table structure for A_STATION_MAP_POINT_XREF
-- ----------------------------
DROP TABLE IF EXISTS `A_STATION_MAP_POINT_XREF`;
CREATE TABLE `A_STATION_MAP_POINT_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `MAP_POINT_ID` bigint(20) NOT NULL COMMENT '点索引',
  `STATION_ID` bigint(20) NOT NULL COMMENT '站索引',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of A_STATION_MAP_POINT_XREF
-- ----------------------------
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('20', '11', '14');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('21', '10', '14');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('22', '11', '15');
INSERT INTO `A_STATION_MAP_POINT_XREF` VALUES ('23', '10', '15');

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
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_USER
-- ----------------------------
INSERT INTO `AC_USER` VALUES ('53', 'ray', '123456', '', null, '100', '1', '2017-06-27 10:59:28');
INSERT INTO `AC_USER` VALUES ('54', 'test', '123456', '', '1234', '100', '1', '2017-06-27 10:59:31');
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
INSERT INTO `AC_USER` VALUES ('81', 'test30', '123456', '', '4321', '100', null, '2017-06-28 11:49:45');
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
INSERT INTO `AC_USER` VALUES ('101', 'iverson1', '123456', '', null, '100', '1', '2017-07-01 16:28:45');

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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_USER_STATION_XREF
-- ----------------------------
INSERT INTO `AC_USER_STATION_XREF` VALUES ('4', '93', '2');
INSERT INTO `AC_USER_STATION_XREF` VALUES ('5', '94', '5');
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
  PRIMARY KEY (`ID`),
  KEY `TYPE` (`TYPE_ID`),
  CONSTRAINT `AS_ROBOT_ibfk_1` FOREIGN KEY (`TYPE_ID`) REFERENCES `AS_ROBOT_TYPE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=305 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_ROBOT
-- ----------------------------
INSERT INTO `AS_ROBOT` VALUES ('26', 'carrier1', 'CA1', '2', 'carrier1', '2017-06-20 10:30:03', null, '100', '2017-06-21 10:30:52', '');
INSERT INTO `AS_ROBOT` VALUES ('28', 'carrier2', 'ca2', '2', 'carrier2', '2017-06-20 10:30:09', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('29', 'carrier3', 'ca4', '1', 'carrier3', '2017-06-20 10:30:12', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('30', '5675', '567', '1', '', '2017-06-20 10:30:14', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('31', '756757', '75675', '1', '5757', '2017-06-20 10:30:17', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('32', '75675', '75675756', '1', '', '2017-06-20 10:30:21', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('33', '57657', '7567567', '1', '567567', '2017-06-20 10:30:23', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('34', '76575', 'utyutyu', '1', '', '2017-06-20 10:30:26', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('35', 'gjh', '75675gj', '1', 'gj', '2017-06-20 10:30:29', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('36', '7567', 'jh56756', '1', '56756757', '2017-06-20 10:30:32', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('37', '测试', 'ceshi', '1', '测试测试', '2017-06-20 10:30:34', null, '100', '2017-06-20 10:59:40', '');
INSERT INTO `AS_ROBOT` VALUES ('40', '广泛士大夫敢死队公司', 'gdsgsd', '1', 'gsdgsdg', null, null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('49', '测试啦3', '911111', '1', null, '2017-06-22 09:46:26', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('50', '测试啦222', '9112', '2', null, '2017-06-23 16:11:46', null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('51', '测试啦22222', '91124', '2', null, '2017-06-23 16:47:40', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('52', '测试啦222222', '9112421', '2', null, null, null, '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('53', '测试个', '92', '2', null, '2017-06-23 16:53:28', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('54', 'xxxxxxxxxxxxxx', '02100059', '1', '自己测试新添加的机器人信息', '2017-06-27 10:28:01', '1', '100', '2017-06-27 10:31:26', null);
INSERT INTO `AS_ROBOT` VALUES ('100', 'vimkk', '9099', '1', 'detail description', '2017-06-27 10:45:54', '1', '100', '2017-06-27 10:45:54', '');
INSERT INTO `AS_ROBOT` VALUES ('200', 'vimkk', '9099', '1', 'detail description', '2017-06-27 10:55:50', '1', '100', '2017-06-27 10:55:50', '');
INSERT INTO `AS_ROBOT` VALUES ('201', 'petermain', 'cccddd9', '1', '自己测试新添加的机器人信息', '2017-06-27 10:55:50', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('300', 'vimkk', '9099', '1', 'detail description', '2017-06-27 11:18:35', '1', '100', '2017-06-27 11:18:35', '');
INSERT INTO `AS_ROBOT` VALUES ('301', '测试密码', 'fsdf342423', '1', '', '2017-06-29 11:34:07', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('302', '测试密码2', 'fgsdf435', '3', '', '2017-06-29 11:34:30', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('303', '测试密码3', 'fsd3232', '2', '', '2017-06-29 11:37:02', '1', '100', null, '');
INSERT INTO `AS_ROBOT` VALUES ('304', 'meiya_robot', 'ylae', '1', null, '2017-06-30 16:57:13', '1', '100', '2017-06-30 17:04:02', null);

-- ----------------------------
-- Table structure for AS_ROBOT_CONFIG
-- ----------------------------
DROP TABLE IF EXISTS `AS_ROBOT_CONFIG`;
CREATE TABLE `AS_ROBOT_CONFIG` (
  `ROBOT_ID` int(11) NOT NULL COMMENT '机器人ID',
  `BATTERY_THRESHOLD` int(3) DEFAULT NULL COMMENT '电量阈值'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_ROBOT_CONFIG
-- ----------------------------
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('26', '10');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('28', '20');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('29', '30');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('30', '20');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('31', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('32', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('33', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('34', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('35', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('36', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('37', '15');
INSERT INTO `AS_ROBOT_CONFIG` VALUES ('40', '15');

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AS_SHELF
-- ----------------------------
INSERT INTO `AS_SHELF` VALUES ('1', 'test0', 'test0', 'tes0', 'test0', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('2', 'test1', 'test1', 'test1', 'test1', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('3', 'test2', 'test2', 'tes2', 'test2', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('4', 'test3', 'test3', 'tes3', 'test3', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('5', 'test4', 'test4', 'test4', 'test4', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('9', 'test8', 'test8', 'test8', 'test8', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('10', 'test9', 'test9', 'tes9', 'test9', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('11', 'test10', 'test10', 'tes10', 'test10', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('12', 'test11', 'test11', 'test11', 'test11', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('13', 'test12', 'test12', 'test12', 'test12', null, null, null, null);
INSERT INTO `AS_SHELF` VALUES ('14', 'huojiaA', 'A货架1', '1234', '88888', '99999', '100', '1', '2017-06-30 17:09:23');

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of C_FILE_UPLOAD
-- ----------------------------
INSERT INTO `C_FILE_UPLOAD` VALUES ('1', 'upload_2017-06-23.zip', '1234632', 'e:\\download_home\\upload\\upload_2017-06-23.zip', null, '0', '2017-06-23 17:29:47', '2017-06-23 17:29:47');

-- ----------------------------
-- Table structure for CHARGING_INFO
-- ----------------------------
DROP TABLE IF EXISTS `CHARGING_INFO`;
CREATE TABLE `CHARGING_INFO` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DEVICE_ID` varchar(256) NOT NULL,
  `CHARGING_STATUS` bit(1) NOT NULL,
  `PLUGIN_STATUS` bit(1) NOT NULL,
  `POWER_PERCENT` int(11) NOT NULL,
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of CHARGING_INFO
-- ----------------------------
INSERT INTO `CHARGING_INFO` VALUES ('46', 'cookyPlus1301_jelynn', '\0', '\0', '62', '2017-06-19 09:55:30');
INSERT INTO `CHARGING_INFO` VALUES ('47', 'cookyPlus1301_jelynn', '\0', '\0', '60', '2017-06-19 10:00:37');

-- ----------------------------
-- Table structure for D_FEATURE_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `D_FEATURE_ITEM`;
CREATE TABLE `D_FEATURE_ITEM` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `VALUE` varchar(256) DEFAULT NULL COMMENT '值',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_FEATURE_ITEM
-- ----------------------------
INSERT INTO `D_FEATURE_ITEM` VALUES ('1', '测试', 'hahahah', '你猜啊');
INSERT INTO `D_FEATURE_ITEM` VALUES ('2', '测试', 'hahahah', '你猜啊');
INSERT INTO `D_FEATURE_ITEM` VALUES ('3', '测试', 'hahahah', '你猜啊');
INSERT INTO `D_FEATURE_ITEM` VALUES ('4', '测试', 'hahahah', '你猜啊');
INSERT INTO `D_FEATURE_ITEM` VALUES ('5', '测试1', 'hahahah啊', '你猜啊1');
INSERT INTO `D_FEATURE_ITEM` VALUES ('6', '测试13', 'hahahah啊3', '你猜啊1');

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
-- Table structure for D_MISSION_CHAIN
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_CHAIN`;
CREATE TABLE `D_MISSION_CHAIN` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `REPEAT_COUNT` int(11) DEFAULT NULL COMMENT '重复次数',
  `INTERVAL_TIME` bigint(20) DEFAULT NULL COMMENT '间隔时间',
  `MISSION_MAIN_ID` bigint(20) DEFAULT NULL COMMENT '总任务编号',
  `PRIORITY` int(11) DEFAULT NULL COMMENT '优先等级',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_CHAIN
-- ----------------------------
INSERT INTO `D_MISSION_CHAIN` VALUES ('2', 'asdasdfasdjkl', 'asdfasdfasdf', '2016-06-14 18:52:45', null, '8', '4545748', null, '1');

-- ----------------------------
-- Table structure for D_MISSION_MAIN
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_MAIN`;
CREATE TABLE `D_MISSION_MAIN` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `DEVICE_ID` varchar(256) DEFAULT NULL COMMENT '设备编号',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `INTERVAL_TIME` bigint(20) DEFAULT NULL COMMENT '间隔时间',
  `REPEAT_COUNT` int(11) DEFAULT NULL COMMENT '重复次数',
  `START_TIME` bigint(20) DEFAULT NULL COMMENT '开始时间',
  `PRIORITY` int(11) DEFAULT NULL COMMENT '优先等级',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_MAIN
-- ----------------------------
INSERT INTO `D_MISSION_MAIN` VALUES ('9', 'afasdf', 'aasdfasdf', 'sdfa', '2017-08-01 15:22:20', '2017-06-14 13:43:58', '545455', '45', '165124502452', '5');

-- ----------------------------
-- Table structure for D_MISSION_MAIN_CHAIN_XREF
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_MAIN_CHAIN_XREF`;
CREATE TABLE `D_MISSION_MAIN_CHAIN_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MISSION_MAIN_ID` bigint(20) DEFAULT NULL,
  `MISSION_CHAIN_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_MAIN_CHAIN_XREF
-- ----------------------------
INSERT INTO `D_MISSION_MAIN_CHAIN_XREF` VALUES ('1', '9', '2');

-- ----------------------------
-- Table structure for D_MISSION_NODE
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_NODE`;
CREATE TABLE `D_MISSION_NODE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(256) DEFAULT NULL COMMENT '名称',
  `DESCRIPTION` varchar(256) DEFAULT NULL COMMENT '描述',
  `REPEAT_COUNT` int(11) DEFAULT NULL COMMENT '重复次数',
  `INTERVAL_TIME` bigint(20) DEFAULT NULL COMMENT '间隔时间',
  `DATA` varchar(256) DEFAULT NULL COMMENT '数据',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `PRIORITY` int(11) DEFAULT NULL COMMENT '优先等级',
  `MISSION_CHAIN_ID` bigint(20) DEFAULT NULL COMMENT '任务编号',
  `FEATURE_ITEM_ID` bigint(20) DEFAULT NULL COMMENT '功能ID',
  `FEATURE_ITEM_TYPE_ID` bigint(20) DEFAULT NULL COMMENT '功能类型Id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_NODE
-- ----------------------------
INSERT INTO `D_MISSION_NODE` VALUES ('3', '测试43', 'hahaha', '2', '152458452121', 'sadfasdfas', '2016-06-14 09:12:58', null, '1', null, null, null);

-- ----------------------------
-- Table structure for D_MISSION_NODE_CHAIN_XREF
-- ----------------------------
DROP TABLE IF EXISTS `D_MISSION_NODE_CHAIN_XREF`;
CREATE TABLE `D_MISSION_NODE_CHAIN_XREF` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MISSION_CHAIN_ID` bigint(20) DEFAULT NULL,
  `MISSION_NODE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of D_MISSION_NODE_CHAIN_XREF
-- ----------------------------

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
  `MISSION_LIST_ID` int(11) NOT NULL COMMENT '任务列表ID',
  `MISSION_ID` int(11) DEFAULT NULL COMMENT '任务ID',
  `MISSION_ITEM_ID` int(11) DEFAULT NULL COMMENT '任务节点ID',
  `MISSION_LIST_REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '任务列表重复',
  `MISSION_REPEAT_TIMES` int(11) DEFAULT NULL COMMENT '任务重复',
  `MISSION_EVENT` varchar(255) NOT NULL COMMENT 'event 目前包括（后续可能增加）：
    start_success：开始成功
    start_fail：开始失败
    pause_success：暂停成功
    pause_fail：暂停失败
    resume_success：恢复成功
    resume_fail：恢复失败
    cancel_success：取消成功
    cancel_fail：取消失败
    finish：完成',
  `MISSION_DESCRIPTION` TEXT NOT NULL COMMENT '事件描述，对于特殊的事件加以说明，若无说明则为空字符串',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '继承自BaseBean:创建时间',
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '继承自BaseBean:创建来源',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '继承自BaseBean:门店ID',
  `CHARGING_STATUS` BIT DEFAULT 0 COMMENT '充电状态  1：正在充电  0：未充电',
  `PLUGIN_STATUS` BIT DEFAULT 0  COMMENT '1：插入充电桩   0：未插入充电桩',
  `POWER_PERCENT` INT DEFAULT 0 COMMENT '电量  范围  0-100',
  `ROS` varchar(200) DEFAULT NULL COMMENT 'ros当前位置信息',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_MISSION
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
INSERT INTO `oauth_access_token` VALUES ('6aca61f9c15e8253b2c1d9e3e95fa619', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CE50B56B47870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000043F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002436333836613261372D356439322D346430312D393634342D623765623733636165633464, '2fcca3e1dd63ccde11adaeb375dcf03f', 'zhangsan', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F400000000000037708000000040000000274000A6772616E745F7479706574000870617373776F7264740008757365726E616D657400087A68616E6773616E78737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D740004726561647371007E000D7400057772697465787371007E001A3F40000000000000770800000010000000007870707371007E0023770C000000103F400000000000027400046175746874000475736572787371007E0023770C000000103F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F400000000000067708000000080000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F78707400087A68616E6773616E, null);
INSERT INTO `oauth_access_token` VALUES ('4696737b5672452893ba8c48554e7ca1', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CFEAE25BE7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000043F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002436616132353234662D656161332D346332642D616630622D623336323334386661363233, '87a6a71dc7b7587f007a7f193023fbdc', 'ray', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F400000000000037708000000040000000274000A6772616E745F7479706574000870617373776F7264740008757365726E616D6574000372617978737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D740004726561647371007E000D7400057772697465787371007E001A3F40000000000000770800000010000000007870707371007E0023770C000000103F400000000000027400046175746874000475736572787371007E0023770C000000103F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F400000000000067708000000080000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F7870740003726179, null);
INSERT INTO `oauth_access_token` VALUES ('04d3147b8c51fa73ce54a9af8863955f', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CFF483E687870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002464656265316361642D333263392D346130662D396234322D323832613836343834353562, 'acbd2b9ea424b3ba09ca96557e18bafa', 'test', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D657400047465737474000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000474657374, null);
INSERT INTO `oauth_access_token` VALUES ('3dadc9e4cdae166b31e32040ec69448a', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CF42349AF7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000043F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002437643866396364632D373665632D343639392D393164332D666461346265303662643632, '6dfc8f42776074d17dcce63f4f244e4a', 'test11', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000001A40200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F400000000000037708000000040000000274000A6772616E745F7479706574000870617373776F7264740008757365726E616D6574000674657374313178737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D740004726561647371007E000D7400057772697465787371007E001A3F40000000000000770800000010000000007870707371007E0023770C000000103F400000000000027400046175746874000475736572787371007E0023770C000000103F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000001A40200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F400000000000067708000000080000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657200000000000001A40200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F7200000000000001A4020000787077040000000171007E000F7870740006746573743131, null);
INSERT INTO `oauth_access_token` VALUES ('f5954f3c4a3daedde316f72d0564e502', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CFEB56C237870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002434303932626432372D373439312D343036342D623238362D663933343839393930666238, '5440cf7d3020631e034584f6d6ba1fb2', 'hero1', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D657400056865726F3174000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F78707400056865726F31, null);
INSERT INTO `oauth_access_token` VALUES ('dd772fad885c13812fd76e4a64e64a23', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CFAACB24F7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000043F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002430393839326439382D366332392D343037382D386435662D323863663064393536316363, '2b50d0706486db53a07a65ede6aa3504', 'hero2', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F400000000000037708000000040000000274000A6772616E745F7479706574000870617373776F7264740008757365726E616D657400056865726F3278737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D740004726561647371007E000D7400057772697465787371007E001A3F40000000000000770800000010000000007870707371007E0023770C000000103F400000000000027400046175746874000475736572787371007E0023770C000000103F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F400000000000067708000000080000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F78707400056865726F32, null);
INSERT INTO `oauth_access_token` VALUES ('b2383568c97690c44c38a51051fc99d8', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000015CFF4FC7BD7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F77726974657874000662656172657274002438303062396231652D316165612D346538332D623963332D366533653265646465386539, 'ea854ac2c137cb3f6ab46c05e7f85e0c', 'ray3', 'web', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F72697479000000000000019A0200014C0004726F6C657400124C6A6176612F6C616E672F537472696E673B7870740009524F4C455F555345527871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B726564697265637455726971007E000E4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0014787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000E4C001172657175657374506172616D657465727371007E00124C000573636F706571007E00147870740003776562737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E00127870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000002740008757365726E616D657400047261793374000A6772616E745F7479706574000870617373776F726478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F40000000000002740009757365725F7265616474000A757365725F777269746578017371007E0023770C000000103F400000000000027371007E000D74000577726974657371007E000D74000472656164787371007E001A3F40000000000010770800000010000000007870707371007E0023770C000000103F400000000000027400047573657274000461757468787371007E0023770C000000003F40000000000000787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E000000000000019A0200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017371007E00077371007E000B0000000177040000000171007E000F7871007E0034737200176A6176612E7574696C2E4C696E6B6564486173684D617034C04E5C106CC0FB0200015A000B6163636573734F726465727871007E001A3F4000000000000C7708000000100000000271007E001C71007E001D71007E001E71007E001F780070737200326F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E55736572000000000000019A0200075A00116163636F756E744E6F6E457870697265645A00106163636F756E744E6F6E4C6F636B65645A001563726564656E7469616C734E6F6E457870697265645A0007656E61626C65644C000B617574686F72697469657371007E00144C000870617373776F726471007E000E4C0008757365726E616D6571007E000E7870010101017371007E0020737200116A6176612E7574696C2E54726565536574DD98509395ED875B0300007870737200466F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E7573657264657461696C732E5573657224417574686F72697479436F6D70617261746F72000000000000019A020000787077040000000171007E000F787074000472617933, null);

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
CREATE TABLE `OFFLINE_MESSAGE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
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
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of OFFLINE_MESSAGE
-- ----------------------------
INSERT INTO `OFFLINE_MESSAGE` VALUES ('31', 'goor-server', 'GOOR_SERVER', 'cookyPlus1301chay', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '201', '2017-06-06 17:36:50', '2017-06-06 18:10:29', '\0');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('32', 'goor-server', 'GOOR_SERVER', 'cookyPlus1301chay', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, 'success receive message', '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '7', '2017-06-06 20:44:43', '2017-06-06 20:45:46', '');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('33', 'goor-server', 'GOOR_SERVER', 'cookyPlus1301chay', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, 'success receive message', '{\"localFileName\":\"test.apk\",\"localPath\":\"D:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"D:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22443A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22443A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '82', '2017-06-10 11:17:36', '2017-06-10 11:31:25', '');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('34', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '67', '2017-06-14 13:45:50', '2017-06-14 13:50:02', '');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('35', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '18', '2017-06-14 17:42:32', '2017-06-14 17:45:40', '');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('36', 'goor-server', 'GOOR_SERVER', 'goor-server', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '4', '2017-06-14 17:47:54', '2017-06-14 17:49:27', '');
INSERT INTO `OFFLINE_MESSAGE` VALUES ('37', 'goor-server', 'GOOR_SERVER', 'goor', 'GOOR', 'user-9', '', '\0', '0', 'EXECUTOR_UPGRADE', '', null, '100', null, null, '{\"localFileName\":\"test.apk\",\"localPath\":\"E:/TEMP/TEST/aaa/ccc/test\",\"mD5\":\"e3d9ef05786e10c1fdd4e55633c12c99\",\"publishMessage\":\"{\\\"localFileName\\\":\\\"test.apk\\\",\\\"localPath\\\":\\\"E:/TEMP/TEST/aaa/ccc/test\\\",\\\"mD5\\\":\\\"e3d9ef05786e10c1fdd4e55633c12c99\\\",\\\"remoteFileUrl\\\":\\\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\\\",\\\"topicName\\\":\\\"/enva_test\\\",\\\"topicType\\\":\\\"std_msgs/String\\\"}\",\"remoteFileUrl\":\"http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk\",\"topicName\":\"/enva_test\",\"topicType\":\"std_msgs/String\"}', 0x7B226C6F63616C46696C654E616D65223A22746573742E61706B222C226C6F63616C50617468223A22453A2F54454D502F544553542F6161612F6363632F74657374222C226D4435223A226533643965663035373836653130633166646434653535363333633132633939222C227075626C6973684D657373616765223A227B5C226C6F63616C46696C654E616D655C223A5C22746573742E61706B5C222C5C226C6F63616C506174685C223A5C22453A2F54454D502F544553542F6161612F6363632F746573745C222C5C226D44355C223A5C2265336439656630353738366531306331666464346535353633336331326339395C222C5C2272656D6F746546696C6555726C5C223A5C22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B5C222C5C22746F7069634E616D655C223A5C222F656E76615F746573745C222C5C22746F706963547970655C223A5C227374645F6D7367732F537472696E675C227D222C2272656D6F746546696C6555726C223A22687474703A2F2F6D796565372E636F6D2F707573685F746573742F3130352F6170702F476165612F476165615F312E312E305F31313036395F32303136303831362E61706B222C22746F7069634E616D65223A222F656E76615F74657374222C22746F70696354797065223A227374645F6D7367732F537472696E67227D, '201', '2017-06-14 18:21:16', '2017-06-14 18:39:30', '\0');

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

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
-- Table structure for AC_GOOD_TYPE
-- ----------------------------
DROP TABLE IF EXISTS `AC_GOOD_TYPE`;
CREATE TABLE `AC_GOOD_TYPE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `CREATED_BY` bigint(11) DEFAULT NULL COMMENT '创建人ID',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of AC_GOOD_TYPE
-- ----------------------------
INSERT INTO `AC_GOOD_TYPE` VALUES ('1', '药物', '2017-07-07 11:47:02', null, '1', '100');
INSERT INTO `AC_GOOD_TYPE` VALUES ('2', '垃圾', '2017-07-07 11:47:05', null, '1', '100');
INSERT INTO `AC_GOOD_TYPE` VALUES ('3', '被草', '2017-07-07 11:47:09', null, '1', '100');
INSERT INTO `AC_GOOD_TYPE` VALUES ('4', '医疗器械', '2017-07-07 11:47:12', null, '1', '100');
INSERT INTO `AC_GOOD_TYPE` VALUES ('5', '餐饮', '2017-07-07 11:47:14', null, '1', '100');

-- ----------------------------
-- Table structure for A_MAP_ZIP
-- ----------------------------
CREATE TABLE IF NOT EXISTS A_MAP_ZIP
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  STORE_ID BIGINT COMMENT '店铺ID',
  CREATED_BY BIGINT COMMENT '创建人',
  CREATE_TIME datetime COMMENT '创建时间' DEFAULT NOW(),
  MAP_NAME VARCHAR(50) COMMENT '地图名称',
  SCENE_NAME VARCHAR(50) NOT NULL COMMENT '场景名称',
  FILE_PATH VARCHAR(256) COMMENT '文件路径',
  VERSION VARCHAR(50) COMMENT '版本号',
  FILE_NAME VARCHAR(50) COMMENT '文件名',
  MD5 VARCHAR(256) COMMENT 'MD5',
  DEVICE_ID VARCHAR(256) COMMENT '上传地图的设备编号',
  FILE_UPLOAD_ID BIGINT COMMENT '文件上传编号',
  ROBOT_PATH VARCHAR(256) COMMENT '机器人上存储路径'
);

-- ----------------------------
-- Table structure for A_MAP_INFO
-- ----------------------------
CREATE TABLE IF NOT EXISTS A_MAP_INFO
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  STORE_ID BIGINT COMMENT '店铺ID',
  CREATED_BY BIGINT COMMENT '创建人',
  CREATE_TIME datetime COMMENT '创建时间' DEFAULT NOW(),
  MAP_NAME VARCHAR(50) COMMENT '地图名',
  SCENE_NAME VARCHAR(50) COMMENT '场景名',
  MAP_ALIAS VARCHAR(50) COMMENT '场景名',
  PNG_IMAGE_LOCAL_PATH VARCHAR(100) COMMENT '地图png文件本地地址',
  ROS VARCHAR(400)
);

-- ----------------------------
-- Table structure for C_FILE_UPLOAD
-- ----------------------------
CREATE TABLE IF NOT EXISTS C_FILE_UPLOAD
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(50) NOT NULL COMMENT '名称',
  LENGTH BIGINT COMMENT '长度',
  PATH VARCHAR(256) COMMENT '路径',
  MD5 VARCHAR(256) COMMENT 'MD5',
  STATUS INT COMMENT '状态（0传输成功 1传输失败）',
  CREATE_TIME DATETIME COMMENT '创建时间' DEFAULT NOW(),
  UPDATE_TIME DATETIME COMMENT '更新时间'
);