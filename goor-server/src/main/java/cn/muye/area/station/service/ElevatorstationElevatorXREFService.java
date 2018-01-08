package cn.muye.area.station.service;

import cn.mrobot.bean.area.station.Station;

import java.util.List;

/**
 * @author wlkfec
 */
public interface ElevatorstationElevatorXREFService {

    public List<Station> findByElevator(Long id);

}
