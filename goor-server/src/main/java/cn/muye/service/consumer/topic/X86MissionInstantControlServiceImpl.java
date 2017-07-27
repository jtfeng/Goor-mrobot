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
public class X86MissionInstantControlServiceImpl
        implements X86MissionInstantControlService {

    private Logger logger = Logger
            .getLogger(X86MissionInstantControlServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void sendX86MissionInstantControlPause(String robotCode) {
        send(robotCode, "pause");
    }

    @Override
    public void sendX86MissionInstantControlResume(String robotCode) {
        send(robotCode, "resume");
    }

    @Override
    public void sendX86MissionInstantControlSkipMission(String robotCode) {
        send(robotCode, "skipMission");

    }

    @Override
    public void sendX86MissionInstantControlSkipMissionList(String robotCode) {
        send(robotCode, "skipMissionList");

    }

    @Override
    public void sendX86MissionInstantControlClear(String robotCode) {
        send(robotCode, "clear");

    }

    @Override
    public void sendX86MissionInstantControlStartNextMission(String robotCode) {
        send(robotCode, "startNextMission");

    }

    void send(String robotCode, String command){
        MissionInstantControlBody body =
                new MissionInstantControlBody();
        body.setCommand(command);
        baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.X86_MISSION_INSTANT_CONTROL,
                JsonUtils.toJson(body,
                        new TypeToken<MissionInstantControlBody>(){}.getType()));
    }

    class MissionInstantControlBody implements Serializable{
        private static final long serialVersionUID = -9141994816589400751L;
        String command;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
