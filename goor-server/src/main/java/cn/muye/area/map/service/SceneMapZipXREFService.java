package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.SceneMapZipXREF;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/2.
 */
public interface SceneMapZipXREFService {

    long save(SceneMapZipXREF sceneMapZipXREF);

    List<SceneMapZipXREF> list(Long mapZipId);

    long getMapZipId(String sceneName, String mapName);

    void delete(Long mapZipId);

    void updateDeleteFlag(Long mapZipId, int deleteFlag);

    void delete(int deleteFlag);

}
