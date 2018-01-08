package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.service.ElevatorstationElevatorXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wlkfec
 */
@Transactional
@Service
public class ElevatorstationElevatorXREFServiceImpl implements ElevatorstationElevatorXREFService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public Station findByElevator(Long id) {
        List<Station> stations = stationMapper.findStationsByElevator(id);
        if (stations != null && stations.size() > 0) {
            return stations.get(0);
        }
        return null;
    }
}
