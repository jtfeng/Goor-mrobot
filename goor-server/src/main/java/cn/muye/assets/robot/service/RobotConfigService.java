package cn.muye.assets.robot.service;


import cn.mrobot.bean.assets.robot.RobotConfig;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
public interface RobotConfigService {

    void add(RobotConfig robotConfig);

    void update(RobotConfig robotConfig);

    RobotConfig getByRobotId(Long robotId);
}
