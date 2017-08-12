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
    private String intro;
    private Integer state;//0 代表正在上传、1 代表上传成功、2 代表上传失败、3 代表有更新

    public Scene() {
    }

    public Scene(Long id) {
        super(id);
    }

    @Transient
    private String mapSceneName;
    @Transient
    private List<Robot> robots;
    private String mapSyncResult;

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

    public String getMapSyncResult() {
        return mapSyncResult;
    }

    public void setMapSyncResult(String mapSyncResult) {
        this.mapSyncResult = mapSyncResult;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                ", state=" + state +
                ", mapSceneName='" + mapSceneName + '\'' +
                ", robots=" + robots +
                ", mapSyncResult=" + mapSyncResult +
                '}';
    }

    /**
     * 在使用的两种场景状态
     */
    public static enum SCENE_STATE {
        UPLOAD_SUCCESS,
        UPDATE_STATE
    }
}