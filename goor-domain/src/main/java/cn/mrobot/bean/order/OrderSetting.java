package cn.mrobot.bean.order;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.base.BaseBean;

/**
 * Created by Selim on 2017/7/6.
 */
public class OrderSetting extends BaseBean{

    private String nickName; //别名

    private Long stationId;  //关联stationId

    private MapPoint startPoint; //装货地点

    private MapPoint endPoint; //货架回收点

    private GoodsType goodsType; //货物类型

    private Integer packageType; //装货方式 0为手动  1为自动

    private Boolean defaultSetting = Boolean.FALSE; //是否为默认设置

    private Boolean deleteStatus = Boolean.FALSE; //是否已删除 默认初始不删除

    public OrderSetting() {
    }

    public OrderSetting(Long id) {
        super(id);
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

    public MapPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(MapPoint endPoint) {
        this.endPoint = endPoint;
    }

    public MapPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(MapPoint startPoint) {
        this.startPoint = startPoint;
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
}
