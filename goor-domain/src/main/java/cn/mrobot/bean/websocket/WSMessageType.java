package cn.mrobot.bean.websocket;

/**
 * Created by Jelynn on 2017/8/17.
 */
public enum  WSMessageType {
    REGISTER,
    NOTIFICATION,
    WARNING,
    POSE,
    PING;

    public boolean is(String msgType) {
        return name().equalsIgnoreCase(msgType);
    }
}
