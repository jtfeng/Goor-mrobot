package cn.muye.dispatch.service;


import cn.mrobot.bean.mission.Mission;
import cn.mrobot.bean.mission.MissionList;
import cn.mrobot.utils.WhereRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/9
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
public interface MissionListService {

	long save(MissionList missionList);

	void update(MissionList missionList);

	void update(MissionList missionList, List<Long> missionIdList);

//	void update(MissionList missionList, List<Mission> missions);

    MissionList get(long id);

	MissionList findByName(String name);

	void delete(MissionList missionList);

    List<MissionList> list(WhereRequest whereRequest);

	List<MissionList> list();
}

