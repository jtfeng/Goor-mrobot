package cn.muye.assets.roadpath.service;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Map;

public interface RoadPathService extends BaseService<RoadPath> {

    void createRoadPath(Map<String, Object> body) throws Exception;

    void updateRoadPath(Map<String, Object> body) throws Exception;

    void createRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    void updateRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    void createOrUpdateRoadPathByStartAndEndPoint(Long startPointId, Long endPointId, String sceneName, String mapName, Integer pathType,RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    void saveOrUpdateRoadPathByPathDTOList(List<PathDTO> pathDTOList, String sceneName , boolean isPointDuplicate) throws Exception;

    List<RoadPathDetail> findRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName) throws Exception;

    List<RoadPathDetail> listRoadPathDetailByStartAndEndPointType(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPaths(WhereRequest whereRequest) throws Exception;

    Boolean hasRelatedRoadPath(Long id);

    RoadPath findRoadPath(RoadPath roadPath) throws Exception ;

    List<RoadPath> listRoadPathsBySceneNamePathType(String sceneName, Integer pathType);

    RoadPath findBySceneAndX86RoadPathId(Long x86RoadPathId, String sceneName);
}