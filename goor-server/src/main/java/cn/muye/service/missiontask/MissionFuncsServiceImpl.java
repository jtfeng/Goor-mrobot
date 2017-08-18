package cn.muye.service.missiontask;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.*;
import cn.mrobot.bean.mission.task.*;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionItemDTO;
import cn.mrobot.dto.mission.MissionListDTO;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.service.FeatureItemService;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.service.consumer.topic.X86MissionDispatchService;
import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * 根据订单数据创建任务列表
     * @param order
     * @return
     */
    @Override
    public AjaxResult createMissionLists(Order order) {

        logger.info("##############  createMissionLists #################");

        //判断order
        if (order == null ||
                order.getOrderSetting() == null ||
                order.getRobot() == null ||
                order.getOrderSetting().getStartPoint() == null ||
                order.getOrderSetting().getEndPoint() == null){
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

        //首先插入起点
        mapPoints.add(order.getOrderSetting().getStartPoint());
        //设置属性
        atts = new MPointAtts();
        atts.type = MPointType_QUHUO;
        mpAttrs.put(order.getOrderSetting().getStartPoint(), atts);
        prePoint = order.getOrderSetting().getStartPoint();
        logger.info("###### quhuo is ok ");

        //判断中间站点，如果有中间站点，添加中间站点的地图点，如果中间点跨楼层，则要在两个任务中间插入电梯任务
        if (order.getDetailList() != null){
            for (OrderDetail od :
                    order.getDetailList()) {
                if (od != null &&
                        od.getStationId() != null) {
                    logger.info("###### begin get order detail station ");
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
                                addElevatorPoint(mp, mapPoints, mpAttrs);
                                //判断当前点的属性，根据属性加入相应的任务
                                switch (MapPointType.getType(mp.getMapPointTypeId())){
                                    case ELEVATOR:
                                    case ELEVATOR_WAIT:
                                    default:
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
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (order.getOrderSetting().getEndPoint() != null) {
            logger.info("### end point ####  mpname: " + order.getOrderSetting().getEndPoint().getMapName() +
                    ", scenename: " + order.getOrderSetting().getEndPoint().getSceneName() +
                    ", pointname: " + order.getOrderSetting().getEndPoint().getPointName());
        }

        //判断添加电梯点
        addElevatorPoint(order.getOrderSetting().getEndPoint(), mapPoints, mpAttrs);
        //中间点添加完毕，添加卸货点
        mapPoints.add(order.getOrderSetting().getEndPoint());
        //设置属性
        atts = new MPointAtts();
        atts.type = MPointType_XIAHUO;
        mpAttrs.put(order.getOrderSetting().getEndPoint(), atts);
        logger.info("###### xiahuo is ok ");

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
                    addElevatorPoint(mp, mapPoints, mpAttrs);
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
            mpAttrs.put(temp, atts);
            logger.info("###### addElevatorPoint is ok ");
        }
        prePoint = mp;
    }

    /**
     * 获取地图点所在的楼层
     * @param mp
     * @return
     */
    private MPointAtts getMapPointFloor(MapPoint mp) {
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
                    mPointAtts.currentFloor);
            jsonMissionItemDataElevator.setArrival_floor(mPointAtts.nextFloor);
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
                                //单点路径导航任务，当前路径导航到电梯等待点
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
                    mPointAtts.nextFloor);
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

        //单点导航任务，回到下货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务，等待货架取下（同时语音提示我回来了，请取下货箱？）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_TAKE_CABINET);
        missionListTask.getMissionTasks().add(mp3loadTask);

        //终点卸货任务,目前先代替等待任务
        MissionTask finalUnloadTask = getFinalUnloadTask(order, mp, parentName);
//        finalUnloadTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));

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

        //离开充电任务
//        MissionTask leavechargeTask = getLeaveChargeTask(order, mp, parentName);
//        missionListTask.getMissionTasks().add(leavechargeTask);

        //添加单点导航任务,导航到取货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp, parentName);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //到达，等待任务（同时语音播报，请放上货箱？）
//        MissionTask waitingTask = getWaitingTask(order, mp, parentName);
//        waitingTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_ARRIVE));
//
//        missionListTask.getMissionTasks().add(waitingTask);

        MissionTask mp3loadTask = getMp3VoiceTask(order, mp, parentName, MP3_CABINET);
        missionListTask.getMissionTasks().add(mp3loadTask);

        //load任务，取代等待任务
        MissionTask loadTask = getLoadTask(order, mp, parentName);
//        loadTask.getMissionItemTasks().add(getMp3VoiceItemTask(order, mp, parentName, MP3_LOAD));

        missionListTask.getMissionTasks().add(loadTask);

        //装载完毕语音任务
        MissionTask mp3loadFinishTask = getMp3VoiceTask(order, mp, parentName, MP3_LOAD_FINISH);
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
                new TypeToken<JsonMissionItemDataLaserNavigation>(){}.getType()));
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
        itemTask.setData("");
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
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataMp3>(){}.getType()));
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
                new TypeToken<JsonMissionItemDataGotocharge>(){}.getType()));
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
        itemTask.setData("");
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
            String parentName) {
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
        missionItemTasks.add(getLoadItemTask(order, mp, parentName));

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
            String parentName) {
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
        if (order.getShelf() != null){
            itemTask.setData(JsonUtils.toJson(order.getShelf(),
                    new TypeToken<Shelf>(){}.getType()));
        }else{
            itemTask.setData("");
        }
        itemTask.setState(MissionStateInit);
        itemTask.setFeatureValue(FeatureValue_load);

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
            String parentName) {
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
        missionItemTasks.add(getFinalUnloadItemTask(order, mp, parentName));

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
            String parentName) {
        MissionItemTask itemTask = new MissionItemTask();
        if (order.getScene() != null) {
            itemTask.setSceneId(order.getScene().getId());
        }
        itemTask.setDescription(parentName + "终点卸货Item");
        itemTask.setName(MissionItemName_finalUnload);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
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
        itemTask.setData(JsonUtils.toJson(json,
                new TypeToken<JsonMissionItemDataElevator>(){}.getType()));
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

    public static final String FeatureValue_test = "test";//测试命令

    public static final String FeatureValue_nav = "nav";//单点导航命令
    public static final String FeatureValue_waiting = "waiting";//等待命令
    public static final String FeatureValue_gotocharge = "gotocharge";//进入充电命令
    public static final String FeatureValue_leavecharge = "leavecharge";//离开充电命令
    public static final String FeatureValue_mp3 = "mp3";//语音命令
    public static final String FeatureValue_load = "load";//装货
    public static final String FeatureValue_unload = "unload";//卸货
    public static final String FeatureValue_finalUnload = "finalUnload";//终点卸货
    public static final String FeatureValue_elevator = "elevator";//电梯
    public static final String FeatureValue_elevator_lock = "elevatorLock";
    public static final String FeatureValue_elevator_unlock = "elevatorUnlock";

    public static final String MissionItemName_test = "fake";

    public static final String MissionItemName_nav = "laserNavigation";
    public static final String MissionItemName_gotocharge = "gotoCharge";
    public static final String MissionItemName_leavecharge = "leaveCharge";
    public static final String MissionItemName_mp3 = "mp3";
    public static final String MissionItemName_waiting = "waiting";
    public static final String MissionItemName_load = "load";
    public static final String MissionItemName_unload = "unload";
    public static final String MissionItemName_finalUnload = "finalUnload";
    public static final String MissionItemName_elevator = "elevator";
    public static final String MissionItemName_elevator_lock = "elevatorLock";
    public static final String MissionItemName_elevator_unlock = "elevatorUnlock";

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
        public Long currentMapId;
        public Long nextMapId;
    }
}

