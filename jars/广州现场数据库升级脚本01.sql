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