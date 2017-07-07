DROP TABLE IF EXISTS APP_CONFIG;
DROP TABLE IF EXISTS OFFLINE_MESSAGE;
DROP TABLE IF EXISTS RECEIVE_MESSAGE;
DROP TABLE IF EXISTS CHARGING_INFO;
DROP TABLE IF EXISTS LOG_INFO;
DROP TABLE IF EXISTS D_FEATURE_ITEM;
DROP TABLE IF EXISTS D_FEATURE_ITEM_TYPE;
DROP TABLE IF EXISTS D_MISSION_MAIN;
DROP TABLE IF EXISTS D_MISSION_CHAIN;
DROP TABLE IF EXISTS D_MISSION_NODE;
DROP TABLE IF EXISTS D_MISSION_NODE_CHAIN_XREF;
DROP TABLE IF EXISTS D_MISSION_MAIN_CHAIN_XREF;

CREATE TABLE IF NOT EXISTS APP_CONFIG
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  MPUSH_PUBLICKEY VARCHAR(256) NOT NULL COMMENT 'publickey',
  MPUSH_ALLOCSERVER VARCHAR(256) NOT NULL COMMENT 'allocserver',
  MPUSH_PUSHSERVER VARCHAR(256) NOT NULL COMMENT 'pushserver',
  MPUSH_DEVICEID VARCHAR(256) NOT NULL COMMENT 'deviceid',
  MPUSH_OSNAME VARCHAR(256) NOT NULL COMMENT 'osname',
  MPUSH_OSVERSION VARCHAR(256) NOT NULL COMMENT 'osversion',
  MPUSH_CLIENTVERSION VARCHAR(256) NOT NULL ,
  MPUSH_USERID VARCHAR(256) NOT NULL COMMENT 'userid',
  MPUSH_TAGS VARCHAR(256) NOT NULL COMMENT 'tags',
  MPUSH_SESSIONSTORAGEDIR VARCHAR(256) NOT NULL COMMENT 'sessionstoragedir',
  ROS_PATH VARCHAR(256) NOT NULL COMMENT 'ros_path'
);

CREATE TABLE IF NOT EXISTS OFFLINE_MESSAGE
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  SENDER_ID VARCHAR(128) NOT NULL COMMENT '发送者ID',
  SEND_DEVICE_TYPE VARCHAR(128) NOT NULL COMMENT '发送的设备类型',
  RECEIVER_ID VARCHAR(128) NOT NULL COMMENT '接收者ID',
  RECEIVER_DEVICE_TYPE VARCHAR(128) NOT NULL COMMENT '接收的设备类型',
  WEBSOCKET_ID VARCHAR(128) COMMENT 'webSocketId，暂定回执时使用',
  RECEIPT_WEBSOCKET BIT NOT NULL COMMENT '是否给webSocket发送消息，暂定回执时使用',
  FINISH BIT NOT NULL COMMENT '是否完成消息处理',
  MESSAGE_KIND SMALLINT NOT NULL DEFAULT 0 COMMENT '消息种类，默认为0，0：文本消息，1：二进制消息',
  MESSAGE_TYPE VARCHAR(256) NOT NULL COMMENT '消息类型',
  FAIL_RESEND BIT NOT NULL COMMENT '是否是否需要失败重新发送',
  SESSION_ID INTEGER NULL COMMENT 'sessionId,暂时不使用',
  MESSAGE_STATUS_TYPE SMALLINT NOT NULL DEFAULT 0 COMMENT '消息状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成',
  VERSION VARCHAR(256) COMMENT '版本号',
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
  ID BIGINT NOT NULL,
  SENDER_ID VARCHAR(128) NOT NULL COMMENT '发送者ID',
  SEND_DEVICE_TYPE VARCHAR(128) NOT NULL COMMENT '发送的设备类型',
  RECEIVER_ID VARCHAR(128) NOT NULL COMMENT '接收者ID',
  RECEIVER_DEVICE_TYPE VARCHAR(128) NOT NULL COMMENT '接收的设备类型',
  WEBSOCKET_ID VARCHAR(128) COMMENT 'webSocketId，暂定回执时使用',
  RECEIPT_WEBSOCKET BIT NOT NULL COMMENT '是否给webSocket发送消息，暂定回执时使用',
  FINISH BIT NOT NULL COMMENT '是否完成消息处理',
  MESSAGE_KIND SMALLINT NOT NULL DEFAULT 0 COMMENT '消息种类，默认为0，0：文本消息，1：二进制消息',
  MESSAGE_TYPE VARCHAR(256) NOT NULL COMMENT '消息类型',
  FAIL_RESEND BIT NOT NULL COMMENT '是否是否需要失败重新发送',
  SESSION_ID INTEGER NULL COMMENT 'sessionId,暂时不使用',
  MESSAGE_STATUS_TYPE SMALLINT NOT NULL DEFAULT 0 COMMENT '消息状态默认为0，1：未下载，2：下载完成，3：发送ros消息完成',
  VERSION VARCHAR(256) COMMENT '版本号',
  RELY_MESSAGE VARCHAR(256) COMMENT '回执消息',
  MESSAGE_TEXT TEXT COMMENT '文本消息',
  MESSAGE_BINARY BLOB COMMENT '二进制消息',
  SEND_COUNT INTEGER NOT NULL DEFAULT 0 COMMENT '发送次数',
  SEND_TIME DATETIME NOT NULL COMMENT '发送时间',
  UPDATE_TIME DATETIME NOT NULL COMMENT '更新时间',
  SUCCESS BIT NOT NULL COMMENT '是否发送成功',
  PRIMARY KEY (ID, SENDER_ID)
);

CREATE TABLE IF NOT EXISTS CHARGING_INFO
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  STORE_ID BIGINT COMMENT '店铺ID',
  CREATED_BY BIGINT COMMENT '创建人',
  CREATED datetime COMMENT '创建时间' DEFAULT NOW(),
  DEVICE_ID VARCHAR(256) NOT NULL COMMENT '设备编号',
  CHARGING_STATUS BIT NOT NULL COMMENT '充电状态  1：正在充电  0：未充电',
  PLUGIN_STATUS BIT NOT NULL COMMENT '1：插入充电桩   0：未插入充电桩',
  POWER_PERCENT INT NOT NULL COMMENT '电量  范围  0-100',
  CREATE_DATE DATETIME NOT NULL COMMENT '创建时间'
);


CREATE TABLE IF NOT EXISTS LOG_INFO
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  DEVICE_ID VARCHAR(256) NOT NULL COMMENT '设备编号',
  MESSAGE VARCHAR(256) COMMENT '日志内容',
  LOG_LEVEL VARCHAR(50) NOT NULL COMMENT '日志等级',
  LOG_TYPE  VARCHAR(50) NOT NULL COMMENT '日志类型',
  CREATE_DATE DATETIME NOT NULL COMMENT '创建时间',
  HANDLE_PERSON VARCHAR(50)  COMMENT '处理人',
  HANDLE_TIME  DATETIME COMMENT '处理时间',
);

CREATE TABLE IF NOT EXISTS D_FEATURE_ITEM
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(256) COMMENT '名称',
  VALUE VARCHAR(256) COMMENT '值' ,
  DESCRIPTION VARCHAR(256) COMMENT '描述'
);

CREATE TABLE IF NOT EXISTS D_FEATURE_ITEM_TYPE
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(256) COMMENT '名称',
  VALUE VARCHAR(256) COMMENT '值',
  DESCRIPTION VARCHAR(256) COMMENT '描述',
  DATA_MODEL VARCHAR(256) COMMENT '数据模板，方便前端用户输入',
  FEATURE_ITEM_ID BIGINT COMMENT '功能ID'
);

CREATE TABLE IF NOT EXISTS D_MISSION_MAIN
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(256) COMMENT '名称',
  DESCRIPTION VARCHAR(256) COMMENT '描述',
  DEVICE_ID VARCHAR(256) COMMENT '设备编号',
  CREATE_TIME DATETIME NOT NULL COMMENT '创建时间',
  UPDATE_TIME DATETIME COMMENT '更新时间',
  INTERVAL_TIME BIGINT COMMENT '间隔时间',
  REPEAT_COUNT INT COMMENT '重复次数',
  START_TIME BIGINT COMMENT '开始时间',
  PRIORITY INT COMMENT '优先等级'
);

CREATE TABLE IF NOT EXISTS D_MISSION_CHAIN
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(256) COMMENT '名称',
  DESCRIPTION VARCHAR(256) COMMENT '描述',
  CREATE_TIME DATETIME NOT NULL COMMENT '创建时间',
  UPDATE_TIME DATETIME COMMENT '更新时间',
  REPEAT_COUNT INT COMMENT '重复次数',
  INTERVAL_TIME BIGINT COMMENT '间隔时间',
  MISSION_MAIN_ID BIGINT COMMENT '总任务编号',
  PRIORITY INT COMMENT '优先等级'
);

CREATE TABLE IF NOT EXISTS D_MISSION_NODE
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
  NAME VARCHAR(256) COMMENT '名称',
  DESCRIPTION VARCHAR(256) COMMENT '描述',
  REPEAT_COUNT INT COMMENT '重复次数',
  INTERVAL_TIME BIGINT COMMENT '间隔时间',
  DATA VARCHAR(256) COMMENT '数据',
  CREATE_TIME DATETIME NOT NULL COMMENT '创建时间',
  UPDATE_TIME DATETIME COMMENT '更新时间',
  PRIORITY INT COMMENT '优先等级',
  MISSION_CHAIN_ID BIGINT COMMENT '任务编号',
  FEATURE_ITEM_ID BIGINT COMMENT '功能ID',
  FEATURE_ITEM_TYPE_ID BIGINT COMMENT '功能类型Id'
);

CREATE TABLE IF NOT EXISTS D_MISSION_NODE_CHAIN_XREF
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  MISSION_CHAIN_ID BIGINT,
  MISSION_NODE_ID BIGINT
);

CREATE TABLE IF NOT EXISTS D_MISSION_MAIN_CHAIN_XREF
(
  ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  MISSION_MAIN_ID BIGINT,
  MISSION_CHAIN_ID BIGINT
);


CREATE TABLE IF NOT EXISTS A_MAP_POINT
(
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
  MAP_POINT_TYPE_ID INT COMMENT '点类型索引'
);

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
  PNG_IMAGE_HTTP_PATH VARCHAR(100) COMMENT '地图png文件http地址',
  ROS VARCHAR(400)
);

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