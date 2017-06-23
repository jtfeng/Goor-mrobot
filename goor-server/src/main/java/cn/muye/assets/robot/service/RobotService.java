package cn.muye.assets.robot.service;

import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
public interface RobotService extends BaseService<Robot>{

    List<Robot> listRobot();

    Robot getById(Long id);

    void saveRobot(Robot robot);

    void deleteRobotById(Long id);

    Robot getByName(String name);

    Robot getByCode(String code);
}
