package cn.muye.assets.robot.mapper;


import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.assets.robot.RobotType;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
public interface RobotTypeMapper extends MyMapper<RobotType>{

    List<RobotType> listType();

    void update(RobotConfig robotConfig);

    RobotConfig getConfigByRobotId(Long robotId);

    void deleteByRobotId(Long id);
}
