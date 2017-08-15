package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.muye.area.map.mapper.RobotMapZipXREFMapper;
import cn.muye.area.map.service.RobotMapZipXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/15.
 */
@Service
public class RobotMapZipXREFServiceImpl implements RobotMapZipXREFService {

    @Autowired
    private RobotMapZipXREFMapper robotMapZipXREFMapper;

    @Override
    public void saveOrUpdate(RobotMapZipXREF robotMapZipXREF) {
        if (null == robotMapZipXREF)
            return;
        Long robotId = robotMapZipXREF.getRobotId();
        List<RobotMapZipXREF> robotMapZipXREFList = findByRobotId(robotId);
        if (robotMapZipXREFList.size() <= 0) {
            robotMapZipXREFMapper.insert(robotMapZipXREF);
        } else {
            RobotMapZipXREF robotMapZipXREFDB = robotMapZipXREFList.get(0);
            //将上一次的压缩ID改为历史
            robotMapZipXREF.setLastMapZipId(robotMapZipXREFDB.getNewMapZipId());
            updateByRobotId(robotMapZipXREF);
        }
    }

    public void updateByRobotId(RobotMapZipXREF robotMapZipXREF) {
        Example example = new Example(RobotMapZipXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ROBOT_ID=" + robotMapZipXREF.getRobotId());
        robotMapZipXREFMapper.updateByExampleSelective(robotMapZipXREF, example);
    }

    public List<RobotMapZipXREF> findByRobotId(Long robotId) {
        Example example = new Example(RobotMapZipXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ROBOT_ID = " + robotId);
        return robotMapZipXREFMapper.selectByExample(example);
    }
}
