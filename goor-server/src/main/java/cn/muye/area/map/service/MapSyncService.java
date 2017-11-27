package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.robot.Robot;

import java.util.List;

/**
 * Created by Jelynn on 2017/11/24.
 * @author Jelynn
 */
public interface MapSyncService {

    Object syncMap(MapZip mapZip, long storeId);

    Object syncMap(MapZip mapZip, List<Robot> robotList);

    /**
     * 同步地图
     *
     * @param robotList
     * @param mapZip
     * @return
     */
    Object sendMapSyncMessage(List<Robot> robotList, MapZip mapZip);

    /**
     * 同步地图
     *
     * @param robotList
     * @param mapZip
     * @return
     */
    Object sendMapSyncMessage(List<Robot> robotList, MapZip mapZip, Long sceneId);

    /**
     * 同步地图,robotList所有机器人进行同步，不区分地图上传机器人
     * 接口根据mapSceneName，对该场景下的地图进行压缩，同步
     * 每次仅同步一个场景下地图，
     *agent端接收到地图后对原地图不进行删除，同名覆盖
     *
     * @param robotList 需要同步的机器人列表
     * @param mapSceneName  地图场景名
     * @param sceneId
     * @return
     */
    Object sendMapSyncMessageNew(List<Robot> robotList, String mapSceneName, Long sceneId);

}
