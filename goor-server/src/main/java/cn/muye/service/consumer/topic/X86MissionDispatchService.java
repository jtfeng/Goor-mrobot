package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;

/**
 * Created by abel on 17-7-11.
 */
public interface X86MissionDispatchService {

    AjaxResult sendX86MissionDispatch(
            String robotCode,
            String missionListData);
}
