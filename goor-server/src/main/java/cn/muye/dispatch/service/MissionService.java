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

	void update(Mission mission, List<Long> nodeIdList);

    Mission get(long id);

	void delete(Mission mission);

	Mission findByName(String name);

	List<Mission> list(WhereRequest whereRequest);

	List<Mission> list();
}

