package cn.muye.order.bean;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.shelf.Shelf;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/7/14.
 * 护士平板查看显示类
 */
public class OrderDetailVO {

    private List<GoodsInfoVO>  goodsInfoList; //装配货物的列表

    private Robot robot;  //运输的robot

    private Station startStation; //下单站

    private Boolean needSign; //是否需要签收

    private Shelf shelf; //货架编号

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date beginDate; //开始时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate; //到达或者签收时间

    private Integer status; //状态

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public List<GoodsInfoVO> getGoodsInfoList() {
        return goodsInfoList;
    }

    public void setGoodsInfoList(List<GoodsInfoVO> goodsInfoList) {
        this.goodsInfoList = goodsInfoList;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Boolean getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Boolean needSign) {
        this.needSign = needSign;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
