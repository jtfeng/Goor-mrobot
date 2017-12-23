package cn.muye.dijkstra.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.area.map.bean.RosCurrentPose;
import cn.muye.base.service.BaseService;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;

public interface RoadPathResultService {

    RoadPathResult addCloudRoadPathPoint(RoadPathResult result) throws Exception;

    RoadPathResult replaceDoorWaitPoint(RoadPathResult result, MapPointType mapPointType) throws Exception;

    RoadPathResult getShortestCloudRoadPathForMission(Long startPointId, Long endPointId, RoadPathMaps roadPathMaps,RoadPathResult result) throws Exception;

    RoadPathResult getShortestCloudRoadPathForMission(MapPoint startPoint, MapPoint endPoint, RoadPathMaps roadPathMaps, RoadPathResult result) throws Exception;

    /**
     * 通过机器人所在位置找到最近的路径的起点作为机器人的出发点
     *
     * @param roadPathMaps
     * @param rosCurrentPose 机器人位置
     * @param targetPoint 目的地点
     * @param sceneName
     * @param mapName
     * @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultByRosCurrentPose(RoadPathMaps roadPathMaps, RosCurrentPose rosCurrentPose, MapPoint targetPoint, String sceneName, String mapName) throws Exception;

    /**
     * 根据机器人，查询机器人当前位置，返回从机器人当前位置到目的地的路径规划结果
     * @param robotDb
     * @param targetPoint
     * @param roadPathMaps
     * @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultByRobotCode(Robot robotDb, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception;
}