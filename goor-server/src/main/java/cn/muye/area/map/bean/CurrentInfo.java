package cn.muye.area.map.bean;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.charge.ChargeInfo;

import java.util.List;

/**
 * Created by Jelynn on 2017/7/13.
 */
public class CurrentInfo {

    private boolean online; //是否开机

    private String pose; //位置信息

    private MapInfo mapInfo; //地图信息

    private ChargeInfo chargeInfo; //电量信息

    private List<StateDetail> list;  //状态

    private String fault; //故障

    private int taskStateCode; //任务状态

    private List<StateDetail> mission; //任务主状态

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

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

    public List<StateDetail> getMission() {
        return mission;
    }

    public void setMission(List<StateDetail> mission) {
        this.mission = mission;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public int getTaskStateCode() {
        return taskStateCode;
    }

    public void setTaskStateCode(int taskStateCode) {
        this.taskStateCode = taskStateCode;
    }
}
