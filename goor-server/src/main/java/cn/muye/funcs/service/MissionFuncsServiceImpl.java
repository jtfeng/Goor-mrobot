package cn.muye.funcs.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionItemDTO;
import cn.mrobot.dto.mission.MissionListDTO;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.consumer.service.X86MissionDispatchService;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by abel on 17-7-13.
 */
@Service
public class MissionFuncsServiceImpl implements MissionFuncsService {

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

    /**
     * 根据订单数据创建任务列表
     * @param order
     * @return
     */
    @Override
    public boolean createMissionLists(Order order) {
        boolean ret = false;

        //判断order
        if (order == null ||
                order.getOrderSetting() == null ||
                order.getRobot() == null ||
                order.getOrderSetting().getStartPoint() == null ||
                order.getOrderSetting().getEndPoint() == null){
            return ret;
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
        x86MissionDispatchService.sendX86MissionDispatch(
                order.getRobot().getCode(),
                getGoorMissionMsg(listTasks)
        );

        return true;
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

        return JsonUtils.toJson(dtos,
                new TypeToken<MissionListDTO>(){}.getType());
    }

    /**
     * 获取MissionListDTO对象
     * @param task
     * @return
     */
    private MissionListDTO getGoorMissionList(MissionListTask task) {
        if (task != null){
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
                                missionDTO.setMissionItemDTOSet(new HashSet<>(missionItemDTOS));
                            }
                            missionDTOS.add(missionDTO);
                        }
                    }
                }
                missionListDTO.setMissionDTOList(missionDTOS);
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

        MPointAtts atts;

        //首先插入起点
        mapPoints.add(order.getOrderSetting().getStartPoint());
        //设置属性
        atts = new MPointAtts();
        atts.type = MPointType_QUHUO;
        mpAttrs.put(order.getOrderSetting().getStartPoint(), atts);

        //判断中间站点，如果有中间站点，添加中间站点的地图点
        if (order.getDetailList() != null){
            for (OrderDetail od :
                    order.getDetailList()) {
                if (od != null &&
                        od.getStationId() != null) {
                    //取得站点对象
                    Station station = stationService.findById(od.getStationId());
                    if (station != null &&
                            station.getMapPoints() != null){
                        //目前只取第一个坐标点加入
                        for (MapPoint mp :
                                station.getMapPoints()) {
                            if(mp != null){
                                //加入该点，并标记这个点状态是orderDetail点
                                mapPoints.add(mp);
                                //标记该点的属性
                                atts = new MPointAtts();
                                atts.type = MPointType_SONGHUO;
                                mpAttrs.put(mp, atts);
                                break;
                            }
                        }
                    }
                }
            }
        }

        //中间点添加完毕，添加卸货点
        mapPoints.add(order.getOrderSetting().getEndPoint());
        //设置属性
        atts = new MPointAtts();
        atts.type = MPointType_XIAHUO;
        mpAttrs.put(order.getOrderSetting().getEndPoint(), atts);

        //最后添加充电点，目前充电点从机器人的数据库里面查询出来
        //查询列表
        List<MapPoint> chongMPs = robotService
                .getChargerMapPointByRobotCode(order.getRobot().getCode());
        //取第一个有效的点设置进去
        if (chongMPs != null){
            for (MapPoint mp :
                    chongMPs) {
                if (mp != null) {
                    mapPoints.add(mp);
                    //设置属性
                    atts = new MPointAtts();
                    atts.type = MPointType_CHONGDIAN;
                    atts.orderDetailMP = str_one;//标记是orderdetail的点
                    mpAttrs.put(mp, atts);
                    break;
                }
            }
        }
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
        missionListTask.setIntervalTime(0L);
        missionListTask.setDescription("下单自动任务列表"+System.currentTimeMillis());
        missionListTask.setMissionListType("");
        missionListTask.setName(missionListTask.getDescription());
        missionListTask.setOrderId(order.getId());
        missionListTask.setRepeatTimes(1);
        missionListTask.setPriority(0);
        missionListTask.setRobotCode(order.getRobot().getCode());
        missionListTask.setStartTime(System.currentTimeMillis() + 60);
        missionListTask.setStopTime(System.currentTimeMillis() + 600);
        missionListTask.setState("");
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
                    initMissionTaskChongDian(
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
        //单点路径导航任务，当前路径导航到充电点
        MissionTask sigleNavTask = getSigleNavTask(order, mp);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //自动充电任务
        MissionTask gotochargeTask = getGotoChargeTask(order, mp);
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
        //单点导航任务，回到下货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务，等待货架取下（同时语音提示我回来了，请取下货箱？）
        MissionTask waitingTask = getWaitingTask(order, mp);
        waitingTask.getMissionItemTasks().add(getVoiceItemTask(order, mp));

        missionListTask.getMissionTasks().add(waitingTask);

        //语音任务，感谢使用，我要回去充电了？
        MissionTask voiceTask = getVoiceTask(order, mp);
        missionListTask.getMissionTasks().add(voiceTask);

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
        //离开充电任务
        MissionTask leavechargeTask = getLeaveChargeTask(order, mp);
        missionListTask.getMissionTasks().add(leavechargeTask);

        //添加单点导航任务,导航到取货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //到达，等待任务（同时语音播报，请放上货箱？）
        MissionTask waitingTask = getWaitingTask(order, mp);
        waitingTask.getMissionItemTasks().add(getVoiceItemTask(order, mp));

        missionListTask.getMissionTasks().add(waitingTask);

        //语音任务，我要出发了？
        MissionTask voiceTask = getVoiceTask(order, mp);
        missionListTask.getMissionTasks().add(voiceTask);

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
        //单点导航任务，导航到目标送货点
        MissionTask sigleNavTask = getSigleNavTask(order, mp);
        missionListTask.getMissionTasks().add(sigleNavTask);

        //等待任务（同时语音提示，物品已经送达，请查收）
        MissionTask waitingTask = getWaitingTask(order, mp);
        waitingTask.getMissionItemTasks().add(getVoiceItemTask(order, mp));

        missionListTask.getMissionTasks().add(waitingTask);

        //语音任务，感谢使用，我要出发了，再见？
        MissionTask voiceTask = getVoiceTask(order, mp);
        missionListTask.getMissionTasks().add(voiceTask);
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
    private MissionTask getSigleNavTask(Order order, MapPoint mp) {
        MissionTask missionTask = new MissionTask();
        missionTask.setDescription("单点导航任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState("");
        missionTask.setPresetMissionCode("");
        missionTask.setCreatedBy(System.currentTimeMillis());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(order.getStoreId());

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getSigleNavItemTask(order, mp));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取单点导航Item任务
     * @param mp
     * @return
     */
    private MissionItemTask getSigleNavItemTask(Order order, MapPoint mp) {
        MissionItemTask itemTask = new MissionItemTask();
        itemTask.setDescription("单点导航");
        itemTask.setName(MissionItemName_nav);
        //这里就是单点导航的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
        itemTask.setState("");
        itemTask.setCreatedBy(System.currentTimeMillis());
        itemTask.setCreateTime(new Date());
        itemTask.setStoreId(order.getStoreId());
        itemTask.setFeatureValue(FeatureValue_nav);

        return itemTask;
    }

    /**
     * 获取等待任务
     * @param mp
     * @return
     */
    private MissionTask getWaitingTask(Order order, MapPoint mp) {
        MissionTask missionTask = new MissionTask();
        missionTask.setDescription("等待任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState("");
        missionTask.setPresetMissionCode("");
        missionTask.setCreatedBy(System.currentTimeMillis());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(order.getStoreId());

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getWaitingItemTask(order, mp));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取等待Item任务
     * @param mp
     * @return
     */
    private MissionItemTask getWaitingItemTask(Order order, MapPoint mp) {
        MissionItemTask itemTask = new MissionItemTask();
        itemTask.setDescription("等待任务");
        itemTask.setName(MissionItemName_waiting);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
        itemTask.setState("");
        itemTask.setCreatedBy(System.currentTimeMillis());
        itemTask.setCreateTime(new Date());
        itemTask.setStoreId(order.getStoreId());
        itemTask.setFeatureValue(FeatureValue_waiting);
        return itemTask;
    }

    /**
     * 获取语音任务
     * @param mp
     * @return
     */
    private MissionTask getVoiceTask(Order order, MapPoint mp) {
        MissionTask missionTask = new MissionTask();
        missionTask.setDescription("语音任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState("");
        missionTask.setPresetMissionCode("");
        missionTask.setCreatedBy(System.currentTimeMillis());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(order.getStoreId());

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getVoiceItemTask(order, mp));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取语音ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getVoiceItemTask(Order order, MapPoint mp) {
        MissionItemTask itemTask = new MissionItemTask();
        itemTask.setDescription("语音任务");
        itemTask.setName(MissionItemName_mp3);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
        itemTask.setState("");
        itemTask.setCreatedBy(System.currentTimeMillis());
        itemTask.setCreateTime(new Date());
        itemTask.setStoreId(order.getStoreId());
        itemTask.setFeatureValue(FeatureValue_mp3);

        return itemTask;
    }

    /**
     * 获取进入充电任务
     * @param mp
     * @return
     */
    private MissionTask getGotoChargeTask(Order order, MapPoint mp) {
        MissionTask missionTask = new MissionTask();
        missionTask.setDescription("进入充电任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState("");
        missionTask.setPresetMissionCode("");
        missionTask.setCreatedBy(System.currentTimeMillis());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(order.getStoreId());

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getVoiceItemTask(order, mp));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取进入充电ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getGotoChargeItemTask(Order order, MapPoint mp) {
        MissionItemTask itemTask = new MissionItemTask();
        itemTask.setDescription("进入充电任务");
        itemTask.setName(MissionItemName_gotocharge);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
        itemTask.setState("");
        itemTask.setCreatedBy(System.currentTimeMillis());
        itemTask.setCreateTime(new Date());
        itemTask.setStoreId(order.getStoreId());
        itemTask.setFeatureValue(FeatureValue_gotocharge);

        return itemTask;
    }

    /**
     * 获取离开充电任务
     * @param mp
     * @return
     */
    private MissionTask getLeaveChargeTask(Order order, MapPoint mp) {
        MissionTask missionTask = new MissionTask();
        missionTask.setDescription("离开充电任务");
        missionTask.setName(missionTask.getDescription());
        missionTask.setRepeatTimes(1);
        missionTask.setIntervalTime(0L);
        missionTask.setState("");
        missionTask.setPresetMissionCode("");
        missionTask.setCreatedBy(System.currentTimeMillis());
        missionTask.setCreateTime(new Date());
        missionTask.setStoreId(order.getStoreId());

        List<MissionItemTask> missionItemTasks =
                new ArrayList<>();
        missionItemTasks.add(getVoiceItemTask(order, mp));

        missionTask.setMissionItemTasks(missionItemTasks);

        return missionTask;
    }

    /**
     * 获取离开充电ITEM任务
     * @param mp
     * @return
     */
    private MissionItemTask getLeaveChargeItemTask(Order order, MapPoint mp) {
        MissionItemTask itemTask = new MissionItemTask();
        itemTask.setDescription("离开充电任务");
        itemTask.setName(MissionItemName_leavecharge);
        //这里就是任务的数据格式存储地方,根据mp和数据格式定义来创建
        itemTask.setData("");
        itemTask.setState("");
        itemTask.setCreatedBy(System.currentTimeMillis());
        itemTask.setCreateTime(new Date());
        itemTask.setStoreId(order.getStoreId());
        itemTask.setFeatureValue(FeatureValue_leavecharge);

        return itemTask;
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

    public static final String FeatureValue_nav = "nav";//单点导航命令
    public static final String FeatureValue_waiting = "waiting";//等待命令
    public static final String FeatureValue_gotocharge = "gotocharge";//进入充电命令
    public static final String FeatureValue_leavecharge = "leavecharge";//离开充电命令
    public static final String FeatureValue_mp3 = "mp3";//语音命令

    public static final String MissionItemName_nav = "laserNavigation";
    public static final String MissionItemName_gotocharge = "gotoCharge";
    public static final String MissionItemName_leavecharge = "leaveCharge";
    public static final String MissionItemName_mp3 = "mp3";
    public static final String MissionItemName_waiting = "waiting";

    /**
     * 地图点的属性类
     */
    public static class MPointAtts{
        public MPointAtts() {
            this.orderDetailMP = str_zero;
        }

        public String type;
        public String orderDetailMP;
    }
}
