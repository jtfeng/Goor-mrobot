package cn.muye.area.map.bean;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.charge.ChargeInfo;

import java.util.List;

/**
 * Created by Jelynn on 2017/7/13.
 */
public class CurrentInfo {

    private String pose;

    private MapInfo mapInfo;

    private ChargeInfo chargeInfo;

    private List<StateDetail> list;

    public String getPose() {
        return pose;
    }

    public void setPose(String pose) {
        this.pose = pose;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }

    public List<StateDetail> getList() {
        return list;
    }

    public void setList(List<StateDetail> list) {
        this.list = list;
    }
}
