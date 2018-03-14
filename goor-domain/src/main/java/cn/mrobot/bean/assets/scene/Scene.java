package cn.mrobot.bean.assets.scene;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlkfec on 19/07/2017.
 */
@Table(name = "AS_SCENE")
public class Scene extends BaseBean{

    private String name;
    private String intro;
    /**
     * state 0 代表未同步、1 代表上传成功、2 代表上传失败、3 代表有更新
     */
    private Integer state;
    /**
     * active 表示当前场景的状态：1 表示激活 ； 0 表示锁定
     */
    private Integer active;

    public Scene() {
    }

    public Scene(Long id) {
        super(id);
    }

    @Transient
    private String mapSceneName;

    @Transient
    private List<MapInfo> mapInfoList = new ArrayList<>();

    @Transient
    private List<Robot> robots = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMapSceneName() {
        return mapSceneName;
    }

    public void setMapSceneName(String mapSceneName) {
        this.mapSceneName = mapSceneName;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    public List<MapInfo> getMapInfoList() {
        return mapInfoList;
    }

    public void setMapInfoList(List<MapInfo> mapInfoList) {
        this.mapInfoList = mapInfoList;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                ", state=" + state +
                ", active=" + active +
                ", mapSceneName='" + mapSceneName + '\'' +
                ", robots=" + robots +
                ", mapInfoList=" + mapInfoList +
                '}';
    }
}