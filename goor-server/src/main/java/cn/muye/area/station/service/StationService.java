package cn.muye.area.station.service;


import cn.mrobot.bean.area.station.Station;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

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
public interface StationService extends BaseService<Station>{

	int save(Station station);

	int update(Station station );

	Station findById(long id, long storeId,Long sceneId);

	/**
	 * 查询站列表
	 * @param whereRequest
	 * @param storeId 超级管理员传storeId=null，能查看所有站；医院管理员传storeId!=null，只能查看该医院的站
	 * @param sceneId sceneId=null，不按场景过滤
	 * @return
	 */
	List<Station> list(WhereRequest whereRequest, Long storeId,Long sceneId);

	/**
	 * 根据名称查看站列表
	 * @param name
	 * @param storeId
	 * @param sceneId
	 * @return
	 */
	List<Station> listByName(String name, long storeId,long sceneId);

	int delete(Station station);

	void bindRobots(Station station);

	List<Station> findStationsByRobotCode(String robotCode);
}

