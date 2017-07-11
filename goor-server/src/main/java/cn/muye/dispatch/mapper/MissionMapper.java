package cn.muye.dispatch.mapper;


import cn.mrobot.bean.mission.Mission;
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
public interface MissionMapper {

	long save(Mission mission);

	void update(Mission mission);

	Mission get(long id);

	void delete(long id);

	Mission findByName(String name);

	List<Mission> list(/*@Param("missionMainId") Object missionMainId,*/
                       @Param("name") Object name,
                       @Param("beginDate") Object beginDate,
                       @Param("endDate") Object endDate/*,
                       @Param("priority") Object priority*/);

	List<Mission> listAll();
}

