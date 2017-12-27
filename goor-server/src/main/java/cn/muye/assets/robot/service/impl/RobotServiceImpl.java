package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.*;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.dijkstra.RoadPathResult;
import cn.mrobot.bean.dijkstra.RobotRoadPathResult;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.task.JsonMissionItemDataLaserNavigation;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationRobotXREFService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.assets.robot.service.RobotChargerMapPointXREFService;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.dijkstra.service.RoadPathResultService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.util.PathUtil;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl extends BaseServiceImpl<Robot> implements RobotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotServiceImpl.class);

    @Autowired
    private RobotPasswordService robotPasswordService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private StationRobotXREFService stationRobotXREFService;

    @Autowired
    private RobotChargerMapPointXREFService robotChargerMapPointXREFService;

    @Autowired
    private PointService pointService;

    @Autowired
    private RobotMapper robotMapper;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private RoadPathResultService roadPathResultService;

    @Autowired
    private RoadPathService roadPathService;

    @Autowired
    private MapInfoService mapInfoService;

    public static final Lock lock1 = new ReentrantLock();

    @Autowired
    private UserUtil userUtil;

    /**
     * 更新机器人
     *
     * @param robot
     */
    @Override
    public AjaxResult updateRobotAndBindChargerMapPoint(Robot robot, Integer lowBatteryThresholdDb, Integer sufficientBatteryThresholdDb, Integer lowRobotBatteryThreshold, Integer sufficientBatteryThreshold, String robotCodeDb) throws RuntimeException {
        List<MapPoint> list = robot.getOriginChargerMapPointList();
//        if (list != null && list.size() > 0) {
        list = bindChargerMapPoint(robot.getId(), robot.getOriginChargerMapPointList());
        robot.setOriginChargerMapPointList(list);
//        }
        if (lowBatteryThresholdDb == null || lowBatteryThresholdDb != null && !lowBatteryThresholdDb.equals(lowRobotBatteryThreshold)) {
            robot.setLowBatteryThreshold(lowRobotBatteryThreshold);
        }
        if (sufficientBatteryThresholdDb == null || sufficientBatteryThresholdDb != null && !sufficientBatteryThresholdDb.equals(sufficientBatteryThreshold)) {
            robot.setSufficientBatteryThreshold(sufficientBatteryThreshold);
        }
        //更新机器人信息
        updateSelectiveByStoreId(robot);
        //更新机器人配置信息
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (robotConfig != null && robot.getLowBatteryThreshold() != null) {
            robotConfig.setLowBatteryThreshold(robot.getLowBatteryThreshold());
            robotConfig.setSufficientBatteryThreshold(robot.getSufficientBatteryThreshold());
            robotConfigService.updateSelective(robotConfig);
        }
        //向X86上同步修改后的机器人电量阈值信息
        if (CacheInfoManager.getRobotOnlineCache(robot.getCode()) != null && CacheInfoManager.getRobotOnlineCache(robot.getCode()) == true && lowBatteryThresholdDb != null && lowRobotBatteryThreshold != null && sufficientBatteryThresholdDb != null && sufficientBatteryThreshold != null) {
            if (lowBatteryThresholdDb != null && !lowBatteryThresholdDb.equals(lowRobotBatteryThreshold)) {
                robot.setLowBatteryThreshold(lowRobotBatteryThreshold);
            }
            if (sufficientBatteryThresholdDb != null && !sufficientBatteryThresholdDb.equals(sufficientBatteryThreshold)) {
                robot.setSufficientBatteryThreshold(sufficientBatteryThreshold);
            }
            Boolean flag = CacheInfoManager.getRobotOnlineCache(robot.getCode());
            if (flag == null) {
                flag = false;
            }
            robot.setOnline(flag);
            syncRobotBatteryThresholdToRos(robot);
        }
        return AjaxResult.success(robot, "修改成功");
    }

    /**
     * 向X86上同步修改后的机器人电量阈值信息
     *
     * @param robotDb
     * @return
     */
    private void syncRobotBatteryThresholdToRos(Robot robotDb) throws RuntimeException {
        //给应用和任务管理器下发topic
        for (int i = 0; i < 2; i++) {
            try {
                CommonInfo commonInfo = new CommonInfo();
                commonInfo.setTopicName(TopicConstants.AGENT_PUB);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                String uuid = UUID.randomUUID().toString().replace("-", "");
                robotDb.setUuid(UUID.randomUUID().toString().replace("-", ""));
                SlamBody slamBody = new SlamBody();
                slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
                slamBody.setUuid(uuid);
                convertChargerMapPointToJsonMissionItemDataLaserNavigation(robotDb);
                slamBody.setData(JsonUtils.toJson(robotDb,
                        new TypeToken<Robot>() {
                        }.getType()));
                LOGGER.info("下发机器人信息json串=>", slamBody.getData());
                slamBody.setErrorCode("0");
                slamBody.setMsg("success");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
                messageInfo.setReceiverId(robotDb.getCode());
                messageInfo.setSenderId("goor-server");
                messageInfo.setMessageType(MessageType.ROBOT_INFO);
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotDb.getCode(), messageInfo);
            } catch (Exception e) {
                LOGGER.error("发送错误", e);
            }
        }
    }

    /**
     * 由站点ID查询可用的机器人
     *
     * @param stationId
     * @return
     */
    @Override
    public Robot getAvailableRobotByStationId(Long stationId, Integer typeId) throws RuntimeException {
        List<StationRobotXREF> list = stationRobotXREFService.getByStationId(stationId);
        Robot availableRobot = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (StationRobotXREF xref : list) {
                Long robotId = xref.getRobotId();
                Robot robotDb = getById(robotId);
                if (robotDb != null) {
                    //todo 紧急制动以后在做
                    AjaxResult ajaxResult = testSendRobotMessage(robotDb);
                    if (ajaxResult != null && ajaxResult.isSuccess() && robotDb.getBusy() == false && !robotDb.isLowPowerState()) {
                    //if (robotDb.getBusy() == false && robotDb.getTypeId().equals(typeId) && !robotDb.isLowPowerState()) {
                        if(typeId != null){
                            if(robotDb.getTypeId().equals(typeId)){
                                availableRobot = robotDb;
                            }else {
                                stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "不可用，原因：机器人类型不匹配");
                                LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                                continue;
                            }
                        }else{
                            availableRobot = robotDb;
                        }
                        stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "可用");
                        LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                        break;
                    } else {
                        stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "不可用，原因：" + (robotDb.getBusy() ? "忙碌," : "空闲,") + (ajaxResult != null && ajaxResult.isSuccess() ? "在线," : "离线,") + (robotDb.isLowPowerState() ? "低电量" : "电量正常"));
                        LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                    }
                }
            }
        }
        if (availableRobot != null) {
            availableRobot.setBusy(true);
            super.updateSelective(availableRobot);
        }
        return availableRobot;
    }

    @Override
    public RobotRoadPathResult getNearestAvailableRobotByOrder(Integer typeId, Order order) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        //获取下单站ID
        Long orderStationId = order.getStartStation().getId();

        //根据下单站查询下单站可以调度的机器人
        List<StationRobotXREF> list = stationRobotXREFService.getByStationId(orderStationId);
        Robot availableRobot = null;
        RobotRoadPathResult robotRoadPathResultReturn = null;

        //站未绑定机器人直接返回
        if(list == null || list.size() == 0) {
            return null;
        }

        /**根据订单设置，有没有装货站来判断选哪个点作为下单的第一个目的地点**/

        MapPoint pathStationPoint = PathUtil.getFirstPathStationPointByOrder(order, pointService);
        if(pathStationPoint == null) {
            stringBuffer.append("下单获取可用机器,失败。原因：下单信息错误，没有装货站且没有要去的站，无效订单。");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return null;
        }
        //查找与站点同属性的路径点，作为查询路径的终点
        String sceneName = pathStationPoint.getSceneName();

        //路径列表缓存机制，这样在动态调度里面可以从缓存读出图
        RoadPathMaps roadPathMaps = CacheInfoManager.getRoadPathMapsCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName, roadPathService);

        if(roadPathMaps == null) {
            stringBuffer.append("下单获取可用机器,失败。原因：未找到可供算法使用的图。");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return null;
        }

        List<RobotRoadPathResult> robotRoadPathResultList= new ArrayList<RobotRoadPathResult>();
        //遍历循环机器人列表，按照空闲且距离远近排序
        for(StationRobotXREF xref : list) {
            Long robotId = xref.getRobotId();
            Robot robotDb = getById(robotId);

            //如果关联数据库机器人不存在
            if(robotDb == null) {
                continue;
            }
            //忙碌和低电量的机器人之间过滤
            if(robotDb.getBusy() || robotDb.isLowPowerState() ) {
                stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "不可用，原因：" + (robotDb.getBusy() ? "忙碌," : "空闲,") + (robotDb.isLowPowerState() ? "低电量" : "电量正常"));
                LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                continue;
            }

            //以机器人在路径上的投影点与路径的关系为计算远近条件，做权值的补偿，以满足同一路径附近如果有多个机器人也能正确排序
            RoadPathResult result = roadPathResultService.getNearestPathResultStartShadowPointByRobotCode(robotDb, pathStationPoint, roadPathMaps);

            //未找到路径则继续，只有一个点可能就是起点附近
            if(result == null || result.getPointIds() == null || result.getPointIds().size() <= 0) {
                stringBuffer.append("下单获取可用机器：" + robotDb.getCode()  + "不可用。未找到机器人所在位置匹配的可到达目的地的路径！");
                LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                continue;
            }
            RobotRoadPathResult robotRoadPathResult = new RobotRoadPathResult();
            robotRoadPathResult.setRobot(robotDb);
            robotRoadPathResult.setRoadPathResult(result);
            robotRoadPathResultList.add(robotRoadPathResult);
        }

        if(robotRoadPathResultList == null || robotRoadPathResultList.size() == 0) {
            stringBuffer.append("下单获取可用机器,失败。原因：未找到该站关联的可用机器人。");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            return null;
        }

        //对查找出的路径结果进行从小到大排序
        PathUtil.sortByRobotRoadPathResultList(robotRoadPathResultList);

        //遍历排序后的结果集，依次取第一个可用的机器人作为下单机器人
        for(RobotRoadPathResult robotRoadPathResult : robotRoadPathResultList) {
            Robot robotDb = robotRoadPathResult.getRobot();

            //todo 紧急制动以后在做
            AjaxResult ajaxResult = testSendRobotMessage(robotDb);
            if(ajaxResult == null || !ajaxResult.isSuccess() || robotDb.getBusy() || robotDb.isLowPowerState() ) {
                stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "不可用，原因：" + (robotDb.getBusy() ? "忙碌," : "空闲,") + (ajaxResult != null && ajaxResult.isSuccess() ? "在线," : "离线,") + (robotDb.isLowPowerState() ? "低电量" : "电量正常"));
                LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                continue;
            }

            //if (robotDb.getBusy() == false && robotDb.getTypeId().equals(typeId) && !robotDb.isLowPowerState()) {
            if(typeId != null){
                if(robotDb.getTypeId().equals(typeId)){
                    availableRobot = robotDb;
                }else {
                    stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "不可用，原因：机器人类型不匹配");
                    LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
                    continue;
                }
            }else{
                availableRobot = robotDb;
                //如果找到了可用机器人，就返回结果
                robotRoadPathResultReturn = robotRoadPathResult;
            }
            stringBuffer.append("下单获取可用机器：" + robotDb.getCode() + "可用");
            LogInfoUtils.info("server", ModuleEnums.SCENE, LogType.INFO_USER_OPERATE, stringBuffer.toString());
            break;
        }
        if (availableRobot != null) {
            availableRobot.setBusy(true);
            super.updateSelective(availableRobot);
            robotRoadPathResultReturn.setRobot(availableRobot);
        }

        return robotRoadPathResultReturn;
    }

    /**
     * 根据站查询可调用的机器人数量
     *
     * @param stationId
     * @return
     */
    @Override
    public Map getCountAvailableRobotByStationId(Long stationId) {
        List<StationRobotXREF> list = stationRobotXREFService.getByStationId(stationId);
        Map<String, Integer> availableRobotCountMap = Maps.newHashMap();
        int trailerCount = 0;
        int cabinetCount = 0;
        int drawerCount = 0;
        int cookyCount = 0;
        int cookyPlusCount = 0;
        int carsonCount = 0;
        if (list != null && list.size() > 0) {
            for (StationRobotXREF xref : list) {
                Long robotId = xref.getRobotId();
                Robot robotDb = getById(robotId);
                //todo 暂时先不考虑低电量和紧急制动状态
                if (robotDb != null && robotDb.getBusy() == false && CacheInfoManager.getRobotOnlineCache(robotDb.getCode()) != null && CacheInfoManager.getRobotOnlineCache(robotDb.getCode()) == true) {
                    if (robotDb.getTypeId().equals(RobotTypeEnum.TRAILER.getCaption())) {
                        trailerCount++;
                    } else if (robotDb.getTypeId().equals(RobotTypeEnum.CABINET.getCaption())) {
                        cabinetCount++;
                    } else if (robotDb.getTypeId().equals(RobotTypeEnum.DRAWER.getCaption())) {
                        drawerCount++;
                    } else if (robotDb.getTypeId().equals(RobotTypeEnum.COOKY.getCaption())) {
                        cookyCount++;
                    } else if (robotDb.getTypeId().equals(RobotTypeEnum.COOKYPLUS.getCaption())) {
                        cookyPlusCount++;
                    } else if (robotDb.getTypeId().equals(RobotTypeEnum.CARSON.getCaption())) {
                        carsonCount++;
                    }
                }
            }
            availableRobotCountMap.put(RobotTypeEnum.TRAILER.name(), trailerCount);
            availableRobotCountMap.put(RobotTypeEnum.CABINET.name(), cabinetCount);
            availableRobotCountMap.put(RobotTypeEnum.DRAWER.name(), drawerCount);
            availableRobotCountMap.put(RobotTypeEnum.COOKY.name(), cookyCount);
            availableRobotCountMap.put(RobotTypeEnum.COOKYPLUS.name(), cookyPlusCount);
            availableRobotCountMap.put(RobotTypeEnum.CARSON.name(), carsonCount);
        }
        return availableRobotCountMap;
    }

    @Override
    public List<MapPoint> bindChargerMapPoint(Long robotId, List<MapPoint> list) {
        List listMapPoint = null;
        if (robotId != null) {
            robotChargerMapPointXREFService.deleteByRobotId(robotId);
            if (list != null && list.size() > 0) {
                for (MapPoint mapPoint : list) {
                    Long mapPointId = mapPoint.getId();
                    RobotChargerMapPointXREF xref = new RobotChargerMapPointXREF();
                    xref.setRobotId(robotId);
                    xref.setChargerMapPointId(mapPointId);
                    robotChargerMapPointXREFService.save(xref);
                    MapPoint mapPointDb = pointService.findById(mapPointId);
                    listMapPoint = Lists.newArrayList(mapPointDb);
                }
            }
        }
        return listMapPoint;
    }

    /**
     * 由机器人编号获取绑定的充电桩List
     *
     * @param robotCode
     * @return
     * @author Ray.Fu
     */
    @Override
    public List<MapPoint> getChargerMapPointByRobotCode(String robotCode, Long storeId) {
        if (robotCode != null) {
            Robot robotDb = getByCode(robotCode, storeId);
            if (robotDb != null) {
                Long robotId = robotDb.getId();
                List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robotId);
                List<MapPoint> mapPointList = Lists.newArrayList();
                if (xrefList != null && xrefList.size() > 0) {
                    for (RobotChargerMapPointXREF xref : xrefList) {
                        MapPoint point = pointService.findById(xref.getChargerMapPointId());
                        mapPointList.add(point);
                    }
                }
                return mapPointList;
            }
        } else {
            return null;
        }
        return null;
    }

    private List<Robot> listPageByStoreIdAndOrder(int page, int pageSize, Map map) {
        PageHelper.startPage(page, pageSize);
        return robotMapper.listRobot(map);
    }

    @Override
    public List<Robot> listRobot(WhereRequest whereRequest) {
        Map map = Maps.newHashMap();
        if (!StringUtil.isNullOrEmpty(whereRequest.getQueryObj())) {
            JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
            String name = (String) jsonObject.get(SearchConstants.SEARCH_NAME);
            String sceneId = String.valueOf(jsonObject.get(SearchConstants.SEARCH_SCENE_ID));
            String sceneName = (String) jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
            Integer type = jsonObject.get(SearchConstants.SEARCH_TYPE) != null ? Integer.valueOf((String) jsonObject.get(SearchConstants.SEARCH_TYPE)) : null;
            map.put("name", name);
            map.put("sceneId", sceneId);
            map.put("sceneName", sceneName);
            map.put("type", type);
        }
        List<Robot> list = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), map);
        list.forEach(robot -> {
            Long robotId = robot.getId();
            RobotConfig robotConfigDb = robotConfigService.getByRobotId(robotId);
            robot.setLowBatteryThreshold(robotConfigDb != null ? robotConfigDb.getLowBatteryThreshold() : null);
            robot.setSufficientBatteryThreshold(robotConfigDb != null ? robotConfigDb.getSufficientBatteryThreshold() : null);
            List<RobotPassword> robotPasswordList = robotPasswordService.listRobotPassword(robotId);
            robot.setPasswords(robotPasswordList);
            Boolean flag = CacheInfoManager.getRobotOnlineCache(robot.getCode());
            if (flag != null) {
                robot.setOnline(flag);
                LOGGER.info(robot.getCode() + (flag ? "在线" : "离线"));
            } else {
                robot.setOnline(false);
                LOGGER.info(robot.getCode() + "离线");
            }
            List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robotId);
            List<MapPoint> mapPointList = Lists.newArrayList();
            xrefList.forEach(xref -> {
                MapPoint mapPoint = pointService.findById(xref.getChargerMapPointId());
                if (mapPoint != null) {
                    mapPointList.add(mapPoint);
                }
            });
            robot.setOriginChargerMapPointList(mapPointList);
        });
        return list;
    }

    @Override
    public List<Robot> listRobot(Long storeId) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("STORE_ID =", storeId);
        return myMapper.selectByExample(example);
    }

    public Robot getById(Long id) {
        return myMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增机器人信息
     *
     * @param robot
     * @throws RuntimeException
     */
    public void saveRobotAndBindChargerMapPoint(Robot robot) throws RuntimeException {
        super.save(robot);
        Long robotNewId = robot.getId();
        List list = robot.getChargerMapPointList();
        if (list != null && list.size() == 1 && robotNewId != null) {
            bindChargerMapPoint(robotNewId, list);
        }
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setLowBatteryThreshold(robot.getLowBatteryThreshold());
        robotConfig.setSufficientBatteryThreshold(robot.getSufficientBatteryThreshold());
        robotConfig.setRobotId(robot.getId());
        robotConfig.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        robotConfig.setCreateTime(new Date());
        robotConfig.setCreatedBy(userUtil.getCurrentUserId());
        robotConfigService.add(robotConfig);
        if (robot.getTypeId() != null) {
            robotPasswordService.saveRobotPassword(robot);
        }
    }

    /**
     * 自动注册
     *
     * @param robotNew
     * @return
     */
    @Override
    public AjaxResult autoRegister(Robot robotNew) throws RuntimeException {
        if (lock1.tryLock()) {
            try {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                if (lock1.tryLock()) {
                    try {
                        return doingAutoRegister(robotNew);
                    } finally {
                        lock1.unlock();
                    }
                }
            } finally {
                lock1.unlock();
            }
        } else {
            return AjaxResult.failed("其他线程正在注册，请稍后...");
        }
        return null;
    }

    private AjaxResult doingAutoRegister(Robot robotNew) {
        try {
            if (robotNew != null) {
                String robotCode = robotNew.getCode();
                Integer robotTypeId = robotNew.getTypeId();
                String robotName = robotNew.getName();
                Long robotStoreId = robotNew.getStoreId();
                Integer lowBatteryThreshold = robotNew.getLowBatteryThreshold();
                Integer sufficientBatteryThreshold = robotNew.getSufficientBatteryThreshold();
                List<RobotPassword> passwordList = robotNew.getPasswords();
                //todo 按照robotCode的规范来赋值typeId,本次暂时写死
                if (!StringUtil.isNullOrEmpty(robotCode) && robotTypeId == null) {
                    robotNew.setTypeId(Constant.ROBOT_PRIMARY_TYPE_ID);
                }
                if (passwordList == null || passwordList.size() == 0) {
                    passwordList = Lists.newArrayList();
                    RobotPassword robotPassword = new RobotPassword();
                    robotPassword.setPassword(Constant.PRIMARY_PWD);
                    passwordList.add(robotPassword);
                    robotNew.setPasswords(passwordList);
                }
                if (!StringUtil.isNullOrEmpty(robotCode) && StringUtil.isNullOrEmpty(robotName)) {
                    robotNew.setName(robotCode);
                }
                if (lowBatteryThreshold == null) {
                    robotNew.setLowBatteryThreshold(Constant.ROBOT_LOW_BATTERY_THRESHOLD_DEFAULT);
                }
                if (sufficientBatteryThreshold == null) {
                    robotNew.setSufficientBatteryThreshold(Constant.ROBOT_SUFFICIENT_BATTERY_THRESHOLD_DEFAULT);
                }
                if (StringUtil.isNullOrEmpty(robotCode)) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人编号不能为空");
                }
                Robot robotDb = getByCode(robotCode, robotStoreId);
                //如果表中没有该机器人记录，则写缓存，再写数据库
                CacheInfoManager.setRobotAutoRegisterTimeCache(robotNew.getCode(), System.currentTimeMillis());
                if (robotDb == null) {
                    saveRobotAndBindChargerMapPoint(robotNew);
                    //往ros上透传电量阈值,机器人注册同步往应用下发消息，不需要回执，发不成功，应用那边会有查询请求，再给其反馈机器人信息
                    syncRosRobotConfig(robotNew);
                    return AjaxResult.success(robotNew, "注册成功");
                } else {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人编号重复,注册失败");
                }
            } else {
                return AjaxResult.failed("注册失败");
            }
        } catch (Exception e) {
            CacheInfoManager.setRobotAutoRegisterTimeCache(robotNew.getCode(), null);
            LOGGER.error("注册失败, 错误日志 >>>> {}", e);
            return AjaxResult.failed("注册失败");
        } finally {
        }
    }

    /**
     * 下单检查可用机器人(发送机器人消息判断是否离线)
     * @param robot
     * @return
     * @throws RuntimeException
     */
    private AjaxResult testSendRobotMessage(Robot robot) throws RuntimeException {
        AjaxResult ajaxResult;
        try {
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName(TopicConstants.AGENT_PUB);
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            SlamBody slamBody = new SlamBody();
            slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
            slamBody.setUuid(uuid);
            slamBody.setData(JsonUtils.toJson(robot,
                    new TypeToken<Robot>() {
                    }.getType()));
            slamBody.setErrorCode("0");
            slamBody.setMsg("success");
            slamBody.setMsg("测试机器人是否离线");
            commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
            messageInfo.setReceiverId(robot.getCode());
            messageInfo.setSenderId("goor-server");
            messageInfo.setMessageType(MessageType.ROBOT_INFO);
            messageInfo.setMessageText(JSON.toJSONString(commonInfo));
            ajaxResult = messageSendHandleService.sendCommandMessage(true, true, robot.getCode(), messageInfo);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
        }
        return ajaxResult;
    }

    /**
     * 往ros上透传机器人配置信息（电量阈值，机器人编号。。。）
     *
     * @param robotNew
     */
    private void syncRosRobotConfig(Robot robotNew) {
        for (int i = 0; i < 2; i++) {
            try {
                CommonInfo commonInfo = new CommonInfo();
                commonInfo.setTopicName(TopicConstants.AGENT_PUB);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                //todo 暂时用唐林的SlamBody的结构，之后如果可复用，建议把名字换成通用的
                String uuid = UUID.randomUUID().toString().replace("-", "");
                robotNew.setUuid(uuid);
                SlamBody slamBody = new SlamBody();
                slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
                slamBody.setUuid(uuid);
                convertChargerMapPointToJsonMissionItemDataLaserNavigation(robotNew);
                slamBody.setData(JsonUtils.toJson(robotNew,
                        new TypeToken<Robot>() {
                        }.getType()));
                slamBody.setErrorCode("0");
                slamBody.setMsg("success");
                slamBody.setMsg("机器人" + robotNew.getCode() + "注册成功");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
                messageInfo.setReceiverId(robotNew.getCode());
                messageInfo.setSenderId("goor-server");
                messageInfo.setMessageType(MessageType.ROBOT_INFO);
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotNew.getCode(), messageInfo);
            } catch (Exception e) {
                LOGGER.error("发送错误{}", e);
            } finally {
            }
        }
    }

    /**
     * 充电桩点List转换导航点的List
     * @param robotNew
     */
    private void convertChargerMapPointToJsonMissionItemDataLaserNavigation(Robot robotNew) {
        if (robotNew != null && robotNew.getOriginChargerMapPointList() != null) {
            List<MapPoint> originChargerMapPointList = robotNew.getOriginChargerMapPointList();
            List<JsonMissionItemDataLaserNavigation> list = Lists.newArrayList();
            if (originChargerMapPointList != null && originChargerMapPointList.size() > 0) {
                for (MapPoint mapPoint : originChargerMapPointList) {
                    String mapName = mapPoint.getMapName();
                    JsonMissionItemDataLaserNavigation jsonMissionItemDataLaserNavigation = new JsonMissionItemDataLaserNavigation();
                    jsonMissionItemDataLaserNavigation.setMap(mapName);
                    jsonMissionItemDataLaserNavigation.setMap_name(mapName);
                    jsonMissionItemDataLaserNavigation.setScene_name(mapPoint.getSceneName());
                    jsonMissionItemDataLaserNavigation.setX(mapPoint.getX());
                    jsonMissionItemDataLaserNavigation.setY(mapPoint.getY());
                    jsonMissionItemDataLaserNavigation.setTh(mapPoint.getTh());
                    list.add(jsonMissionItemDataLaserNavigation);
                }
            }
            robotNew.setChargerMapPointList(null);
            robotNew.setChargerMapPointList(list);
        }
    }

    public void deleteRobotById(Long id) {
        myMapper.deleteByPrimaryKey(id);
        robotPasswordService.delete(new RobotPassword(null, id));
    }

    @Override
    public void deleteRobotByCode(String code) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("CODE =", code);
        myMapper.deleteByExample(example);
    }

    public Robot getByName(String name) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("NAME =", name);
        List<Robot> list = myMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Robot getByCode(String code, Long storeId) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("CODE =", code);
        List<Robot> list = myMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Robot getByCodeByXml(String code, Long storeId, Long robotId) {
        Map map = Maps.newHashMap();
        map.put("code", code);
        map.put("storeId", storeId);
        map.put("robotId", robotId);
        Robot robot = robotMapper.getRobotByCode(map);
        return robot;
    }

    @Override
    public void setRobotPassword(String newPassword) {
        List<Robot> allRobots = this.robotMapper.selectAll();
        // 实例化一个线程池 ， 后台发送消息并且轮询监听数据回执情况（线程池的各项配置可以根据特定的机器配置来确定的 ， 可以优化）
        for (Robot robot : allRobots) {
            checkNotNull(robot.getCode(), String.format("数据库编号为 %s 的机器人的机器人编号不存在，请检查解决后重试!",
                    String.valueOf(robot.getId())));
        }
        for (Robot robot : allRobots) {
//            try {
//                executor.submit(new SendPasswordToSpecialRobotThread(robot, newPassword));
//            }catch (Exception e){
//            }
            //调整最新策略，更新机器人密码的时候，现在只是云端本身的操作，只修改本地的数据库，本地数据库密码用作密码校验。
            robot.setPassword(newPassword);
            updateSelective(robot);
        }
    }

    //TODO: 任一时刻只有一个机器人密码修改的操作，其余的必须等待上一次任务处理完成才能够触发下一次的任务
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            20,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<Runnable>(100));
    private static final String PASSWORD = "password";
    private static final String SENDER = "goor-server";
    private class SendPasswordToSpecialRobotThread extends Thread {
        private String password;
        private Robot robot ;
        private SendPasswordToSpecialRobotThread(Robot robot, String password){
            this.robot = robot;
            this.password = password;
        }
        @Override
        public void run() {
            try {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                CommonInfo commonInfo = new CommonInfo();
                // TODO: 此处 TopicName 与 TopicType需要进一步确定
                commonInfo.setTopicName(TopicConstants.X86_MISSION_INSTANT_CONTROL);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                // TODO: 此处 TopicName 与 TopicType需要进一步确定
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, Object>() {{
                    put(PASSWORD, password);// TODO: 此处 设置密码的数据格式也需要进一步确定
                }}))));
                MessageInfo info = new MessageInfo();
                info.setUuId(uuid);
                info.setSendTime(new Date());
                info.setSenderId(SENDER);
                info.setReceiverId(robot.getCode());
                info.setMessageType(MessageType.EXECUTOR_COMMAND);
                info.setMessageText(JSON.toJSONString(commonInfo));
                AjaxResult result = messageSendHandleService.sendCommandMessage(true, true, robot.getCode(), info);
                if (!result.isSuccess()) {
                    LOGGER.info(String.format("编号为 %s 的机器人下发新密码 %s 失败!", String.valueOf(robot.getCode()), password));
                }
                for (int i = 0; i < 300; i++) {
                    Thread.sleep(1000);
                    MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(info.getUuId());
                    if (messageInfo1 != null && messageInfo1.isSuccess()) {
                        info.setSuccess(true);
                        break;
                    }
                }
                if (info.isSuccess()) {
                    // 将数据库中的机器人密码更新为本次更新的密码
                    robot.setPassword(password);
                    updateSelective(robot);
                } else {
                    LOGGER.info(String.format("编号为 %s 的机器人下发新密码 %s 失败!", String.valueOf(robot.getCode()), password));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 为应用提供一个验证 操作密码 是否合法的操作接口 （ 应用获取云端配置的操作密码 ）
     *
     * @param robotCode
     * @param password
     * @return
     */
    @Override
    public boolean checkPasswordIsValid(String uuid, String robotCode, String password) {
        checkNotNull(robotCode, "验证机器人编号不允许为空，请重新输入!!");
        checkNotNull(password, "验证密码不允许为空，请重新输入!");
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition(" CODE = ", robotCode);
        Robot robot = this.robotMapper.selectByExample(example).get(0); // 根据机器人 code 编号查询对应的机器人对象
        boolean result = password.equals(robot.getPassword());
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_PUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, String>() {{
            put("sub_name", TopicConstants.PUB_SUB_NAME_CHECK_OPERATE_PWD);
            put("uuid", uuid);
            put("msg", result ? "success" : "error");
            put("error_code", result ? "0" : "-1");
        }}))));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setReceiverId(robotCode);
        messageInfo.setSenderId("goor-server");
        messageInfo.setMessageType(MessageType.ROBOT_INFO);
        messageInfo.setMessageText(JSON.toJSONString(commonInfo));
        try {
            messageSendHandleService.sendCommandMessage(true, false, robotCode, messageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public void setRobotBusyAndOnline(String robotCode, Boolean busy, Boolean online) {
        checkNotNull(robotCode, "机器人编号不允许为空!!");
        if (StringUtil.isEmpty(robotCode)) {
            return;
        }
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition(" CODE = ", robotCode);
        List<Robot> robotDbList = this.robotMapper.selectByExample(example);
        if (robotDbList != null && robotDbList.size() > 0) {
            Robot robot = robotDbList.get(0); // 根据机器人 code 编号查询对应的机器人对象
            if (robot != null) {
                if (busy != null) {
                    robot.setBusy(busy);
                }
                if (online != null) {
                    CacheInfoManager.setRobotOnlineCache(robot.getCode(), online);
                }
                if (busy != null || online != null) {
                    super.updateSelective(robot);
                }
            }
        }
    }
}