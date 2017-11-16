package cn.mrobot.bean.order;

import cn.mrobot.bean.base.BaseBean;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by Selim on 2017/10/10.
 */
@Table(name = "MESSAGE_BELL")
public class MessageBell extends BaseBean{

    private String message;   //消息内容

    private String robotSn;   //机器人编号

    private Integer type;     //消息类型

    private Long stationId;  //关联站id

    private Long missionItemId;  //关联的itemId，为了区分订单警报

    private Integer status; //状态 0为未读  1为已读

    @Transient
    private Long createdTimeLong;  //创建时间戳

    @Transient
    @JSONField(format = "HH:mm")
    private Date createdTimeHour;  //创建时间 时分

    public MessageBell() {
    }

    public MessageBell(String message, String robotSn, Integer type, Long stationId, Integer status) {
        this.message = message;
        this.robotSn = robotSn;
        this.type = type;
        this.stationId = stationId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRobotSn() {
        return robotSn;
    }

    public void setRobotSn(String robotSn) {
        this.robotSn = robotSn;
    }

    public Long getCreatedTimeLong() {
        return super.createTime.getTime();
    }

    public void setCreatedTimeLong(Long createdTimeLong) {
        this.createdTimeLong = createdTimeLong;
    }

    public Date getCreatedTimeHour() {
        return createTime;
    }

    public void setCreatedTimeHour(Date createdTimeHour) {
        this.createdTimeHour = createdTimeHour;
    }

    public Long getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Long missionItemId) {
        this.missionItemId = missionItemId;
    }
}
