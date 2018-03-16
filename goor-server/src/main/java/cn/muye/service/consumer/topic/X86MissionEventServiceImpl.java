package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.mission.JsonLogMission;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.bean.mission.task.JsonMissionItemDataRoadPathUnlock;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.mission.service.LogMissionService;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Created by abel on 17-7-15.
 */
@Service
public class X86MissionEventServiceImpl implements X86MissionEventService {

    private Logger logger = Logger.getLogger(X86MissionEventServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    LogMissionService logMissionService;

    @Autowired
    private MissionListTaskService missionListTaskService;

    @Autowired
    private MissionItemTaskService missionItemTaskService;

    @Override
    public AjaxResult handleX86MissionEvent(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)){
            JsonLogMission jsonLogMission =
                    (JsonLogMission) JsonUtils.fromJson(data,
                            new TypeToken<JsonLogMission>(){}.getType());
            if (jsonLogMission != null &&
                    !StringUtil.isEmpty(jsonLogMission.getObject())){
                LogMission logMission = new LogMission();
                logMission.setTime(jsonLogMission.getTime());
                logMission.setRobotCode(baseMessageService.getSenderId(messageInfo));
                logMission.setMissionListId(jsonLogMission.getMission_list_id());
                logMission.setMissionListRepeatTimes(jsonLogMission.getMission_list_repeat_times());
                logMission.setMissionEvent(jsonLogMission.getEvent());
                logMission.setMissionDescription(jsonLogMission.getDescription());
                //object：表示事件对象，包括mission_list, mission, mission_item
                switch (jsonLogMission.getObject()){
                    case LogMission.object_mission_list:
                        //任务列表
                        logMission.setMissionType(LogMission.MissionLogType.MISSION_LIST.ordinal());
                        break;
                    case LogMission.object_mission:
                        //任务
                        logMission.setMissionType(LogMission.MissionLogType.MISSION.ordinal());
                        logMission.setMissionId(jsonLogMission.getMission_id());
                        logMission.setMissionRepeatTimes(jsonLogMission.getMission_repeat_times());
                        break;
                    case LogMission.object_mission_item:
                        //任务item
                        logMission.setMissionType(LogMission.MissionLogType.MISSION_ITEM.ordinal());
                        logMission.setMissionId(jsonLogMission.getMission_id());
                        logMission.setMissionItemId(jsonLogMission.getMission_item_id());
                        logMission.setMissionRepeatTimes(jsonLogMission.getMission_repeat_times());
                        logMission.setMissionItemName(jsonLogMission.getMission_item_name());
                        //update MissionItemTask
                        String itemName = jsonLogMission.getMission_item_name();
                        if(itemName.equals(MissionFuncsServiceImpl.MissionItemName_load)
                                || itemName.equals(MissionFuncsServiceImpl.MissionItemName_finalUnload)){
                            if(jsonLogMission.getEvent().equals(LogMission.event_start_success)){
                                MissionItemTask missionItemTask = new MissionItemTask();
                                missionItemTask.setId(jsonLogMission.getMission_item_id());
                                missionItemTask.setStartDate(new Date(jsonLogMission.getTime()*1000L));
                                missionItemTaskService.updateSelective(missionItemTask);
                            }else if(jsonLogMission.getEvent().equals(LogMission.event_finish)){
                                MissionItemTask missionItemTask = new MissionItemTask();
                                missionItemTask.setId(jsonLogMission.getMission_item_id());
                                missionItemTask.setFinishDate(new Date(jsonLogMission.getTime()*1000L));
                                missionItemTaskService.updateSelective(missionItemTask);
                            }
                        }else if (itemName.equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)
                            || itemName.equals(MissionFuncsServiceImpl.MissionItemName_unload)){
                            if(jsonLogMission.getEvent().equals(LogMission.event_start_success)){
                                MissionItemTask missionItemTask = new MissionItemTask();
                                missionItemTask.setId(jsonLogMission.getMission_item_id());
                                missionItemTask.setStartDate(new Date(jsonLogMission.getTime()*1000L));
                                missionItemTaskService.updateSelective(missionItemTask);
                            }else if(jsonLogMission.getEvent().equals(LogMission.event_cancel_success)) {
                                MissionItemTask missionItemTask = new MissionItemTask();
                                missionItemTask.setId(jsonLogMission.getMission_item_id());
                                missionItemTask.setFinishDate(new Date(jsonLogMission.getTime() * 1000L));
                                missionItemTaskService.updateSelective(missionItemTask);
                            }
                        }else if (itemName.equals(MissionFuncsServiceImpl.MissionItemName_roadpath_unlock)){
                            MissionItemTask missionItemTask = missionItemTaskService.findById(jsonLogMission.getMission_item_id());
                            if(missionItemTask != null){
                                logger.info("机器解锁任务为" + missionItemTask.getData());
                                JsonMissionItemDataRoadPathUnlock jsonMissionItemDataRoadPathUnlock= JSONObject.parseObject(missionItemTask.getData(), JsonMissionItemDataRoadPathUnlock.class);
                                if(jsonMissionItemDataRoadPathUnlock != null && jsonMissionItemDataRoadPathUnlock.getRoadpath_id().equals(Constant.RELEASE_ROBOT_LOCK_ID)){
                                    if(jsonLogMission.getEvent().equals(LogMission.event_start_success)){
                                        logger.info("最后机器解锁任务开始");
                                        MissionItemTask queryMissionItemTask = new MissionItemTask();
                                        queryMissionItemTask.setId(jsonLogMission.getMission_item_id());
                                        queryMissionItemTask.setStartDate(new Date(jsonLogMission.getTime() * 1000L));
                                        missionItemTaskService.updateSelective(queryMissionItemTask);
                                        logger.info("最后机器解锁任务开始---已记录时间");
                                    }else if(jsonLogMission.getEvent().equals(LogMission.event_finish)) {
                                        logger.info("最后机器解锁任务结束");
                                        MissionItemTask queryMissionItemTask = new MissionItemTask();
                                        queryMissionItemTask.setId(jsonLogMission.getMission_item_id());
                                        queryMissionItemTask.setFinishDate(new Date(jsonLogMission.getTime() * 1000L));
                                        missionItemTaskService.updateSelective(queryMissionItemTask);
                                        logger.info("最后机器解锁任务结束---已记录时间");
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        logMission.setMissionType(LogMission.MissionLogType.NOT_USE.ordinal());
                        break;
                }
                // 17-7-17 继续从缓存里面取电量等缓存值，放入日志
                MessageInfo currentPosInfo = CacheInfoManager.getMessageCache(baseMessageService.getSenderId(messageInfo));
                ChargeInfo chargeInfo = CacheInfoManager.getRobotChargeInfoCache(baseMessageService.getSenderId(messageInfo));
                if (currentPosInfo != null &&
                        !StringUtil.isNullOrEmpty(currentPosInfo.getMessageText())){
                    logMission.setRos(parsePoseData(currentPosInfo));
                }
                if (chargeInfo != null){
                    logMission.setChargingStatus(chargeInfo.getChargingStatus());
                    logMission.setPluginStatus(chargeInfo.getPluginStatus());
                    logMission.setPowerPercent(chargeInfo.getPowerPercent());
                }
                //保存日志
                logMissionService.save(logMission);
                if (logMission.getId() == null){
                    logger.warn("logMissionService save new mission log failed! Pls check!!!");
                }
                MissionListTask missionListTaskDb = missionListTaskService.findById(jsonLogMission.getMission_list_id());
                if (missionListTaskDb != null) {
                    missionListTaskDb.setState(jsonLogMission.getEvent());
                    missionListTaskService.update(missionListTaskDb);
                }
            }
        }
        return null;
    }

    private String parsePoseData(MessageInfo messageInfo) {
        JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
        String poseData = jsonObject.getString("pose");
        JSONObject poseObject = JSON.parseObject(poseData);
        return poseObject.getString("pose");
    }
}
