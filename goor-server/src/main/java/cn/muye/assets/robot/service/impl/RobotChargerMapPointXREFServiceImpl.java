package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.RobotChargerMapPointXREF;
import cn.muye.assets.robot.mapper.RobotChargerMapPointXREFMapper;
import cn.muye.assets.robot.service.RobotChargerMapPointXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/13.
 */
@Service
@Transactional
public class RobotChargerMapPointXREFServiceImpl implements RobotChargerMapPointXREFService {

    @Autowired
    private RobotChargerMapPointXREFMapper robotChargerMapPointXREFMapper;

    @Override
    public int deleteByRobotId(Long id) {
        Example example = new Example(StationRobotXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("robotId", id);
        return robotChargerMapPointXREFMapper.deleteByExample(example);
    }

    @Override
    public int save(RobotChargerMapPointXREF robotChargerMapPointXREF) {
        return robotChargerMapPointXREFMapper.insert(robotChargerMapPointXREF);
    }

    @Override
    public List<RobotChargerMapPointXREF> getByRobotId(Long id) {
        Example example = new Example(StationRobotXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ROBOT_ID=", id);
        return robotChargerMapPointXREFMapper.selectByExample(example);
    }
}
