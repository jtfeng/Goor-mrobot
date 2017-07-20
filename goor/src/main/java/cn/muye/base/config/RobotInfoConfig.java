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

    @Value("${local.robot.name}")
    private String robotName;

    @Value("${local.robot.SN}")
    private String robotCode;

    @Value("${local.robot.typeId}")
    private int robotTypeId;

    @Value("${local.robot.batteryThreshold}")
    private int robotBatteryThreshold;

    @Value("${local.robot.storeId}")
    private Long storeId;

    @Bean
    public Robot getRobotInfo() {
        Robot robot = new Robot();
        robot.setName(robotName);
        robot.setCode(robotCode);
        robot.setBatteryThreshold(robotBatteryThreshold);
        robot.setTypeId(robotTypeId);
        robot.setStoreId(storeId);
        CacheInfoManager.setRobotInfoCache(robot);
        return robot;
    }
}
