package cn.mrobot.bean.area.station;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by chay on 2017/6/7.
 * 护士站
 */
@Table(name = "A_STATION")
public class Station extends BaseBean{
    /**
     * 站名
     */
    private String name;

    /**
     * 站类型索引
     * */
    private Integer stationTypeId;

    /**
     * 关联场景
     * */
    private Long sceneId;

    /**
     * 假删除标志：0 未删除，1 已删除
     */
    private int active;

    /**
     * 站类型JSON串:不存库
     * */
//    @Transient
//    private String stationType;
    /**
     * 描述
     */
    private String description;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Station() {
    }

    public Station(Long id) {
        super(id);
    }

    /**
     * 关联点列表：卸货点、停车点、充电桩点、充电桩原点、装货点等
     * */
    @Transient
    private List<MapPoint> mapPoints;

    @Transient
    private List<Robot> robotList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStationTypeId() {
        return stationTypeId;
    }

    public void setStationTypeId(Integer stationTypeId) {
        this.stationTypeId = stationTypeId;
    }

//    public String getStationType() {
//        return stationType;
//    }
//
//    public void setStationType(String stationType) {
//        this.stationType = stationType;
//    }

    public List<MapPoint> getMapPoints() {
        return mapPoints;
    }

    public void setMapPoints(List<MapPoint> mapPoints) {
        this.mapPoints = mapPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Robot> getRobotList() {
        return robotList;
    }

    public void setRobotList(List<Robot> robotList) {
        this.robotList = robotList;
    }
}
