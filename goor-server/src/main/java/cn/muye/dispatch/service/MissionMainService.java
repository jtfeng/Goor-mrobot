package cn.muye.dispatch.service;


import cn.mrobot.bean.misssion.MissionChain;
import cn.mrobot.bean.misssion.MissionMain;
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
public interface MissionMainService{

	long save(MissionMain missionMain);

	void update(MissionMain missionMain);

	void update(MissionMain missionMain, List<Long> nodeIdList);

    MissionMain get(long id);

	MissionMain findByName(String name);

	void delete(MissionMain missionMain);

    List<MissionMain> list(WhereRequest whereRequest);

	List<MissionMain> list();
}

