package cn.muye.area.station.service;

import cn.mrobot.bean.area.station.StationStationXREF;

import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */
public interface StationStationXREFService {
    void save(Long originStationId, List<Long> accessArriveStationIdList);

    List<StationStationXREF> list(Long id);

    List<StationStationXREF> listByDestinationStationId(Long destId);
}
