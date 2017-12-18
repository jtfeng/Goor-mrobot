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
    ORDER,
    STOP_SENDING;

    public boolean is(String msgType) {
        return name().equalsIgnoreCase(msgType);
    }
}
