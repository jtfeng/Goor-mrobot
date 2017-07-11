package cn.muye.area.station.service;

import cn.mrobot.bean.area.station.StationRobotXREF;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/11.
 */
public interface StationRobotXREFService {

    long save(StationRobotXREF stationRobotXREF);

    int deleteByStationId(Long id);

    List<StationRobotXREF> getByStationId(Long id);
}
