package cn.muye.area.station.mapper;

import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationStationXREF;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */
@Component
public interface StationStationXREFMapper extends MyMapper<StationStationXREF> {

    List<Station> getReceiveNoticeStationList(@Param("currentArrivalStationId") Long currentArrivalStationId,
                                              @Param("type") int type);
}
