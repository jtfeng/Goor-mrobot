package cn.muye.assets.scene.mapper;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.muye.util.MyMapper;

import java.util.List;

public interface SceneMapper extends MyMapper<Scene> {

    int insertSceneAndMapRelations(Long sceneId, String mapSceneName);

    int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds);

    List<Robot> findRobotBySceneId(Long sceneId);

    List<MapInfo> findMapBySceneId(Long sceneId, Long storeId);

    void deleteRobotAndSceneRelations(Long sceneId);

    void deleteMapAndSceneRelations(Long sceneId);

    int checkRobot(Long robotId);

    int checkRobotLegal(Long robotId);

    int checkMapInfo(String mapSceneName, Long storeId);

    int checkMapLegal(String mapSceneName, Long storeId);

    void setSceneState(String sceneName, Long storeId, Integer state, String mapSyncResult);

    List<MapInfo> findMapBySceneName(String SceneName, Long storeId);

    void setSceneStateForUpload(Long sceneId, Integer state, String mapSyncResult);
}