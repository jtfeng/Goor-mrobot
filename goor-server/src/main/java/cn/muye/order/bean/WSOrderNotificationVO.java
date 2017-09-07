package cn.muye.order.bean;

/**
 * Created by Selim on 2017/8/31.
 */
public class WSOrderNotificationVO {

    private WSOrderNotificationType type;  //类型

    private Object object;                 //传输内容

    public WSOrderNotificationVO() {
    }

    public WSOrderNotificationVO(WSOrderNotificationType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public WSOrderNotificationType getType() {
        return type;
    }

    public void setType(WSOrderNotificationType type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
