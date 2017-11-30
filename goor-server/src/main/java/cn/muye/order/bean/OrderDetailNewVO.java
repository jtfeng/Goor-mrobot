package cn.muye.order.bean;

import java.util.List;

/**
 * Created by Selim on 2017/11/30.
 * 护士平板查看显示类
 */
public class OrderDetailNewVO {

    private String startStationName; //下单站

    private String robotCode;  //运输的robot

    private String transferInfo; //当前运输详情

    private MapPathInfoVO mapPathInfoVO;   //

    private String orderYear;  //下单时间（年月日）

    private String orderHour;  //下单时间（时分）

    private Integer status; //运输状态

    private List<GoodsInfoVO>  goodsInfoList; //装配货物的列表

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<GoodsInfoVO> getGoodsInfoList() {
        return goodsInfoList;
    }

    public void setGoodsInfoList(List<GoodsInfoVO> goodsInfoList) {
        this.goodsInfoList = goodsInfoList;
    }

    public String getTransferInfo() {
        return transferInfo;
    }

    public void setTransferInfo(String transferInfo) {
        this.transferInfo = transferInfo;
    }




    public String getOrderYear() {
        return orderYear;
    }

    public void setOrderYear(String orderYear) {
        this.orderYear = orderYear;
    }

    public String getOrderHour() {
        return orderHour;
    }

    public void setOrderHour(String orderHour) {
        this.orderHour = orderHour;
    }
}
