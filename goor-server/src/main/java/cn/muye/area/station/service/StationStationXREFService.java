package cn.muye.area.station.service;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationStationXREF;

import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */
public interface StationStationXREFService {
    void save(Long originStationId, List<Long> accessArriveStationIdList);

    List<StationStationXREF> list(Long id);

    List<StationStationXREF> listByDestinationStationId(Long destId);

    /**
     * 根据站列表保存可到达站信息
     * @param id
     * @param accessArriveStationIdList
     */
    void saveByStationList(Long id, List<Station> accessArriveStationIdList);

    /**
     * 根据当前到达站获取接收信息站的list
     * @param currentArrivalStationId
     * @return
     */
    List<Station> getReceiveNoticeStationList(Long currentArrivalStationId);
}
