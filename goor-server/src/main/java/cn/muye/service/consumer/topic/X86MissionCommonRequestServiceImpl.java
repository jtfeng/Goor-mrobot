package cn.muye.service.consumer.topic;

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
    public void sendX86MissionStateCommonRequest(String robotCode) {
        send(robotCode, "currentState");
    }

    @Override
    public void sendX86MissionQueueCommonRequest(String robotCode) {
        send(robotCode, "missionQueue");
    }

    @Override
    public void sendX86MissionStateCommonRequest() {
        send("currentState");
    }

    @Override
    public void sendX86MissionQueueCommonRequest() {
        send("missionQueue");
    }

    void send(String robotCode, String content){
        MissionCommonRequestBody body =
                new MissionCommonRequestBody();
        body.setContent(content);
        baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.X86_MISSION_COMMON_REQUEST,
                JsonUtils.toJson(body,
                        new TypeToken<MissionCommonRequestBody>(){}.getType()));
    }

    void send(String content){
        MissionCommonRequestBody body =
                new MissionCommonRequestBody();
        body.setContent(content);
        baseMessageService.sendAllRobotMessage(
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
