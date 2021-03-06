package cn.muye.dispatch.mapper;

import cn.mrobot.bean.mission.MissionMissionItemXREF;

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
public interface MissionMissionItemXREFMapper {

	long save(MissionMissionItemXREF missionMissionItemXREF);

	void delete(long  id);

	void deleteByMissionId(long  missionId);

	List<MissionMissionItemXREF> findByMissionId(long  missionChainId);
}
