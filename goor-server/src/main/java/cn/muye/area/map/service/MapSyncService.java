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

    Object sendMapSyncMessage(List<Robot> robotList, MapZip mapZip, Long sceneId);

    /**
     * 同步地图,忽略地图上传机器人
     *
     * @param robotList
     * @param mapZip
     * @return
     */
    Object sendMapSyncMessageIgnoreUploadRobot(List<Robot> robotList, MapZip mapZip, Long sceneId);

}
