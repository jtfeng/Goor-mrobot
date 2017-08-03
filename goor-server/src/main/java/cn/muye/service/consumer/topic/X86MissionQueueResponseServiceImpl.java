package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-7-27.
 */
@Service
public class X86MissionQueueResponseServiceImpl
        implements X86MissionQueueResponseService {

    private Logger logger = Logger
            .getLogger(X86MissionQueueResponseServiceImpl.class);

    @Override
    public AjaxResult handleX86MissionQueueResponse(MessageInfo messageInfo) {
        return null;
    }
}
