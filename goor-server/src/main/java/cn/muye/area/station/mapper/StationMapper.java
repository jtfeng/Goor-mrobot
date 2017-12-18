package cn.muye.area.station.mapper;


import cn.mrobot.bean.area.station.Station;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/17
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
@Component
public interface StationMapper  extends MyMapper<Station> {

//	long save(Station station);
//
//	void update(Station station );
//
//	Station findByStation(long id);
//
	List<Station> list(@Param("name") Object name);

	List<Station> findStationsByRobotCode(String robotCode);

	List<Station> listStationsBySceneAndMapPointType(@Param("sceneId")Long sceneId, @Param("type")Integer type);

	/**
	 * 删除原始机器人和站之间的绑定关系
	 * @param robotCode
	 */
	void deleteStationWithRobotRelationByRobotCode(String robotCode);
}

