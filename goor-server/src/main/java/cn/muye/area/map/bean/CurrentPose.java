package cn.muye.area.map.bean;

import cn.mrobot.bean.area.map.MapInfo;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by Jelynn on 2017/7/13.
 */
public class CurrentPose {

    private String pose;

   private MapInfo mapInfo;

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
}
