package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.station.Station;
import cn.muye.area.station.mapper.StationMapper;
import cn.muye.area.station.service.ElevatorstationElevatorXREFService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public List<Station> findByElevator(Long id) {
        List<Station> stations = stationMapper.findStationsByElevator(id);
        if (stations == null) {
            return Lists.newArrayList();
        }
        return stations;
    }
}
