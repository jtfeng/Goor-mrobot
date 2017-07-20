package cn.muye.assets.scene.mapper;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.muye.util.MyMapper;

import java.util.List;

public interface SceneMapper extends MyMapper<Scene> {

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

    /**
     * 根据 ID 查询所有关联的机器人 ID 编号
     * @param sceneId
     * @return
     */
    List<Robot> findRobotBySceneId(Long sceneId);

    /**
     * 根据 ID 查询所有关联的地图 ID 编号
     * @param sceneId
     * @return
     */
    List<MapInfo> findMapBySceneId(Long sceneId);

    /**
     * 删除历史数据（场景与机器人的对应关系）
     */
    void deleteRobotAndSceneRelations(Long sceneId);

    /**
     * 删除历史数据（场景与地图的对应关系）
     */
    void deleteMapAndSceneRelations(Long sceneId);
}