package cn.muye.dispatch.mapper;

import cn.mrobot.bean.misssion.MissionChainNodeXREF;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/12
 * Time: 14:37
 * Describe:
 * Version:1.0
 */
public interface MissionChainNodeXREFMapper {

	long save(MissionChainNodeXREF missionChainNodeXREF);

	void delete(long  id);

	void deleteByChainId(long  missionChainId);

	List<MissionChainNodeXREF> findByChainId(long  missionChainId);
}
