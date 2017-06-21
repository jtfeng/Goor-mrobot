package cn.mrobot.bean.enums;

public enum MessageStatusType {
    INIT("初始化", 100),//初始化
    FILE_NOT_DOWNLOADED("文件未下载", 1),//文件未下载
    FILE_DOWNLOAD_COMPLETE("文件下载完成", 2),//文件下载完成
    PUBLISH_ROS_MESSAGE("发送消息成功", 3),//publish ros message success
    CANCEL_MESSAGE("消息取消", 4),//消息取消
    FAILURE_MESSAGE("消息发送失败", 5),//失败消息
    SUCCESS_MESSAGE("消息发送成功", 6),//成功消息
    PARAMETER_ERROR("参数传入错误", 7),//参数错误
    PROCESSING_MESSAGE("消息正在处理", 8),//消息处理中
    ROS_OFF_LINE("机器人不在线", 9);//ros不在线

    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private MessageStatusType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (MessageStatusType c : MessageStatusType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    public static MessageStatusType getType(int index){
        for (MessageStatusType c : MessageStatusType.values()) {
            if (c.getIndex() == index) {
                return c;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
