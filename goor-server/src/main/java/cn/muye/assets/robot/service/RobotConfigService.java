package cn.muye.assets.robot.service;


import cn.mrobot.bean.assets.robot.RobotConfig;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
public interface RobotConfigService {

    void add(RobotConfig robotConfig);

    int updateSelective(RobotConfig robotConfig);

    RobotConfig getByRobotId(Long robotId);

    Integer getLowBatteryThreshold(String code, long storeId);
}
