package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.area.station.StationStationXREF;
import cn.muye.area.station.mapper.StationStationXREFMapper;
import cn.muye.area.station.service.StationStationXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */
@Transactional
@Service
public class StationStationXREFServiceImpl implements StationStationXREFService {

    @Autowired
    private StationStationXREFMapper stationStationXREFMapper;

    @Override
    public void save(Long originStationId, List<Long> accessArriveStationIdList) {
        Example example = new Example(StationStationXREF.class);
        example.createCriteria().andCondition("ORIGIN_STATION_ID=", originStationId);
        stationStationXREFMapper.deleteByExample(example);
        accessArriveStationIdList.stream().forEach(stationId ->{
            StationStationXREF xref = new StationStationXREF();
            xref.setOriginStationId(originStationId);
            xref.setDestinationStationId(stationId);
            stationStationXREFMapper.insert(xref);
        });
    }

    @Override
    public List<StationStationXREF> list(Long id) {
        Example example = new Example(StationStationXREF.class);
        example.createCriteria().andCondition("ORIGIN_STATION_ID=", id);
        return stationStationXREFMapper.selectByExample(example);
    }
}
