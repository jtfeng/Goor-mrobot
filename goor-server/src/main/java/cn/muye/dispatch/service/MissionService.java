package cn.muye.dispatch.service;

import cn.mrobot.bean.mission.Mission;
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
public interface MissionService {

	long save(Mission mission);

	void update(Mission mission);

	void updateFull(Mission mission, Mission missionDB,long storeId);

	void update(Mission mission, List<Long> nodeIdList,long storeId);

//	void update(Mission mission, List<MissionItem> missionItems);

    Mission get(long id,long storeId);

	void delete(Mission mission,long storeId);

	Mission findByName(String name,long storeId);

	List<Mission> list(WhereRequest whereRequest,long storeId);

	List<Mission> list(long storeId);
}

