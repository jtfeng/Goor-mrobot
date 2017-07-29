package cn.mrobot.bean.enums;

/**
 * Created by enva on 2017/5/9.
 */
public enum MessageType {
    EXECUTOR_LOG,//log消息
    EXECUTOR_CLIENT,//Client(仅处理x86业务逻辑)消息
    EXECUTOR_COMMAND,//命令消息
    EXECUTOR_UPGRADE,//升级消息
    EXECUTOR_MAP,//地图消息
    EXECUTOR_RESOURCE,//资源消息
    TIME_SYNCHRONIZED,//时间同步请求消息
    REPLY,//回执消息
    ROBOT_AUTO_REGISTER,//自动注册机器人信息消息
    ROBOT_INFO;//机器人信息(包含电量阈值以及其他属性)消息
}
