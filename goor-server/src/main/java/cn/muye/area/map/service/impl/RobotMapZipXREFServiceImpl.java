package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.muye.area.map.mapper.RobotMapZipXREFMapper;
import cn.muye.area.map.service.RobotMapZipXREFService;
import cn.muye.base.service.BaseCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Jelynn on 2017/8/15.
 */
@Service
public class RobotMapZipXREFServiceImpl implements RobotMapZipXREFService {

    @Autowired
    private RobotMapZipXREFMapper robotMapZipXREFMapper;

    @Override
    public void save(RobotMapZipXREF robotMapZipXREF) {
        if (null == robotMapZipXREF) {
            return;
        }
        robotMapZipXREFMapper.insert(robotMapZipXREF);
    }

    @Override
    public List<RobotMapZipXREF> findByRobotId(Long robotId, Long sceneId) {
        Example example = new Example(RobotMapZipXREF.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andCondition("ROBOT_ID = " + robotId).andCondition("SCENE_ID=" + sceneId).andCondition("DELETE_FLAG = 0");
        return robotMapZipXREFMapper.selectByExample(example);
    }

    @Override
    public void removeBySceneId(Long sceneId) {
        robotMapZipXREFMapper.removeBySceneId(sceneId, 1, new Date());
    }
}
