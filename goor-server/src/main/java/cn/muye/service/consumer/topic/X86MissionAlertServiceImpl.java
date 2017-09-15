package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.alert.JsonLogAlert;
import cn.mrobot.bean.log.alert.LogAlert;
import cn.mrobot.bean.log.mission.JsonLogMission;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.utils.DateUtil;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.alert.service.LogAlertService;
import cn.muye.log.mission.service.LogMissionService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ray.fu on 2017/9/11.
 */
@Service
public class X86MissionAlertServiceImpl implements X86MissionAlertService {

    private Logger logger = Logger.getLogger(X86MissionAlertServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    LogAlertService logAlertService;

    @Override
    public AjaxResult handleX86MissionAlert(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(messageInfo, new TypeToken<MessageInfo>() {
        }.getType()));
        String data = baseMessageService.getData(messageInfo);
        JSONObject jsonObject = JSON.parseObject(data);
        String jsonData = (String) jsonObject.get(TopicConstants.DATA);
        if (!StringUtil.isEmpty(jsonData)) {
//            JsonLogAlert jsonLogAlert = (JsonLogAlert) JsonUtils.fromJson(jsonData, new TypeToken<JsonLogAlert>() {
//            }.getType());
            JsonLogAlert jsonLogAlert = JSON.parseObject(jsonData, JsonLogAlert.class);
            if (jsonLogAlert != null) {
                //设置最新任务报警状态的缓存
                CacheInfoManager.setRobotMissionAlertStatusCache(baseMessageService.getSenderId(messageInfo), String.valueOf(jsonLogAlert.getAlert_code()));
                LogAlert logAlert = new LogAlert();
                logAlert.setRobotCode(baseMessageService.getSenderId(messageInfo));
                logAlert.setAlertCode(String.valueOf(jsonLogAlert.getAlert_code()));
                logAlert.setAlertTime(new Date(jsonLogAlert.getAlert_time()*1000));
                logAlert.setDescription(jsonLogAlert.getMsg());
                logAlert.setMissionItemId(jsonLogAlert.getMission_item_id());
                logAlertService.save(logAlert);
            }
        }
        return null;
    }
}
