package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "AS_ELEVATORMODE")
public class ElevatorMode extends BaseBean {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;//开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;//结束时间
    private Integer state;//电梯当前的模式 (0 代表全自动, 1 代表半自动)
    private Long elevatorId;//电梯 id

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(Long elevatorId) {
        this.elevatorId = elevatorId;
    }

    @Override
    public String toString() {
        return "ElevatorMode{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", state=" + state +
                ", elevatorId=" + elevatorId +
                '}';
    }
}
