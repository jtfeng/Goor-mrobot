package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.good.GoodsType;

/**
 * Created by Selim on 2018/1/5.
 * 半自动 电梯消息通知
 */
public class JsonElevatorNotice {

    private Integer callFloor;  //当前呼叫楼层

    private Integer targetFloor;   //到达目标楼层

    private Long elevatorId;     //使用电梯的id

    private String fromStationName; //发货站名称

    private String goodsTypeName; //物品类型名称

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

    public String getFromStationName() {
        return fromStationName;
    }

    public void setFromStationName(String fromStationName) {
        this.fromStationName = fromStationName;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }
}
