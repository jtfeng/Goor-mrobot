package cn.muye.dispatch.service;

import cn.mrobot.bean.misssion.MissionChain;
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
public interface MissionChainService {

	long save(MissionChain missionChain);

	void update(MissionChain missionChain);

	void update(MissionChain missionChain, List<Long> nodeIdList);

    MissionChain get(long id);

	void delete(MissionChain missionChain);

	MissionChain findByName(String name);

	List<MissionChain> list(WhereRequest whereRequest);

	List<MissionChain> list();
}

