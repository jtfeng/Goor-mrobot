package cn.muye.area.station.service.impl;

import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.muye.area.station.mapper.StationRobotXREFMapper;
import cn.muye.area.station.service.StationRobotXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/11.
 */
@Service
@Transactional
public class StationRobotXREFServiceImpl implements StationRobotXREFService {

    @Autowired
    private StationRobotXREFMapper stationRobotXREFMapper;

    @Override
    public long save(StationRobotXREF stationRobotXREF) {
        return stationRobotXREFMapper.insert(stationRobotXREF);
    }

    @Override
    public int deleteByStationId(Long id) {
        Example example = new Example(StationRobotXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stationId", id);
        return stationRobotXREFMapper.deleteByExample(example);
    }

    @Override
    public List<StationRobotXREF> getByStationId(Long id) {
        Example example = new Example(StationRobotXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("STATION_ID=", id);
        List<StationRobotXREF> list = stationRobotXREFMapper.selectByExample(example);
        return list;
    }
}
