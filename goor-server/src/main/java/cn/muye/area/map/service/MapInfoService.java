package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.CurrentInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/7/5
 * Time: 15:04
 * Describe:
 * Version:1.0
 */
public interface MapInfoService {

    long save(MapInfo mapInfo);

    MapInfo getMapInfo(long id);

    List<MapInfo> getMapInfo(String name, String sceneName, long storeId);

    void delete(MapInfo mapInfo);

    void deleteByPrimaryKey(Long id);

    void update(MapInfo mapInfo);

    void delete(long storeId, int deleteFlag);

    void updateDeleteFlag(long storeId, long mapZipId, int deleteFlag);

    List<MapInfo> getMapInfo(WhereRequest whereRequest, long storeId);

    List<CascadePoint> cascadeSceneMapName(int level, String sceneName);

    CurrentInfo getCurrentInfo(String code) throws Exception;

    List<MapInfo> findByName(String sceneName, String mapName, Long storeId);
}
