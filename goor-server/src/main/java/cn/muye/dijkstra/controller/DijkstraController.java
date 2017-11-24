package cn.muye.dijkstra.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.dto.area.PathDTO;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.point.service.PointService;
import cn.muye.area.point.service.impl.PointServiceImpl;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.dijkstra.service.RoadPathResultService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private RoadPathResultService dijkstraService;
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

                        //清空路径图的缓存
                        CacheInfoManager.removeRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

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

                        //清空路径图的缓存
                        CacheInfoManager.removeRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

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
            //遍历循环电梯列表，建立同一个电梯的电梯四点对象两两间等待点到出电梯的path点的假工控路径(不同电梯之间是没有路径的)
            for(Elevator elevator:elevatorList) {
                log.info(placeholder + "elevator、" + elevator.toString());
                //该电梯至少得有两个四点对象才能建立电梯楼层间云端路径
                if (elevator == null ||
                        elevator.getElevatorPointCombinations() == null ||
                        elevator.getElevatorPointCombinations().size() <= 1) {
                    continue;
                }
                List<ElevatorPointCombination> elevatorPointCombinations = elevator.getElevatorPointCombinations();
                log.info(placeholder + "elevatorPointCombinations、" + elevatorPointCombinations.toString());
                //遍历同一部电梯的四点对象，生成两两之间的假工控路径
                for(ElevatorPointCombination startCombination : elevatorPointCombinations) {
                    log.info(placeholder + "startCombination、" + startCombination.toString());
                    MapPoint startCombinePoint = startCombination.getwPoint();
                    log.info(placeholder + "startCombinePoint、" + startCombinePoint.toString());
                    if(startCombinePoint == null || startCombinePoint.getId() == null) {
                        return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'等待点为空，生成错误！");
                    }
                    for(ElevatorPointCombination endCombination : elevatorPointCombinations) {
                        log.info(placeholder + "endCombination、" + endCombination.toString());
                        //跳过相同的四点集合
                        if(endCombination.getId().equals(startCombination.getId())) {
                            continue;
                        }
                        MapPoint endCombinePoint = endCombination.getoPoint();
                        if(endCombinePoint == null || endCombinePoint.getId() == null) {
                            return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'出去点为空，生成错误！");
                        }
                        log.info(placeholder + "endCombinePoint、" + endCombinePoint.toString());

                        //电梯云端路径起点是出发楼层工控路径终点
                        MapPoint startPathPoint = PointServiceImpl.findPathPointByXYTH(sceneName,
                                startCombinePoint.getMapName(),startCombinePoint.getX(),startCombinePoint.getY(),startCombinePoint.getTh(),null, pointService);
                        if(startPathPoint == null) {
                            log.info(placeholder + "startPathPoint is null");
                            return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'等待点相关联的path路径点为空，生成错误！");
                        }

                        log.info(placeholder + "startPathPoint、" + startPathPoint.toString());
                        Long startPathPointId = startPathPoint.getId();
                        Long startCombinePointId = startCombinePoint.getId();
                        Long endCombinePointId = endCombinePoint.getId();
                        //没有则新建
                        RoadPath roadPath = new RoadPath();
                        roadPath.setWeight(Constant.DEFAULT_ELEVATOR_X86_WEIGHT);
                        roadPath.setMapName(startCombinePoint.getMapName());
                        roadPath.setSceneName(sceneName);
                        roadPath.setData("");
                        roadPath.setEndPoint(endCombinePointId);
                        roadPath.setStartPoint(startPathPointId);
                        roadPath.setPattern("");
                        roadPath.setPathName(startCombinePoint.getPointAlias() + "_to_" + endCombinePoint.getPointAlias() + "_auto");
                        roadPath.setCreateTime(new Date());
                        roadPath.setPathType(Constant.PATH_TYPE_CLOUD);
                        roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                        log.info(placeholder + "roadPath、" + roadPath.toString());
                        List<Long> pointIds = new ArrayList<>();
                        pointIds.add(startPathPointId);
                        pointIds.add(startCombinePointId);
                        pointIds.add(endCombinePointId);
                        log.info(placeholder + "pointIds、" + pointIds.toString());

                        roadPathService.createOrUpdateRoadPathByStartAndEndPoint(startPathPointId,
                                endCombinePointId,sceneName,null,Constant.PATH_TYPE_CLOUD,
                                roadPath,pointIds);
                    }
                }
            }

            //清空路径图的缓存
            CacheInfoManager.removeRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

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
            AjaxResult elevatorAjaxResult = generateElevatorPath(sceneId);
            if(!elevatorAjaxResult.isSuccess()) {
                return elevatorAjaxResult;
            }

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
                    MapPoint startPoint = PointServiceImpl.findPathPointByXYTH(sceneName,startStationPoint.getMapName(),
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
                            MapPoint endPoint = PointServiceImpl.findPathPointByXYTH(sceneName,endStationPoint.getMapName(),
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

                            result = dijkstraService.getShortestCloudRoadPathForMission(startPoint, endPoint,roadPathMaps,result);

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
        MapPoint endPoint = PointServiceImpl.findPathPointByXYTH(endStationPoint.getSceneName(),endStationPoint.getMapName(),
                endStationPoint.getX(),endStationPoint.getY(),endStationPoint.getTh(),null, pointService);
        return AjaxResult.success(endPoint);
    }
}