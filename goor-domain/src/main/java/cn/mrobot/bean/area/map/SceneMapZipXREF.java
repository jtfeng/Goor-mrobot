package cn.mrobot.bean.area.map;

import javax.persistence.Table;

/**
 * Created by Jelynn on 2017/8/2.
 * 地图名，场景名和地图压缩包关联表
 */
@Table(name = "A_SCENE_MAP_ZIP_XREF")
public class SceneMapZipXREF{

    /**
     * 地图名
     */
    private String mapName;

    /**
     * 场景名
     */
    private String sceneName;

    private Long mapZipId;

    private int deleteFlag;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public Long getMapZipId() {
        return mapZipId;
    }

    public void setMapZipId(Long mapZipId) {
        this.mapZipId = mapZipId;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
