package cn.muye.base.consumer.service;

import cn.mrobot.utils.JsonUtils;
import cn.muye.base.bean.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-7-15.
 */
@Service
public class X86MissionEventServiceImpl implements X86MissionEventService {

    private Logger logger = Logger.getLogger(X86MissionEventServiceImpl.class);

    @Override
    public void handleX86MissionEvent(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
    }
}
