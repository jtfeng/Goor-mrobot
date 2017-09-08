package cn.muye.order.bean;

import cn.mrobot.utils.DateTimeUtils;

import java.util.Date;

/**
 * Created by Selim on 2017/8/31.
 */
public class WSOrderNotificationVO {

    private WSOrderNotificationType type;  //类型

    private String name;                   //小车编号

    private Object object;                 //传输内容

    private Long currentTime;             //当前时间戳

    private String formatTime;              //当前时间，对应的时间

    public WSOrderNotificationVO() {
    }

    public WSOrderNotificationVO(WSOrderNotificationType type, Object object, String name) {
        this.type = type;
        this.object = object;
        this.name = name;
        this.currentTime = System.currentTimeMillis();
        this.formatTime = DateTimeUtils.getDateString(new Date(currentTime),DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT);
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

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }
}
