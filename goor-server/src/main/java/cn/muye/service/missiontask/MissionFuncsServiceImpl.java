package cn.muye.service.missiontask;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.door.Door;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorModeEnum;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.*;
import cn.mrobot.bean.mission.task.*;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionItemDTO;
import cn.mrobot.dto.mission.MissionListDTO;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.account.employee.service.EmployeeService;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.door.service.DoorService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.service.FeatureItemService;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.service.consumer.topic.X86MissionDispatchService;
import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * Created by abel on 17-7-13.
 */
@Service
@Transactional
public class MissionFuncsServiceImpl implements MissionFuncsService {

    protected static final Logger logger = LoggerFactory.getLogger(MissionFuncsServiceImpl.class);


    @Autowired
    StationService stationService;

    @Autowired
    RobotService robotService;

    @Autowired
    MissionListTaskService missionListTaskService;

    @Autowired
    MissionTaskService missionTaskService;

    @Autowired
    MissionItemTaskService missionItemTaskService;

    @Autowired
    X86MissionDispatchService x86MissionDispatchService;

    @Autowired
    private FeatureItemService featureItemService;

    @Autowired
    MapInfoService mapInfoService;

    @Autowired
    private PointService pointService;

    @Autowired
    RoadPathService roadPathService;

    @Autowired
    private EmployeeService employeeService;

    @Value("${mission.item.concurrentable:false}")
    private Boolean missionItemConcurrentable;

    @Autowired
    private MissionWarningService missionWarningService;

    /**
     * 根据订单数据创建任务列表
     * @param order
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public AjaxResult createMissionLists(Order order) {
    
    
        logger.info("##############  createMissionLists #################");

        //判断order
        if (order == null ||
                order.getOrderSetting() == null ||
                order.getRobot() == null ||
                order.getOrderSetting().getStartStation() == null ||
                order.getOrderSetting().getEndStation() == null){
            logger.info("##############  createMissionLists attrs error #################");
        }

        //定义地图点的集合
        List<MapPoint> mapPoints = new ArrayList<>();
        //定义地图点对应属性的map
        HashMap<MapPoint, MPointAtts> mpAttrs = new HashMap<>();

        //根据订单信息搜集任务点
        collectMapPoints(order, mapPoints, mpAttrs);

        //根据任务点及其属性完善任务列表
        MissionListTask missionListTask = new MissionListTask();

        //根据任务点，实例化任务列表
        initMissionListTask(missionListTask, order, mapPoints, mpAttrs);

        //任务列表实例化完成，将数据存储到数据库
        saveMissionListTask(missionListTask);

        //下发任务到机器人任务管理器
        List<MissionListTask> listTasks =
                new ArrayList<>();
        listTasks.add(missionListTask);
        AjaxResult ajaxResult = x86MissionDispatchService.sendX86MissionDispatch(
                order.getRobot().getCode(),
                getGoorMissionMsg(listTasks)
        );
        if(ajaxResult == null || !ajaxResult.isSuccess()){
            logger.info("##############  createMissionLists failed ，发送客户端goor失败#################");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ajaxResult == null ? AjaxResult.failed("客户端无法连接，订单失败") : ajaxResult;
        }else {
            logger.info("robot code is: " + order.getRobot().getCode() +
                    " , ####### tesk is: " + getGoorMissionMsg(listTasks));
            logger.info("##############  createMissionLists successed #################");
            return ajaxResult;
        }



    }



    /**
     * 根据MissionList列表和机器人列表生成MissionListTask列表并发送到机器人
     * @param robotCode
     * @param missionLists
     * @return 返回结果表示对应机器人的下发消息成功失败状态
     *
     */
    @Override
    public AjaxResult createMissionListTasksByMissionLists(String robotCode,
                                                           List<MissionList> missionLists) throws Exception{
        if(missionLists == null || missionLists.size() <= 0
                || robotCode == null ) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误");
        }

        List<MissionListTask> listTasks = new ArrayList<>();
        //先转换对象
        for(MissionList missionList : missionLists) {
            if(missionList == null) {
                continue;
            }
            //missionList转化成missionListTask
            MissionListTask missionListTask = missionListToTask(missionList,robotCode);
            //任务列表实例化完成，将数据存储到数据库
            saveMissionListTask(missionListTask);
            listTasks.add(missionListTask);
        }
        AjaxResult ajaxResult = x86MissionDispatchService.sendX86MissionDispatch(
                robotCode,
                getGoorMissionMsg(listTasks)
        );
        //TODO 加延时判断，ajaxResult为空也是报错
        ajaxResult = (ajaxResult == null ? AjaxResult.failed(AjaxResult.CODE_FAILED,"消息发送失败") : ajaxResult);
        if(ajaxResult.getCode() != AjaxResult.CODE_SUCCESS) {
            //发送失败回滚数据库
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ajaxResult;
        }
        else {
            return AjaxResult.success("消息发送成功");
        }
    }

    @Override
    public String getGoorMissionMsg(List<MissionListTask> listTasks) {
        if (listTasks == null ||
                listTasks.isEmpty()){
            return "";
        }
        List<MissionListDTO> dtos = new ArrayList<>();
        for (MissionListTask task :
                listTasks) {
            if (task != null) {
                MissionListDTO missionListDTO =
                        getGoorMissionList(task);
                if (missionListDTO != null){
                    dtos.add(missionListDTO);
                }
            }
        }

        //加入uuid
        JsonMissionListPub jsonMissionListPub =
                new JsonMissionListPub();
        jsonMissionListPub.setSendTime(System.currentTimeMillis());
        jsonMissionListPub.setMissionLists(dtos);
        jsonMissionListPub.setUuid(UUID.randomUUID().toString().replace("-", ""));

        return JsonUtils.toJson(jsonMissionListPub,
                new TypeToken<JsonMissionListPub>(){}.getType());
    }

    @Override
    public List<MissionTask> getMissionTaskStatus(String robotCode) {
        if (StringUtil.isEmpty(robotCode)){
            return null;
        }
        //查询最后一个任务列表
        MissionListTask listTask =
                missionListTaskService.findLastByRobotCode(
                        robotCode);
        if (listTask != null){
            return missionTaskService.findByListId(
                    listTask.getId()
            );
        }
        return null;
    }

    /**
     * 获取MissionListDTO对象
     * @param task
     * @return
     */
    private MissionListDTO getGoorMissionList(MissionListTask task) {
        if (task != null){

            logger.info("### MissionListTask is: " + JsonUtils.toJson(task, new TypeToken<MissionListTask>(){}.getType()));

            MissionListDTO missionListDTO = new MissionListDTO();
            missionListDTO.setId(task.getId());
            missionListDTO.setIntervalTime(task.getIntervalTime());
            missionListDTO.setMissionListType(task.getMissionListType());
            missionListDTO.setPriority(task.getPriority());
            missionListDTO.setRepeatCount(task.getRepeatTimes());
            missionListDTO.setStartTime(task.getStartTime());
            missionListDTO.setStopTime(task.getStopTime());

            //进行mission task的解析
            if (task.getMissionTasks() != null &&
                    !task.getMissionTasks().isEmpty()){
                List<MissionDTO> missionDTOS = new ArrayList<>();
                for (MissionTask missionTask :
                        task.getMissionTasks()) {
                    if (missionTask != null) {
                        MissionDTO missionDTO = getGoorMission(missionTask);
                        if (missionDTO != null){
                            //进行mission item task的解析
                            if (missionTask.getMissionItemTasks() != null &&
                                    !missionTask.getMissionItemTasks().isEmpty()){
                                List<MissionItemDTO> missionItemDTOS =
                                        new ArrayList<>();
                                for (MissionItemTask mit :
                                        missionTask.getMissionItemTasks()) {
                                    if (mit != null) {
                                        MissionItemDTO missionItemDTO =
                                                getGoorMissionItem(mit);
                                        if (missionItemDTO != null){
                                            missionItemDTOS.add(missionItemDTO);
                                        }
                                    }
                                }
                                missionDTO.setMissionItemSet(new HashSet<>(missionItemDTOS));
                            }
                            missionDTOS.add(missionDTO);
                        }
                    }
                }
                missionListDTO.setMissionList(missionDTOS);
            }

            return missionListDTO;
        }

        return null;
    }

    /**
     * 获取MissionItemDTO对象
     * @param mit
     * @return
     */
    private MissionItemDTO getGoorMissionItem(MissionItemTask mit) {
        if (mit == null){
            return null;
        }
        MissionItemDTO missionItemDTO =
                new MissionItemDTO();
        missionItemDTO.setId(mit.getId());
        missionItemDTO.setName(mit.getName());
        missionItemDTO.setData(mit.getData());
        missionItemDTO.setIgnorable(mit.getIgnorable());
        return missionItemDTO;
    }

    /**
     * 获取MissionDTO对象
     * @param missionTask
     * @return
     */
    private MissionDTO getGoorMission(MissionTask missionTask) {
        if (missionTask == null){
            return null;
        }
        MissionDTO missionDTO =
                new MissionDTO();
        missionDTO.setId(missionTask.getId());
        missionDTO.setIntervalTime(missionTask.getIntervalTime());
        missionDTO.setRepeatCount(missionTask.getRepeatTimes());
        return missionDTO;
    }

    /**
     * 将任务列表存储到数据库
     * @param missionListTask
     */
    private void saveMissionListTask(MissionListTask missionListTask) {
        if (missionListTask == null){
            return;
        }

        //保存任务列表
        missionListTaskService.save(missionListTask);
        if (missionListTask.getId() == null){
            return;
        }

        if (missionListTask.getMissionTasks() != null){
            for (MissionTask mt :
                    missionListTask.getMissionTasks()) {
                if (mt != null) {
                    mt.setMissionListId(missionListTask.getId());
                    //保存任务节点
                    missionTaskService.save(mt);
                    if (mt.getId() != null &&
                            mt.getMissionItemTasks() != null){
                        for (MissionItemTask mit :
                                mt.getMissionItemTasks()) {
                            if (mit != null) {
                                mit.setMissionListId(mt.getMissionListId());
                                mit.setMissionId(mt.getId());
                                //保存任务item节点
                                missionItemTaskService.save(mit);
                            }
                        }
                    }
                }
            }
        }


    }

    private MapPoint prePoint;
    /**
     * 根据订单信息搜集任务点
     * @param order
     * @param mapPoints
     * @param mpAttrs
     */
    private void collectMapPoints(
            Order order,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {

        logger.info("### order is: " + JsonUtils.toJson(order, new TypeToken<Order>(){}.getType()));

        MPointAtts atts;

        //判断中间站点，如果有中间站点，添加中间站点的地图点，如果中间点跨楼层，则要在两个任务中间插入电梯任务
        if (order.getDetailList() != null){
            for (OrderDetail od :
                    order.getDetailList()) {
                if (od != null &&
                        od.getStationId() != null) {
                    logger.info("###### begin get order detail station ");
                    //判定是否为起始点
                    if(Objects.equals(od.getPlace(), OrderConstant.ORDER_DETAIL_PLACE_START)){
                        //首先插入起点
                        Long stationId = order.getOrderSetting().getStartStation().getId();
                        MapPoint startPoint = pointService.findMapPointByStationIdAndCloudType(stationId, MapPointType.LOAD.getCaption());
                        mapPoints.add(startPoint);
                        //设置属性
                        atts = new MPointAtts();
                        atts.type = MPointType_QUHUO;
                        atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                        mpAttrs.put(startPoint, atts);
                        prePoint = startPoint;
                        logger.info("###### quhuo is ok ");
                    }else if(Objects.equals(od.getPlace(), OrderConstant.ORDER_DETAIL_PLACE_END)){
                        Long endStationId = order.getOrderSetting().getEndStation().getId();
                        MapPoint endPoint = pointService.findMapPointByStationIdAndCloudType(endStationId, MapPointType.FINAL_UNLOAD.getCaption());
                        if (endPoint != null) {
                            logger.info("### end point ####  mpname: " + endPoint.getMapName() +
                                    ", scenename: " + endPoint.getSceneName() +
                                    ", pointname: " + endPoint.getPointName());
                        }

                        //判断添加电梯点
                        addRoadPathPoint(endPoint, mapPoints, mpAttrs);
                        //addElevatorPoint(order.getOrderSetting().getEndPoint(), mapPoints, mpAttrs);
                        //中间点添加完毕，添加卸货点
                        mapPoints.add(endPoint);
                        //设置属性
                        atts = new MPointAtts();
                        atts.type = MPointType_XIAHUO;
                        atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                        mpAttrs.put(endPoint, atts);
                        logger.info("###### xiahuo is ok ");
                    }else {
                        //取得站点对象
                        //TODO 以后AGV也需要按照切换的场景过滤
                        Station station = stationService.findById(od.getStationId(), od.getStoreId(),null);
                        if (station != null &&
                                station.getMapPoints() != null){
                            logger.info("###### get order detail station is ok， list size is: " + station.getMapPoints().size());
                            for (MapPoint mp :
                                    station.getMapPoints()) {
                                if (mp != null) {
                                    logger.info("mpname: " + mp.getMapName() + ", scenename: " + mp.getSceneName() + ", pointname: " + mp.getPointName());
                                }
                            }
                            //目前只取第一个坐标点加入
                            for (MapPoint mp :
                                    station.getMapPoints()) {
                                if(mp != null){
                                    //首先判断当前点和前一个点的关系，判断是否需要加入电梯任务
                                    addRoadPathPoint(mp, mapPoints, mpAttrs);
//                                addElevatorPoint(mp, mapPoints, mpAttrs);
                                    //判断当前点的属性，根据属性加入相应的任务
                                    //加入该点，并标记这个点状态是orderDetail点
                                    mapPoints.add(mp);
                                    //标记该点的属性
                                    atts = new MPointAtts();
                                    atts.type = MPointType_SONGHUO;
                                    atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                                    mpAttrs.put(mp, atts);
                                    logger.info("###### order detail station is ok ");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //最后添加充电点，目前充电点从机器人的数据库里面查询出来
        //查询列表
        List<MapPoint> chongMPs = robotService
                .getChargerMapPointByRobotCode(order.getRobot().getCode(), SearchConstants.FAKE_MERCHANT_STORE_ID);
        //取第一个有效的点设置进去
        if (chongMPs != null){
            for (MapPoint mp :
                    chongMPs) {
                if (mp != null) {
                    //如果充电点和上一个点不在同一个楼层，则要添加相应的电梯任务。
                    addRoadPathPoint(mp, mapPoints, mpAttrs);
//                    addElevatorPoint(mp, mapPoints, mpAttrs);
                    //添加充电点任务
                    mapPoints.add(mp);
                    //设置属性
                    atts = new MPointAtts();
                    atts.type = MPointType_CHONGDIAN;
                    mpAttrs.put(mp, atts);
                    logger.info("###### chongdian is ok ");
                    break;
                }
            }
        }
    }

    /**
     * 判断新增路径任务
     * @param mp
     * @param mapPoints
     * @param mpAttrs
     */
    private void addRoadPathPoint(
            MapPoint mp,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {
        logger.info("###### addRoadPathPoint start ");
        //查询两个点之间是否有路径
        if (prePoint == null || mp == null){
            return;
        }

        try {
//            List<RoadPathDetail> roadPathDetails =
//                    roadPathService.findRoadPathByStartAndEndPoint(
//                            prePoint.getId(),
//                            mp.getId(),
//                            mp.getSceneName(),
//                            null
//                    );
            List<RoadPathDetail> roadPathDetails =
                    roadPathService.listRoadPathDetailByStartAndEndPointType(
                            prePoint.getId(),
                            mp.getId(),
                            mp.getSceneName(),
                            null,
                            Constant.PATH_TYPE_CLOUD
                    );
            //取第一个有效的进行任务插入
            MapPoint temp = null;
            if (roadPathDetails != null){
                for (RoadPathDetail rpd :
                        roadPathDetails) {
                    if (rpd != null &&
                            rpd.getStart() != null &&
                            rpd.getEnd() != null &&
                            rpd.getRelatePoints() != null) {
                        for (MapPoint point :
                                rpd.getRelatePoints()) {
                            if (point != null){
                                switch (MapPointType.getType(point.getCloudMapPointTypeId())){
                                    case DOOR_WAIT:
                                        //添加门任务
                                        mapPoints.add(point);
                                        //标记该点的属性
                                        MPointAtts atts = new MPointAtts();
                                        atts.type = MPointType_DOOR;
                                        mpAttrs.put(point, atts);
                                        logger.info("###### addRoadPathPoint door is ok ");
                                        break;
                                    case ELEVATOR_WAIT:
                                        addElevatorPoint(mp, mapPoints, mpAttrs);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        prePoint = mp;
    }


    /**
     * 判断新增电梯任务
     * @param mp
     * @param mapPoints
     * @param mpAttrs
     */
    private void addElevatorPoint(MapPoint mp,
                                  List<MapPoint> mapPoints,
                                  HashMap<MapPoint, MPointAtts> mpAttrs) {
        if (prePoint == null || mp == null){
            return;
        }
        MPointAtts prefloor = getMapPointFloor(prePoint);
        MPointAtts mpfloor = getMapPointFloor(mp);
        if (prefloor == null ||
                prefloor.currentMapId == null ||
                prefloor.currentFloor == null ||
                mpfloor == null ||
                mpfloor.currentFloor == null ||
                mpfloor.currentMapId == null){
            return;
        }
        if (!prefloor.currentFloor.equals(mpfloor.currentFloor)){
            //楼层不一样，需要新增电梯任务
            MapPoint temp = new MapPoint();
            //
            mapPoints.add(temp);
            //标记该点的属性
            MPointAtts atts = new MPointAtts();
            atts.type = MPointType_ELEVATOR;
            atts.currentFloor = prefloor.currentFloor;
            atts.currentMapId = prefloor.currentMapId;
            atts.nextFloor = mpfloor.currentFloor;
            atts.nextMapId = mpfloor.currentMapId;
            atts.logicFloor = mpfloor.logicFloor;
            mpAttrs.put(temp, atts);
            logger.info("###### addElevatorPoint is ok ");
        }
//        prePoint = mp;
    }

    /**
     * 获取地图点所在的楼层
     * @param mp
     * @return
     */
    private MPointAtts getMapPointFloor(MapPoint mp) {
        return getMapPointFloorStatic(mp,mapInfoService);
    }

    /**
     * 获取地图点所在的逻辑楼层
     * @param mp
     * @return
     */
    private MPointAtts getMapPointLogicFloor(MapPoint mp) {
        return getMapPointLogicFloorStatic(mp,mapInfoService);
    }

    /**
     * 获取地图点所在的楼层
     * @param mp
     * @return
     */
    public static MPointAtts getMapPointFloorStatic(MapPoint mp,MapInfoService mapInfoService) {
        MPointAtts ret = new MPointAtts();
        List<MapInfo> mapInfos = mapInfoService.getMapInfo(
                mp.getMapName(),
                mp.getSceneName(),
                SearchConstants.FAKE_MERCHANT_STORE_ID
        );
        if (mapInfos != null){
            for (MapInfo m :
                    mapInfos) {
                if (m != null) {
                    ret.currentFloor = m.getFloor();
                    ret.currentMapId = m.getId();
                    ret.logicFloor = m.getLogicFloor();
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 获取地图点所在的逻辑楼层
     * @param mp
     * @return
     */
    public static MPointAtts getMapPointLogicFloorStatic(MapPoint mp,MapInfoService mapInfoService) {
        MPointAtts ret = new MPointAtts();
        List<MapInfo> mapInfos = mapInfoService.getMapInfo(
                mp.getMapName(),
                mp.getSceneName(),
                SearchConstants.FAKE_MERCHANT_STORE_ID
        );
        if (mapInfos != null){
            for (MapInfo m :
                    mapInfos) {
                if (m != null) {
                    ret.logicFloor = m.getLogicFloor();
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 根据任务点，实例化任务列表
     * @param missionListTask
     * @param order
     * @param mapPoints
     * @param mpAttrs
     */
    private void initMissionListTask(
            MissionListTask missionListTask,
            Order order,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {
        if (missionListTask == null ||
                order == null){
            return;
        }
        //先初始化任务列表的相关属性
        if (order.getScene() != null) {
            missionListTask.setSceneId(order.getScene().getId());
        }
        missionListTask.setIntervalTime(0L);
        missionListTask.setDescription("下单自动任务列表"+System.currentTimeMillis());
        missionListTask.setMissionListType(MissionListType_normal);
        missionListTask.setName(missionListTask.getDescription());
        missionListTask.setOrderId(order.getId());
        missionListTask.setRepeatTimes(1);
        missionListTask.setPriority(0);
        missionListTask.setRobotCode(order.getRobot().getCode());
        missionListTask.setStartTime(System.currentTimeMillis() + 60);
        missionListTask.setStopTime(System.currentTimeMillis() + 600);
        missionListTask.setState(MissionStateInit);
        missionListTask.setCreatedBy(System.currentTimeMillis());
        missionListTask.setCreateTime(new Date());
        missionListTask.setStoreId(order.getStoreId());

        List<MissionTask> missionTasks = new ArrayList<>();
        missionListTask.setMissionTasks(missionTasks);

        //顺序遍历，添加任务
        if (mapPoints != null &&
                mpAttrs != null){
            for (MapPoint mp :
                    mapPoints) {
                if (mp != null) {
                    //根据地点及其属性，添加任务
                    initMissionTask(missionListTask,
                            order,
                            mp,
                            mpAttrs.get(mp));
                }
            }
        }
    }

    /**
     * 根据地点及其属性，添加任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTask(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {
        //必须要有点属性对象，否则无法判断当前点是什么节点，如何加入前后置任务
        if (mPointAtts != null &&
                !StringUtil.isEmpty(mPointAtts.type)){
            //根据属性实例化任务
            switch (mPointAtts.type){
                case MPointType_SONGHUO:
                    initMissionTaskSongHuo(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_QUHUO:
                    initMissionTaskQuHuo(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_XIAHUO:
                    initMissionTaskXiaHuo(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_CHONGDIAN:
                    //暂时取消充电任务
//                    initMissionTaskChongDian(
//                            missionListTask,
//                            order,
//                            mp,
//                            mPointAtts
//                    );
                    break;
                case MPointType_ELEVATOR:
                    initMissionTaskElevator(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_DOOR:
                    initMissionTaskDoor(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    break;
                    default:
                        break;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  任务相关方法
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Autowired
    DoorService doorService;

    /**
     * 实例化门任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskDoor(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskDoor ");

        String parentName = "门任务-";

        //单点路径导航任务，当前路径导航到门任务等待点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //查询门任务关联数据加入门任务
        List<Door> doors =
                doorService.listByWaitPoint(mp.getId(),
                        100L,
                        null);
        if (doors != null){
            for (Door door :
                    doors) {
                if (door != null) {
                    JsonMissionItemDataDoor obj =
                            new JsonMissionItemDataDoor();
                    obj.setWaitTime(5L);
                    MissionTask doorTask = null;
                    JsonMissionItemDataLaserNavigation point = null;
                    //添加门任务
                    if (door.getDoorOrderType() != null){
                        switch (door.getDoorOrderType()){
                            case DoorType_door:

                                point =
                                        new JsonMissionItemDataLaserNavigation();
                                point.setX(door.getoPoint().getX());
                                point.setY(door.getoPoint().getY());
                                point.setTh(door.getoPoint().getTh());
                                point.setMap_name(door.getoPoint().getMapName());
                                point.setScene_name(door.getoPoint().getSceneName());
                                point.setMap(door.getoPoint().getMapName());
                                obj.setPoint(point);

                                doorTask = getDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                            case DoorType_laneDoor:

                                point =
                                        new JsonMissionItemDataLaserNavigation();
                                point.setX(door.getoPoint().getX());
                                point.setY(door.getoPoint().getY());
                                point.setTh(door.getoPoint().getTh());
                                point.setMap_name(door.getoPoint().getMapName());
                                point.setScene_name(door.getoPoint().getSceneName());
                                point.setMap(door.getoPoint().getMapName());
                                obj.setPoint(point);

                                doorTask = getLaneDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                            case DoorType_pathDoor:
                                JsonMissionItemDataDoor.Path path =
                                        new JsonMissionItemDataDoor.Path();
                                path.setId(Long.parseLong(door.getPathId()));
                                path.setScene_name(door.getoPoint().getSceneName());
                                path.setMap_name(door.getoPoint().getMapName());
                                obj.setPath(path);

                                doorTask = getPathDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                                default:
                                    break;
                        }
                    }
                    missionListTask.getMissionTasks().add(doorTask);

                    break;
                }
            }
        }


    }

    @Autowired
    ElevatorService elevatorService;

    /**
     * 实例化电梯任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskElevator(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskElevator ");

        String parentName = "电梯任务-";

        Long elevatorid = null;
        //电梯任务，发送进入电梯到第几层
        JsonMissionItemDataElevator jsonMissionItemDataElevator =
                new JsonMissionItemDataElevator();
        if (mPointAtts != null){
            List<Elevator> preElevator = elevatorService.findByMapFloor(
                    mPointAtts.currentMapId,
                    mPointAtts.currentFloor,
                    mp);
            jsonMissionItemDataElevator.setArrival_floor(mPointAtts.nextFloor);
            jsonMissionItemDataElevator.setLogic_floor(mPointAtts.logicFloor);
            jsonMissionItemDataElevator.setCurrent_floor(mPointAtts.currentFloor);
            if (preElevator != null){
                for (Elevator ev :
                        preElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                //等待点导航任务

                                //单点路径导航任务，当前路径导航到电梯等待点,测试门任务的时候，不需要单点导航了
                                MissionTask sigleNavTask = getSigleNavTask(
                                        order, epc.getwPoint(), parentName);
                                missionListTask.getMissionTasks().add(sigleNavTask);
                                //加入check电梯状态任务
//                                JsonMissionItemDataElevatorLock lock =
//                                        new JsonMissionItemDataElevatorLock();
//                                lock.setElevator_id(ev.getId());
//                                lock.setInterval_time(30);
//                                MissionTask elevatorLockTask = getElevatorLockTask(
//                                        order, epc.getwPoint(), parentName,
//                                        lock
//                                );
//                                missionListTask.getMissionTasks().add(elevatorLockTask);
                                elevatorid = ev.getId();

                                jsonMissionItemDataElevator
                                        .setEnter_point(
                                                changeToPoint(epc.getiPoint())
                                        );
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            List<Elevator> nextElevator = elevatorService.findByMapFloor(
                    mPointAtts.nextMapId,
                    mPointAtts.nextFloor,
                    mp);
            if (nextElevator != null){
                for (Elevator ev :
                        nextElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                jsonMissionItemDataElevator
                                        .setSet_pose_point(
                                                changeToPoint(epc.getiPoint())
                                        );
                                jsonMissionItemDataElevator
                                        .setBack_point(
                                                changeToPoint(epc.getoPoint())
                                        );
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }else{
            logger.error("没有获取到电梯到达的楼层，请注意查看地图是否配置了楼层数据，或者电梯点后续是否没有设置到达点！");
        }



        //电梯任务
        MissionTask elevatorTask = getElevatorTask(
                order,
                mp,
                parentName,
                jsonMissionItemDataElevator);
        missionListTask.getMissionTasks().add(elevatorTask);

        //加入check电梯状态解锁任务
//        JsonMissionItemDataElevatorUnlock unlock =
//                new JsonMissionItemDataElevatorUnlock();
//        unlock.setElevator_id(elevatorid);
//        unlock.setInterval_time(30);
//        MissionTask elevatorUnlockTask = getElevatorUnlockTask(
//                order, mp, parentName,
//                unlock
//        );
//        missionListTask.getMissionTasks().add(elevatorUnlockTask);

    }

    private JsonMissionItemDataElevator.Point changeToPoint(MapPoint mapPoint){
        JsonMissionItemDataElevator.Point point= new JsonMissionItemDataElevator.Point();
        point.setMap_name(mapPoint.getMapName());
        point.setPoint_name(mapPoint.getPointName());
        point.setScene_name(mapPoint.getSceneName());
        point.setTh(mapPoint.getTh());
        point.setX(mapPoint.getX());
        point.setY(mapPoint.getY());
        return point;
    }


    /**
     * 实例化充电任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskChongDian(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskChongDian ");

        String parentName = "充电任务-";

        //单点路径导航任务，当前路径导航到充电点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        missionListTask.getMissionTasks().add(sigleNavTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_CHARGE);
        missionListTask.getMissionTasks().add(mp3loadTask);

        //自动充电任务,会自动导航
        MissionTask gotochargeTask = getGotoChargeTask(order, mp, parentName);
//        gotochargeTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_CHARGE));

        missionListTask.getMissionTasks().add(gotochargeTask);
    }

    /**
     * 实例化返回卸载货架任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskXiaHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskXiaHuo ");

        String parentName = "卸货任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //单点导航任务，回到下货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        if (isSetOrderDetailMP){
            sigleNavTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务，等待货架取下（同时语音提示我回来了，请取下货箱？）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_CABINET);
        if (isSetOrderDetailMP){
            mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3loadTask);

        //终点卸货任务,目前先代替等待任务
        MissionTask finalUnloadTask = getFinalUnloadTask(order, mp, parentName, mPointAtts.orderDetailMP);
//        finalUnloadTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
        if (isSetOrderDetailMP){
            finalUnloadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(finalUnloadTask);

        //语音任务，感谢使用，我要回去充电了？
//        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
//        missionListTask.getMissionTasks().add(voiceTask);

    }


    /**
     * 实例化取货装货任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskQuHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskQuHuo ");

        String parentName = "取货任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //离开充电任务
//        MissionTask leavechargeTask = getLeaveChargeTask(order, mp, parentName);
//        missionListTask.getMissionTasks().add(leavechargeTask);

        //添加单点导航任务,导航到取货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        if (isSetOrderDetailMP){
            sigleNavTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(sigleNavTask);

        //到达，等待任务（同时语音播报，请放上货箱？）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_CABINET);
        if (isSetOrderDetailMP){
            mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3loadTask);

        //load任务，取代等待任务
        MissionTask loadTask = getLoadTask(order, mp, parentName, mPointAtts.orderDetailMP);
//        loadTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_LOAD));
        if (isSetOrderDetailMP){
            loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(loadTask);

        //装载完毕语音任务
        MissionTask mp3loadFinishTask = getMp3VoiceTask(order, mp, parentName, MP3_LOAD_FINISH);
        if (isSetOrderDetailMP){
            mp3loadFinishTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3loadFinishTask);

        //语音任务，我要出发了？
//        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
//        missionListTask.getMissionTasks().add(voiceTask);

    }


    /**
     * 实例化送货任务
     * 这里的missionTask都要设置orderDetailMission为1
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initMissionTaskSongHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initMissionTaskSongHuo ");

        String parentName = "中间送货站点任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //单点导航任务，导航到目标送货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        if (isSetOrderDetailMP){
            sigleNavTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务（同时语音提示，物品已经送达，请查收）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_CABINET);
        if (isSetOrderDetailMP){
            mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3loadTask);

        MissionTask mp3SignTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_MEDICINE_SIGN);
        if (isSetOrderDetailMP){
            mp3SignTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3SignTask);

        //卸货任务，取代等待任务
        MissionTask unloadTask = getUnloadTask(order, mp, parentName, mPointAtts.orderDetailMP);
        if (isSetOrderDetailMP){
            unloadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
//        unloadTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));

        missionListTask.getMissionTasks().add(unloadTask);

        //语音任务，感谢使用，我要出发了，再见？
//        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
//        missionListTask.getMissionTasks().add(voiceTask);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  测试任务及任务item相关方法
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 获取测试任务
     * @param mp
     *@param parentName @return
     */
    private MissionTask getTestTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "测试任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getTestItemTask(order, mp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取测试ITEM任务
     * @param mp
     * @param parentName
     * @return
     */
    private MissionItemTask getTestItemTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "测试Item");
        itemTask.setName(MissionItemName_test);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataFake fake = new JsonMissionItemDataFake();
        fake.setId(0L);
        fake.setX(0L);
        fake.setY(0L);
        fake.setZ(0L);
        fake.setMapName("agv");
        fake.setSceneName("agv");
        itemTask.setData(JsonUtils.toJson(fake,
                new TypeToken<JsonMissionItemDataFake>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_test);

        return itemTask;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  任务及任务item相关方法
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取单点导航任务
     * @param mp
     * @return
     */
    private MissionTask getSigleNavTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "单点导航任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getSigleNavItemTask(order, mp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取单点导航Item任务
     * @param mp
     * @return
     */
    private MissionItemTask getSigleNavItemTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "单点导航Item");
        itemTask.setName(MissionItemName_nav);
        //这里就是单点导航的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataLaserNavigation data =
                new JsonMissionItemDataLaserNavigation();
        data.setX(mp.getX());
        data.setY(mp.getY());
        data.setTh(mp.getTh());
        data.setMap_name(mp.getMapName());
        data.setMap(mp.getMapName());
        data.setScene_name(mp.getSceneName());
        itemTask.setData(JsonUtils.toJson(data,
                new TypeToken<JsonMissionItemDataLaserNavigation>() {
                }.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_nav);

        return itemTask;
    }

    /**
     * 获取固定路径导航任务，如果路径不存在，则用单点导航任务代替
     * @param startMp 起点
     * @param endMp 结束点
     * @return
     */
    private MissionTask getPathNavTask(
            Order order,
            MapPoint startMp,
            MapPoint endMp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "优先固定路径导航任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getPathNavItemTask(order, startMp, endMp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取固定路径导航Item任务，如果路径不存在，则用单点导航任务代替
     * @param startMp 起点
     * @param endMp 结束点
     * @return
     */
    private MissionItemTask getPathNavItemTask(
            Order order,
            MapPoint startMp,
            MapPoint endMp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        try {
            List<RoadPath> roadPaths = null;

            //当初始点不传的时候，则用单点导航任务，如果传初始点则进行固定路径导航的流程判断
            if(startMp != null) {
                roadPaths = roadPathService.listRoadPathByStartAndEndPoint(
                        startMp.getId(),
                        endMp.getId(),
                        endMp.getSceneName(),
                        null,
                        Constant.PATH_TYPE_X86
                );

                //如果查找到路径则用固定路径导航任务
                if(roadPaths != null
                        && roadPaths.size() >0
                        && roadPaths.get(0).getPathId() != null) {
                    //TODO 如果有多条需要根据条件过滤掉取一条，比如权值最高等
                    RoadPath roadPath = roadPaths.get(0);
                    itemTask.setDescription(parentName + "固定路径导航Item");
                    itemTask.setName(MissionItemName_path_nav);
                    //这里就是固定路径导航的数据格式存储地方,根据mp和数据格式定义来创建
                    JsonMissionItemDataPathNavigation data =
                            new JsonMissionItemDataPathNavigation();
                    data.setId(Long.parseLong(roadPath.getPathId()));
                    data.setScene_name(endMp.getSceneName());
                    data.setTolerance_type(roadPath.getX86PathType());
                    data.setMap_name(endMp.getMapName());
                    itemTask.setData(JsonUtils.toJson(data,
                            new TypeToken<JsonMissionItemDataPathNavigation>(){}.getType()));
                    itemTask.setState(MissionStateInit);
                    itemTask.setFeatureValue(FeatureValue_nav);
                    return itemTask;
                }
            }

            //如果没查找到路径则用单点导航任务
            itemTask.setDescription(parentName + "单点导航Item");
            itemTask.setName(MissionItemName_nav);
            //这里就是单点导航的数据格式存储地方,根据mp和数据格式定义来创建
            JsonMissionItemDataLaserNavigation data =
                    new JsonMissionItemDataLaserNavigation();
            data.setX(endMp.getX());
            data.setY(endMp.getY());
            data.setTh(endMp.getTh());
            data.setMap_name(endMp.getMapName());
            data.setMap(endMp.getMapName());
            data.setScene_name(endMp.getSceneName());
            itemTask.setData(JsonUtils.toJson(data,
                    new TypeToken<JsonMissionItemDataLaserNavigation>(){}.getType()));
            itemTask.setState(MissionStateInit);
            itemTask.setFeatureValue(FeatureValue_nav);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return itemTask;
    }



    /**
     * 获取固定路径导航任务
     * @param mp
     * @param mPointAtts
     * @return
     */
    private MissionTask getStaticPathTask(
            Order order,
            MapPoint mp,
            String parentName,
            MPointAtts mPointAtts) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "进入固定路径导航任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getStaticPathItemTask(order, mp, parentName, mPointAtts));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取进入固定路径导航ITEM任务
     * @param mp
     * @param mPointAtts
     * @return
     */
    private MissionItemTask getStaticPathItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            MPointAtts mPointAtts) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "进入固定路径导航Item");
        itemTask.setName(MissionItemName_path_nav);
        //这里就是固定路径导航的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataPathNavigation data =
                new JsonMissionItemDataPathNavigation();
        //工控路径ID
        Long x86RoadPathId = Long.parseLong(mPointAtts.pathId);
        data.setId(x86RoadPathId);
        String sceneName = mp.getSceneName();
        String mapName = mp.getMapName();
        RoadPath roadPath = roadPathService.findBySceneAndX86RoadPathId(x86RoadPathId,sceneName,mapName);
        if(roadPath == null) {
            logger.error("###find roadPath error###,x86RoadPathId: {}, sceneName: {} roadPath not found!!" , x86RoadPathId,sceneName);
        }
        if(roadPath != null) {
            data.setTolerance_type(roadPath.getX86PathType());
        }
        data.setScene_name(sceneName);
        data.setMap_name(mapName);
        itemTask.setData(JsonUtils.toJson(data,
                new TypeToken<JsonMissionItemDataPathNavigation>() {
                }.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_nav);
        return itemTask;
    }

    /**
     * 获取等待任务
     * @param mp
     * @return
     */
    private MissionTask getWaitingTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "等待任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getWaitingItemTask(order, mp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取等待Item任务
     * @param mp
     * @return
     */
    private MissionItemTask getWaitingItemTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "等待Item");
        itemTask.setName(MissionItemName_waiting);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(NULL_JSON_OBJ);
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_waiting);
        return itemTask;
    }

    /**
     * 获取语音任务
     * @param mp
     * @param fileName
     * @return
     */
    private MissionTask getMp3VoiceTask(
            Order order,
            MapPoint mp,
            String parentName,
            String fileName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "语音任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getMp3VoiceItemTask(order, mp, parentName, fileName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取语音ITEM任务
     * @param mp
     * @param fileName
     * @return
     */
    private MissionItemTask getMp3VoiceItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            String fileName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "语音Item");
        itemTask.setName(MissionItemName_mp3);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataMp3 json =
                new JsonMissionItemDataMp3();
        json.setFilename(fileName);
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(fileName);
        json.setFilenames(filenames);
        json.setResscene( StringUtil.isEmpty(order.getResscene()) ? "default":order.getResscene());
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataMp3>() {
                }.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_mp3);

        return itemTask;
    }

    /**
     * 获取列表语音ITEM任务
     * @param mp
     * @param fileNames
     * @return
     */
    private MissionItemTask getListMp3VoiceItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            List<String> fileNames) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "列表语音Item");
        itemTask.setName(MissionItemName_mp3);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataMp3 json =
                new JsonMissionItemDataMp3();
        json.setFilenames(fileNames);
        json.setResscene(StringUtil.isEmpty(order.getResscene()) ? "default":order.getResscene());
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataMp3>() {
                }.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_mp3);

        return itemTask;
    }

    /**
     * 获取进入充电任务
     * @param mp
     * @return
     */
    private MissionTask getGotoChargeTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "进入充电任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getGotoChargeItemTask(order, mp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取进入充电ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getGotoChargeItemTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "进入充电Item");
        itemTask.setName(MissionItemName_gotocharge);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataGotocharge json =
                new JsonMissionItemDataGotocharge();
        json.setX(mp.getX());
        json.setY(mp.getY());
        json.setTh(mp.getTh());
        json.setMap(mp.getMapName());
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataGotocharge>() {
                }.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_gotocharge);

        return itemTask;
    }

    /**
     * 获取离开充电任务
     * @param mp
     * @return
     */
    private MissionTask getLeaveChargeTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "离开充电任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getLeaveChargeItemTask(order, mp, parentName));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取离开充电ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getLeaveChargeItemTask(
            Order order,
            MapPoint mp,
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "离开充电Item");
        itemTask.setName(MissionItemName_leavecharge);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(NULL_JSON_OBJ);
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_leavecharge);

        return itemTask;
    }

    /**
     * 装货
     * @param mp
     * @return
     */
    private MissionTask getLoadTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "装货任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getLoadItemTask(order, mp, parentName, orderDetailMP));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取装货ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getLoadItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "装货Item");
        itemTask.setName(MissionItemName_load);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
//        JsonMissionItemDataLoad json =
//                new JsonMissionItemDataLoad();
//        json.setShelf(order.getShelf());
//        itemTask.setData(JsonUtils.toJson(json,
//                new TypeToken<JsonMissionItemDataLoad>(){}.getType()));
        //查取 工号可用列表
        OrderDetail currentOrderDetail = null;
        List<String> employee_num_list = null;
        if (!StringUtil.isNullOrEmpty(orderDetailMP) && !str_zero.equalsIgnoreCase(orderDetailMP)){
            Long id = Long.valueOf(orderDetailMP);
            if (id != null && order.getDetailList() != null){
                for (OrderDetail de : order.getDetailList()) {
                    if (de != null && Objects.equals(id, de.getId())){
                        currentOrderDetail = de;
                        break;
                    }
                }
            }
        }
        if(currentOrderDetail!= null){
             employee_num_list = employeeService.listAvailableEmployees(currentOrderDetail.getStationId(),Constant.EMPLOYEE_TYPE_NORMAL);
        }
        if (order.getShelf() != null){
            Shelf shelf = order.getShelf();
            shelf.setEmployee_num_list(employee_num_list);
            itemTask.setData(JsonUtils.toJson(shelf,
                    new TypeToken<Shelf>(){}.getType()));
        }else{
            JsonMissionItemDataEmployee jsonMissionItemDataEmployee = new JsonMissionItemDataEmployee();
            jsonMissionItemDataEmployee.setEmployee_num_list(employee_num_list);
            itemTask.setData(JsonUtils.toJson(jsonMissionItemDataEmployee, new TypeToken<JsonMissionItemDataEmployee>(){}.getType()));
        }
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_load);

        return itemTask;
    }

    /**
     * 无货架装货
     * @param mp
     * @return
     */
    private MissionTask getLoadNoShelfTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "无货架装货任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getLoadNoShelfItemTask(order, mp, parentName, orderDetailMP));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取无货架装货ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getLoadNoShelfItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "无货架装货Item");
        itemTask.setName(MissionItemName_loadNoShelf);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        OrderDetail currentOrderDetail = null;
        List<String> employee_num_list = null;
        if (!StringUtil.isNullOrEmpty(orderDetailMP) && !str_zero.equalsIgnoreCase(orderDetailMP)){
            Long id = Long.valueOf(orderDetailMP);
            if (id != null && order.getDetailList() != null){
                for (OrderDetail de : order.getDetailList()) {
                    if (de != null && Objects.equals(id, de.getId())){
                        currentOrderDetail = de;
                        break;
                    }
                }
            }
        }
        if(currentOrderDetail!= null){
            employee_num_list = employeeService.listAvailableEmployees(currentOrderDetail.getStationId(),Constant.EMPLOYEE_TYPE_NORMAL);
        }
        JsonMissionItemDataEmployee jsonMissionItemDataEmployee = new JsonMissionItemDataEmployee();
        jsonMissionItemDataEmployee.setEmployee_num_list(employee_num_list);
        itemTask.setData(JsonUtils.toJson(jsonMissionItemDataEmployee, new TypeToken<JsonMissionItemDataEmployee>(){}.getType()));

        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_loadNoShelf);

        return itemTask;
    }

    /**
     * 卸货
     * @param mp
     * @param orderDetailMP
     * @return
     */
    private MissionTask getUnloadTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "卸货任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getUnloadItemTask(order, mp, parentName, orderDetailMP));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取卸货ITEM任务
     * @param mp
     * @param orderDetailMP
     * @return
     */
    private MissionItemTask getUnloadItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "卸货Item");
        itemTask.setName(MissionItemName_unload);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        JsonMissionItemDataUnload json =
                new JsonMissionItemDataUnload();
        if (!StringUtil.isNullOrEmpty(orderDetailMP) &&
                !str_zero.equalsIgnoreCase(orderDetailMP)){
            Long id = Long.valueOf(orderDetailMP);

            if (id != null && order.getDetailList() != null){
                for (OrderDetail de :
                        order.getDetailList()) {
                    if (de != null &&
                            Objects.equals(id, de.getId())){
                        //判定是否需要签收
                        Boolean needSign = order.getOrderSetting().getNeedSign();
                        json.setSign_in_mode(needSign ? OrderConstant.ORDER_NEED_SIGN_YES : OrderConstant.ORDER_NEED_SIGN_NO);
                        //可校验的员工列表
                        List<String> employeeCodeList = employeeService.listAvailableEmployees(de.getStationId(), Constant.EMPLOYEE_TYPE_NORMAL);
                        json.setEmployee_num_list(employeeCodeList);
                        //填充货物信息
                        json.setGoodsInfos(de.getGoodsInfoList());
                        break;
                    }
                }
            }
        }

        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataUnload>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_unload);

        return itemTask;
    }

    /**
     * 终点卸货
     * @param mp
     * @return
     */
    private MissionTask getFinalUnloadTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "终点卸货任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getFinalUnloadItemTask(order, mp, parentName,orderDetailMP));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取终点卸货ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getFinalUnloadItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            String orderDetailMP) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "终点卸货Item");
        itemTask.setName(MissionItemName_finalUnload);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        OrderDetail currentOrderDetail = null;
        List<String> employee_num_list = null;
        if (!StringUtil.isNullOrEmpty(orderDetailMP) && !str_zero.equalsIgnoreCase(orderDetailMP)){
            Long id = Long.valueOf(orderDetailMP);
            if (id != null && order.getDetailList() != null){
                for (OrderDetail de : order.getDetailList()) {
                    if (de != null && Objects.equals(id, de.getId())){
                        currentOrderDetail = de;
                        break;
                    }
                }
            }
        }
        if(currentOrderDetail!= null){
            employee_num_list = employeeService.listAvailableEmployees(currentOrderDetail.getStationId(),Constant.EMPLOYEE_TYPE_NORMAL);
        }
        JsonMissionItemDataEmployee jsonMissionItemDataEmployee = new JsonMissionItemDataEmployee();
        jsonMissionItemDataEmployee.setEmployee_num_list(employee_num_list);
        itemTask.setData(JsonUtils.toJson(jsonMissionItemDataEmployee, new TypeToken<JsonMissionItemDataEmployee>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_finalUnload);

        return itemTask;
    }

    /**
     * 电梯
     * @param mp
     * @return
     */
    private MissionTask getElevatorTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevator json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "电梯任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getElevatorItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取电梯ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getElevatorItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevator json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "电梯Item");
        itemTask.setName(MissionItemName_elevator);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        List<String> employee_num_list = employeeService.listAvailableEmployees(null,Constant.EMPLOYEE_TYPE_ELEVATOR_ADMIN);
        json.setEmployee_num_list(employee_num_list);
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataElevator>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_elevator);

        return itemTask;
    }

    /**
     * 双电梯
     * @param mp
     * @return
     */
    private MissionTask getTwoElevatorTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataTwoElevator json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "双电梯任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getTwoElevatorItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取双电梯ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getTwoElevatorItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataTwoElevator json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "双电梯Item");
        itemTask.setName(MissionItemName_elevator);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        List<String> employee_num_list = employeeService.listAvailableEmployees(null,Constant.EMPLOYEE_TYPE_ELEVATOR_ADMIN);
        json.setEmployee_num_list(employee_num_list);
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataTwoElevator>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_elevator);

        return itemTask;
    }


    /**
     * 电梯解锁
     * @param mp
     * @return
     */
    private MissionTask getElevatorUnlockTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevatorUnlock json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "电梯解锁任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getElevatorUnlockItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取电梯解锁ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getElevatorUnlockItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevatorUnlock json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "电梯解锁Item");
        itemTask.setName(MissionItemName_elevator_unlock);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataElevatorUnlock>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_elevator_unlock);

        return itemTask;
    }

    /**
     * 电梯加锁
     * @param mp
     * @return
     */
    private MissionTask getElevatorLockTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevatorLock json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "电梯加锁任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getElevatorLockItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取电梯加锁ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getElevatorLockItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataElevatorLock json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "电梯加锁Item");
        itemTask.setName(MissionItemName_elevator_lock);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataElevatorLock>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_elevator_lock);

        return itemTask;
    }

    /**
     * door
     * @param mp
     * @return
     */
    private MissionTask getDoorTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "门任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getDoorItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取door ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getDoorItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "门Item");
        itemTask.setName(MissionItemName_door);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataDoor>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_door);

        return itemTask;
    }

    /**
     * laneDoor
     * @param mp
     * @return
     */
    private MissionTask getLaneDoorTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "laneDoor任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getLaneDoorItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取laneDoor ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getLaneDoorItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "laneDoor Item");
        itemTask.setName(MissionItemName_laneDoor);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataDoor>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_laneDoor);

        return itemTask;
    }

    /**
     * pathDoor
     * @param mp
     * @return
     */
    private MissionTask getPathDoorTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "pathDoor任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getPathDoorItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取pathDoor ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getPathDoorItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataDoor json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "pathDoor Item");
        itemTask.setName(MissionItemName_pathDoor);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataDoor>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_pathDoor);

        return itemTask;
    }



    /**
     * 路径解锁
     * @param mp
     * @return
     */
    private MissionTask getRoadPathUnlockTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataRoadPathUnlock json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "路径解锁任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getRoadPathUnlockItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取路径解锁ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getRoadPathUnlockItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataRoadPathUnlock json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "路径解锁Item");
        itemTask.setName(MissionItemName_roadpath_unlock);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataRoadPathUnlock>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_roadpath_unlock);

        return itemTask;
    }

    /**
     * 路径加锁
     * @param mp
     * @return
     */
    private MissionTask getRoadPathLockTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataRoadPathLock json) {
        MissionTask missionTask = new MissionTask();
        if (order.getScene() != null) {
            missionTask.setSceneId(order.getScene().getId());
        }
        missionTask.setDescription(parentName + "路径加锁任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState(MissionStateInit);
        missionTask.setPresetMissionCode("");

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getRoadPathLockItemTask(order, mp, parentName, json));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取路径加锁ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getRoadPathLockItemTask(
            Order order,
            MapPoint mp,
            String parentName,
            JsonMissionItemDataRoadPathLock json) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "路径加锁Item");
        itemTask.setName(MissionItemName_roadpath_lock);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataRoadPathLock>(){}.getType()));
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_roadpath_lock);

        return itemTask;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  missionItem,mission,missionList转成对应DTO和Task对象的方法
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 从MapPoint类，转换成激光导航的数据格式
     * @param mapPoint
     * @return
     */
    public JsonMissionItemDataLaserNavigation pointToJSONMissionItemDataLaserNavigation(MapPoint mapPoint) {
        JsonMissionItemDataLaserNavigation data1 = new JsonMissionItemDataLaserNavigation();
        data1.setX(mapPoint.getX());
        data1.setY(mapPoint.getY());
        data1.setTh(mapPoint.getTh());
        data1.setMap(mapPoint.getMapName());
        data1.setMap_name(mapPoint.getMapName());
        data1.setScene_name(mapPoint.getSceneName());
        return data1;
    }

    /**
     * 从MissionItem转成missionItemDTO
     * @return
     */
    public MissionItemDTO missionItemToDTO(MissionItem missionItem) {
        MissionItemDTO missionItemDTO = new MissionItemDTO();
        missionItemDTO.setId(missionItem.getId());
        FeatureItem featureItem = featureItemService.get(missionItem.getFeatureItemId());
        missionItemDTO.setName(featureItem.getValue());
        missionItemDTO.setData(missionItem.getData());
        return missionItemDTO;
    }

    /**
     * 从MissionItem转成missionItemTask
     * @param missionItem
     * @param mission
     * @param missionList
     * @return
     */
    public MissionItemTask missionItemToTask(MissionItem missionItem, Mission mission, MissionList missionList) {
        MissionItemTask missionItemTask = new MissionItemTask();
//        missionItemTask.setId(missionItem.getId());
        FeatureItem featureItem = featureItemService.get(missionItem.getFeatureItemId());
        missionItemTask.setFeatureValue(featureItem.getValue());
//        missionItemTask.setName(missionItem.getName()==null?"":missionItem.getName());
        missionItemTask.setName(featureItem.getValue() == null ? "" : featureItem.getValue());

        //只有导航相关的任务才需要转换点数据
        String data = missionItem.getData();
        Long featureItemId = missionItem.getFeatureItemId();
        if(Constant.ORDER_MAP_POINT_RELATE_LIST.contains(featureItemId)) {
            MapPoint mapPoint = JSON.parseObject(missionItem.getData(),MapPoint.class);
            //这里就是单点导航的数据格式存储地方,根据mp和数据格式定义来创建
            data = JSON.toJSONString(pointToJSONMissionItemDataLaserNavigation(mapPoint));
        }
        //带充电时长的数据结构
        else if(featureItemId.equals(Constant.ORDER_TIME_CHARGE_ID)) {
            JsonMissionItemDataTimeCharge timeCharge = JSON.parseObject(data,JsonMissionItemDataTimeCharge.class);
            JsonMissionItemDataLaserNavigation point = timeCharge.getPoint();
            //给兼容老的导航数据格式字段map赋值
            if(point.getMap() == null || "".equals(point.getMap())) {
                timeCharge.getPoint().setMap(timeCharge.getPoint().getMap_name());
            }
            data = JSON.toJSONString(timeCharge);
        }

        missionItemTask.setData(data);
        missionItemTask.setDescription(featureItem.getDescription()==null?"":featureItem.getDescription());
        missionItemTask.setSceneId(missionList.getSceneId());
        missionItemTask.setCreatedBy(missionItem.getCreatedBy());
        missionItemTask.setCreateTime(new Date());
        missionItemTask.setMissionId(mission.getId());
        missionItemTask.setMissionListId(missionList.getId());
        //保存missionItemTask到数据库
//        missionItemTaskService.save(missionItemTask);
        return missionItemTask;
    }

    /**
     * 从mission转成missionDTO
     * @return
     */
    public MissionDTO missionToDTO(Mission mission) {
        MissionDTO missionDTO = new MissionDTO();
        missionDTO.setId(mission.getId());
        missionDTO.setIntervalTime(mission.getIntervalTime());
        missionDTO.setRepeatCount(mission.getRepeatCount());
        Set<MissionItem> missionItems = mission.getMissionItemSet();
        if(missionItems != null) {
            Set<MissionItemDTO> missionItemDTOS = new HashSet<MissionItemDTO>();
            for(MissionItem missionItem : missionItems) {
                missionItemDTOS.add(missionItemToDTO(missionItem));
            }
            missionDTO.setMissionItemSet(missionItemDTOS);
        }
        return missionDTO;
    }

    /**
     * 从mission转成missionTask
     * @param mission
     * @param missionList
     * @return
     */
    public MissionTask missionToTask(Mission mission, MissionList missionList) {
        Long missionId = mission.getId();
        MissionTask missionTask = new MissionTask();
//        missionTask.setId(mission.getId());
        missionTask.setIntervalTime(mission.getIntervalTime());
        missionTask.setRepeatTimes(mission.getRepeatCount());
        Set<MissionItem> missionItems = mission.getMissionItemSet();
        if(missionItems != null) {
            List<MissionItemTask> missionItemTasks = new ArrayList<MissionItemTask>();
            for(MissionItem missionItem : missionItems) {
                missionItemTasks.add(missionItemToTask(missionItem,mission, missionList));
            }
            missionTask.setMissionItemTasks(missionItemTasks);
        }
        missionTask.setDescription(mission.getDescription() == null ? "" : mission.getDescription());
        missionTask.setName(mission.getName() == null ? "" : mission.getName());
        missionTask.setSceneId(missionList.getSceneId());
        missionTask.setCreatedBy(mission.getCreatedBy());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(mission.getStoreId());
        //保存missionTask到数据库
//        missionTaskService.save(missionTask);
        return missionTask;
    }

    /**
     * 从MissionList转化到MissionListDTO对象
     * @return
     */
    public MissionListDTO missionListToDTO(MissionList missionList) {
        MissionListDTO missionListDTO = new MissionListDTO();
        missionListDTO.setId(missionList.getId());
        missionListDTO.setIntervalTime(missionList.getIntervalTime());
        //从我们的类型转换成任务管理器可识别的类型
        missionListDTO.setMissionListType(MissionListTypeEnum.getDtoCaption(missionList.getMissionListType()));
        missionListDTO.setPriority(missionList.getPriority());
        missionListDTO.setRepeatCount(missionList.getRepeatCount());
        missionListDTO.setStartTime(missionList.getStartTime());
        missionListDTO.setStopTime(missionList.getStopTime());
        List<Mission> missions = missionList.getMissionList();
        if(missions != null) {
            List<MissionDTO> missionDTOS = new ArrayList<MissionDTO>();
            for(Mission mission : missions) {
                missionDTOS.add(missionToDTO(mission));
            }
            missionListDTO.setMissionList(missionDTOS);
        }
        return missionListDTO;
    }

    /**
     * 从MissionList转化到MissionListTask对象
     * @param missionList
     * @param robotCode
     * @return
     */
    public MissionListTask missionListToTask(MissionList missionList,String robotCode) {
        Long missionListId = missionList.getId();
        MissionListTask missionListTask = new MissionListTask();
//        missionListTask.setId(missionList.getId());
        missionListTask.setIntervalTime(missionList.getIntervalTime());
        //从我们的类型转换成任务管理器可识别的类型
        missionListTask.setMissionListType(MissionListTypeEnum.getDtoCaption(missionList.getMissionListType()));
        missionListTask.setPriority(missionList.getPriority());
        missionListTask.setRepeatTimes(missionList.getRepeatCount());
        missionListTask.setStartTime(missionList.getStartTime());
        missionListTask.setStopTime(missionList.getStopTime());
        List<Mission> missions = missionList.getMissionList();
        if(missions != null) {
            List<MissionTask> missionTasks = new ArrayList<MissionTask>();
            for(Mission mission : missions) {
                missionTasks.add(missionToTask(mission,missionList));
            }
            missionListTask.setMissionTasks(missionTasks);
        }
        missionListTask.setDescription(missionList.getDescription()==null?"":missionList.getDescription());
        String missionListName = missionList.getName();
        missionListTask.setName(missionListName==null?"":missionListName);
        missionListTask.setRobotCode(robotCode);
        missionListTask.setSceneId(missionList.getSceneId());
        missionListTask.setStoreId(missionList.getStoreId());
        missionListTask.setCreatedBy(missionList.getCreatedBy());
        missionListTask.setCreateTime(new Date());
        missionListTask.setOrderId(Constant.MANUAL_ORDER_ID);
        missionListTask.setPriority(missionList.getPriority()==null?0:missionList.getPriority());
        //保存missionListTask对象到数据库
//        missionListTaskService.save(missionListTask);
        return missionListTask;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  导航用固定路径导航接口相关
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 根据订单数据创建任务列表,导航优先用固定路径导航
     * @param order
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public AjaxResult createMissionListsPathNav(Order order) {

        logger.info("##############  createMissionListsPathNav #################");

        //判断order
        if (order == null ||
                order.getOrderSetting() == null ||
                order.getRobot() == null ||
                order.getOrderSetting().getStartStation() == null ||
                order.getOrderSetting().getEndStation() == null){
            logger.info("##############  createMissionListsPathNav attrs error #################");
        }

        //定义地图点的集合
        List<MapPoint> mapPoints = new ArrayList<>();
        //定义地图点对应属性的map
        HashMap<MapPoint, MPointAtts> mpAttrs = new HashMap<>();

        //根据订单信息搜集任务点
        collectPathMapPoints(order, mapPoints, mpAttrs);

        //根据任务点及其属性完善任务列表
        MissionListTask missionListTask = new MissionListTask();

        //根据任务点，实例化任务列表
        initPathMissionListTask(missionListTask, order, mapPoints, mpAttrs);

        //任务列表实例化完成，将数据存储到数据库
        saveMissionListTask(missionListTask);

        //下发任务到机器人任务管理器
        List<MissionListTask> listTasks =
                new ArrayList<>();
        listTasks.add(missionListTask);
        AjaxResult ajaxResult = x86MissionDispatchService.sendX86MissionDispatch(
                order.getRobot().getCode(),
                getGoorMissionMsg(listTasks)
        );
        if(ajaxResult == null || !ajaxResult.isSuccess()){
            logger.info("##############  createPathMissionLists failed ，发送客户端goor失败#################");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ajaxResult == null ? AjaxResult.failed("客户端无法连接，订单失败") : ajaxResult;
        }else {
            logger.info("robot code is: " + order.getRobot().getCode() +
                    " , ####### tesk is: " + getGoorMissionMsg(listTasks));
            logger.info("##############  createPathMissionLists successed #################");
            return ajaxResult;
        }
    }

    /**
     * 根据订单信息搜集任务点
     * @param order
     * @param mapPoints
     * @param mpAttrs
     */
    private void collectPathMapPoints(
            Order order,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {

        logger.info("### order is: " + JsonUtils.toJson(order, new TypeToken<Order>(){}.getType()));

        MPointAtts atts;

        //查询充电点列表
        List<MapPoint> chongMPs = robotService
                .getChargerMapPointByRobotCode(order.getRobot().getCode(), SearchConstants.FAKE_MERCHANT_STORE_ID);
        MapPoint chargePoint = null;
        if (chongMPs != null){
            for (MapPoint mp :
                    chongMPs) {
                if (mp != null) {
                    chargePoint = mp;
                    break;
                }
            }
        }

        //判断中间站点，如果有中间站点，添加中间站点的地图点，如果中间点跨楼层，则要在两个任务中间插入电梯任务
        if (order.getDetailList() != null){
            for (OrderDetail od :
                    order.getDetailList()) {
                if (od != null &&
                        od.getStationId() != null) {
                    logger.info("###### begin get order detail station ");
                    if(Objects.equals(od.getPlace(), OrderConstant.ORDER_DETAIL_PLACE_START)){
                        //首先插入起点
                        Long stationId = order.getOrderSetting().getStartStation().getId();
                        MapPoint startPoint = pointService.findMapPointByStationIdAndCloudType(stationId, MapPointType.LOAD.getCaption());

                        //判断充电点和起点的关系，加入相关任务
                        if (chargePoint != null){
                            prePoint = chargePoint;
                        }else{
                            prePoint = null;
                        }
                        //首先判断当前点和前一个点的关系，判断是否需要加入电梯任务
                        addPathRoadPathPoint(startPoint, mapPoints, mpAttrs);

                        mapPoints.add(startPoint);
                        //设置属性
                        atts = new MPointAtts();
                        atts.type = MPointType_QUHUO;

                        atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                        mpAttrs.put(startPoint, atts);
                        if (prePoint == null){
                            prePoint = startPoint;
                        }
                        logger.info("###### quhuo is ok ");
                    }else if(Objects.equals(od.getPlace(), OrderConstant.ORDER_DETAIL_PLACE_END)){
                        Long endStationId = order.getOrderSetting().getEndStation().getId();
                        MapPoint endPoint = pointService.findMapPointByStationIdAndCloudType(endStationId, MapPointType.FINAL_UNLOAD.getCaption());
                        if (endPoint != null) {
                            logger.info("### end point ####  mpname: " + endPoint.getMapName() +
                                    ", scenename: " + endPoint.getSceneName() +
                                    ", pointname: " + endPoint.getPointName());
                        }

                        //判断添加电梯点
                        addPathRoadPathPoint(endPoint, mapPoints, mpAttrs);
                        //addElevatorPoint(order.getOrderSetting().getEndPoint(), mapPoints, mpAttrs);
                        //中间点添加完毕，添加卸货点
                        mapPoints.add(endPoint);
                        //设置属性
                        atts = new MPointAtts();
                        atts.type = MPointType_XIAHUO;
                        atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                        mpAttrs.put(endPoint, atts);
                        logger.info("###### xiahuo is ok ");
                    }else {

                        /*//取得站点对象
                        //TODO 以后AGV也需要按照切换的场景过滤
                        Station station = stationService.findById(od.getStationId(), od.getStoreId(),null);
                        if (station != null &&
                                station.getMapPoints() != null){
                            logger.info("###### get order detail station is ok， list size is: " + station.getMapPoints().size());
                            for (MapPoint mp :
                                    station.getMapPoints()) {
                                if (mp != null) {
                                    logger.info("mpname: " + mp.getMapName() + ", scenename: " + mp.getSceneName() + ", pointname: " + mp.getPointName());
                                }
                            }
                            //目前只取第一个坐标点加入
                            for (MapPoint mp :
                                    station.getMapPoints()) {
                                if(mp != null){
                                    //首先判断当前点和前一个点的关系，判断是否需要加入电梯任务
                                    addPathRoadPathPoint(mp, mapPoints, mpAttrs);
//                                addElevatorPoint(mp, mapPoints, mpAttrs);
                                    //判断当前点的属性，根据属性加入相应的任务
                                    //加入该点，并标记这个点状态是orderDetail点
                                    mapPoints.add(mp);
                                    //标记该点的属性
                                    atts = new MPointAtts();
                                    atts.type = MPointType_SONGHUO;
                                    atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                                    mpAttrs.put(mp, atts);
                                    logger.info("###### order detail station is ok ");
                                    break;
                                }
                            }
                        }*/

                        MapPoint songhuoPoint = pointService.findMapPointByStationIdAndCloudType(od.getStationId(), MapPointType.UNLOAD.getCaption());
                        if(songhuoPoint != null){
                            //首先判断当前点和前一个点的关系，判断是否需要加入电梯任务
                            addPathRoadPathPoint(songhuoPoint, mapPoints, mpAttrs);
//                                addElevatorPoint(mp, mapPoints, mpAttrs);
                            //判断当前点的属性，根据属性加入相应的任务
                            //加入该点，并标记这个点状态是orderDetail点
                            mapPoints.add(songhuoPoint);
                            //标记该点的属性
                            atts = new MPointAtts();
                            atts.type = MPointType_SONGHUO;
                            atts.orderDetailMP = String.valueOf(od.getId());//标记是orderdetail的点
                            mpAttrs.put(songhuoPoint, atts);
                            logger.info("###### order detail station is ok ");
                        }
                    }
                }
            }
        }

        //最后添加充电点，目前充电点从机器人的数据库里面查询出来
        //取第一个有效的点设置进去
        if (chargePoint != null){
            //如果充电点和上一个点不在同一个楼层，则要添加相应的电梯任务。
            addPathRoadPathPoint(chargePoint, mapPoints, mpAttrs);
            //添加充电点任务
            mapPoints.add(chargePoint);
            //设置属性
            atts = new MPointAtts();
            atts.type = MPointType_CHONGDIAN;
            mpAttrs.put(chargePoint, atts);
            logger.info("###### chongdian is ok ");
        }
    }

    /**
     * 判断新增路径任务
     * @param mp
     * @param mapPoints
     * @param mpAttrs
     */
    private void addPathRoadPathPoint(
            MapPoint mp,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {
        logger.info("###### addPathRoadPathPoint start ");
        //查询两个点之间是否有路径
        if (prePoint == null || mp == null){
            return;
        }

        try {
//            List<RoadPathDetail> roadPathDetails =
//                    roadPathService.findRoadPathByStartAndEndPoint(
//                            prePoint.getId(),
//                            mp.getId(),
//                            mp.getSceneName(),
//                            null
//                    );
            List<RoadPathDetail> roadPathDetails =
                    roadPathService.listRoadPathDetailByStartAndEndPointType(
                            prePoint.getId(),
                            mp.getId(),
                            mp.getSceneName(),
                            null,
                            Constant.PATH_TYPE_CLOUD
                    );
            //取第一个有效的进行任务插入
            MapPoint temp = null;
            if (roadPathDetails != null){
                for (RoadPathDetail rpd :
                        roadPathDetails) {
                    if (rpd != null &&
                            rpd.getStart() != null &&
                            rpd.getEnd() != null &&
                            rpd.getRelatePoints() != null) {
                        MapPoint prePoint = null;
                        for (MapPoint point :
                                rpd.getRelatePoints()) {
                            if (point != null){
                                switch (MapPointType.getType(point.getCloudMapPointTypeId())){
                                    case DOOR_WAIT:
                                        //添加门任务
                                        mapPoints.add(point);
                                        //标记该点的属性
                                        MPointAtts atts = new MPointAtts();
                                        atts.type = MPointType_DOOR;
                                        mpAttrs.put(point, atts);
                                        logger.info("###### addPathRoadPathPoint door is ok ");
                                        break;
                                    case ELEVATOR_WAIT:
                                        addPathElevatorPoint(mp, point, mapPoints, mpAttrs);
                                        break;
                                    default:
                                        if (prePoint != null){
                                            //使用上一个点和当前点查询是否工控路径，如果是工控路径，则添加工控导航任务
                                            addStaticPathPoint(prePoint, point, mp, mapPoints, mpAttrs);
                                        }
                                        break;
                                }
                                prePoint = point;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        prePoint = mp;
    }

    /**
     * 判断新增工控路径
     * @param prePoint
     * @param point
     * @param mp
     * @param mapPoints
     * @param mpAttrs
     */
    private void addStaticPathPoint(
            MapPoint prePoint,
            MapPoint point,
            MapPoint mp,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {
        try {
            List<RoadPath> roadPaths =
                    roadPathService.listRoadPathByStartAndEndPoint(
                            prePoint.getId(),
                            point.getId(),
                            prePoint.getSceneName(),
                            prePoint.getMapName(),
                            Constant.PATH_TYPE_X86
                    );
            if (roadPaths != null){
                for (RoadPath rp :
                        roadPaths) {
                    if (rp != null) {
                        MapPoint temp = new MapPoint();
                        MapPoint.copyValue(temp,prePoint);
                        temp.setSceneName(rp.getSceneName());
                        mapPoints.add(temp);
                        //标记该点的属性
                        MPointAtts atts = new MPointAtts();
                        atts.type = MPointType_STATIC_PATH;
                        atts.pathId = rp.getPathId();
                        atts.roadpathId = rp.getPathLock();//将路径锁对象id放入。
                        mpAttrs.put(temp, atts);
                        logger.info("###### addStaticPathPoint is ok ");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 判断新增电梯任务
     * @param finalTargetMp
     * @param currentMp
     * @param mapPoints
     * @param mpAttrs
     */
    private void addPathElevatorPoint(MapPoint finalTargetMp,
                                  MapPoint currentMp,
                                  List<MapPoint> mapPoints,
                                  HashMap<MapPoint, MPointAtts> mpAttrs) {
        if (prePoint == null || finalTargetMp == null){
            return;
        }
        MPointAtts prefloor = getMapPointFloor(prePoint);
        MPointAtts mpfloor = getMapPointFloor(finalTargetMp);
        if (prefloor == null ||
                prefloor.currentMapId == null ||
                prefloor.currentFloor == null ||
                mpfloor == null ||
                mpfloor.currentFloor == null ||
                mpfloor.currentMapId == null){
            return;
        }
        if (!prefloor.currentFloor.equals(mpfloor.currentFloor)){
            //楼层不一样，需要新增电梯任务
            MapPoint temp = new MapPoint();
            MapPoint.copyValue(temp,currentMp);
//            temp.setCloudMapPointTypeId(currentMp.getCloudMapPointTypeId());
//            temp.setDeleteFlag(currentMp.getDeleteFlag());
//            temp.setICPointType(currentMp.getICPointType());
//            temp.setLabel(currentMp.getLabel());
//            temp.setMapName(currentMp.getMapName());
//            temp.setMapPointTypeId(currentMp.getMapPointTypeId());
//            temp.setMapZipId(currentMp.getMapZipId());
//            temp.setPointAlias(currentMp.getPointAlias());
//            temp.setPointLevel(currentMp.getPointLevel());
//            temp.setPointName(currentMp.getPointName());
//            temp.setSceneName(currentMp.getSceneName());
//            temp.setStoreId(currentMp.getStoreId());
//            temp.setTh(currentMp.getTh());
//            temp.setX(currentMp.getX());
//            temp.setY(currentMp.getY());
            //
            mapPoints.add(temp);
            //标记该点的属性
            MPointAtts atts = new MPointAtts();
            atts.type = MPointType_ELEVATOR;
            atts.currentFloor = prefloor.currentFloor;
            atts.currentMapId = prefloor.currentMapId;
            atts.nextFloor = mpfloor.currentFloor;
            atts.nextMapId = mpfloor.currentMapId;
            atts.logicFloor = mpfloor.logicFloor;
            mpAttrs.put(temp, atts);
            logger.info("###### addPathElevatorPoint is ok ");
        }
//        prePoint = mp;
    }

    /**
     * 根据任务点，实例化任务列表
     * @param missionListTask
     * @param order
     * @param mapPoints
     * @param mpAttrs
     */
    private void initPathMissionListTask(
            MissionListTask missionListTask,
            Order order,
            List<MapPoint> mapPoints,
            HashMap<MapPoint, MPointAtts> mpAttrs) {
        if (missionListTask == null ||
                order == null){
            return;
        }
        //先初始化任务列表的相关属性
        if (order.getScene() != null) {
            missionListTask.setSceneId(order.getScene().getId());
        }
        missionListTask.setIntervalTime(0L);
        missionListTask.setDescription("下单自动任务列表"+System.currentTimeMillis());
        missionListTask.setMissionListType(MissionListType_normal);
        missionListTask.setName(missionListTask.getDescription());
        missionListTask.setOrderId(order.getId());
        missionListTask.setRepeatTimes(1);
        missionListTask.setPriority(0);
        missionListTask.setRobotCode(order.getRobot().getCode());
        missionListTask.setStartTime(System.currentTimeMillis() + 60);
        missionListTask.setStopTime(System.currentTimeMillis() + 600);
        missionListTask.setState(MissionStateInit);
        missionListTask.setCreatedBy(System.currentTimeMillis());
        missionListTask.setCreateTime(new Date());
        missionListTask.setStoreId(order.getStoreId());

        List<MissionTask> missionTasks = new ArrayList<>();
        missionListTask.setMissionTasks(missionTasks);

        //锁操作相关变量初始化
        RoadPathLockAtts roadPathLockAtts =
                new RoadPathLockAtts();

        //顺序遍历，添加任务
        if (mapPoints != null &&
                mpAttrs != null){
            for (MapPoint mp :
                    mapPoints) {
                if (mp != null) {
                    initPathMissionTask(missionListTask,
                            order,
                            null,
                            mp,
                            mpAttrs.get(mp),
                            roadPathLockAtts);
                }
            }
        }
    }

    /**
     * 根据地点及其属性，添加任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     * @param roadPathLockAtts
     */
    private void initPathMissionTask(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts,
            RoadPathLockAtts roadPathLockAtts) {
        //必须要有点属性对象，否则无法判断当前点是什么节点，如何加入前后置任务
        if (mPointAtts != null &&
                !StringUtil.isEmpty(mPointAtts.type)){
            //根据属性实例化任务
            switch (mPointAtts.type){
                case MPointType_SONGHUO:
                    initPathMissionTaskSongHuo(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_QUHUO:
                    //如果充电点属性不为null，则查询充电点到取货点的工控路径
                    if (mPointAtts.chargePoint != null){
                        initChargePointStaticPathPoint(mPointAtts.chargePoint,
                                mp,
                                missionListTask,
                                order);
                    }
                    initPathMissionTaskQuHuo(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_XIAHUO:
                    initPathMissionTaskXiaHuo(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts
                    );
                    //如果充电点属性不为null，则查询充电点到取货点的工控路径
                    if (mPointAtts.chargePoint != null){
                        initChargePointStaticPathPoint(mp,
                                mPointAtts.chargePoint,
                                missionListTask,
                                order);
                    }
                    break;
                case MPointType_CHONGDIAN:
                    //首先检索是否充电点和上一个站点之间是否有云端路径点序列，如果有，加入相关任务
                    //暂时取消充电任务
                    initPathMissionTaskChongDian(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_ELEVATOR:
//                    initPathMissionTaskElevator(
                    initPathMissionTaskTwoElevator(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts
                    );
                    break;
                case MPointType_DOOR:
                    initPathMissionTaskDoor(
                            missionListTask,
                            order,
                            startMp,
                            mp,
                            mPointAtts,
                            roadPathLockAtts
                    );
                    break;
                case MPointType_STATIC_PATH:
                    addRoadPathLockOrUnlock(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts,
                            roadPathLockAtts);
                    initPathMissionTaskStaticPath(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts);
                    break;
//                case MPointType_ROAD_PATH_LOCK:
//                    initPathMissionTaskRoadPathLock(
//                            missionListTask,
//                            order,
//                            mp,
//                            mPointAtts);
//                    break;
//                case MPointType_ROAD_PATH_UNLOCK:
//                    initPathMissionTaskRoadPathUnlock(
//                            missionListTask,
//                            order,
//                            mp,
//                            mPointAtts);
//                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断充电工控路径
     */
    private void initChargePointStaticPathPoint(
            MapPoint prePoint,
            MapPoint point,
            MissionListTask missionListTask,
            Order order) {
        try {
            List<RoadPath> roadPaths =
                    roadPathService.listRoadPathByStartAndEndPoint(
                            prePoint.getId(),
                            point.getId(),
                            prePoint.getSceneName(),
                            prePoint.getMapName(),
                            Constant.PATH_TYPE_X86
                    );
            if (roadPaths != null){
                for (RoadPath rp :
                        roadPaths) {
                    if (rp != null) {
                        MapPoint temp = new MapPoint();
                        MapPoint.copyValue(temp,prePoint);
                        temp.setSceneName(rp.getSceneName());
                        //标记该点的属性
                        MPointAtts atts = new MPointAtts();
                        atts.type = MPointType_STATIC_PATH;
                        atts.pathId = rp.getPathId();
                        atts.roadpathId = rp.getPathLock();//将路径锁对象id放入。
                        initPathMissionTaskStaticPath(
                                missionListTask,
                                order,
                                temp,
                                atts
                        );
                        logger.info("###### addChargePointStaticPathPoint is ok ");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 通过先后点，获取对应的工控路径
     */
    private JsonMissionItemDataTwoElevator.PathEntity getStaticPathPointPathEntity(
            String sceneName,
            String mapName,
            Long prePointid,
            Long nextPointid) {
        JsonMissionItemDataTwoElevator.PathEntity ret = null;
        try {
            List<RoadPath> roadPaths =
                    roadPathService.listRoadPathByStartAndEndPoint(
                            prePointid,
                            nextPointid,
                            sceneName,
                            mapName,
                            Constant.PATH_TYPE_X86
                    );
            if (roadPaths != null){
                for (RoadPath rp :
                        roadPaths) {
                    if (rp != null) {
                        ret = new JsonMissionItemDataTwoElevator.PathEntity();
                        ret.setId(Integer.valueOf(rp.getPathId()));
                        ret.setScene_name(sceneName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    /**
     * 判断是否新增或者解锁路径段
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     * @param roadPathLockAtts
     */
    private void addRoadPathLockOrUnlock(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts,
            RoadPathLockAtts roadPathLockAtts) {
        logger.info("### addRoadPathLockOrUnlock ");
        if (mPointAtts.roadpathId != null && mPointAtts.roadpathId > 0){
            if (roadPathLockAtts.lastRoadPathId != null){
                if (!Objects.equals(mPointAtts.roadpathId,
                        roadPathLockAtts.lastRoadPathId)){
                    //如果两个记录不一样，则先解锁旧的，再加锁新的
                    //解锁
                    Long temp = mPointAtts.roadpathId;
                    mPointAtts.roadpathId =
                            roadPathLockAtts.lastRoadPathId;
                    initPathMissionTaskRoadPathUnlock(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                    //加锁
                    mPointAtts.roadpathId = temp;
                    roadPathLockAtts.lastRoadPathId = temp;
                    initPathMissionTaskRoadPathLock(
                            missionListTask,
                            order,
                            mp,
                            mPointAtts
                    );
                }
            }else{
                //给路径加锁
                roadPathLockAtts.lastRoadPathId =
                        mPointAtts.roadpathId;
                initPathMissionTaskRoadPathLock(
                        missionListTask,
                        order,
                        mp,
                        mPointAtts
                );
            }
        }else{
            //当前已经没有路径锁了，如果前面的路径锁了，则要解锁
            if (roadPathLockAtts.lastRoadPathId != null){
                mPointAtts.roadpathId =
                        roadPathLockAtts.lastRoadPathId;
                roadPathLockAtts.lastRoadPathId = null;
                initPathMissionTaskRoadPathUnlock(
                        missionListTask,
                        order,
                        mp,
                        mPointAtts
                );
            }
        }
    }

    /**
     * 逻辑路径解锁
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskRoadPathUnlock(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskRoadPathUnlock ");

        String parentName = "工控固定路径逻辑路径解锁任务-";

        JsonMissionItemDataRoadPathUnlock json =
                new JsonMissionItemDataRoadPathUnlock();
//        json.setInterval_time(30);
        json.setInterval_time(5);
        json.setRoadpath_id(mPointAtts.roadpathId);
        MissionTask roadpathUnlockTask = getRoadPathUnlockTask(
                order,
                mp,
                parentName,
                json
        );
        missionListTask.getMissionTasks().add(roadpathUnlockTask);

    }

    /**
     * 逻辑路径加锁
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskRoadPathLock(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskRoadPathLock ");

        String parentName = "工控固定路径逻辑路径加锁任务-";

        JsonMissionItemDataRoadPathLock json =
                new JsonMissionItemDataRoadPathLock();
//        json.setInterval_time(30);
        json.setInterval_time(5);
        json.setRoadpath_id(mPointAtts.roadpathId);
        MissionTask roadpathLockTask = getRoadPathLockTask(
                order,
                mp,
                parentName,
                json
        );
        missionListTask.getMissionTasks().add(roadpathLockTask);

    }

    /**
     * 实例化工控固定路径导航任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskStaticPath(
            MissionListTask missionListTask,
            Order order,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskStaticPath ");

        String parentName = "工控固定路径导航任务-";

        //单点路径导航任务，当前路径导航到充电点
        MissionTask staticNavTask = getStaticPathTask(order, mp, parentName, mPointAtts);
        missionListTask.getMissionTasks().add(staticNavTask);
    }

    /**
     * 实例化门任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     * @param roadPathLockAtts
     */
    private void initPathMissionTaskDoor(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts,
            RoadPathLockAtts roadPathLockAtts) {

        logger.info("### initPathMissionTaskDoor ");

        String parentName = "固定路径门任务-";

        //单点路径导航任务，当前路径导航到门任务等待点
//        MissionTask sigleNavTask = getPathNavTask(order, startMp, mp, parentName);
//        missionListTask.getMissionTasks().add(sigleNavTask);

        //查询门任务关联数据加入门任务
        List<Door> doors =
                doorService.listByWaitPoint(mp.getId(),
                        100L,
                        null);
        if (doors != null){
            for (Door door :
                    doors) {
                if (door != null) {
                    JsonMissionItemDataDoor obj =
                            new JsonMissionItemDataDoor();
                    obj.setWaitTime(5L);
                    MissionTask doorTask = null;
                    JsonMissionItemDataLaserNavigation point = null;
                    //添加门任务
                    if (door.getDoorOrderType() != null){
                        switch (door.getDoorOrderType()){
                            case DoorType_door:

                                point =
                                        new JsonMissionItemDataLaserNavigation();
                                point.setX(door.getoPoint().getX());
                                point.setY(door.getoPoint().getY());
                                point.setTh(door.getoPoint().getTh());
                                point.setMap_name(door.getoPoint().getMapName());
                                point.setScene_name(door.getoPoint().getSceneName());
                                point.setMap(door.getoPoint().getMapName());
                                obj.setPoint(point);

                                doorTask = getDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                            case DoorType_laneDoor:

                                point =
                                        new JsonMissionItemDataLaserNavigation();
                                point.setX(door.getoPoint().getX());
                                point.setY(door.getoPoint().getY());
                                point.setTh(door.getoPoint().getTh());
                                point.setMap_name(door.getoPoint().getMapName());
                                point.setScene_name(door.getoPoint().getSceneName());
                                point.setMap(door.getoPoint().getMapName());
                                obj.setPoint(point);

                                doorTask = getLaneDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                            case DoorType_pathDoor:
                                JsonMissionItemDataDoor.Path path =
                                        new JsonMissionItemDataDoor.Path();
                                //工控路径ID
                                Long x86RoadPathId = Long.parseLong(door.getPathId());
                                path.setId(x86RoadPathId);
                                String sceneName = door.getoPoint().getSceneName();
                                String mapName = door.getoPoint().getMapName();
                                path.setMap_name(mapName);
                                RoadPath roadPath = roadPathService.findBySceneAndX86RoadPathId(x86RoadPathId,sceneName,mapName);
                                if(roadPath == null) {
                                    logger.error("###find roadPath error###,x86RoadPathId: {}, sceneName: {} roadPath not found!!" , x86RoadPathId,sceneName);
                                }
                                if(roadPath != null) {
                                    path.setTolerance_type(roadPath.getX86PathType());
                                }
                                path.setScene_name(door.getoPoint().getSceneName());
                                obj.setPath(path);

                                //通过pathid，查询到对应的逻辑路径段id
//                                try {
//                                    RoadPath roadPath =
//                                            roadPathService.findByPathId(
//                                                    String.valueOf(path.getId()));
//                                    if (roadPath != null){
//                                        mPointAtts.roadpathId = roadPath.getPathLock();
//                                        //添加路径锁或解锁
//                                        addRoadPathLockOrUnlock(
//                                                missionListTask,
//                                                order,
//                                                mp,
//                                                mPointAtts,
//                                                roadPathLockAtts
//                                        );
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    logger.info("Path Door find RoadPath Error: " + e.getMessage());
//                                }
                                //直接通过门对象关联的逻辑路径对象，判断是否加入逻辑路径锁
                                mPointAtts.roadpathId = door.getPathLock();
                                //添加路径锁或解锁
                                addRoadPathLockOrUnlock(
                                        missionListTask,
                                        order,
                                        mp,
                                        mPointAtts,
                                        roadPathLockAtts
                                );

                                doorTask = getPathDoorTask(order,
                                        door.getoPoint(),
                                        parentName,
                                        obj);
                                break;
                            default:
                                break;
                        }
                    }
                    missionListTask.getMissionTasks().add(doorTask);

                    break;
                }
            }
        }


    }

    /**
     * 实例化电梯任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskElevator(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskElevator ");

        String parentName = "固定路径电梯任务-";

        Long elevatorid = null;
        //电梯任务，发送进入电梯到第几层
        JsonMissionItemDataElevator jsonMissionItemDataElevator =
                new JsonMissionItemDataElevator();
        if (mPointAtts != null){
            List<Elevator> preElevator = elevatorService.findByMapFloor(
                    mPointAtts.currentMapId,
                    mPointAtts.currentFloor,
                    mp);
            jsonMissionItemDataElevator.setArrival_floor(mPointAtts.nextFloor);
            jsonMissionItemDataElevator.setLogic_floor(mPointAtts.logicFloor);
            jsonMissionItemDataElevator.setCurrent_floor(mPointAtts.currentFloor);
            if (preElevator != null){
                for (Elevator ev :
                        preElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                //等待点导航任务

                                //单点路径导航任务，当前路径导航到电梯等待点,测试门任务的时候，不需要单点导航了
//                                MissionTask sigleNavTask = getPathNavTask(
//                                        order, startMp, epc.getwPoint(), parentName);
//                                missionListTask.getMissionTasks().add(sigleNavTask);
                                //加入check电梯状态任务
//                                JsonMissionItemDataElevatorLock lock =
//                                        new JsonMissionItemDataElevatorLock();
//                                lock.setElevator_id(ev.getElevatorshaftId());
//                                lock.setInterval_time(30);
//                                MissionTask elevatorLockTask = getElevatorLockTask(
//                                        order, epc.getwPoint(), parentName,
//                                        lock
//                                );
//                                missionListTask.getMissionTasks().add(elevatorLockTask);
                                elevatorid = ev.getElevatorshaftId();

                                jsonMissionItemDataElevator
                                        .setEnter_point(
                                                changeToPoint(epc.getiPoint())
                                        );
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            List<Elevator> nextElevator = elevatorService.findByMapFloor(
                    mPointAtts.nextMapId,
                    mPointAtts.nextFloor,
                    mp);
            if (nextElevator != null){
                for (Elevator ev :
                        nextElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                jsonMissionItemDataElevator
                                        .setSet_pose_point(
                                                changeToPoint(epc.getiPoint())
                                        );
                                jsonMissionItemDataElevator
                                        .setBack_point(
                                                changeToPoint(epc.getoPoint())
                                        );
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }else{
            logger.error("没有获取到电梯到达的楼层，请注意查看地图是否配置了楼层数据，或者电梯点后续是否没有设置到达点！");
        }



        //电梯任务
        MissionTask elevatorTask = getElevatorTask(
                order,
                mp,
                parentName,
                jsonMissionItemDataElevator);
        missionListTask.getMissionTasks().add(elevatorTask);

        //加入check电梯状态解锁任务
//        JsonMissionItemDataElevatorUnlock unlock =
//                new JsonMissionItemDataElevatorUnlock();
//        unlock.setElevator_id(elevatorid);
//        unlock.setInterval_time(30);
//        MissionTask elevatorUnlockTask = getElevatorUnlockTask(
//                order, mp, parentName,
//                unlock
//        );
//        missionListTask.getMissionTasks().add(elevatorUnlockTask);

    }

    /**
     * 实例化双电梯任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskTwoElevator(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskTwoElevator ");

        String parentName = "固定路径双电梯任务-";

        boolean isNotTwo = false;//取到的不是两个电梯数据的情况
        Long elevatorid = null;
        //电梯任务，发送进入电梯到第几层
        JsonMissionItemDataTwoElevator jsonMissionItemDataTwoElevator =
                new JsonMissionItemDataTwoElevator();
        List<JsonMissionItemDataTwoElevator.ElevatorsEntity> elevatorsEntities =
                new ArrayList<>();
        if (mPointAtts != null){
            JsonMissionItemDataTwoElevator.ElevatorsEntity temp =
                    new JsonMissionItemDataTwoElevator.ElevatorsEntity();
            temp.setCurrent_floor(mPointAtts.currentFloor);
            temp.setArrival_floor(mPointAtts.nextFloor);
            temp.setLogic_floor(mPointAtts.logicFloor);
            elevatorsEntities.add(temp);
            temp = new JsonMissionItemDataTwoElevator.ElevatorsEntity();
            temp.setCurrent_floor(mPointAtts.currentFloor);
            temp.setArrival_floor(mPointAtts.nextFloor);
            temp.setLogic_floor(mPointAtts.logicFloor);
            elevatorsEntities.add(temp);
            List<Elevator> preElevator = elevatorService.findByMapFloor(
                    mPointAtts.currentMapId,
                    mPointAtts.currentFloor,
                    mp);
            int count = -1;
            if (preElevator != null){
                if (preElevator.size() != 2){
                    isNotTwo = true;
                }
                for (Elevator ev :
                        preElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        count++;
                        if (count >= 2){
                            break;
                        }
                        try {
                            ElevatorModeEnum elevatorModeEnum =
                                    elevatorService
                                            .determineCurrentElevatorMode(
                                                    ev.getId()
                                            );
                            if (elevatorModeEnum != null){
                                    elevatorsEntities.get(count).setAuto_mode(elevatorModeEnum.getModelCode());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        elevatorsEntities.get(count)
                                .setDefault_elevator(ev.getDefaultElevator()?1:0);
                        if (ev.getIpElevatorId() != null){
                            elevatorsEntities.get(count)
                                    .setIp_elevator_id(Integer.valueOf(ev.getIpElevatorId()));
                        }
                        elevatorsEntities.get(count)
                                .setElevatorId(ev.getId());
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                //等待点导航任务

                                //单点路径导航任务，当前路径导航到电梯等待点,测试门任务的时候，不需要单点导航了
//                                MissionTask sigleNavTask = getPathNavTask(
//                                        order, startMp, epc.getwPoint(), parentName);
//                                missionListTask.getMissionTasks().add(sigleNavTask);
                                if (count == 0){
                                    //加入check电梯状态任务
//                                    JsonMissionItemDataElevatorLock lock =
//                                            new JsonMissionItemDataElevatorLock();
//                                    lock.setElevator_id(ev.getElevatorshaftId());
//                                    lock.setInterval_time(30);
//                                    MissionTask elevatorLockTask = getElevatorLockTask(
//                                            order, epc.getwPoint(), parentName,
//                                            lock
//                                    );
//                                    missionListTask.getMissionTasks().add(elevatorLockTask);
//                                    elevatorid = ev.getElevatorshaftId();
                                }

                                elevatorsEntities.get(count).setWaitPointId(
                                        epc.getwPoint() != null ? epc.getwPoint().getId() : 0L);
                                if (epc.getwPoint() != null){
                                    elevatorsEntities.get(count)
                                            .setSceneName(epc.getwPoint().getSceneName());
                                    elevatorsEntities.get(count)
                                            .setMapName(epc.getwPoint().getMapName());
                                }
                                elevatorsEntities.get(count).setEnter_point(
                                        changeToPoint(epc.getiPoint())
                                );
                                break;
                            }
                        }
                    }
                }
            }
            List<Elevator> nextElevator = elevatorService.findByMapFloor(
                    mPointAtts.nextMapId,
                    mPointAtts.nextFloor,
                    mp);
            count = -1;
            if (nextElevator != null){
                for (Elevator ev :
                        nextElevator) {
                    if (ev != null &&
                            ev.getElevatorPointCombinations() != null &&
                            ev.getElevatorPointCombinations().size() > 0) {
                        count++;
                        if (count >= 2){
                            break;
                        }
                        Long elevatorId = ev.getId();
                        for (ElevatorPointCombination epc :
                                ev.getElevatorPointCombinations()) {
                            if (epc != null) {
                                for (int tempcount = 0; tempcount < 2; tempcount++){
                                    if (elevatorsEntities.get(tempcount).getElevatorId() != null){
                                        if (Objects.equals(elevatorId,
                                                elevatorsEntities.get(tempcount).getElevatorId())){
                                            elevatorsEntities.get(tempcount)
                                                    .setSet_pose_point(
                                                            changeToPoint(epc.getiPoint())
                                                    );
                                            elevatorsEntities.get(tempcount)
                                                    .setBack_point(
                                                            changeToPoint(epc.getoPoint())
                                                    );
                                            elevatorsEntities.get(tempcount).setWaitPointIdNext(
                                                    epc.getwPoint() != null ? epc.getwPoint().getId() : 0L);
                                            if (epc.getwPoint() != null){
                                                elevatorsEntities.get(tempcount)
                                                        .setSceneNameNext(epc.getwPoint().getSceneName());
                                                elevatorsEntities.get(tempcount)
                                                        .setMapNameNext(epc.getwPoint().getMapName());
                                            }

                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }else{
            logger.error("没有获取到电梯到达的楼层，请注意查看地图是否配置了楼层数据，或者电梯点后续是否没有设置到达点！");
        }

        JsonMissionItemDataTwoElevator.ElevatorsEntity defaultElevator = null;
        JsonMissionItemDataTwoElevator.ElevatorsEntity nextElevator = null;

        int defaultindex;
        int nextindex;
        for (int i = 0; i < 2; i++){
            if (i >= elevatorsEntities.size()){
                break;
            }
            if (elevatorsEntities.get(i).getDefault_elevator() == 1){
                defaultElevator = elevatorsEntities.get(i);
                defaultindex = i;
            }else{
                nextElevator = elevatorsEntities.get(i);
                nextindex = i;
                if (isNotTwo){
                    elevatorsEntities.remove(nextElevator);
                    nextElevator = null;
                }
            }
        }

        //这里查询工控路径数据
        if (defaultElevator != null &&
                nextElevator != null){
            jsonMissionItemDataTwoElevator.setPath0_1(
                    getStaticPathPointPathEntity(
                            defaultElevator.getSceneName(),
                            defaultElevator.getMapName(),
                            defaultElevator.getWaitPointId(),
                            nextElevator.getWaitPointId()
                    )
            );
            jsonMissionItemDataTwoElevator.setPath1_0(
                    getStaticPathPointPathEntity(
                            defaultElevator.getSceneName(),
                            defaultElevator.getMapName(),
                            nextElevator.getWaitPointId(),
                            defaultElevator.getWaitPointId()
                    )
            );
            jsonMissionItemDataTwoElevator.setPath2_3(
                    getStaticPathPointPathEntity(
                            defaultElevator.getSceneNameNext(),
                            defaultElevator.getMapNameNext(),
                            defaultElevator.getWaitPointIdNext(),
                            nextElevator.getWaitPointIdNext()
                    )
            );
            jsonMissionItemDataTwoElevator.setPath3_2(
                    getStaticPathPointPathEntity(
                            defaultElevator.getSceneNameNext(),
                            defaultElevator.getMapNameNext(),
                            nextElevator.getWaitPointIdNext(),
                            defaultElevator.getWaitPointIdNext()
                    )
            );
        }

        //将电梯数据放入
        jsonMissionItemDataTwoElevator.setElevators(elevatorsEntities);


        //电梯任务
        MissionTask elevatorTask = getTwoElevatorTask(
                order,
                mp,
                parentName,
                jsonMissionItemDataTwoElevator);
        missionListTask.getMissionTasks().add(elevatorTask);

        //加入check电梯状态解锁任务
//        JsonMissionItemDataElevatorUnlock unlock =
//                new JsonMissionItemDataElevatorUnlock();
//        unlock.setElevator_id(elevatorid);
//        unlock.setInterval_time(30);
//        MissionTask elevatorUnlockTask = getElevatorUnlockTask(
//                order, mp, parentName,
//                unlock
//        );
//        missionListTask.getMissionTasks().add(elevatorUnlockTask);

    }


    /**
     * 实例化充电任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskChongDian(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskChongDian ");

        String parentName = "充电任务-";

        //单点路径导航任务，当前路径导航到充电点
//        MissionTask sigleNavTask = getPathNavTask(order, startMp, mp, parentName);
//        missionListTask.getMissionTasks().add(sigleNavTask);

//        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_CHARGE);
//        missionListTask.getMissionTasks().add(mp3loadTask);

        //自动充电任务,会自动导航
        MissionTask gotochargeTask = getGotoChargeTask(order, mp, parentName);
//        gotochargeTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_CHARGE));

        missionListTask.getMissionTasks().add(gotochargeTask);
    }

    /**
     * 实例化返回卸载货架任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskXiaHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskXiaHuo ");

        String parentName = "固定路径卸货任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //如果没有货架，就不需要再加入卸货架任务了
        if (order.getShelf() == null){
            return;
        }

        //单点导航任务，回到下货点
//        MissionTask sigleNavTask = getPathNavTask(order, startMp, mp, parentName);
//        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务，等待货架取下（同时语音提示我回来了，请取下货箱？）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        if (!missionItemConcurrentable){
            MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_CABINET);
            if (isSetOrderDetailMP){
                mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(mp3loadTask);
        }

        //终点卸货任务,目前先代替等待任务
        MissionTask finalUnloadTask = getFinalUnloadTask(order, mp, parentName, mPointAtts.orderDetailMP);
        if (isSetOrderDetailMP){
            finalUnloadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }

        if (missionItemConcurrentable){
            //并行执行语音任务
            MissionItemTask temp = getMp3VoiceItemTask(order, mp, parentName, MP3_TAKE_CABINET);
            if (temp != null){
                temp.setIgnorable(true);
                finalUnloadTask.getMissionItemTasks().add(temp);
            }
        }

        missionListTask.getMissionTasks().add(finalUnloadTask);

        //语音任务，感谢使用，我要回去充电了？
        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
        missionListTask.getMissionTasks().add(voiceTask);

    }


    /**
     * 实例化取货装货任务
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskQuHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskQuHuo ");

        String parentName = "取货任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //添加单点导航任务,导航到取货点
//        MissionTask sigleNavTask = getPathNavTask(order, startMp, mp, parentName);
//        missionListTask.getMissionTasks().add(sigleNavTask);

        if (!missionItemConcurrentable){
            MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_CABINET);
            if (isSetOrderDetailMP){
                mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(mp3loadTask);
        }

        if (order.getShelf() == null){
            //不需要装货架的取货任务
            MissionTask loadNoShelfTask = getLoadNoShelfTask(order, mp, parentName, mPointAtts.orderDetailMP);
            if (isSetOrderDetailMP){
                loadNoShelfTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(loadNoShelfTask);

            if (missionItemConcurrentable){
                //并行执行语音任务
                MissionItemTask temp = getMp3VoiceItemTask(order, mp, parentName, MP3_CABINET);
                if (temp != null){
                    temp.setIgnorable(true);
                    loadNoShelfTask.getMissionItemTasks().add(temp);
                }
            }
        }else{
            //load任务，取代等待任务
            MissionTask loadTask = getLoadTask(order, mp, parentName, mPointAtts.orderDetailMP);
            if (isSetOrderDetailMP){
                loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(loadTask);

            if (missionItemConcurrentable){
                //并行执行语音任务
                MissionItemTask temp = getMp3VoiceItemTask(order, mp, parentName, MP3_CABINET);
                if (temp != null){
                    temp.setIgnorable(true);
                    loadTask.getMissionItemTasks().add(temp);
                }
            }
        }

        //装载完毕语音任务
        MissionTask mp3loadFinishTask = getMp3VoiceTask(order, mp, parentName, MP3_LOAD_FINISH);
        if (isSetOrderDetailMP){
            mp3loadFinishTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }
        missionListTask.getMissionTasks().add(mp3loadFinishTask);

        //语音任务，我要出发了？
//        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
//        missionListTask.getMissionTasks().add(voiceTask);

    }


    /**
     * 实例化送货任务
     * 这里的missionTask都要设置orderDetailMission为1
     * @param missionListTask
     * @param order
     * @param mp
     * @param mPointAtts
     */
    private void initPathMissionTaskSongHuo(
            MissionListTask missionListTask,
            Order order,
            MapPoint startMp,
            MapPoint mp,
            MPointAtts mPointAtts) {

        logger.info("### initPathMissionTaskSongHuo ");

        String parentName = "固定路径中间送货站点任务-";

        boolean isSetOrderDetailMP = false;
        if (!StringUtil.isNullOrEmpty(mPointAtts.orderDetailMP) &&
                !str_zero.equalsIgnoreCase(mPointAtts.orderDetailMP)){
            isSetOrderDetailMP = true;
        }

        //单点导航任务，导航到目标送货点
//        MissionTask sigleNavTask = getPathNavTask(order, startMp, mp, parentName);
//        if (isSetOrderDetailMP){
//            sigleNavTask.setOrderDetailMission(mPointAtts.orderDetailMP);
//        }
//        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务（同时语音提示，物品已经送达，请查收）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        if (!missionItemConcurrentable){
            MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_CABINET);
            if (isSetOrderDetailMP){
                mp3loadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(mp3loadTask);

            MissionTask mp3SignTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_MEDICINE_SIGN);
            if (isSetOrderDetailMP){
                mp3SignTask.setOrderDetailMission(mPointAtts.orderDetailMP);
            }
            missionListTask.getMissionTasks().add(mp3SignTask);
        }

        //卸货任务，取代等待任务
        MissionTask unloadTask = getUnloadTask(order, mp, parentName, mPointAtts.orderDetailMP);
        if (isSetOrderDetailMP){
            unloadTask.setOrderDetailMission(mPointAtts.orderDetailMP);
        }

        if (missionItemConcurrentable){
            //并行执行语音任务
            ArrayList<String> filenames = new ArrayList<>();
            filenames.add(MP3_TAKE_CABINET);
            filenames.add(MP3_TAKE_MEDICINE_SIGN);
            MissionItemTask temp = getListMp3VoiceItemTask(order, mp, parentName, filenames);
            if (temp != null){
                temp.setIgnorable(true);
                unloadTask.getMissionItemTasks().add(temp);
            }
        }


        missionListTask.getMissionTasks().add(unloadTask);

        //语音任务，感谢使用，我要出发了，再见？
//        MissionTask voiceTask = getMp3VoiceTask(order, mp, parentName, MP3_DEFAULT);
//        missionListTask.getMissionTasks().add(voiceTask);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  定义
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String str_one = "1";
    public static final String str_zero = "0";

    public static final String MPointType_QUHUO = "quhuo";//取货点，人工或自动上货架点
    public static final String MPointType_XIAHUO = "xiahuo";//下货点,卸载货架点
    public static final String MPointType_SONGHUO = "songhuo";//中间的送货点，order detail station point
    public static final String MPointType_CHONGDIAN = "chongdian";//充电点
    public static final String MPointType_ELEVATOR = "elevator";//电梯点
    public static final String MPointType_ELEVATOR_END = "elevator_end";//电梯结束点
    public static final String MPointType_DOOR = "door";//门任务等待点
    public static final String MPointType_DOOR_END = "door_end";//门任务结束点
    public static final String MPointType_UNDEFINED = "undefined";//未定义
    public static final String MPointType_STATIC_PATH = "static_path";//固定路径
    public static final String MPointType_ROAD_PATH_LOCK = "road_path_lock";//加锁路径
    public static final String MPointType_ROAD_PATH_UNLOCK = "road_path_unlock";//解锁路径

    public static final String FeatureValue_test = "test";//测试命令

    public static final String FeatureValue_nav = "nav";//单点导航命令
    public static final String FeatureValue_waiting = "waiting";//等待命令
    public static final String FeatureValue_gotocharge = "gotocharge";//进入充电命令
    public static final String FeatureValue_leavecharge = "leavecharge";//离开充电命令
    public static final String FeatureValue_mp3 = "mp3";//语音命令
    public static final String FeatureValue_load = "load";//装货
    public static final String FeatureValue_loadNoShelf = "loadNoShelf";//无货架装货
    public static final String FeatureValue_unload = "unload";//卸货
    public static final String FeatureValue_finalUnload = "finalUnload";//终点卸货
    public static final String FeatureValue_elevator = "elevator";//电梯
    public static final String FeatureValue_elevator_lock = "elevatorLock";
    public static final String FeatureValue_elevator_unlock = "elevatorUnlock";
    public static final String FeatureValue_roadpath_lock = "roadpathLock";
    public static final String FeatureValue_roadpath_unlock = "roadpathUnlock";
    public static final String FeatureValue_door = "door";
    public static final String FeatureValue_laneDoor = "laneDoor";
    public static final String FeatureValue_pathDoor = "pathDoor";

    public static final String MissionItemName_test = "fake";

    public static final String MissionItemName_nav = "laserNavigation";//普通激光导航
    public static final String MissionItemName_path_nav = "pathNavigation";//固定路径导航
    public static final String MissionItemName_lane_nav = "laneNavigation";//沿线导航
    public static final String MissionItemName_gotocharge = "gotoCharge";
    public static final String MissionItemName_leavecharge = "leaveCharge";
    public static final String MissionItemName_mp3 = "mp3";
    public static final String MissionItemName_waiting = "waiting";
    public static final String MissionItemName_load = "load";
    public static final String MissionItemName_loadNoShelf = "loadNoShelf";
    public static final String MissionItemName_unload = "unload";
    public static final String MissionItemName_finalUnload = "finalUnload";
    public static final String MissionItemName_elevator = "elevator";
    public static final String MissionItemName_elevator_lock = "elevatorLock";
    public static final String MissionItemName_elevator_unlock = "elevatorUnlock";
    public static final String MissionItemName_roadpath_lock = "roadpathLock";
    public static final String MissionItemName_roadpath_unlock = "roadpathUnlock";
    public static final String MissionItemName_door = "door";
    public static final String MissionItemName_laneDoor = "laneDoor";
    public static final String MissionItemName_pathDoor = "pathDoor";

    public static final String MissionListType_normal = "normal";

    //音频定义
    public static final String MP3_DEFAULT = "default.mp3";//默认语音
    public static final String MP3_TAKE_MEDICINE = "arrive_take_medicine.mp3";//请您取走药品
    public static final String MP3_CABINET = "arrive_place_medicine_cabinet.mp3";//请您放置药柜
    public static final String MP3_TAKE_CABINET = "arrive_take_medicine_cabinet.mp3";//请您取走药柜
    public static final String MP3_TAKE_MEDICINE_SIGN = "take_medicine_sign.mp3";//请确认取走药品后，完成签收
    public static final String MP3_CHARGE = "charge.mp3";//充电语音
    public static final String MP3_LOAD_FINISH = "load_finish.mp3";//装货完毕语音

    //Mission State
    public static final String MissionStateFinished = "finished";//已经完成
    public static final String MissionStateExecuting = "executing";//正在执行
    public static final String MissionStatePaused = "paused";//暂停中
    public static final String MissionStateWaiting = "waiting";//等待中
    public static final String MissionStateCanceled = "canceled";//被取消
    public static final String MissionStateInit = "init";//初始状态，代表未执行,待执行

    //DoorType
    public static final String DoorType_door = "door";
    public static final String DoorType_laneDoor = "laneDoor";
    public static final String DoorType_pathDoor = "pathDoor";

    public static final String NULL_JSON_OBJ = "{}";

    /**
     * 地图点的属性类
     */
    public static class MPointAtts{
        public MPointAtts() {
            this.orderDetailMP = str_zero;
        }

        public String type;
        public String orderDetailMP;
        public Integer nextFloor;
        public Integer currentFloor;
        public Integer logicFloor;
        public Long currentMapId;
        public Long nextMapId;
        public String pathId;
        public Long roadpathId;
        public MapPoint chargePoint;
        public MapPoint chargePrePoint;
    }

    /**
     * 路径锁相关属性变量
     */
    public static class RoadPathLockAtts{
        public Long lastRoadPathId;
    }
}

