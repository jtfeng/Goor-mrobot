package cn.muye.dispatch.service;


import cn.mrobot.bean.misssion.MissionNode;
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
public interface MissionNodeService {

	long save(MissionNode missionNode);

	MissionNode findByName(String name);

	void update(MissionNode missionNode);

	void delete(MissionNode missionNode);

    MissionNode get(long id);

    List<MissionNode> list(WhereRequest whereRequest);

	List<MissionNode> list();
}

