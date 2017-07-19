package cn.muye.area.map.bean;

import cn.mrobot.bean.area.map.MapInfo;
//import cn.mrobot.bean.state.enums.CollectorState;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by Jelynn on 2017/7/13.
 */
public class CurrentInfo {

    private String pose;

    private MapInfo mapInfo;

//    private CollectorState collectorState;

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

//    public CollectorState getCollectorState() {
//        return collectorState;
//    }
//
//    public void setCollectorState(CollectorState collectorState) {
//        this.collectorState = collectorState;
//    }
}
