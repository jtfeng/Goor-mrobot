package cn.mrobot.bean.assets.scene;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by wlkfec on 19/07/2017.
 */
@Table(name = "AS_SCENE")
public class Scene extends BaseBean{
    private String name;
    private String aliasName;
    private String intro;

    public Scene() {
    }

    public Scene(Long id) {
        super(id);
    }

    @Transient
    private List<MapInfo> mapInfos;
    @Transient
    private List<Robot> robots;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<MapInfo> getMapInfos() {
        return mapInfos;
    }

    public void setMapInfos(List<MapInfo> mapInfos) {
        this.mapInfos = mapInfos;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", intro='" + intro + '\'' +
                ", mapInfos=" + mapInfos +
                ", robots=" + robots +
                '}';
    }
}