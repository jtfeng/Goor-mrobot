package cn.muye.assets.robot.service;

import cn.mrobot.bean.robot.Robot;
import cn.mrobot.bean.robot.RobotPassword;

import java.util.List;

/**
 * Created by Selim on 2017/6/21.
 */
public interface RobotPasswordService {
    void saveRobotPassword(Robot robot);

    List<RobotPassword> listRobotPassword(Long robotId);
}
