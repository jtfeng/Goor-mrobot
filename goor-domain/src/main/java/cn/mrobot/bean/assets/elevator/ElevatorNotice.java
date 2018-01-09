package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.erp.order.OperationOrder;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author Jelynn
 * @date 2018/1/8
 */
@Table(name = "AS_ELEVATOR_NOTICE")
public class ElevatorNotice extends BaseBean {

    private Integer callFloor;  //当前呼叫楼层

    private Integer targetFloor;   //到达目标楼层

    private Long elevatorId;     //使用电梯的id

    private String uuid;

    private int state; //状态:0 :初始状态，1：pad接收到消息并反馈

    private String robotCode;

    private Long toStationId;

    public Integer getCallFloor() {
        return callFloor;
    }

    public void setCallFloor(Integer callFloor) {
        this.callFloor = callFloor;
    }

    public Integer getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(Integer targetFloor) {
        this.targetFloor = targetFloor;
    }

    public Long getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(Long elevatorId) {
        this.elevatorId = elevatorId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Long getToStationId() {
        return toStationId;
    }

    public void setToStationId(Long toStationId) {
        this.toStationId = toStationId;
    }

    public ElevatorNotice init() {
        this.setState(State.INIT.getCode());
        this.setCreateTime(new Date());
        return this;
    }

    public enum State {
        INIT(0),
        RECEIVED(1);

        private int code;

        private State(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
