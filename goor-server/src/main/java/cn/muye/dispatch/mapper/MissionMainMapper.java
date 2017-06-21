package cn.muye.dispatch.mapper;


import cn.mrobot.bean.misssion.MissionMain;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
public interface MissionMainMapper {

	long save(MissionMain missionMain);

	void update(MissionMain missionMain);

	MissionMain get(long id);

	MissionMain findByName(String name);

	void delete(long id);

	List<MissionMain> listAll();

	List<MissionMain> list(@Param("name") Object name,
						   @Param("deviceId") Object deviceId,
						   @Param("beginDate") Object beginDate,
						   @Param("endDate") Object endDate,
						   @Param("priority") Object priority);
}

