package cn.muye.area.point.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.RosCurrentPose;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/15
 * Time: 16:15
 * Describe:
 * Version:1.0
 */
public interface PointService {

    long save(MapPoint mapPoint);

    int save(List<MapPoint> mapPointList);

    void delete(MapPoint mapPoint);

    void delete(String sceneName, String mapName, long storeId);

    void update(MapPoint mapPoint);

    MapPoint findById(long id);

    List<MapPoint> findByName(String pointName, String sceneName, String mapName, long storeId);

    List<MapPoint> findByNameCloudType(String pointName, String sceneName, String mapName, long storeId, MapPointType mapPointType);

    @Deprecated
    List<MapPoint> findBySceneName(String sceneName);

    List<MapPoint> list(WhereRequest whereRequest, long storeId);

    List<MapPoint> listBySceneId(WhereRequest whereRequest, Long storeId);

    @Deprecated
    void handle(SlamResponseBody slamResponseBody);

    List<CascadePoint> cascadeMapPoint(int level, String sceneName);

    void delete(long storeId, int deleteFlag);

    void updateDeleteFlag(long storeId, long mapZipId, int deleteFlag);

    List<MapPoint> listByMapSceneNameAndPointType(String mapSceneName, Integer type, Long storeId);

    /**
     * 根据站ID和点类型查找对应的站里面的点，只返回第一个点
     * @param stationId
     * @param pointType
     * @return
     */
    MapPoint findMapPointByStationIdAndCloudType(Long stationId, int pointType);

    /**
     * 根据站ID和点类型查找与对应的站里面的该类型的点相同属性的路径点，只返回第一个点
     * @param stationId
     * @param pointType
     * @return
     */
    MapPoint findPathMapPointByStationIdAndCloudType(Long stationId, int pointType);

    List<MapPoint> listBySceneMapXYTH(String sceneName, String mapName, double x, double y, double th, MapPointType mapPointType);



}
