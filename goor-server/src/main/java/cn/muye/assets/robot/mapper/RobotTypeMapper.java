package cn.muye.assets.robot.mapper;


import cn.mrobot.bean.robot.RobotType;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
public interface RobotTypeMapper extends MyMapper<RobotType>{

    List<RobotType> listType();

}
