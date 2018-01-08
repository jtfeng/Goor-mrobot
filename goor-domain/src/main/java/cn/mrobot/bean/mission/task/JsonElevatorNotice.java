package cn.mrobot.bean.mission.task;

/**
 * Created by Selim on 2018/1/5.
 * 半自动 电梯消息通知
 */
public class JsonElevatorNotice {

    private Integer callFloor;  //当前呼叫楼层

    private Integer targetFloor;   //到达目标楼层

    private Long elevatorId;     //使用电梯的id

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
}
