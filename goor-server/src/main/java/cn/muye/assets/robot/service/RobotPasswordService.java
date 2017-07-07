package cn.muye.assets.robot.service;

import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.base.service.BaseService;
import cn.mrobot.bean.assets.robot.RobotPassword;

import java.util.List;

/**
 * Created by Selim on 2017/6/21.
 */
public interface RobotPasswordService extends BaseService<RobotPassword> {
    void saveRobotPassword(Robot robot);

    List<RobotPassword> listRobotPassword(Long robotId);

    void batchUpdateRobotPwdList(List<RobotPassword> robotPasswordList);

    RobotPassword findByRobotIdAndBoxNumAndPswd(RobotPassword robotPassword);
}
