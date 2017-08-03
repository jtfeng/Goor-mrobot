package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;

/**
 * Created by abel on 17-7-27.
 */
public interface X86MissionInstantControlService {

    AjaxResult sendX86MissionInstantControlPause(
            String robotCode);

    AjaxResult sendX86MissionInstantControlResume(
            String robotCode);

    AjaxResult sendX86MissionInstantControlSkipMission(
            String robotCode);

    AjaxResult sendX86MissionInstantControlSkipMissionList(
            String robotCode);

    AjaxResult sendX86MissionInstantControlClear(
            String robotCode);

    AjaxResult sendX86MissionInstantControlStartNextMission(
            String robotCode);
}
