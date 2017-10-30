package cn.muye.dijkstra.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.constant.Constant;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.muye.dijkstra.service.RoadPathResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chay on 2017/10/24.
 */
@Service
@Transactional
public class RoadPathResultServiceImpl implements RoadPathResultService {
    private static final Logger log = LoggerFactory.getLogger(RoadPathResultServiceImpl.class);
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private PointService pointService;

    /**
     * 遍历result的点序列，两点间有云端路径的话，把云端路径点添加到两点间
     * @param result
     */
    @Override
    public RoadPathResult addCloudRoadPathPoint(RoadPathResult result) throws Exception{
        List<Long> idList = result.getPointIds();
        List<Long> returnIdList = new ArrayList<Long>();

        for(int i=1;i<idList.size();i++) {
            //第一次循环的第一个点必须添加
            if(returnIdList.size() == 0) {
                returnIdList.add(idList.get(0));
            }

            Long preId = idList.get(i-1);
            Long thisId = idList.get(i);
            if(preId == null || thisId == null) {
                continue;
            }

            MapPoint prePoint = pointService.findById(preId);
            if(prePoint == null) {
                continue;
            }

            List<RoadPathDetail> roadPathDetails =
                    roadPathService.listRoadPathDetailByStartAndEndPointType(
                            preId,
                            thisId,
                            prePoint.getSceneName(),
                            prePoint.getMapName(),
                            Constant.PATH_TYPE_CLOUD
                    );
            if(roadPathDetails == null || roadPathDetails.size() <= 0) {
                returnIdList.add(thisId);
            }
            else {
                //取第一个有效的进行任务插入
                for (RoadPathDetail rpd :
                        roadPathDetails) {
                    if (rpd != null &&
                            rpd.getStart() != null &&
                            rpd.getEnd() != null &&
                            rpd.getRelatePoints() != null) {
                        for (MapPoint point :
                                rpd.getRelatePoints()) {
                            //相同的前一个点ID不需要重复插入
                            if (point != null && !point.getId().equals(preId)){
                                returnIdList.add(point.getId());
                            }
                        }
                        break;
                    }
                }
            }
        }
        result.setPointIds(returnIdList);
        return result;
    }

    /**
     * 遍历result的点序列，替换为根据坐标、场景、地图名查找到的门等待点的点
     * @param result
     * @param mapPointType
     */
    @Override
    public RoadPathResult replaceDoorWaitPoint(RoadPathResult result, MapPointType mapPointType) throws Exception {
        List<Long> idList = result.getPointIds();
        //最后一个点不用替换成门任务，因为没有下一个任务点，说明不需要过门
        for(int i=0;i<idList.size() - 1;i++) {
            Long id = idList.get(i);
            MapPoint pathPoint = pointService.findById(id);
            if(pathPoint == null) {
                continue;
            }
            //查询所有与路径点坐标相同的点，且类型为：门等待点
            List<MapPoint> endPointList = pointService.listBySceneMapXYTH(pathPoint.getSceneName(),pathPoint.getMapName(),
                    pathPoint.getX(),pathPoint.getY(),pathPoint.getTh(),mapPointType);
            if(endPointList == null || endPointList.size() == 0) {
                continue;
            }
            idList.set(i , endPointList.get(0).getId());
        }
        return result;
    }

    /**
     * 获取最短的路径，并替换门店，并将最短路径中的云端路径的点加入到路径结果中
     * @param startPointId
     * @param endPointId
     * @param roadPathMaps
     * @param result
     * @return
     * @throws Exception
     */
    @Override
    public RoadPathResult getShortestCloudRoadPathForMission(Long startPointId, Long endPointId, RoadPathMaps roadPathMaps, RoadPathResult result) throws Exception {
        result = roadPathMaps.getShortestPath(startPointId,endPointId);
        if(result == null || result.getPointIds() == null || result.getPointIds().size() <= 1) {
            return result;
        }
        //TODO 遍历result的点序列，替换为根据坐标、场景、地图名查找到的门等待点的点
        addCloudRoadPathPoint(result);
        replaceDoorWaitPoint(result , MapPointType.DOOR_WAIT);
        return result;
    }

    @Override
    public RoadPathResult getShortestCloudRoadPathForMission(MapPoint startPoint, MapPoint endPoint, RoadPathMaps roadPathMaps, RoadPathResult result) throws Exception {
        return getShortestCloudRoadPathForMission(startPoint.getId(),endPoint.getId(),roadPathMaps,result);
    }
}
