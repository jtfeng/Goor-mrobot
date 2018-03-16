package cn.muye.order.bean.export;

/**
 * Created by Selim on 2018/3/12.
 */
public class TransferTaskAnalysisVO {

    private Integer index;  //序号

    private String orderDate;  //下单时间 显示年月日

    private String orderStation;  //下单站 （excel 解析为场景）

    private Long orderId;  //订单编号Id

    private String robotCode; //机器人编号

    private String goodsInfo;  //运送物资集合

    private String destinations;  //目的地集合

    private Integer numOfDestination; //目的地数量

    private String orderDateTime;  //下单时间 时分

    private String executeOrderDateTime;  //执行下单时间

    private String arriveStartPlaceTime; //到达发车点

    private String startPlaceTime;   //发车时间

    private String backEndPlaceTime;  //返回时间

    private Long totalTransferTime;  //机器人运输总时间

    private Long startTime;  //发车装货时间

    private Long totalTime;  //本单时间

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

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public Integer getNumOfDestination() {
        return numOfDestination;
    }

    public void setNumOfDestination(Integer numOfDestination) {
        this.numOfDestination = numOfDestination;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getExecuteOrderDateTime() {
        return executeOrderDateTime;
    }

    public void setExecuteOrderDateTime(String executeOrderDateTime) {
        this.executeOrderDateTime = executeOrderDateTime;
    }

    public String getArriveStartPlaceTime() {
        return arriveStartPlaceTime;
    }

    public void setArriveStartPlaceTime(String arriveStartPlaceTime) {
        this.arriveStartPlaceTime = arriveStartPlaceTime;
    }

    public String getStartPlaceTime() {
        return startPlaceTime;
    }

    public void setStartPlaceTime(String startPlaceTime) {
        this.startPlaceTime = startPlaceTime;
    }

    public String getBackEndPlaceTime() {
        return backEndPlaceTime;
    }

    public void setBackEndPlaceTime(String backEndPlaceTime) {
        this.backEndPlaceTime = backEndPlaceTime;
    }

    public Long getTotalTransferTime() {
        return totalTransferTime;
    }

    public void setTotalTransferTime(Long totalTransferTime) {
        this.totalTransferTime = totalTransferTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }
}
