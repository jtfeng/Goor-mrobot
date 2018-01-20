package cn.muye.mission.service;

import cn.mrobot.bean.mission.MissionWarning;

import java.util.List;

/**
 * Created by Selim on 2017/10/23.
 */
public interface MissionWarningService {

    boolean hasExistWarning(MissionWarning missionWarning);

    void save(MissionWarning missionWarning);

    void update(MissionWarning missionWarning);

    List<MissionWarning> pageListMissionWarnings(int page, int pageSize);

    void dailyUpdateWarningData();

    Long getWarningTime(Long startStationId, Long endStationId);

    //定时检测机器人状态
    void checkRobotWarningState();


}
