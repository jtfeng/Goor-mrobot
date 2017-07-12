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

	Station findById(long id, long storeId);

	List<Station> list(WhereRequest whereRequest, Long storeId);

	List<Station> listByName(String name);

	int delete(Station station);

	void bindRobots(Station station);
}

