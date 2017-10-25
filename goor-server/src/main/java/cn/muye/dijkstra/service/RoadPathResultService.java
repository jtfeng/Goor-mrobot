package cn.muye.dijkstra.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.muye.base.service.BaseService;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;

public interface RoadPathResultService {

    RoadPathResult addCloudRoadPathPoint(RoadPathResult result) throws Exception;

    RoadPathResult replaceDoorWaitPoint(RoadPathResult result, MapPointType mapPointType) throws Exception;

    RoadPathResult getShortestCloudRoadPathForMission(Long startPointId, Long endPointId, RoadPathMaps roadPathMaps,RoadPathResult result) throws Exception;

    RoadPathResult getShortestCloudRoadPathForMission(MapPoint startPoint, MapPoint endPoint, RoadPathMaps roadPathMaps, RoadPathResult result) throws Exception;
}