package cn.muye.dispatch.mapper;

import cn.mrobot.bean.mission.MissionListMissionXREF;

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
public interface MissionListMissionXREFMapper {

	long save(MissionListMissionXREF missionListMissionXREF);

	void delete(long id);

	void deleteByMainId(long missionMainId);

	List<MissionListMissionXREF> findByMainId(long missionMainId);
}
