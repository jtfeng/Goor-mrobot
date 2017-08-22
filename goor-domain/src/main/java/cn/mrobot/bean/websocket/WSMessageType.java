package cn.mrobot.bean.websocket;

/**
 * Created by Jelynn on 2017/8/17.
 */
public enum  WSMessageType {
    NOTIFICATION,
    WARNING,
    POSE,
    STOP_SENDING;

    public boolean is(String msgType) {
        return name().equalsIgnoreCase(msgType);
    }
}
