package cn.muye.assets.roadpath.service;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Map;

public interface RoadPathService extends BaseService<RoadPath> {

    void createRoadPath(Map<String, Object> body) throws Exception;

    void updateRoadPath(Map<String, Object> body) throws Exception;

    List<RoadPathDetail> findRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName) throws Exception;

    List<RoadPathDetail> listRoadPathDetailByStartAndEndPointType(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPaths(WhereRequest whereRequest) throws Exception;


}