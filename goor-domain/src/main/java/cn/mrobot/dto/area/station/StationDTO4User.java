package cn.mrobot.dto.area.station;

import cn.mrobot.bean.assets.robot.Robot;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/4.
 */
public class StationDTO4User {

    private Long id;

    private String name;

    private Long sceneId;

    private Integer stationTypeId;

    private List<Robot> robotList;

    private int active;

    public Integer getStationTypeId() {
        return stationTypeId;
    }

    public void setStationTypeId(Integer stationTypeId) {
        this.stationTypeId = stationTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public List<Robot> getRobotList() {
        return robotList;
    }

    public void setRobotList(List<Robot> robotList) {
        this.robotList = robotList;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
