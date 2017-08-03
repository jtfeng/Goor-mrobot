package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;

import java.util.List;

/**
 * Created by abel on 17-7-27.
 */
public interface X86MissionQueueCancelService {

    AjaxResult sendX86MissionQueueCancel(
            String robotCode,
            List<X86MissionQueueCancelServiceImpl.QueueCancelBody> bodyList);

}
