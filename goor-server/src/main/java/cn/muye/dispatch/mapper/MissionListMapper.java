package cn.muye.dispatch.mapper;


import cn.mrobot.bean.mission.MissionList;
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
public interface MissionListMapper {

	long save(MissionList missionList);

	void update(MissionList missionList);

	MissionList get(@Param("id")long id,
					@Param("storeId")long storeId);

	MissionList findByName(@Param("name")String name,
						   @Param("storeId")long storeId,
						   @Param("sceneId")Long sceneId);

	void delete(@Param("id")long id,
				@Param("storeId")long storeId);

	List<MissionList> listAll(long storeId);

	List<MissionList> list(@Param("name") Object name,
						   @Param("mapName") Object mapName,
                           /*@Param("deviceId") Object deviceId,*/
                           @Param("beginDate") Object beginDate,
                           @Param("endDate") Object endDate,
                           @Param("priority") Object priority,
						   @Param("storeId") Object storeId,
						   @Param("sceneId") Object sceneId,
						   @Param("missionListType") Object missionListType);
}

