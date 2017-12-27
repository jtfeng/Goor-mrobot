package cn.muye.dijkstra.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.area.map.bean.RosCurrentPose;
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
     * @param startPointType
     * @param rosCurrentPose 机器人位置
     * @param targetPoint 目的地点
     * @param sceneName
     * @param mapName     @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultByRosCurrentPose(RoadPathMaps roadPathMaps, int startPointType, RosCurrentPose rosCurrentPose, MapPoint targetPoint, String sceneName, String mapName) throws Exception;

    /**
     * 根据机器人，查询机器人当前位置，根据起点类型返回从机器人当前位置到目的地的路径规划结果
     * 起点类型可配置，决定返回以当前机器人位置最近的路径起点算权值，还是以当前机器人位置最近的路径点投影算权值
     * @param robotDb
     * @param startPointType
     * @param targetPoint
     * @param roadPathMaps
     * @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultByRobotCode(Robot robotDb, int startPointType, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception;

    /**
     * 根据机器人，查询机器人当前位置，返回从机器人当前位置到目的地的路径规划结果
     * 以当前机器人位置最近的路径点投影算权值
     * @param robotDb
     * @param targetPoint
     * @param roadPathMaps
     * @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultStartShadowPointByRobotCode(Robot robotDb, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception;

    /**
     * 根据机器人，查询机器人当前位置，返回从机器人当前位置到目的地的路径规划结果
     * 以当前机器人位置最近的路径点投影算权值
     * @param robotDb
     * @param targetPoint
     * @param roadPathMaps
     * @throws Exception
     * @return
     */
    RoadPathResult getNearestPathResultStartPathPointByRobotCode(Robot robotDb, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception;
}