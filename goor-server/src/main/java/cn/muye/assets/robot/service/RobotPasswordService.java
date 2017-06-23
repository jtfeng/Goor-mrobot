package cn.muye.assets.robot.service;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.robot.RobotPassword;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Selim on 2017/6/21.
 */
public interface RobotPasswordService extends BaseService<RobotPassword> {
    void saveRobotPassword(Robot robot);

    List<RobotPassword> listRobotPassword(Long robotId);

    void batchUpdateRobotPwdList(List<RobotPassword> robotPasswordList);
}
