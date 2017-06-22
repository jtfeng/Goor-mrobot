package cn.muye.area.station.service;


import cn.mrobot.bean.area.station.StationMapPointXREF;
import cn.mrobot.utils.WhereRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/20
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
public interface StationMapPointXREFService {

	long save(StationMapPointXREF stationMapPointXREF);

	void update(StationMapPointXREF stationMapPointXREF);

	StationMapPointXREF get(long id);

	List<StationMapPointXREF> list(WhereRequest whereRequest);

	List<StationMapPointXREF> listByStationId(Long stationId);

	void delete(long id);

	void deleteByStationId(long id);
}

