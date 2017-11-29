package cn.mrobot.bean.order;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.assets.robot.RobotType;
import cn.mrobot.bean.base.BaseBean;

/**
 * Created by Selim on 2017/7/6.
 */
public class OrderSetting extends BaseBean{

    private String nickName; //别名

    private Long stationId;  //关联stationId

    private Station startStation; //装货地点

    private Station endStation; //货架回收点

    private GoodsType goodsType; //货物类型

    private Integer packageType; //装货方式 0为手动  1为自动

    private RobotType robotType;  //机器人类型

    private Boolean needShelf = Boolean.FALSE;  //默认不需要货架

    private Boolean needSign = Boolean.FALSE;  //是否需要签收 false 不需要 true 需要

    private Boolean defaultSetting = Boolean.FALSE; //是否为默认设置

    private Boolean needAutoCharge = Boolean.FALSE; //到达机器人充电点后，是否需要充电任务 默认初始不需要

    private Boolean deleteStatus = Boolean.FALSE; //是否已删除 默认初始不删除

    public OrderSetting() {
    }

    public Boolean getNeedShelf() {
        return needShelf;
    }

    public void setNeedShelf(Boolean needShelf) {
        this.needShelf = needShelf;
    }

    public OrderSetting(Long id) {
        super(id);
    }

    public RobotType getRobotType() {
        return robotType;
    }

    public void setRobotType(RobotType robotType) {
        this.robotType = robotType;
    }

    public Boolean getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Boolean needSign) {
        this.needSign = needSign;
    }

    public Boolean getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Boolean deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public GoodsType getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(GoodsType goodsType) {
        this.goodsType = goodsType;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Boolean getDefaultSetting() {
        return defaultSetting;
    }

    public void setDefaultSetting(Boolean defaultSetting) {
        this.defaultSetting = defaultSetting;
    }

    public Integer getPackageType() {
        return packageType;
    }

    public void setPackageType(Integer packageType) {
        this.packageType = packageType;
    }

    public Boolean getNeedAutoCharge() {
        return needAutoCharge;
    }

    public void setNeedAutoCharge(Boolean needAutoCharge) {
        this.needAutoCharge = needAutoCharge;
    }
}
