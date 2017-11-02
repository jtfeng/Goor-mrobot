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

    /**
     * 新增roadPath及其点集合
     * @param roadPath
     * @param roadPathPointIds
     * @throws Exception
     */
    void createRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    /**
     * 更新roadPath及其点集
     * @param roadPath
     * @param roadPathPointIds
     * @throws Exception
     */
    void updateRoadPathByRoadPathPointList(RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    /**
     * 根据起始点和终点ID、场景名、地图名，新增或更新roadPath及其关联点序列
     * @param startPointId
     * @param endPointId
     * @param sceneName
     * @param mapName
     * @param pathType
     * @param roadPath
     * @param roadPathPointIds
     * @throws Exception
     */
    void createOrUpdateRoadPathByStartAndEndPoint(Long startPointId, Long endPointId, String sceneName, String mapName, Integer pathType,RoadPath roadPath,List<Long> roadPathPointIds) throws Exception;

    /**
     * 根据PathDTOList插入点和工控路径
     * @param pathDTOList
     * @param sceneName
     * @param isPointDuplicate 是否建立重复的路径交点
     * @throws Exception
     */
    void saveOrUpdateRoadPathByPathDTOList(List<PathDTO> pathDTOList, String sceneName , boolean isPointDuplicate) throws Exception;

    /**
     * 根据PathDTOList插入点和工控路径, 重复建立路径交叉点
     * @param pathDTOList
     * @param sceneName
     * @throws Exception
     */
    void saveOrUpdateRoadPathByPathDTOListDuplicatePoint(List<PathDTO> pathDTOList, String sceneName) throws Exception;

    /**
     * 根据PathDTOList插入点和工控路径, 不重复建立路径交叉点
     * @param pathDTOList
     * @param sceneName
     * @throws Exception
     */
    void saveOrUpdateRoadPathByPathDTOListNoDuplicatePoint(List<PathDTO> pathDTOList, String sceneName) throws Exception;

    List<RoadPathDetail> findRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName) throws Exception;

    List<RoadPathDetail> listRoadPathDetailByStartAndEndPointType(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPathByStartAndEndPoint(Long startPoint, Long endPoint, String sceneName, String mapName, Integer pathType) throws Exception;

    List<RoadPath> listRoadPaths(WhereRequest whereRequest) throws Exception;

    Boolean hasRelatedRoadPath(Long id);

    RoadPath findRoadPath(RoadPath roadPath) throws Exception ;

    List<RoadPath> listRoadPathsBySceneNamePathType(String sceneName, Integer pathType);

    RoadPath findBySceneAndX86RoadPathId(Long x86RoadPathId, String sceneName, String mapName);

    void deleteBySceneName(String sceneName);
}