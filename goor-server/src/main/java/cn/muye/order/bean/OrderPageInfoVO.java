package cn.muye.order.bean;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.order.Goods;

import java.util.List;

/**
 * Created by Selim on 2017/7/21.
 */
public class OrderPageInfoVO {

    private List<Shelf> shelfList;

    private List<Station> stationList;

    private List<Goods> goodsList;

    public List<Shelf> getShelfList() {
        return shelfList;
    }

    public void setShelfList(List<Shelf> shelfList) {
        this.shelfList = shelfList;
    }

    public List<Station> getStationList() {
        return stationList;
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }
}
