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

    List<Scene> list();

    int save(Scene scene);

    Scene getById(Long id);

    int update(Scene scene);

    int deleteById(Long id);

    List<Scene> listScenes(WhereRequest whereRequest);

    Scene updateAliasName(Long sceneId, String aliasName);

    /**
     * 保存场景与地图之间的多对多关系数据
     * @param sceneId
     * @param mapIds
     * @return
     */
    int insertSceneAndMapRelations(Long sceneId, List<Long> mapIds);

    /**
     * 保存场景与机器人之间的多对多关系
     * @param sceneId
     * @param robotIds
     * @return
     */
    int insertSceneAndRobotRelations(Long sceneId, List<Long> robotIds);

    void sendSyncMapMessageToRobots(Long sceneId);

    /**
     * 删除历史数据（场景与机器人的对应关系）
     */
    void deleteRobotAndSceneRelations(Long sceneId);

    /**
     * 删除历史数据（场景与地图的对应关系）
     */
    void deleteMapAndSceneRelations(Long sceneId);
}