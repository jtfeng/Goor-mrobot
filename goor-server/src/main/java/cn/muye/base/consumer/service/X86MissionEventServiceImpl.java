package cn.muye.base.consumer.service;

import cn.mrobot.bean.log.mission.JsonLogMission;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.log.mission.service.LogMissionService;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void handleX86MissionEvent(MessageInfo messageInfo) {
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
                        break;
                        default:
                            break;
                }
                // TODO: 17-7-17 继续从缓存里面取电量等缓存值，放入日志

                //保存日志
                logMissionService.save(logMission);
                if (logMission.getId() == null){
                    logger.warn("logMissionService save new mission log failed! Pls check!!!");
                }
            }
        }
    }
}
