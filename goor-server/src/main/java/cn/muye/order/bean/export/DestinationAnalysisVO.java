package cn.muye.order.bean.export;

/**
 * Created by Selim on 2018/3/12.
 */
public class DestinationAnalysisVO {

    private Integer index;    //序号

    private String orderDate;    //日期

    private String orderStation;   //下单站

    private Long orderId;         //订单id

    private String robotCode;     //机器人编号

    private String destinationStation;  //目的地

    private String arriveDestinationDate; //到达目的地时间

    private String leaveDestinationDate;   //刷卡返回时间

    private Long destinationInteractiveTime;   //目的地交互时间

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStation() {
        return orderStation;
    }

    public void setOrderStation(String orderStation) {
        this.orderStation = orderStation;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getArriveDestinationDate() {
        return arriveDestinationDate;
    }

    public void setArriveDestinationDate(String arriveDestinationDate) {
        this.arriveDestinationDate = arriveDestinationDate;
    }

    public String getLeaveDestinationDate() {
        return leaveDestinationDate;
    }

    public void setLeaveDestinationDate(String leaveDestinationDate) {
        this.leaveDestinationDate = leaveDestinationDate;
    }

    public Long getDestinationInteractiveTime() {
        return destinationInteractiveTime;
    }

    public void setDestinationInteractiveTime(Long destinationInteractiveTime) {
        this.destinationInteractiveTime = destinationInteractiveTime;
    }
}
