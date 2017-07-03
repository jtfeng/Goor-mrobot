package cn.muye.dispatch.service;


import cn.mrobot.bean.misssion.MissionItem;
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
public interface MissionItemService {

	long save(MissionItem missionItem);

	MissionItem findByName(String name);

	void update(MissionItem missionItem);

	void delete(MissionItem missionItem);

    MissionItem get(long id);

    List<MissionItem> list(WhereRequest whereRequest);

	List<MissionItem> list();
}

