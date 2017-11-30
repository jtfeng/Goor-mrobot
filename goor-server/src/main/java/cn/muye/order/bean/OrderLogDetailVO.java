package cn.muye.order.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/11/21.
 */
public class OrderLogDetailVO {

    private Date checkTime;

    private String location;

    private String checkPerson;

    private String eventDetail;

    private List<GoodsInfoVO> goodsInfoList;

    private String distance;

    private String weight;

    private Integer warningNum;

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public List<GoodsInfoVO> getGoodsInfoList() {
        return goodsInfoList;
    }

    public void setGoodsInfoList(List<GoodsInfoVO> goodsInfoList) {
        this.goodsInfoList = goodsInfoList;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Integer getWarningNum() {
        return warningNum;
    }

    public void setWarningNum(Integer warningNum) {
        this.warningNum = warningNum;
    }
}
