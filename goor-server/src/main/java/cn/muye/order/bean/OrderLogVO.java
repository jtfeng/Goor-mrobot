package cn.muye.order.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/11/21.
 */
public class OrderLogVO {

    private String robotName;

    @JSONField(format = "yyyy/MM/dd HH:mm")
    private Date beginTime;

    @JSONField(format = "yyyy/MM/dd HH:mm")
    private Date endTime;

    private String event;

    private List<OrderLogDetailVO> orderLogDetailVOList;

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<OrderLogDetailVO> getOrderLogDetailVOList() {
        return orderLogDetailVOList;
    }

    public void setOrderLogDetailVOList(List<OrderLogDetailVO> orderLogDetailVOList) {
        this.orderLogDetailVOList = orderLogDetailVOList;
    }
}
