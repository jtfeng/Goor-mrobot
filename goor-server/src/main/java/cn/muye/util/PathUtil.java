package cn.muye.util;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.mrobot.bean.dijkstra.RobotRoadPathResult;
import cn.mrobot.bean.dijkstra.TriangleResult;
import cn.mrobot.bean.order.Order;
import cn.mrobot.dto.area.PathDTO;
import cn.muye.area.map.bean.Orientation;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.dijkstra.service.RoadPathResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Created by chay on 2017/12/13.
 * 路径规划相关的工具类
 */
public class PathUtil {
    private final static Logger logger = LoggerFactory.getLogger(PathUtil.class);

    /**
     * 计算两点间坐标的长度，最终单位mm
     * @param start
     * @param end
     * @return
     */
    public static Long calDistance(MapPoint start, MapPoint end) {
        Long result = 0L;
        Double db = calPointToPointDistance(start.getX(), start.getY(), end.getX(), end.getY())
                 * 1000;//换算成mm
        result = doubleToLongRoundHalfUp(db);
        return result;
    }

    /**
     * 计算机器人位置到路径的距离，最终单位mm
     * 权值计算根据类型取路径起点，还是取投影点
     * @param roadPathDetail
     * @param robotPosition
     * @return
     */
    public static Long calDistanceByRoadPathDetail(RoadPathDetail roadPathDetail, MapPoint robotPosition) {
        Long result = 0L;
        MapPoint start = roadPathDetail.getStart();
        MapPoint end = roadPathDetail.getEnd();
        double x0 = robotPosition.getX();
        double y0 = robotPosition.getY();
        double x1 = start.getX();
        double y1 = start.getY();
        double x2 = end.getX();
        double y2 = end.getY();
        //以离机器人最近的路径起点计算机器人到线段的距离,换算成mm
        Double db = calPointToSegmentDistance(x0, y0, x1, y1, x2, y2) * 1000;
        result = doubleToLongRoundHalfUp(db);
        logger.info("=================计算机器人点(" + x0 + "," + y0 + ")到路径'" + roadPathDetail.getPathName() + "'的距离" + result + "mm");
        return result;
    }

    /**
     * 测试计算坐标长度函数
     */
    /*public static void main(String[] args) {
        MapPoint start = new MapPoint();
        MapPoint end = new MapPoint();
        start.setX(0L);
        start.setY(0L);
        end.setX(3L);
        end.setY(4L);
        RoadPathServiceImpl roadPathService = new RoadPathServiceImpl();
        System.out.println(roadPathService.calDistance(start, end));
    }*/

    /**
     * * 已知两点的坐便(x1, y1); (x2, y2)
     另外一个点的坐标是(x0, y0);
     求(x0, y0)到经过(x1, y1); (x2, y2)直线的距离。
     直线方程中
     A = y2 - y1,
     B = x1- x2,
     C = x2 * y1 - x1 * y2;
     点的直线的距离公式为:
     double d = (fabs((y2 - y1) * x0 +(x1 - x2) * y0 + ((x2 * y1) -(x1 * y2)))) / (sqrt(pow(y2 - y1, 2) + pow(x1 - x2, 2)));
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calPointToLineDistance(double x0, double y0,
                                                double x1, double y1,
                                                double x2, double y2) {
        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2 * y1 - x1 * y2;
        double d = (Math.abs(A * x0 +B * y0 + C)) / (Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)));
        logger.info("####################" +
                "计算点(" + x0 + "," + y0 + ")到直线上的点(" + x1 + "," + y1 + "),(" + x2 + "," + y2 +")的距离为" + d );
        return d;
    }

    /**
     * 计算点到线段的距离。
     * 与到直线距离区别是线段是有长度的，所以要考虑点到线段的投影是不是在线段上，如果是，则取到直线的距离，如果不是，则取点到最近的端点的距离。
     * r=(AP向量*AB向量)/(AB向量模)^2=AP向量的模/AB向量的模*cosΘ,Θ为AP向量与AB向量的夹角
     * 向量AP在向量AB上的投影:向量AC = r*AB向量
     *
     * P(x0,y0)到线段A(x1,y1),B(x2,y2)的距离为:
     * r<=0,AP向量的模
     * r>=1,BP向量的模
     * 其他,CP向量的模，C是P在AB向量上的投影点
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calPointToSegmentDistance(double x0, double y0,
                                                double x1, double y1,
                                                double x2, double y2) {
        //计算r的分子
        double cross = (x2 - x1) * (x0 - x1) + (y2 - y1) * (y0 - y1);
        //分子小于等于0，说明r<=0
        if (cross <= 0) {
            //返回AP向量的模
            return calPointToPointDistance(x0, y0, x1, y1);
        }

        //计算r的分母，AB向量模的平方
        double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (cross >= d2) {
            //如果r>=1,则返回BP向量的模
            return calPointToPointDistance(x0, y0, x2, y2);
        }

        //其他情况，则返回CP向量的膜
        double r = cross / d2;
        double px = x1 + (x2 - x1) * r;
        double py = y1 + (y2 - y1) * r;
        return calPointToPointDistance(x0, y0, px, py);
    }

    /**
     * 计算两点间距离
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @return
     */
    public static double calPointToPointDistance(double x0, double y0,
                                                double x1,double y1) {
        double d = Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        logger.info("####################" +
                "计算点(" + x0 + "," + y0 + ")到点(" + x1 + "," + y1 + ")的距离为" + d );
        return d;
    }

    /**
     * 查询所有与站点坐标相同的点，且名称中含path的点(因为这是我们设计的)
     * @param sceneName
     * @param mapName
     * @param x
     * @param y
     * @param th
     * @return
     */
    public static MapPoint findPathPointByXYTH(String sceneName, String mapName,
                                               double x, double y, double th , MapPointType mapPointType, PointService pointService) {
        List<MapPoint> startPointList = pointService.listBySceneMapXYTH(
                sceneName,mapName,x,y,th,mapPointType);
        if(startPointList == null || startPointList.size() == 0) {
            return null;
        }
        for(MapPoint tempPoint : startPointList) {
            if(tempPoint.getPointAlias().indexOf(Constant.PATH) > -1) {
                return tempPoint;
            }
        }
        return null;
    }

    /**
     * 根据场景名、固定路径对象查询或者保存MapInfo——名称根据路径的起点和终点取
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @param storeId
     * @return 导航目标点
     */
    public static MapInfo findOrSaveMapInfoByPath(String sceneName, PathDTO pathDTO, boolean start, MapInfoService infoService, Long storeId) {
        String mapName = start?pathDTO.getStartMap() : pathDTO.getEndMap();
        return findOrSaveMapInfoBySceneMapName(sceneName,mapName,storeId, infoService);
    }

    /**
     * 根据场景名、地图名查询或者保存MapInfo——名称根据路径的起点和终点取
     *
     * @param sceneName 场景名
     * @param mapName   地图名
     * @return 地图信息
     */
    public static MapInfo findOrSaveMapInfoBySceneMapName(String sceneName, String mapName, Long storeId, MapInfoService infoService) {
        List<MapInfo> infoList = infoService.listByName(sceneName, mapName, storeId);
        if (null != infoList && infoList.size() > 0) {
            return infoList.get(0);
        }
        //封装MapInfo对象，保存数据库
        MapInfo mapInfo = new MapInfo();
        mapInfo.setStoreId(storeId);
        mapInfo.setSceneName(sceneName);
        mapInfo.setMapName(mapName);
        mapInfo.setCreateTime(new Date());
        infoService.save(mapInfo);
        return mapInfo;
    }

    /**
     * 查询或者保存点_路径交叉点不重复
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @param storeId
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPathNoDuplicate(String sceneName, PathDTO pathDTO, boolean start, PointService pointService, Long storeId) {
        return findOrSaveMapPointByPath(sceneName, pathDTO, start, pointService, false, storeId);
    }

    /**
     * 查询或者保存点——点名根据路径取，用于路径点交叉重复存储点方式
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @param storeId
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPathDuplicate(String sceneName, PathDTO pathDTO, boolean start, PointService pointService, Long storeId) {
        return findOrSaveMapPointByPath(sceneName, pathDTO, start, pointService, true, storeId);
    }

    /**
     * 查询或者保存点_路径交叉点重复或者不重复命名规则不同
     *
     * @param sceneName 场景名
     * @param pathDTO   固定路径对象
     * @param start     是否为固定路径开始点   true:开始点 false:结束点
     * @param isDuplicate   根据路径交叉点是否需要重复建点，采用不同的命名规则
     * @param storeId
     * @return 导航目标点
     */
    public static MapPoint findOrSaveMapPointByPath(String sceneName,
                                                    PathDTO pathDTO,
                                                    boolean start,
                                                    PointService pointService,
                                                    boolean isDuplicate,
                                                    Long storeId) {
        String pointNameResult = "";
        String pointAliasResult = "";
        String mapName = pathDTO.getStartMap();
        String pointName = "";
        //根据路径交叉点是否需要重复建点，采用不同的命名规则
        if(isDuplicate) {
            pointName = Constant.PATH + pathDTO.getId() + ( start ? "start" : "end" );
            String pointNamePre = start ? pathDTO.getStartId() : pathDTO.getEndId();
            pointNameResult = pointNamePre + "_" + pointName;
            pointAliasResult = pointName;
        }
        else {
            pointName = start ? pathDTO.getStartId() : pathDTO.getEndId();
            pointNameResult = pointName;
            pointAliasResult = pointName + "_" + Constant.PATH + pathDTO.getId() + ( start ? "start" : "end" );
        }


        //只查找未配置的云端类型点，其他用于特殊任务的复制点，不在查找之列
        List<MapPoint> pointList = pointService.findByNameCloudType(pointName, sceneName, mapName,
                storeId, MapPointType.UNDEFINED);

        if (null != pointList && pointList.size() > 0) {
            MapPoint mapPointDB = pointList.get(0);
            logger.info("!!!!!!!FixPathServiceImpl --> findOrSaveMapPointByPath:"+ pointName + ",sceneName:" + sceneName + "/" + mapName +" found = " + pointList.size());
            //对于旧的未定义类型点，别名如果没含路径点path关键字，则修改别名
            if(!mapPointDB.getPointAlias().contains(Constant.PATH)) {
                mapPointDB.setPointName(pointNameResult);
                mapPointDB.setPointAlias(pointAliasResult);
                pointService.update(mapPointDB);
            }
            return mapPointDB;
        }
        logger.info("!!!!!!!!FixPathServiceImpl --> findOrSaveMapPointByPath:"+ pointName + ",sceneName:" + sceneName + "/" + mapName + " not found ");
        //封装mapPoint对象，保存数据库
        MapPoint mapPoint = new MapPoint();
        mapPoint.setStoreId(storeId);
        mapPoint.setSceneName(sceneName);
        mapPoint.setMapName(mapName);
        mapPoint.setCreateTime(new Date());
        mapPoint.setPointName(pointNameResult);
        mapPoint.setPointAlias(pointAliasResult);
        mapPoint.setX(start ? pathDTO.getStartX() : pathDTO.getEndX());
        mapPoint.setY(start ? pathDTO.getStartY() : pathDTO.getEndY());
        mapPoint.setTh(start ? pathDTO.getStartTh() : pathDTO.getEndTh());
        pointService.save(mapPoint);
        return mapPoint;
    }

    /**
     * 根据四元数换算欧拉角
     * w^2+x^2+y^2+z^2=1,w=cos(α/2)
     * α=2*arccos(w)
     * @param orientation
     * @return α
     */
    public static double calThByOrientation(Orientation orientation) {
        return 2*Math.acos(orientation.getW());
    }

    /**
     * 根据小数位数四舍五入double数
     * @param x
     * @param scale
     * @return
     */
    public static double roundDoubleByScale(double x, int scale) {
        BigDecimal b =  new  BigDecimal(x);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 根据小数位数截取double数，注意不是四舍五入
     * @param x
     * @param scale
     * @return
     */
    public static double floorDoubleByScale(double x, int scale) {
        BigDecimal b =  new  BigDecimal(x);
        return b.setScale(scale, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * 清空某场景、某门店下的路径相关的缓存
     * @param storeId
     * @param sceneName
     */
    public static void clearPathCache(Long storeId,String sceneName) {
        //清空路径图、云端/工控路径、点的缓存
        CacheInfoManager.removeRoadPathMapsCache(storeId, sceneName);
        CacheInfoManager.removeRoadPathDetailsCache(storeId, sceneName, Constant.PATH_TYPE_X86);
        CacheInfoManager.removeRoadPathDetailsCache(storeId, sceneName, Constant.PATH_TYPE_CLOUD);
        CacheInfoManager.removeMapPointsCache(storeId, sceneName);
    }

    /**
     * 根据机器人坐标点，路径列表，计算离机器人最近的路径起点
     * 权值计算根据类型取路径起点，还是取投影点
     * @param startPointType
     * @param roadPathDetails
     * @return
     */
    public static RoadPathResult calNearestPathPointByRoadPathDetails(
            RoadPathMaps roadPathMaps,
            int startPointType,
            List<RoadPathDetail> roadPathDetails,
            MapPoint robotPosition,
            MapPoint targetPosition,
            RoadPathResultService roadPathResultService) throws Exception{
        RoadPathResult roadPathResult = new RoadPathResult();
        //默认权值设置到最大值
        roadPathResult.setTotalWeight(Long.MAX_VALUE);

        for(RoadPathDetail roadPathDetail : roadPathDetails) {
            //判断是同场景和同地图的路径才计算
            if(!roadPathDetail.getSceneName().equals(robotPosition.getSceneName())
                    || !roadPathDetail.getMapName().equals(robotPosition.getMapName())) {
                continue;
            }

            Long distance = calDistanceByRoadPathDetail(roadPathDetail, robotPosition);
            //计算机器人坐标离的路径距离在1.5米内的路径,单位mm
            if(distance > Constant.PATH_NAVIGATION_SCALE) {
                continue;
            }
            RoadPathResult resultTemp = new RoadPathResult();
            logger.info("找到离机器人坐标1.5米内的路径：" + roadPathDetail.getPathId() + ","
                    + roadPathDetail.getMapName() + "," + roadPathDetail.getSceneName());
            resultTemp = roadPathResultService.getShortestCloudRoadPathForMission(roadPathDetail.getStartPoint(),
                    targetPosition.getId(), roadPathMaps, resultTemp);

            //根据startPointType来对路径权值做补偿。
            resultTemp = compensateRoadPathResultByStartPointType(roadPathDetail, robotPosition, resultTemp, startPointType);

            //取到目的地最优路径的结果集作为输出
            if(resultTemp != null && resultTemp.getTotalWeight() != null
                    && resultTemp.getTotalWeight() < roadPathResult.getTotalWeight()) {
                roadPathResult = resultTemp;
                //结果集的初始点设置成最优路径的起点
                roadPathResult.setStartPoint(roadPathDetail.getStart());
            }
        }
        //设置
        roadPathResult.setEndPoint(targetPosition);
        logger.info("找到的最短路径为:" + roadPathResult.getPointIds() + "总权值:" + roadPathResult.getTotalWeight());
        return roadPathResult;
    }

    /**
     * 根据startPointType来对路径权值做补偿
     * @param roadPathDetail
     * @param robotPosition
     * @param resultTemp
     * @param startPointType
     * @return
     */
    private static RoadPathResult compensateRoadPathResultByStartPointType(RoadPathDetail roadPathDetail, MapPoint robotPosition, RoadPathResult resultTemp, int startPointType) {
        //如果是以路径起点为权值计算起点，则不需要补偿
        if(startPointType == Constant.CAL_ROAD_PATH_START_PATH) {
            return resultTemp;
        }
        //如果是根据路径的投影点来计算起点，则需要补偿
        else if(startPointType == Constant.CAL_ROAD_PATH_START_SHADOW) {
            Long weight = resultTemp.getTotalWeight();
            MapPoint start = roadPathDetail.getStart();
            MapPoint end = roadPathDetail.getEnd();
            double x0 = robotPosition.getX();
            double y0 = robotPosition.getY();
            double x1 = start.getX();
            double y1 = start.getY();
            double x2 = end.getX();
            double y2 = end.getY();
            TriangleResult triangleResult = calTriangleResult(x0 , y0, x1, y1, x2, y2);

            double r = triangleResult.getR();
            //当r<=0,P的投影C在线段AB的A端，补偿路径为PA的长度
            if(r <= 0) {
                //转化成mm且四舍五入取整作为权值
                Long PA = doubleToLongRoundHalfUp(triangleResult.getAbsAP() * 1000);
                resultTemp.setTotalWeight(weight + PA);
            }
            //当r>=1,P的投影C在线段AB的B端，补偿路径为BP的长度-AB的长度
            else if(r >= 1) {
                Long AB = doubleToLongRoundHalfUp(triangleResult.getAbsAB() * 1000);
                Long BP = doubleToLongRoundHalfUp(triangleResult.getAbsBP() * 1000);
                resultTemp.setTotalWeight(weight + BP - AB);
            }
            //其他，P的投影C在线段AB中间，补偿路径为PC的长度-AC的长度
            else {
                Long SHADOW_AC = doubleToLongRoundHalfUp(triangleResult.getShadowAC() * 1000);
                Long PC = doubleToLongRoundHalfUp(triangleResult.getAbsPC() * 1000);
                resultTemp.setTotalWeight(weight + PC - Math.abs(SHADOW_AC));
            }
        }
        return resultTemp;
    }

    /**
     * 把double转换成Long,四舍五入
     * @param x
     * @return
     */
    public static Long doubleToLongRoundHalfUp(double x) {
        return new BigDecimal(x + "").setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
    }

    /**
     * 计算三角形关系
     * 顶点P(x0,y0)，A(x1,y1),B(x2,y2)
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private static TriangleResult calTriangleResult(double x0, double y0, double x1, double y1, double x2, double y2) {
        TriangleResult triangleResult = new TriangleResult();
        double absAB = calPointToPointDistance(x1, y1, x2, y2);
        triangleResult.setAbsAB(absAB);
        triangleResult.setAbsAP(calPointToPointDistance(x1, y1, x0, y0));
        triangleResult.setAbsBP(calPointToPointDistance(x2, y2, x0, y0));
        triangleResult.setAbsPC(calPointToLineDistance(x0, y0, x1, y1, x2, y2));
        //计算r的分子
        double cross = (x2 - x1) * (x0 - x1) + (y2 - y1) * (y0 - y1);
        //计算r的分母，AB向量模的平方
        double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        //其他情况，则返回CP向量的膜
        double r = cross / d2;
        triangleResult.setR(r);
        triangleResult.setShadowAC(r * absAB);
        return triangleResult;
    }

    /**
     * 把路径搜索的点序列结果对象封装成路径详细对象
     * @param result
     * @param pointService
     * @return
     * @throws Exception
     */
    public static RoadPathDetail calRoadPathDetailByRoadPathResult(RoadPathResult result, PointService pointService) throws Exception{
        List<Long> pointIds = result.getPointIds();
        RoadPathDetail roadPathDetail = new RoadPathDetail();
        MapPoint startPoint = result.getStartPoint();
        MapPoint endPoint = result.getEndPoint();
        if(startPoint == null || startPoint.getId() == null
                || endPoint == null || endPoint.getId() == null) {
            logger.info("输入RoadPathResult对象的开始点或结束点参数为空。");
            return null;
        }
        startPoint = pointService.findById(startPoint.getId());
        endPoint = pointService.findById(endPoint.getId());
        if(startPoint == null) {
            logger.info("输入RoadPathResult对象的开始点ID:" + startPoint.getId() + "不存在。");
            return null;
        }
        if(endPoint == null) {
            logger.info("输入RoadPathResult对象的结束点ID:" + endPoint.getId() + "不存在。");
            return null;
        }

        roadPathDetail.setStart(startPoint);
        roadPathDetail.setEnd(endPoint);
        roadPathDetail.setPathName("FROM_" + startPoint.getPointAlias() + "_" + startPoint.getMapName()
                + "_to_" + endPoint.getPointAlias() + endPoint.getMapName() + endPoint.getSceneName());

        //不包含点序列说明此路不通
        if(pointIds != null && pointIds.size() > 0) {
            List<MapPoint> points = new ArrayList<MapPoint>();
            for(Long pointId : pointIds) {
                MapPoint point = pointService.findById(pointId);
                if(point != null) {
                    points.add(point);
                }
            }
            roadPathDetail.setRelatePoints(points);
        }

        roadPathDetail.setWeight(result.getTotalWeight());
        return roadPathDetail;
    }

    /**
     * 获取从开始点到结束点的路径详细
     * 理论上最短路径算法算出来只有一条，但是数据库缓存可能因为人为因素残留多条结果，所以返回列表进行规避
     * 如果数据库存在，则从数据库获取，如果数据库不存在，则实时计算
     * @param start
     * @param end
     * @param sceneName
     * @param storeId
     * @param roadPathService
     * @param roadPathResultService
     * @param pointService
     * @return
     */
    public static List<RoadPathDetail> getRoadPathDetailsByStartEnd(MapPoint start,
                                                                    MapPoint end,
                                                                    String sceneName,
                                                                    Long storeId,
                                                                    RoadPathService roadPathService,
                                                                    RoadPathResultService roadPathResultService,
                                                                    PointService pointService)  throws Exception{
//            List<RoadPathDetail> roadPathDetails =
//                    roadPathService.findRoadPathByStartAndEndPoint(
//                            prePoint.getId(),
//                            mp.getId(),
//                            mp.getSceneName(),
//                            null
//                    );

        List<RoadPathDetail> roadPathDetails =
                roadPathService.listRoadPathDetailByStartAndEndPointType(
                        start.getId(),
                        end.getId(),
                        sceneName,
                        null,
                        Constant.PATH_TYPE_CLOUD
                );
        //当从数据库缓存读取路径为空时，则尝试实时计算两点间的路径详细
        RoadPathMaps roadPathMaps = CacheInfoManager.getRoadPathMapsCache(storeId, sceneName, roadPathService);
        boolean roadPathDetailsIsNullOrEmpty = roadPathDetails == null || roadPathDetails.size() == 0;
        if(roadPathDetailsIsNullOrEmpty && roadPathMaps != null) {
            String startAlias = start.getPointAlias();
            String endAlias = end.getPointAlias();
            logger.info("从数据库查询从点ID:" + startAlias + "到点ID:" + endAlias + "的路径规划缓存为空，改用算法实时查询。");
            RoadPathResult result = new RoadPathResult();
            //根据end点查找对应的路径点
            //查询所有与站点坐标相同的点，且名称中含path的点(因为这是我们设计的)
            MapPoint startPathPoint = start;
            if(startAlias.indexOf(Constant.PATH) <= -1) {
                startPathPoint = PathUtil.findPathPointByXYTH(sceneName, start.getMapName(),
                        start.getX(), start.getY(), start.getTh(), null, pointService);
            }
            MapPoint endPathPoint = end;
            if(endAlias.indexOf(Constant.PATH) <= -1) {
                endPathPoint = PathUtil.findPathPointByXYTH(sceneName, end.getMapName(),
                        end.getX(), end.getY(), end.getTh(), null, pointService);
            }
            result = roadPathResultService.getShortestCloudRoadPathForMission(startPathPoint, endPathPoint, roadPathMaps, result);
            RoadPathDetail roadPathDetail = PathUtil.calRoadPathDetailByRoadPathResult(result, pointService);
            if(roadPathDetail != null) {
                //我们虽然结果只有一个，也要封装成统一的列表返回形式。
                roadPathDetails = new ArrayList<RoadPathDetail>();
                roadPathDetails.add(roadPathDetail);
            }
        }
        return roadPathDetails;
    }

    /**
     * 从缓存取匹配的工控路径，如果不存在则从数据库查询
     * 理论上最短路径算法算出来只有一条，但是数据库缓存可能因为人为因素残留多条结果，所以返回列表进行规避
     * @param startId
     * @param endId
     * @param pathType
     * @param mapName
     * @param sceneName
     * @param roadPathService
     * @return
     * @throws Exception
     */
    public static List<RoadPath> getRoadPathByStartEndType(Long startId,
                                                           Long endId,
                                                           Integer pathType,
                                                           String mapName,
                                                           String sceneName,
                                                           Long storeId,
                                                           RoadPathService roadPathService) throws Exception{
        List<RoadPath> roadPaths = new ArrayList<RoadPath>();
        List<RoadPathDetail>  roadPathDetails = CacheInfoManager.getRoadPathDetailsCache(storeId, sceneName, pathType, roadPathService);
        if(roadPathDetails != null && roadPathDetails.size() > 0) {
            //如果缓存存在，则取匹配的一条作为结果返回
            for(RoadPathDetail roadPathDetail : roadPathDetails) {
                //返回找到的第一条匹配的工控路径
                if(roadPathDetail.getStartPoint().equals(startId)
                        && roadPathDetail.getEndPoint().equals(endId)
                        && roadPathDetail.getMapName().equals(mapName)
                        && roadPathDetail.getSceneName().equals(sceneName)) {
                    logger.info("已从缓存找到从点ID:" + startId + "到点ID:" + endId + "的类型为" + pathType + "的路径。");
                    roadPaths.add(roadPathDetail);
                    break;
                }
            }
        }
        else {
            logger.info("未从缓存找到从点ID:" + startId + "到点ID:" + endId + "的类型为" + pathType + "的路径。从数据库查询。");

            roadPaths = roadPathService.listRoadPathByStartAndEndPoint(
                            startId,
                            endId,
                            sceneName,
                            mapName,
                            pathType
                    );
        }
        return roadPaths;
    }

    /**
     * 对机器人路径查询结果从短到长进行排序
     * @param robotRoadPathResultList
     * @return
     * @throws Exception
     */
    public static List<RobotRoadPathResult> sortByRobotRoadPathResultList(List<RobotRoadPathResult> robotRoadPathResultList) throws Exception{
        //如果只有一个元素，则不需要排序，直接返回
        if(robotRoadPathResultList == null || robotRoadPathResultList.size() <= 1) {
            return robotRoadPathResultList;
        }
        //使用快速排序
        /*quickSortRoadPathResultMapList(robotRoadPathResultList,
                0 ,
                robotRoadPathResultList.size() -1);*/

        //使用JAVA自带的归并排序
        Collections.sort(robotRoadPathResultList);

        return robotRoadPathResultList;
    }

    /**
     * 快速排序
     * 基本思想：选择一个基准元素,通常选择第一个元素或者最后一个元素,通过一趟扫描，将待排序列分成两部分,一部分比基准元素小,
     * 一部分大于等于基准元素,此时基准元素在其排好序后的正确位置,然后再用同样的方法递归地排序划分的两部分。
     * @param roadPathResultMapList
     * @param low
     * @param high
     * @return
     * @throws Exception
     */
    private static void quickSortRoadPathResultMapList(List<RobotRoadPathResult> roadPathResultMapList,
                                                                                  int low,
                                                                                  int high) throws Exception{
        if (low < high) {
            //将list数组进行一分为二
            int middle = getMiddle(roadPathResultMapList, low, high);
            //对低字表进行递归排序
            quickSortRoadPathResultMapList(roadPathResultMapList, low, middle - 1);
            //对高字表进行递归排序
            quickSortRoadPathResultMapList(roadPathResultMapList, middle + 1, high);
        }
    }

    /**
     * 将list数组根据选定的中轴进行一分为二
     * @param roadPathResultMapList
     * @param low
     * @param high
     * @return
     * @throws Exception
     */
    private static int getMiddle(List<RobotRoadPathResult> roadPathResultMapList, int low, int high) throws Exception{
        //数组的第一个作为中轴
        RobotRoadPathResult temp = roadPathResultMapList.get(low);
        while (low < high) {
            while (low < high
                    && roadPathResultMapList.get(high).getRoadPathResult().getTotalWeight()
                    >= temp.getRoadPathResult().getTotalWeight()) {
                high--;
            }

            //比中轴小的记录移到低端
            roadPathResultMapList.set(low, roadPathResultMapList.get(high));
            while (low < high
                    && roadPathResultMapList.get(low).getRoadPathResult().getTotalWeight()
                    <= temp.getRoadPathResult().getTotalWeight()) {
                low++;
            }

            //比中轴大的记录移到高端
            roadPathResultMapList.set(high, roadPathResultMapList.get(low));
        }
        //中轴记录到尾
        roadPathResultMapList.set(low, temp);
        //返回中轴的位置
        return low;
    }

    /**
     * 测试排序
     * @param args
     */
    /*public static void main(String[] args) {
        List<RobotRoadPathResult> robotRoadPathResultList = new ArrayList<RobotRoadPathResult>();
        for(int i = 15;i > 0;i--) {
            Robot robot = new Robot();
            robot.setName("testName" + i);
            RoadPathResult roadPathResult = new RoadPathResult();
            roadPathResult.setTotalWeight(new Random().nextInt(3) + 0L);
            robotRoadPathResultList.add(new RobotRoadPathResult(robot, roadPathResult));
        }

        System.out.println("排序前");
        displayRoadPathResultList(robotRoadPathResultList);
        try {
            sortByRobotRoadPathResultList(robotRoadPathResultList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("排序后");
        displayRoadPathResultList(robotRoadPathResultList);

    }*/

    /**
     * 显示list
     * @param robotRoadPathResultList
     */
    public static void displayRoadPathResultList(List<RobotRoadPathResult> robotRoadPathResultList) {
        for(RobotRoadPathResult robotRoadPathResult : robotRoadPathResultList) {
            System.out.println("robotName:" + robotRoadPathResult.getRobot().getName()
                    + ",weight:" + robotRoadPathResult.getRoadPathResult().getTotalWeight());
        }
    }

    /**
     * 根据订单设置，有没有装货站来判断选哪个点作为下单的第一个目的地点
     * 如果订单设置的装货点不为空，则取装货点作为目的地点
     * 如果订单设置装货点不存在，则取目的地列表的第一个
     * 如果订单设置的装货点不存在，且目的地列表不存在，则取卸货点
     * @param order
     * @param pointService
     * @return
     */
    public static MapPoint getFirstPathStationPointByOrder(Order order, PointService pointService) {
        /**根据订单设置，有没有装货站来判断选哪个点作为下单的第一个目的地点**/
        //如果订单设置为空，直接返回
        if(order.getOrderSetting() == null) {
            return null;
        }
        //如果订单设置的装货点不为空，则取装货点作为目的地点
        //条件：装货点ID存在
        boolean orderSettingStartStationIdExist = order.getOrderSetting().getStartStation() != null
                && order.getOrderSetting().getStartStation().getId() != null;
        //条件：订单详细列表不为空且至少包含一条数据
        boolean orderDetailNotEmpty = order.getDetailList() != null && order.getDetailList().size() > 0;
        boolean orderSettingEndStationIdExist = order.getOrderSetting().getEndStation() != null
                && order.getOrderSetting().getEndStation().getId() != null;
        Long firstStationId = null;
        MapPoint pathStationPoint = null;
        if( orderSettingStartStationIdExist ) {
            firstStationId = order.getOrderSetting().getStartStation().getId();
            if(firstStationId == null) {
                return null;
            }
            pathStationPoint = pointService.findPathMapPointByStationIdAndCloudType(firstStationId, MapPointType.LOAD.getCaption());
        }
        //如果订单设置装货点不存在，则取目的地列表的第一个
        else if(!orderSettingStartStationIdExist && orderDetailNotEmpty){
            firstStationId = order.getDetailList().get(0).getStationId();
            if(firstStationId == null) {
                return null;
            }
            pathStationPoint = pointService.findPathMapPointByStationIdAndCloudType(firstStationId, MapPointType.UNLOAD.getCaption());
        }
        //如果订单设置的装货点不存在，且目的地列表不存在，则取卸货点
        else if(!orderSettingStartStationIdExist && !orderDetailNotEmpty && orderSettingEndStationIdExist) {
            firstStationId = order.getOrderSetting().getEndStation().getId();
            if(firstStationId == null) {
                return null;
            }
            pathStationPoint = pointService.findPathMapPointByStationIdAndCloudType(firstStationId, MapPointType.FINAL_UNLOAD.getCaption());
        }
        logger.info("#####################找到的第一个可用的目标点：" + pathStationPoint == null ? "未找到"
                : pathStationPoint.getPointAlias() + "," + pathStationPoint.getMapName() + "," + pathStationPoint.getSceneName());
        return pathStationPoint;
    }
}
