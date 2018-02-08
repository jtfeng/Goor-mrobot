package cn.muye.dijkstra.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.base.PubX86HeartBeat;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.point.service.PointService;
import cn.muye.area.point.service.impl.PointServiceImpl;
import cn.muye.area.station.service.StationRobotXREFService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.dijkstra.service.RoadPathResultService;
import cn.muye.service.consumer.topic.BaseMessageService;
import cn.muye.service.missiontask.MissionFuncsService;
import cn.muye.util.PathUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class DijkstraController {

    private static final Logger log = LoggerFactory.getLogger(DijkstraController.class);


    @Autowired
    private RoadPathService roadPathService;
    @Value("${goor.push.dirs}")
    private String DOWNLOAD_HOME;
    @Autowired
    private PointService pointService;
    @Autowired
    ElevatorService elevatorService;
    @Autowired
    private StationService stationService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private RoadPathResultService roadPathResultService;
    /**
     * 读取工控路径文件并入库，相同坐标点重复加
     * @return
     */
    @RequestMapping(value = "/services/roadPath/readFile", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addRoadPathByFile(@RequestParam(value = "resFile") MultipartFile file){
        try {
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + "roadPath" + File.separator + DateTimeUtils.getShortDateTime());

            if (!file.isEmpty()) {
                final File dest1 = transferToFile(file,dest);

                //异步插入数据库
                //用线程池代替原来的new Thread方法
                new Thread(() -> {
                    try {
                        String messageData = FileUtils.readTXT(dest1.getPath());

                        if (StringUtil.isNullOrEmpty(messageData)) {
                            return;
                        }

                        JSONObject jsonObject = JSON.parseObject(messageData);
                        String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
                        String paths = jsonObject.getString(TopicConstants.PATHS);
                        List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);
                        roadPathService.saveOrUpdateRoadPathByPathDTOListDuplicatePoint(pathDTOList,sceneName);

                        //清空某场景、某门店下的路径相关的缓存
                        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }).start();
            }
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 读取工控路径文件并入库，相同坐标点不重复加
     * @return
     */
    @RequestMapping(value = "/services/roadPath/readFileNoDuplicate", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addRoadPathByFileNoDuplicate(@RequestParam(value = "resFile") MultipartFile file){
        try {
            File dest = FileUtils.getFile(DOWNLOAD_HOME + File.separator + "roadPath" + File.separator + DateTimeUtils.getShortDateTime());

            if (!file.isEmpty()) {
                final File dest1 = transferToFile(file,dest);

                //异步插入数据库
                //用线程池代替原来的new Thread方法
                new Thread(() -> {
                    try {
                        String messageData = FileUtils.readTXT(dest1.getPath());

                        if (StringUtil.isNullOrEmpty(messageData)) {
                            return;
                        }

                        JSONObject jsonObject = JSON.parseObject(messageData);
                        String sceneName = jsonObject.getString(TopicConstants.SCENE_NAME);
                        String paths = jsonObject.getString(TopicConstants.PATHS);
                        List<PathDTO> pathDTOList = JSONArray.parseArray(paths, PathDTO.class);
                        roadPathService.saveOrUpdateRoadPathByPathDTOListNoDuplicatePoint(pathDTOList,sceneName);

                        //清空某场景、某门店下的路径相关的缓存
                        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }).start();
            }
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 把上传的文件保存到本地并返回保存的文件
     * @param file
     * @param dest
     * @return
     * @throws Exception
     */
    private File transferToFile(MultipartFile file , File dest) throws Exception{
        dest.mkdirs();
        String fileName = file.getOriginalFilename();
        dest = FileUtils.getFile(dest.getPath() + File.separator + fileName);

        if (!dest.exists()) {
            dest.createNewFile();
        }
        file.transferTo(dest);
        return dest;
    }



    /**
     * 生成某一场景下的所有电梯等待点到出电梯点的虚拟云端路径
     * @param sceneId
     * @return
     */
    @RequestMapping(value = "/services/roadPath/generateElevatorPath", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult generateElevatorPath(Long sceneId) {
        try {
            String placeholder = " | ----- | ";
            Scene scene = sceneService.getSceneById(sceneId);
            log.info(placeholder + "scene、" + scene.toString());
            String sceneName = scene.getMapSceneName();
            log.info(placeholder + "sceneName、" + sceneName);
            if(StringUtil.isNullOrEmpty(sceneName)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,sceneName + "云端场景未绑定有效的工控场景");
            }
            List<Elevator> elevatorList = elevatorService.listBySceneName(sceneName);
            log.info(placeholder + "elevatorList、" + elevatorList);
            if(elevatorList == null || elevatorList.size() == 0) {
                return AjaxResult.success(sceneName + "云端场景未设置电梯");
            }

            /*
            * 插入电梯点之间的路径
            * **/
            AjaxResult ajaxResult = elevatorService.generateFakePathByElevatorList(elevatorList);
            if(!ajaxResult.isSuccess()) {
                return ajaxResult;
            }

            //清空某场景、某门店下的路径相关的缓存
            PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

            return AjaxResult.success("执行完毕，若同一部电梯关联的四点对象少于两个，则不会生成");
        } catch (Exception e) {
            log.error("出错:{}", e);
            log.error("出错:{}",e.getMessage());
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }
    }



    /**
     * 计算某场景下所有站点间的云端路径并存储
     * 此算法只适用于用上面方法生成的路径点不重复的情况
     * @param pathType 要查找的路径类型，一般取1，表示查找工控路径
     * @param sceneId  要生成路径的的云端场景ID
     * @return
     */
    @RequestMapping(value = "/services/roadPath/generate")
    @ResponseBody
    public AjaxResult generateRoadPath(@RequestParam(name = "pathType",required = false) Integer pathType, Long sceneId){
        RoadPathResult result = null;
        try {
            Scene scene = sceneService.getSceneById(sceneId);
            String sceneName = scene.getMapSceneName();
            if(StringUtil.isNullOrEmpty(sceneName)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,sceneId + "云端场景未绑定有效的工控场景");
            }

            //创建电梯间路径
            /*AjaxResult elevatorAjaxResult = generateElevatorPath(sceneId);
            if(!elevatorAjaxResult.isSuccess()) {
                return elevatorAjaxResult;
            }*/

            //路径列表缓存机制，这样在动态调度里面可以从缓存读出图
            RoadPathMaps roadPathMaps = CacheInfoManager.getRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName, roadPathService);

            if(roadPathMaps == null) {
                return AjaxResult.failed("未找到该云端场景下的工控路径");
            }

            //TODO test
//            result = roadPathMaps.getShortestPath(3389L,3410L);
//            return AjaxResult.success(result);

            List<Station> stationList = stationService.listStationsBySceneAndMapPointType(sceneId,null);

            if(stationList == null) {
                return AjaxResult.failed("未找到该云端场景下的站点");
            }
            for(Station startStation : stationList) {
                List<MapPoint> mapPoints = startStation.getMapPoints();
                //站没关联点的不用管
                if(mapPoints == null || mapPoints.size() == 0) {
                    continue;
                }
                for(MapPoint startStationPoint : mapPoints) {
                    //查询所有与站点坐标相同的点，且名称中含path的点(因为这是我们设计的)
                    MapPoint startPoint = PathUtil.findPathPointByXYTH(sceneName,startStationPoint.getMapName(),
                            startStationPoint.getX(),startStationPoint.getY(),startStationPoint.getTh(),null, pointService);
                    //没找到与站点相同的可用路径点，则跳过
                    if(startPoint == null) {
                        continue;
                    }

                    //遍历从初始站到结束站的所有路径
                    for(Station endStation : stationList) {
                        //站没关联点的不用管
                        List<MapPoint> mapPoints1 = endStation.getMapPoints();
                        if(mapPoints1 == null || mapPoints1.size() == 0) {
                            continue;
                        }

                        for(MapPoint endStationPoint : mapPoints1) {
                            //查询所有与站点坐标相同的点，且名称中含path的点(因为这是我们设计的)
                            MapPoint endPoint = PathUtil.findPathPointByXYTH(sceneName,endStationPoint.getMapName(),
                                    endStationPoint.getX(),endStationPoint.getY(),endStationPoint.getTh(),null, pointService);
                            //没找到与站点相同的可用路径点，则跳过
                            if(endPoint == null) {
                                continue;
                            }

                            //相同的站点（场景、地图、坐标、朝向）不用设计路径
                            if(endPoint.getMapName().equals(startPoint.getMapName())
                                    && endPoint.getSceneName().equals(startPoint.getSceneName())
                                    && endPoint.getX() == startPoint.getX()
                                    && endPoint.getY() == startPoint.getY()
                                    && endPoint.getTh() == startPoint.getTh()) {
                                continue;
                            }

                            result = roadPathResultService.getShortestCloudRoadPathForMission(startPoint, endPoint,roadPathMaps,result);

                            //未找到路径,或者只找到一个点(工控路径至少两个点)则继续
                            if(result == null || result.getPointIds() == null || result.getPointIds().size() <= 1) {
                                continue;
                            }

                            //TODO test=================================================================
//                            if(1==1) {
//                                return AjaxResult.failed(result);
//                            }

                            //没有则新建
                            RoadPath roadPath = new RoadPath();
                            roadPath.setWeight(result.getTotalWeight());
                            roadPath.setMapName(startPoint.getMapName());
                            roadPath.setSceneName(sceneName);
                            roadPath.setData("");
                            roadPath.setEndPoint(endStationPoint.getId());
                            roadPath.setStartPoint(startStationPoint.getId());
                            roadPath.setPattern("");
                            roadPath.setPathName(
                                    startStation.getName() + "_" + startStationPoint.getPointAlias()
                                    + "_to_"
                                    + endStation.getName() + "_" + endStationPoint.getPointAlias() + "_auto");
                            roadPath.setCreateTime(new Date());
                            roadPath.setPathType(Constant.PATH_TYPE_CLOUD);
                            roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);

                            //TODO test=================================
//                            if(1==1) {
//                                continue;
//                            }
                            roadPathService.createOrUpdateRoadPathByStartAndEndPoint(startStationPoint.getId(),
                                    endStationPoint.getId(),sceneName,null,Constant.PATH_TYPE_CLOUD,
                                    roadPath,result.getPointIds());
                        }
                    }
                }

            }
            return AjaxResult.success("操作成功");

        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }


    /**
     * 测试查找站相同的路径点
     * @return
     */
    @RequestMapping(value = "/services/roadPath/testGetStationPoint")
    @ResponseBody
    public AjaxResult testGetStationPoint(Long id) {
        MapPoint endStationPoint = pointService.findById(id);
        MapPoint endPoint = PathUtil.findPathPointByXYTH(endStationPoint.getSceneName(),endStationPoint.getMapName(),
                endStationPoint.getX(),endStationPoint.getY(),endStationPoint.getTh(),null, pointService);
        return AjaxResult.success(endPoint);
    }

    /**
     * 测试查找站相同的路径点
     * @return
     */
    @RequestMapping(value = "/services/roadPath/testFindPath")
    @ResponseBody
    public AjaxResult testFindPath(Long startPointId,Long endPointId) {
        try {
            MapPoint startPoint = pointService.findById(startPointId);
            //路径列表缓存机制，这样在动态调度里面可以从缓存读出图
            RoadPathMaps roadPathMaps = CacheInfoManager.getRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, startPoint.getSceneName(), roadPathService);

            if(roadPathMaps == null) {
                return AjaxResult.failed("未找到该云端场景下的工控路径");
            }
            RoadPathResult result = null;
            result = roadPathResultService.getShortestCloudRoadPathForMission(startPointId, endPointId,roadPathMaps,result);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,e.getMessage());
        }
    }

    /**
     * 更新某场景工控路径权值
     * @return
     */
    @RequestMapping(value = "/services/roadPath/updateX86PathWeight")
    @ResponseBody
    public AjaxResult updateX86PathWeight(Long sceneId) {
        try {
            Scene scene = sceneService.getSceneById(sceneId);
            String sceneName = scene.getMapSceneName();
            if(StringUtil.isNullOrEmpty(sceneName)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,sceneId + "云端场景未绑定有效的工控场景");
            }

            List<RoadPath> x86RoadPaths = roadPathService.listRoadPathsBySceneNamePathType(sceneName,
                    Constant.PATH_TYPE_X86, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if(x86RoadPaths == null || x86RoadPaths.size() <= 0) {
                return AjaxResult.failed("该场景无工控路径");
            }

            for(RoadPath roadPath : x86RoadPaths) {
                if(roadPath == null || roadPath.getStartPoint() == null
                        || roadPath.getEndPoint() == null) {
                    log.error(roadPath.getPathName() + "路径为空或起点、终点ID中有空值，跳过");
                    continue;
                }
                MapPoint startPoint = pointService.findById(roadPath.getStartPoint());
                MapPoint endPoint = pointService.findById(roadPath.getEndPoint());
                if(startPoint == null || endPoint == null) {
                    log.error(roadPath.getPathName() + "路径关联的"+ (startPoint == null ?"起点对象," : ",")
                            +(endPoint == null ? "终点对象" : "") + "不存在");
                    continue;
                }
                //取两点间长度作为路径权值
                roadPath.setWeight(PathUtil.calDistance(startPoint, endPoint));
                roadPathService.updateSelective(roadPath);
            }

            //清除路径缓存
            PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID , sceneName);

            return AjaxResult.success("更新成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getMessage(), e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,e.getMessage());
        }
    }

    /**
     * 测试机器人到待命点
     * @return
     */
    @Autowired
    private RobotService robotService;
    @Autowired
    private MissionFuncsService missionFuncsService;
    @Autowired
    private StationRobotXREFService stationRobotXREFService;

    @RequestMapping(value = "/services/roadPath/testSendRobotToStandByPoint")
    @ResponseBody
    public AjaxResult testSendRobotToStandByPoint(Long robotId,Long sceneId) {
        try {
            Robot robot = robotService.findById(robotId);
            //TODO 第一阶段，先做机器人执行完开机管理后，自动去充电点，但不执行充电任务，
            // 如果机器人没有绑定充电点，则执行去站的装货点。如果都没有，就原地待命。
            List<StationRobotXREF> stationRobotXREFS = stationRobotXREFService.getByRobotId(robotId);
            List<Long> stationIdList = new ArrayList<Long>();
            for (StationRobotXREF st :
                    stationRobotXREFS) {
                stationIdList.add(st.getStationId());
            }
           return missionFuncsService.sendRobotToStandByPoint(robot, stationIdList, sceneId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,e.getMessage());
        }
    }

    private final static int totalTestSend = 10000;
    /**
     * 测试并发向ros发消息，测试消息阻塞
     * @return
     */
    @RequestMapping(value = "services/testSendRos", method= RequestMethod.GET)
    @ResponseBody
    public AjaxResult testSendRos() {
        try {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread1:");
                }
            });

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread2:");
                }
            });

            Thread thread3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread3:");
                }
            });

            Thread thread4 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread4:");
                }
            });

            Thread thread5 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread5:");
                }
            });

            Thread thread6 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread6:");
                }
            });

            Thread thread7 = new Thread(new Runnable() {
                @Override
                public void run() {
                    testSendRunnable("Thread7:");
                }
            });
            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
            thread7.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return AjaxResult.success();
    }

    @Autowired
    BaseMessageService baseMessageService;
    private AjaxResult testSendHeartBeat(String message) {
        SlamResponseBody slamResponseBody = new SlamResponseBody();
        slamResponseBody.setSubName(TopicConstants.X86_ELEVATOR_LOCK);
        slamResponseBody.setData("测试发送消息:" + message);
        return baseMessageService.sendRobotMessage("cookyplus013", TopicConstants.X86_ELEVATOR_LOCK, slamResponseBody);
    }

    private void testSendRunnable(String message) {
        int success = 0;
        long startTime = System.currentTimeMillis();
        for(int i = 0; i< totalTestSend; i++) {
            AjaxResult result = testSendHeartBeat(message + i);
            if(result != null && result.isSuccess()) {
                success ++;
            }
        }
        long endTime = System.currentTimeMillis();
        log.error("{}开始时间:{},结束时间:{},总耗时:{}ms,总次数:{},成功次数:{}",message,new Date(startTime),new Date(endTime),endTime-startTime, totalTestSend ,success);
    }
}