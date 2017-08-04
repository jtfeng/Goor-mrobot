-- DROP TABLE IF EXISTS ROBOT_INFO_CONFIG;
DROP TABLE IF EXISTS OFFLINE_MESSAGE;
DROP TABLE IF EXISTS RECEIVE_MESSAGE;
DROP TABLE IF EXISTS CHARGING_INFO;


-- CREATE TABLE IF NOT EXISTS ROBOT_INFO_CONFIG
-- (
--   ROBOT_SN VARCHAR(256) NOT NULL COMMENT '编号',
--   ROBOT_NAME VARCHAR(256) NOT NULL COMMENT '名称',
--   ROBOT_TYPE_ID INTEGER NOT NULL COMMENT '类型ID',
--   ROBOT_BATTERY_THRESHOLD INTEGER NOT NULL COMMENT '电量阈值',
--   ROBOT_STORE_ID BIGINT NOT NULL COMMENT '店铺ID'
-- );

CREATE TABLE IF NOT EXISTS OFFLINE_MESSAGE
(
  UUID VARCHAR(228) PRIMARY KEY NOT NULL,
  SENDER_ID VARCHAR(128) NOT NULL COMMENT '发送者ID',
  RECEIVER_ID VARCHAR(128) NOT NULL COMMENT '接收者ID',
  MESSAGE_KIND SMALLINT NOT NULL DEFAULT 0 COMMENT '消息种类，默认为0，0：文本消息，1：二进制消息',
  MESSAGE_TYPE VARCHAR(256) NOT NULL COMMENT '消息类型',
  MESSAGE_STATUS_TYPE SMALLINT NOT NULL DEFAULT 0 COMMENT '消息状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成',
  RELY_MESSAGE VARCHAR(256) COMMENT '回执消息',
  MESSAGE_TEXT TEXT COMMENT '文本消息',
  MESSAGE_BINARY BLOB COMMENT '二进制消息',
  SEND_COUNT INTEGER NOT NULL DEFAULT 0 COMMENT '发送次数',
  SEND_TIME DATETIME NOT NULL COMMENT '发送时间',
  UPDATE_TIME DATETIME NOT NULL COMMENT '更新时间',
  SUCCESS BIT NOT NULL COMMENT '是否发送成功'
);

CREATE TABLE IF NOT EXISTS RECEIVE_MESSAGE
(
  UUID VARCHAR(228) PRIMARY KEY NOT NULL,
  SENDER_ID VARCHAR(128) NOT NULL COMMENT '发送者ID',
  RECEIVER_ID VARCHAR(128) NOT NULL COMMENT '接收者ID',
  MESSAGE_KIND SMALLINT NOT NULL DEFAULT 0 COMMENT '消息种类，默认为0，0：文本消息，1：二进制消息',
  MESSAGE_TYPE VARCHAR(256) NOT NULL COMMENT '消息类型',
  MESSAGE_STATUS_TYPE SMALLINT NOT NULL DEFAULT 0 COMMENT '消息状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成',
  RELY_MESSAGE VARCHAR(256) COMMENT '回执消息',
  MESSAGE_TEXT TEXT COMMENT '文本消息',
  MESSAGE_BINARY BLOB COMMENT '二进制消息',
  SEND_COUNT INTEGER NOT NULL DEFAULT 0 COMMENT '发送次数',
  SEND_TIME DATETIME NOT NULL COMMENT '发送时间',
  UPDATE_TIME DATETIME NOT NULL COMMENT '更新时间',
  SUCCESS BIT NOT NULL COMMENT '是否发送成功'
);

CREATE TABLE IF NOT EXISTS CHARGING_INFO (
  ID bigint PRIMARY KEY NOT NULL,
  STORE_ID BIGINT ,
  CREATED_BY BIGINT ,
  CREATE_TIME datetime,
  DEVICE_ID varchar(256) ,
  CHARGING_STATUS bit ,
  PLUGIN_STATUS bit,
  POWER_PERCENT int
);