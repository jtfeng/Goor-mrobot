package cn.muye.service.consumer.topic;

/**
 * Created by abel on 17-7-27.
 */
public interface X86MissionInstantControlService {

    void sendX86MissionInstantControlPause(
            String robotCode);

    void sendX86MissionInstantControlResume(
            String robotCode);

    void sendX86MissionInstantControlSkipMission(
            String robotCode);

    void sendX86MissionInstantControlSkipMissionList(
            String robotCode);

    void sendX86MissionInstantControlClear(
            String robotCode);

    void sendX86MissionInstantControlStartNextMission(
            String robotCode);
}
