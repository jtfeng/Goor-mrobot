ALTER TABLE AS_DOOR ADD PATH_LOCK BIGINT(20) NULL;

DROP TABLE IF EXISTS `AS_ROADPATHLOCK`;
create table AS_ROADPATHLOCK
(
  ID bigint auto_increment comment 'ID 序号列表' primary key,
  CREATED_BY bigint null comment '信息记录创建人',
  CREATE_TIME datetime null,
  STORE_ID bigint null,
  NAME varchar(50) null comment '名称信息',
  `LOCK` int(1) null comment '是否上锁的标识'
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
ALTER TABLE AS_ROADPATHLOCK ADD ROBOT_CODE varchar(50) NULL;

ALTER TABLE AS_ROADPATH ADD PATH_LOCK BIGINT(20) NULL;

ALTER TABLE OR_ORDER_SETTING ADD NEED_SHELF tinyint(1) DEFAULT NULL;

ALTER TABLE `OR_ORDER_SETTING` CHANGE `END_POINT_ID` `END_STATION_ID` BIGINT(20) NULL DEFAULT NULL;
ALTER TABLE `OR_ORDER_SETTING` CHANGE `START_POINT_ID` `START_STATION_ID` BIGINT(20) NULL DEFAULT NULL;

ALTER TABLE OR_ORDER_DETAIL ADD PLACE int(12) DEFAULT NULL;

//AS_ROBOT 修改字段为varchar
//
//`ROBOT_ID_FOR_ELEVATOR` varchar(8) DEFAULT NULL COMMENT


DROP TABLE IF EXISTS `AC_EMPLOYEE`;
CREATE TABLE `AC_EMPLOYEE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL COMMENT '员工名称',
  `CODE` varchar(255) DEFAULT NULL COMMENT '员工工号',
  `STORE_ID` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `CREATED_BY` bigint(20) DEFAULT NULL COMMENT 'ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `AC_EMPLOYEE_STATION_XREF`;
CREATE TABLE `AC_EMPLOYEE_STATION_XREF` (
  `EMPLOYEE_ID` bigint(20) DEFAULT NULL COMMENT '员工ID',
  `STATION_ID` bigint(20) DEFAULT NULL COMMENT '站点ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE AS_ELEVATOR ADD `IP_ELEVATOR_ID` varchar(8) DEFAULT NULL COMMENT '电梯ID';

ALTER TABLE AS_ELEVATOR ADD `DEFAULT_ELEVATOR` bit(1) DEFAULT b'0' COMMENT '是否为默认';

ALTER TABLE `OAUTH_ACCESS_TOKEN` RENAME `oauth_access_token`;
ALTER TABLE `OAUTH_CLIENT_DETAILS` RENAME `oauth_client_details`;


ALTER TABLE AS_ELEVATORSHAFT ADD LOCK_STATE int(1) NULL;
ALTER TABLE AS_ELEVATORSHAFT ADD ROBOT_CODE varchar(50) NULL;

ALTER TABLE AS_ROADPATHPOINT ADD ORDER_INDEX INT(11) NULL;


DROP TABLE IF EXISTS `AS_ELEVATORMODE`;
create table AS_ELEVATORMODE
(
  ID bigint auto_increment
    primary key,
  START_TIME datetime null comment '开始时间',
  END_TIME datetime null comment '结束时间',
  STATE tinyint(1) null comment '当前电梯的模式',
  ELEVATOR_ID bigint null comment '对应的电梯 ID 信息',
  CREATED_BY bigint null,
  CREATE_TIME datetime null,
  STORE_ID bigint null,
  constraint AS_ELEVATORMODE_ID_uindex
  unique (ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE AC_EMPLOYEE ADD TYPE INT(11) NULL;



