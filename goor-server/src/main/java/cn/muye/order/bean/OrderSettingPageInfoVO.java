package cn.muye.order.bean;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.good.GoodsType;

import java.util.List;

/**
 * Created by Selim on 2017/8/30.
 */
public class OrderSettingPageInfoVO {

    private List<GoodsType> goodsTypes;

    private List<Station> startStations;

    private List<Station> endStations;

    public List<GoodsType> getGoodsTypes() {
        return goodsTypes;
    }

    public void setGoodsTypes(List<GoodsType> goodsTypes) {
        this.goodsTypes = goodsTypes;
    }

    public List<Station> getStartStations() {
        return startStations;
    }

    public void setStartStations(List<Station> startStations) {
        this.startStations = startStations;
    }

    public List<Station> getEndStations() {
        return endStations;
    }

    public void setEndStations(List<Station> endStations) {
        this.endStations = endStations;
    }
}
