package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.RobotType;
import cn.muye.assets.robot.mapper.RobotTypeMapper;
import cn.muye.assets.robot.service.RobotTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotTypeServiceImpl implements RobotTypeService {

    @Autowired
    private RobotTypeMapper robotTypeMapper;

    public List<RobotType> listType() {
        List<RobotType> list = robotTypeMapper.listType();
        return list;
    }
}
