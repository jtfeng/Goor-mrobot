package cn.muye.assets.scene.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/3.
 */
public interface SceneService extends BaseService<Scene> {

    List<Scene> list() throws Exception;

    Object saveScene(Scene scene) throws Exception;

    Scene getSceneById(Long id) throws Exception;

    List<MapPoint> listMapPointIdBySceneId(Long sceneId, Long storeId, Long cloudMapPointTypeId) throws Exception;

    Object updateScene(Scene scene) throws Exception;

    int deleteSceneById(Long id) throws Exception;

    List<Scene> listScenes(WhereRequest whereRequest) throws Exception;

    int insertSceneAndMapRelations(Long sceneId, String mapSceneName) throws Exception;

    int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds) throws Exception;

    Object sendSyncMapMessageToRobots(Long sceneId) throws Exception;

    Object sendSyncMapMessageToSpecialRobots(Map<String, Object> params) throws Exception;

    void deleteRobotAndSceneRelations(Long sceneId) throws Exception;

    void deleteMapAndSceneRelations(Long sceneId) throws Exception;

    int checkRobot(Long robotId) throws Exception;

    void bindSceneAndMapRelations(Scene scene) throws Exception;

    boolean bindSceneAndRobotRelations(Scene scene, List<Long> distinctIDS) throws Exception;

    boolean checkSceneIsNeedToBeUpdated(String mapSceneName, String storeId) throws Exception;

    void updateSceneState(int state, Long sceneId) throws Exception;

    Scene storeSceneInfoToSession(String source, String sceneId, String token) throws Exception;
   
    String getRelatedMapNameBySceneId(Long sceneId);

    /**
     * 机器人开机管理获取云端相关资源 - （场景、地图、站）- 返回一个 JSON 字符串
     * @return
     */
    Map getRobotStartAssets(String robotCode);

    /**
     * 返回云端相关资源给机器人开机管理
     */
    void replyGetRobotStartAssets(String uuid, String robotCode);

    /**
     * 根据反馈更新数据关系
     * @param latestRobotAssets
     */
    void updateGetRobotStartAssets(JSONObject latestRobotAssets);
}