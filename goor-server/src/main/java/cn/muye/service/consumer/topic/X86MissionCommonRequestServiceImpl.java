package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created by abel on 17-7-27.
 */
@Service
public class X86MissionCommonRequestServiceImpl
        implements X86MissionCommonRequestService {

    private Logger logger = Logger
            .getLogger(X86MissionCommonRequestServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public AjaxResult sendX86MissionStateCommonRequest(String robotCode) {
        return send(robotCode, "currentState");
    }

    @Override
    public AjaxResult sendX86MissionQueueCommonRequest(String robotCode) {
        return send(robotCode, "missionQueue");
    }

    @Override
    public AjaxResult sendX86MissionStateCommonRequest() {
        return send("currentState");
    }

    @Override
    public AjaxResult sendX86MissionQueueCommonRequest() {
        return send("missionQueue");
    }

    AjaxResult send(String robotCode, String content){
        MissionCommonRequestBody body =
                new MissionCommonRequestBody();
        body.setContent(content);
        return baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.X86_MISSION_COMMON_REQUEST,
                JsonUtils.toJson(body,
                        new TypeToken<MissionCommonRequestBody>(){}.getType()));
    }

    AjaxResult send(String content){
        MissionCommonRequestBody body =
                new MissionCommonRequestBody();
        body.setContent(content);
        return baseMessageService.sendAllRobotMessage(
                TopicConstants.X86_MISSION_COMMON_REQUEST,
                JsonUtils.toJson(body,
                        new TypeToken<MissionCommonRequestBody>(){}.getType()));
    }

    /**
     * request 请求体的数据
     */
    class MissionCommonRequestBody implements Serializable{
        private static final long serialVersionUID = -575033553662614248L;

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
