package cn.muye.dispatch.mapper;


import cn.mrobot.bean.mission.MissionItem;
import org.apache.ibatis.annotations.Param;

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
public interface MissionItemMapper {

	long save(MissionItem missionItem);

	MissionItem findByName(String name);

	void update(MissionItem missionItem);

	void delete(MissionItem missionItem);

	MissionItem get(long id);

	/*List<MissionItem> list(@Param("missionChainId") Object missionChainId,
						   @Param("name") Object name,
						   @Param("beginDate") Object beginDate,
						   @Param("endDate") Object endDate,
						   @Param("priority") Object priority);*/

	List<MissionItem> list(@Param("name") Object name,
						   @Param("beginDate") Object beginDate,
						   @Param("endDate") Object endDate);

	List<MissionItem> listAll();
}

