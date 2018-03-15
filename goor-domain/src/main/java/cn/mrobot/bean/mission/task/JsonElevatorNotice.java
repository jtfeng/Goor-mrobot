package cn.mrobot.bean.mission.task;

/**
 * Created by Selim on 2018/1/5.
 * 半自动 电梯消息通知
 */
public class JsonElevatorNotice {

    private Integer callFloor;  //当前呼叫楼层

    private Integer targetFloor;   //到达目标楼层

    private Long elevatorId;     //使用电梯的id

    private Long orderDetailId;   //订单细节id

    private Integer type;    //1 为呼梯通知  2 为到站通知

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

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
