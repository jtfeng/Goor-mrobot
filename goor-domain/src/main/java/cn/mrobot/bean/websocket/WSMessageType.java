package cn.mrobot.bean.websocket;

/**
 * Created by Jelynn on 2017/8/17.
 */
public enum  WSMessageType {
    REGISTER,
    NOTIFICATION,
    SPECIFIC_TYPE,
    WARNING,
    POSE,
    STOP_SENDING,
    AVAILABLE_ROBOT_COUNT;

    public boolean is(String msgType) {
        return name().equalsIgnoreCase(msgType);
    }
}
