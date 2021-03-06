package cn.muye.dispatch.mapper;

import cn.mrobot.bean.mission.MissionListMissionXREF;
import org.apache.ibatis.annotations.Param;

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

	void deleteByListId(long missionListId);

	List<MissionListMissionXREF> findByListId(long missionListId);

	List<MissionListMissionXREF> findByListId(@Param("missionListId")long missionListId,
											  @Param("storeId")Long storeId);
}
