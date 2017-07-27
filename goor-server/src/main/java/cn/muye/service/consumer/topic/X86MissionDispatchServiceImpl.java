package cn.muye.service.consumer.topic;

import cn.mrobot.bean.constant.TopicConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-7-15.
 */
@Service
public class X86MissionDispatchServiceImpl
        implements X86MissionDispatchService {

    private Logger logger = Logger
            .getLogger(X86MissionDispatchServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void sendX86MissionDispatch(
            String robotCode,
            String missionListData) {
        baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.X86_MISSION_DISPATCH,
                missionListData);
    }
}
