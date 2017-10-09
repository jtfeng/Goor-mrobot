package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "AS_ELEVATORMODE")
public class ElevatorMode extends BaseBean {
    private String start;//开始时间
    private String end;//结束时间
    private Integer state;//电梯当前的模式 (1 代表全自动, 0 代表半自动)
    private Long elevatorId;//电梯 id

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", state=" + state +
                ", elevatorId=" + elevatorId +
                '}';
    }
}
