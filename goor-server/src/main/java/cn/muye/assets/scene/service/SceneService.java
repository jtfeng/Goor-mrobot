package cn.muye.assets.scene.service;

import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by admin on 2017/7/3.
 */
public interface SceneService extends BaseService<Scene> {

    List<Scene> list() throws Exception;

    int saveScene(Scene scene) throws Exception;

    Scene getSceneById(Long id) throws Exception;

    int updateScene(Scene scene) throws Exception;

    int deleteSceneById(Long id) throws Exception;

    List<Scene> listScenes(WhereRequest whereRequest) throws Exception;

    int insertSceneAndMapRelations(Long sceneId, String mapSceneName) throws Exception;

    int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds) throws Exception;

    void sendSyncMapMessageToRobots(Long sceneId) throws Exception;

    void deleteRobotAndSceneRelations(Long sceneId) throws Exception;

    void deleteMapAndSceneRelations(Long sceneId) throws Exception;

    int checkRobot(Long robotId) throws Exception;

    void bindSceneAndMapRelations(Scene scene) throws Exception;

    void bindSceneAndRobotRelations(Scene scene) throws Exception;

    boolean checkSceneIsNeedToBeUpdated(String mapSceneName, String storeId, Scene.SCENE_STATE state, Long ... sceneId) throws Exception;
}