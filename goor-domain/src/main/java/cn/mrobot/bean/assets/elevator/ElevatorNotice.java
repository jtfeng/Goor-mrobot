package cn.mrobot.bean.assets.elevator;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import java.util.Date;

/**
 * 消息通知的实体类，通过type区分是什么消息通知，
 *
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

    private int type; //消息类型 1:电梯pad消息  2:多站到达消息

    private String robotCode;

    private Long toStationId;

    private String fromStationName; //发货站名称

    private String goodsTypeName; //物品类型名称

    private String data; //其他数据，JSON格式的字符串，到站消息为物品信息的数据

    private Long orderDetailId;   //订单细节id

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public ElevatorNotice init() {
        this.setState(State.INIT.getCode());
        this.setCreateTime(new Date());
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElevatorNotice that = (ElevatorNotice) o;

        if (!uuid.equals(that.uuid)) return false;
        return toStationId.equals(that.toStationId);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + toStationId.hashCode();
        return result;
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

    public enum Type {
        ELEVATOR_NOTICE(1),
        ARRIVAL_STATION_NOTICE(2);

        private int code;

        private Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
