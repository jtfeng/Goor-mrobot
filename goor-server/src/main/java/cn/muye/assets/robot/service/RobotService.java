package cn.muye.assets.robot.service;

import cn.mrobot.bean.assets.robot.Robot;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
public interface RobotService {

    List<Robot> listRobot();

    Robot getById(Long id);

    void update(Robot robotDb);

    void save(Robot robot);

    void deleteById(Long id);

    Robot getByName(String name);

    Robot getByCode(String code);
}
