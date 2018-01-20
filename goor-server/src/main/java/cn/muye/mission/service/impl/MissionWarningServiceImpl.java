package cn.muye.mission.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.MissionWarning;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.order.MessageBell;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.FileUtils;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.mission.bean.RobotPositionRecord;
import cn.muye.mission.mapper.MissionWarningMapper;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.order.service.MessageBellService;
import cn.muye.order.service.OrderDetailService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import cn.muye.util.PathUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Selim on 2017/10/23.
 */
@Service
@Transactional
public class MissionWarningServiceImpl implements MissionWarningService {

    private static Logger LOGGER = LoggerFactory.getLogger(MissionWarningServiceImpl.class);

    @Autowired
    private MissionWarningMapper missionWarningMapper;
    @Autowired
    private MissionListTaskService missionListTaskService;
    @Autowired
    private MissionTaskService missionTaskService;
    @Autowired
    private MissionItemTaskService missionItemTaskService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private StationService stationService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private MessageBellService messageBellService;
    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @Override
    public boolean hasExistWarning(MissionWarning missionWarning) {
        Example example = new Example(MissionWarning.class);
        Example.Criteria criteria = example.createCriteria();
        if(missionWarning.getId()!= null){
            criteria.andNotEqualTo("id",missionWarning.getId());
        }
        criteria.andEqualTo("startStationId",missionWarning.getStartStationId())
                .andEqualTo("endStationId", missionWarning.getEndStationId());
        List<MissionWarning> warningList = missionWarningMapper.selectByExample(example);
        if(warningList.size() >= 1) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void save(MissionWarning missionWarning) {
        missionWarning.setCreatedTime(new Date());
        missionWarning.setUpdatedTime(missionWarning.getCreatedTime());
        missionWarningMapper.insert(missionWarning);
    }

    @Override
    public void update(MissionWarning missionWarning) {
        missionWarning.setUpdatedTime(new Date());
        missionWarningMapper.updateByPrimaryKeySelective(missionWarning);
    }

    @Override
    public List<MissionWarning> pageListMissionWarnings(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(MissionWarning.class);
        example.setOrderByClause("UPDATED_TIME DESC");
        List<MissionWarning> missionWarnings = missionWarningMapper.selectByExample(example);
        missionWarnings.forEach(missionWarning -> {
            missionWarning.setStartStation(stationService.findById(missionWarning.getStartStationId()));
            missionWarning.setEndStation(stationService.findById(missionWarning.getEndStationId()));
        });
        return missionWarnings;
    }

    @Override
    public void dailyUpdateWarningData() {
        List<MissionListTask> missionListTaskList = missionListTaskService.findTodayList();
        for (MissionListTask missionListTask : missionListTaskList) {
            List<MissionItemTask> missionItemTaskList = missionItemTaskService.findByListId(missionListTask.getId());
            Date lastFinishDate = null;
            Long lastStationId = null;
            for (MissionItemTask itemTask : missionItemTaskList) {
                //状态为分别为 装货 中途卸货 终点卸货
                if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_load)
                        ||itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)){
                    lastFinishDate = itemTask.getFinishDate();
                    lastStationId = getStationIdByMissionId(itemTask.getMissionId());
                }else if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_unload)){
                    Date finishDate = itemTask.getFinishDate();
                    Long stationId = getStationIdByMissionId(itemTask.getMissionId());
                    if(lastFinishDate != null && lastStationId != null && finishDate != null && stationId != null ){
                        //判定添加至warningList
                        addMissionWarningData(lastStationId, stationId,lastFinishDate, finishDate);
                    }
                    //修改为上次记录
                    lastFinishDate = finishDate;
                    lastStationId = stationId;
                }else if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_finalUnload)){
                    Date finishDate = itemTask.getFinishDate();
                    Long stationId = getStationIdByMissionId(itemTask.getMissionId());
                    if(lastFinishDate != null && lastStationId != null && finishDate != null && stationId != null ){
                        //判定添加至warningList
                        addMissionWarningData(lastStationId, stationId,lastFinishDate, finishDate);
                    }
                }
            }
        }
    }

    @Override
    public Long getWarningTime(Long startStationId, Long endStationId) {
        MissionWarning missionWarning = new MissionWarning();
        missionWarning.setStartStationId(startStationId);
        missionWarning.setEndStationId(endStationId);
        MissionWarning sqlMissionWarning = missionWarningMapper.selectOne(missionWarning);
        if(sqlMissionWarning != null){
            if(sqlMissionWarning.getUseArtificalTime()){
                return sqlMissionWarning.getArtificalTime();
            }else {
                if(sqlMissionWarning.getActionTime()!=null && sqlMissionWarning.getActionTime()>= Constant.USE_CALCULATE_ACTION_TIME){
                    return sqlMissionWarning.getCalculateTime();
                };
            }
        }
        return null;
    }

    @Override
    public void checkRobotWarningState() {
        Robot queryRobot = new Robot();
        queryRobot.setBusy(Boolean.TRUE);
        List<Robot> busyRobotList = robotService.listByDomain(queryRobot);
        List<String> busyRobotCode = Lists.newArrayList();
        for (Robot robot : busyRobotList) {
            //加入忙碌机器人列表
            busyRobotCode.add(robot.getCode());
            MessageInfo currentPoseInfo = CacheInfoManager.getMessageCache(robot.getCode());
            if (null != currentPoseInfo) {
                //获取到当前机器坐标
                MapPoint currentPosition = parsePoseData(currentPoseInfo);
                LinkedList<RobotPositionRecord> robotPositionRecordList = CacheInfoManager.getRobotPositionRecordsCache(robot.getCode());
                //存在位置往内部添加坐标记录
                if(robotPositionRecordList == null){
                    //不存在此记录
                    LinkedList<RobotPositionRecord> newRobotPositionRecordList = Lists.newLinkedList();
                    newRobotPositionRecordList.addLast(new RobotPositionRecord(currentPosition));
                    CacheInfoManager.setRobotPositionRecordsCache(robot.getCode(), newRobotPositionRecordList);
                }else {
                    //最多记录5个, 去除第一个
                    if(robotPositionRecordList.size() == 5){
                        robotPositionRecordList.removeFirst();
                    }
                    robotPositionRecordList.addLast(new RobotPositionRecord(currentPosition));
                    //在线判定是否机器人长时间位置不变
                    //获取5坐标差值总和
                    MapPoint lastMapPoint = null;
                    long totalDistance = 0;
                    if(robotPositionRecordList.size() == 5){
                        for (RobotPositionRecord robotPositionRecord : robotPositionRecordList) {
                            MapPoint positionRecord = robotPositionRecord.getRecordPosition();
                            if(lastMapPoint == null){
                                lastMapPoint = positionRecord;
                            }else {
                                totalDistance += PathUtil.calDistance(positionRecord, lastMapPoint);;
                            }
                        }
                    }
                    LOGGER.info("机器人（" + robot.getCode() + "）5分钟内位移距离为" + totalDistance + "mm");
                    //判定totalDistance, 坐标变化总和小于1m
                    if(totalDistance < 1000){
                        MapInfo currentMapInfo = getCurrentMapInfo(robot.getCode());
                        String message = "";
                        if(currentMapInfo != null){
                            message =  robot.getCode() + "在" + currentMapInfo.getLogicFloor() + "楼停滞超过5分钟";
                        }else {
                            message =  robot.getCode() + "已停滞超过5分钟";
                        }
                        sendWebSocketSendMessage(message, robot.getId());
                    }
                }
            } else {
                LOGGER.info("未获取到当前机器人（" + robot.getCode() + "）实时坐标");
                LinkedList<RobotPositionRecord> robotPositionRecordList = CacheInfoManager.getRobotPositionRecordsCache(robot.getCode());
                if(robotPositionRecordList != null){
                    RobotPositionRecord lastRobotPositionRecord = robotPositionRecordList.getLast();
                    int minsGap = DateTimeUtils.getTimeGap(new Date(), lastRobotPositionRecord.getRecordDate());
                    if(minsGap >= 5){
                        //提醒已离线 5分钟
                        String message = "机器人" + robot.getCode() + "已离线";
                        LOGGER.info(message);
                        sendWebSocketSendMessage(message, robot.getId());
                    }
                }else {
                    String message = "机器人" + robot.getCode() + "已离线";
                    LOGGER.info(message);
                    sendWebSocketSendMessage(message, robot.getId());
                }
            }
        }
        //除去非忙碌状态下机器人的map坐标点
        CacheInfoManager.removeUnBusyRobotsCache(busyRobotCode);
    }

    //发送警告信息至messageBell(cancel此方案)
    public void sendMessageBell(String message, String robotCode){
        MessageBell messageBell = new MessageBell(message, robotCode, OrderConstant.MESSAGE_BELL_ROBOT_POSITION_WARNING, null, OrderConstant.MESSAGE_BELL_UNREAD);
        messageBellService.save(messageBell);
    };

    //发送警告信息至websocket
    private void sendWebSocketSendMessage(String message, Long robotId) {
        Long sceneId = robotService.getRobotSceneId(robotId);
        WSMessage ws = new WSMessage.Builder().
                title(LogType.WARNING_TIMEOUT.getValue())
                .messageType(WSMessageType.WARNING)
                .body(message)
                .deviceId(sceneId + "")
                .module(LogType.WARNING_TIMEOUT.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(ws);
    }

    /**
     * 获取机器人当前地图信息
     *
     * @param code 机器人编号
     * @return 机器人当前地图信息
     */
    private MapInfo getCurrentMapInfo(String code) {
        //根据场景名和地图名获取地图信息
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
        if (null == currentMap) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
        String data = jsonObject.getString(TopicConstants.DATA);
        JSONObject object = JSON.parseObject(data);
        Integer errorCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
        if (errorCode != null && errorCode == 0) {
            String mapData = object.getString(TopicConstants.DATA);
            JSONObject mapObject = JSON.parseObject(mapData);
            String mapName = mapObject.getString(TopicConstants.MAP_NAME);
            String sceneName = mapObject.getString(TopicConstants.SCENE_NAME);
            MapInfo mapInfo = CacheInfoManager.getMapOriginalCache(FileUtils.parseMapAndSceneName(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID));
            if (mapInfo != null) {
                return mapInfo;
            }
        }
        return null;
    }


    private static MapPoint parsePoseData(MessageInfo messageInfo) {
        JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
        JSONObject poseObject = jsonObject.getJSONObject("pose");
        JSONObject innerPoseObject = poseObject.getJSONObject("pose");
        JSONObject positionObject = innerPoseObject.getJSONObject("position");
        MapPoint mapPoint = new MapPoint();
        mapPoint.setX(positionObject.getDoubleValue("x"));
        mapPoint.setY(positionObject.getDoubleValue("y"));
        mapPoint.setTh(positionObject.getDoubleValue("z"));
        return mapPoint;
    }

    //通过missionId 获取对应站id
    public Long getStationIdByMissionId(Long missionId){
        MissionTask missionTask = missionTaskService.findById(missionId);
        if(missionTask == null){
            return null;
        }else {
            OrderDetail orderDetail = orderDetailService.findById(Long.parseLong(missionTask.getOrderDetailMission()));
            return orderDetail == null ? null: orderDetail.getStationId();
        }
    }

    //添加记录
    public void addMissionWarningData(Long startStationId, Long endStationId, Date lastFinishDate, Date finishDate){
        MissionWarning missionWarning = new MissionWarning();
        missionWarning.setStartStationId(startStationId);
        missionWarning.setEndStationId(endStationId);
        long timeGap = DateTimeUtils.getTimeGapSecond(finishDate, lastFinishDate);
        MissionWarning sqlMissionWarning = missionWarningMapper.selectOne(missionWarning);
        if(sqlMissionWarning == null){
            //新增
            missionWarning.setActionTime(1);
            missionWarning.setCreatedTime(new Date());
            missionWarning.setUpdatedTime(missionWarning.getCreatedTime());
            missionWarning.setCalculateTime(timeGap);
            missionWarningMapper.insert(missionWarning);
        }else{
            //修改
            sqlMissionWarning.setUpdatedTime(new Date());
            int actionTime = sqlMissionWarning.getActionTime();
            long calculateTime = sqlMissionWarning.getCalculateTime();
            long newCalculateTime = (calculateTime * actionTime + timeGap)/(actionTime + 1);
            sqlMissionWarning.setActionTime(actionTime + 1);
            sqlMissionWarning.setCalculateTime(newCalculateTime);
            missionWarningMapper.updateByPrimaryKey(sqlMissionWarning);
        }
    }


}
