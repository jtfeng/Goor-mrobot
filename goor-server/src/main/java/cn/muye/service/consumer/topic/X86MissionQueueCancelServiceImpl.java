package cn.muye.service.consumer.topic;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-27.
 */
@Service
public class X86MissionQueueCancelServiceImpl
        implements X86MissionQueueCancelService {

    private Logger logger = Logger
            .getLogger(X86MissionQueueCancelServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void sendX86MissionQueueCancel(
            String robotCode,
            List<QueueCancelBody> bodyList) {
        baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.X86_MISSION_QUEUE_CANCEL,
                JsonUtils.toJson(bodyList,
                        new TypeToken<List<QueueCancelBody>>(){}.getType()));
    }

    public static class QueueCancelBody implements Serializable{

        private static final long serialVersionUID = -6561870693660582682L;

        Long missionListId;

        public Long getMissionListId() {
            return missionListId;
        }

        public void setMissionListId(Long missionListId) {
            this.missionListId = missionListId;
        }
    }
}
