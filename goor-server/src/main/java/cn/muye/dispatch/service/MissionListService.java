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

	void update(MissionList missionList, List<Long> missionIdList,long storeId);

//	void update(MissionList missionList, List<Mission> missions);

    MissionList get(long id,long storeId);

	MissionList findByName(String name,long storeId);

	void delete(MissionList missionList,long storeId);

    List<MissionList> list(WhereRequest whereRequest,long storeId);

	List<MissionList> list(long storeId);

	List<MissionList> list(long storeId,Long sceneId);

	/**
	 * 任务列表带任务带子任务新建或更新并绑定接口
	 * @param missionList
	 * @param missionListDB
	 * @param storeId
	 */
    void updateFull(MissionList missionList, MissionList missionListDB,long storeId);
}

