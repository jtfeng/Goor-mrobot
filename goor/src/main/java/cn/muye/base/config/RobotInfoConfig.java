package cn.muye.base.config;

import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.base.cache.CacheInfoManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by admin on 2017/7/17.
 */
@Configuration
public class RobotInfoConfig {

    @Value("${robot.name}")
    private String robotName;

    @Value("${robot.sn}")
    private String robotCode;

    @Value("${robot.typeId}")
    private int robotTypeId;

    @Value("${robot.batteryThreshold}")
    private int robotBatteryThreshold;

    @Bean
    public Robot getRobotInfo() {
        Robot robot = new Robot();
        robot.setName(robotName);
        robot.setCode(robotCode);
        robot.setBatteryThreshold(robotBatteryThreshold);
        robot.setTypeId(robotTypeId);
        CacheInfoManager.setRobotInfoCache(robot);
        return robot;
    }
}
