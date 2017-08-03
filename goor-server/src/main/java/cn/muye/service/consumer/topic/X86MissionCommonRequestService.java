package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;

/**
 * Created by abel on 17-7-27.
 */
public interface X86MissionCommonRequestService {

    AjaxResult sendX86MissionStateCommonRequest(
            String robotCode);

    AjaxResult sendX86MissionQueueCommonRequest(
            String robotCode);

    AjaxResult sendX86MissionStateCommonRequest();

    AjaxResult sendX86MissionQueueCommonRequest();
}
