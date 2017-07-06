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

	MissionList get(long id);

	MissionList findByName(String name);

	void delete(long id);

	List<MissionList> listAll();

	List<MissionList> list(@Param("name") Object name,
                           @Param("deviceId") Object deviceId,
                           @Param("beginDate") Object beginDate,
                           @Param("endDate") Object endDate,
                           @Param("priority") Object priority);
}

