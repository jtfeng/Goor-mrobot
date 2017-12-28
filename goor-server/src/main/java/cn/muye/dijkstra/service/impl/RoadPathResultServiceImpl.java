package cn.muye.dijkstra.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.constants.LineMathUtil;
import cn.muye.area.map.bean.CurrentInfo;
import cn.muye.area.map.bean.Orientation;
import cn.muye.area.map.bean.Position;
import cn.muye.area.map.bean.RosCurrentPose;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.muye.dijkstra.service.RoadPathResultService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.util.PathUtil;
import com.alibaba.fastjson.JSON;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadPathResultServiceImpl.class);
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private PointService pointService;
    @Autowired
    private MapInfoService mapInfoService;

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
        //只有一个点的时候，说明就在当前点，不需要过门，直接返回
        if(result == null || result.getPointIds() == null
            || result.getPointIds().size() <= 1) {
            return result;
        }
        List<Long> idList = result.getPointIds();
        List<Long> returnIdList = new ArrayList<Long>();
        int size = idList.size();

        //最后一个点不用替换成门任务，因为没有下一个任务点，说明不需要过门
        for(int i=0;i< size - 1;i++) {
            Long id = idList.get(i);
            MapPoint pathPoint = pointService.findById(id);
            if(pathPoint == null) {
                continue;
            }
            returnIdList.add(id);
            //查询所有与路径点坐标相同的点，且类型为：门等待点
            List<MapPoint> endPointList = pointService.listBySceneMapXYTH(pathPoint.getSceneName(),pathPoint.getMapName(),
                    pathPoint.getX(),pathPoint.getY(),pathPoint.getTh(),mapPointType);
            //如果没有门类型点，则直接返回原来的点
            if(endPointList == null || endPointList.size() == 0) {
                continue;
            }
            //如果找到门任务点，则添加门任务点到原路径后面
            returnIdList.add(endPointList.get(0).getId());
        }
        //把最后一个点要放进去，要不然会少一个点
        returnIdList.add(idList.get(size - 1));
        result.setPointIds(returnIdList);
        return result;
    }

    /**
     * 获取最短的路径，并替换门点，并将最短路径中的云端路径的点加入到路径结果中
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
        if(result == null || result.getPointIds() == null || result.getPointIds().size() == 0) {
            LOGGER.info("查找点" + startPointId + "到目标点" + endPointId
                    +"的可用的最短路径规划：" + (result == null ? "未找到" : "找到" + result.getPointIds()));
            return result;
        }
        //遍历result的点序列，替换为根据坐标、场景、地图名查找到的门等待点的点
        addCloudRoadPathPoint(result);
        replaceDoorWaitPoint(result , MapPointType.DOOR_WAIT);
        result.setStartPoint(new MapPoint(startPointId));
        result.setEndPoint(new MapPoint(endPointId));
        LOGGER.info("查找点" + startPointId + "到目标点" + endPointId
                +"的可用的最短路径规划,并替换门、电梯任务点：" + result.getPointIds());
        return result;
    }

    @Override
    public RoadPathResult getShortestCloudRoadPathForMission(MapPoint startPoint, MapPoint endPoint, RoadPathMaps roadPathMaps, RoadPathResult result) throws Exception {
        return getShortestCloudRoadPathForMission(startPoint.getId(),endPoint.getId(),roadPathMaps,result);
    }

    @Override
    public RoadPathResult getNearestPathResultByRosCurrentPose(RoadPathMaps roadPathMaps, int startPointType, RosCurrentPose rosCurrentPose, MapPoint targetPoint, String sceneName, String mapName) throws Exception {
        Orientation orientation = rosCurrentPose.getOrientation();
        if(orientation == null) {
            LOGGER.info("==============解析机器人坐标角度为空。");
            return null;
        }
        Position position = rosCurrentPose.getPosition();
        if(position == null) {
            LOGGER.info("==============解析机器人坐标为空。");
            return null;
        }

        //根据四元数换算欧拉角
        double th = PathUtil.calThByOrientation(orientation);
        LOGGER.info("================四元数w=" + orientation.getW() + ",换算的欧拉角(弧度)=" + th);
        //我们认为保留3位小数换算出来TH，作为比较条件，后面几位精度基本可以忽略了
        MapPoint rosPoint = PathUtil.findPathPointByXYTH(sceneName, mapName ,
                LineMathUtil.floorDoubleByScale(position.getX(), Constant.XYZ_SCALE),
                LineMathUtil.floorDoubleByScale(position.getY(), Constant.XYZ_SCALE),
                LineMathUtil.floorDoubleByScale(th,Constant.TH_SCALE),
                null, pointService );

        RoadPathResult roadPathResult = new RoadPathResult();
        roadPathResult.setEndPoint(targetPoint);
        //如果精确匹配到了路径点，那么就返回匹配到的点，理论上机器人上传的应该还是误差挺大的
        if(rosPoint != null) {
            LOGGER.info("//==================找到与机器人位置相匹配的路径点" + rosPoint.getPointAlias()
                    + ",mapName:" + rosPoint.getMapName() + ",sceneName:" + rosPoint.getSceneName());
            //查询可用的任务路径(替换过门点和电梯点)
            roadPathResult = getShortestCloudRoadPathForMission(rosPoint.getId(), targetPoint.getId(), roadPathMaps, roadPathResult);
            roadPathResult.setStartPoint(rosPoint);
            return roadPathResult;
        }
        //如果没有匹配到，则需要寻找离机器人位置点最近的路径点
        else {
            rosPoint = new MapPoint();
            rosPoint.setY(position.getY());
            rosPoint.setX(position.getX());
            rosPoint.setTh(th);
            rosPoint.setSceneName(sceneName);
            rosPoint.setMapName(mapName);
            LOGGER.info("//=================未找到与机器人位置相匹配的路径点，开始算法计算最近路径起点，时间：" + cn.mrobot.utils.DateTimeUtils.getCurrentDateTimeString());
            //从缓存的路径去计算距离，然后取起点作为规划路径的代替点。
            List<RoadPathDetail> roadPathDetails = CacheInfoManager.getRoadPathDetailsCache(SearchConstants.FAKE_MERCHANT_STORE_ID,
                    sceneName, Constant.PATH_TYPE_X86, roadPathService);
            if(roadPathDetails == null || roadPathDetails.size() == 0) {
                LOGGER.info("==============未找到当前场景下的工控路径。");
                return null;
            }

            //先计算离机器人位置最近的路径，然后计算路径起点到目的地点最近的路径起点作为输出,权值计算根据类型取路径起点，还是取投影点
            roadPathResult = PathUtil.calNearestPathPointByRoadPathDetails(roadPathMaps, startPointType, roadPathDetails, rosPoint, targetPoint, this);
            LOGGER.info("//=================算法计算最近路径起点结束，时间：" + cn.mrobot.utils.DateTimeUtils.getCurrentDateTimeString() + roadPathResult == null ? "结果为空，未找到路径！"
                : "总权值:" + roadPathResult.getTotalWeight() + ",点序列：" + roadPathResult.getPointIds());
            return roadPathResult;
        }
    }

    @Override
    public RoadPathResult getNearestPathResultByRobotCode(Robot robotDb, int startPointType, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        String robotCode = robotDb.getCode();
        //获取机器人坐标
        CurrentInfo currentInfo = mapInfoService.getCurrentInfo(robotCode);
        String pose = currentInfo.getPose();
        if(pose == null || StringUtil.isNullOrEmpty(pose)) {
            stringBuffer.append("下单获取可用机器：" + robotCode  + "不可用。未获取到机器人坐标！");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return null;
        }

        RosCurrentPose rosCurrentPose = null;
        try {
            rosCurrentPose = JSON.parseObject(pose,RosCurrentPose.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            stringBuffer.append("下单获取可用机器：" + robotCode  + "不可用。获取到的机器人坐标格式解析错误！");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return null;
        }
        LOGGER.info("###############开始查找离" + robotCode + "机器人坐标最近的路径。");
        //通过机器人所在位置找到最近的路径的起点作为机器人的出发点，并计算出结果
        RoadPathResult result = getNearestPathResultByRosCurrentPose(roadPathMaps, startPointType,
                        rosCurrentPose, targetPoint, currentInfo.getMapInfo().getSceneName(), currentInfo.getMapInfo().getMapName());

        return result;
    }

    @Override
    public RoadPathResult getNearestPathResultStartShadowPointByRobotCode(Robot robotDb, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception {
        return getNearestPathResultByRobotCode(robotDb, Constant.CAL_ROAD_PATH_START_SHADOW, targetPoint, roadPathMaps);
    }

    @Override
    public RoadPathResult getNearestPathResultStartPathPointByRobotCode(Robot robotDb, MapPoint targetPoint, RoadPathMaps roadPathMaps) throws Exception {
        return getNearestPathResultByRobotCode(robotDb, Constant.CAL_ROAD_PATH_START_PATH, targetPoint, roadPathMaps);
    }
}
