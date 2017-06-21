package cn.muye.dispatch.mapper;

import cn.mrobot.bean.misssion.MissionChainNodeXREF;
import cn.mrobot.bean.misssion.MissionMainChainXREF;

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
public interface MissionMainChainXREFMapper {

	long save(MissionMainChainXREF missionMainChainXREF);

	void delete(long id);

	void deleteByMainId(long missionMainId);

	List<MissionMainChainXREF> findByMainId(long missionMainId);
}
