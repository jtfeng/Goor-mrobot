package cn.muye.order.bean;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.good.GoodsType;

import java.util.List;

/**
 * Created by Selim on 2017/8/30.
 */
public class OrderSettingPageInfoVO {

    private List<GoodsType> goodsTypes;

    private List<MapPoint> startMapPoints;

    private List<MapPoint> endMapPoints;

    public List<GoodsType> getGoodsTypes() {
        return goodsTypes;
    }

    public void setGoodsTypes(List<GoodsType> goodsTypes) {
        this.goodsTypes = goodsTypes;
    }

    public List<MapPoint> getEndMapPoints() {
        return endMapPoints;
    }

    public void setEndMapPoints(List<MapPoint> endMapPoints) {
        this.endMapPoints = endMapPoints;
    }

    public List<MapPoint> getStartMapPoints() {
        return startMapPoints;
    }

    public void setStartMapPoints(List<MapPoint> startMapPoints) {
        this.startMapPoints = startMapPoints;
    }
}
