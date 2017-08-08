package cn.muye.assets.robot.mapper;


import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.util.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
public interface RobotMapper extends MyMapper<Robot> {

    List<Robot> listRobot(Map map);

    Robot getRobotByCode(Map map);
//
//    Robot getById(Integer id);
//
//    void update(Robot robotDb);
//
//    void save(Robot robot);
//
//    void deleteById(Integer id);
//
//    Robot getByName(String name);
//
//    Robot getByCode(String code);
}
