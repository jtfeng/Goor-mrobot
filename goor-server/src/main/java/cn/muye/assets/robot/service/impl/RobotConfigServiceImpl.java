package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.muye.assets.robot.mapper.RobotConfigMapper;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
@Service
@Transactional
public class RobotConfigServiceImpl extends BaseServiceImpl<RobotConfig> implements RobotConfigService {

    @Autowired
    private RobotConfigMapper robotConfigMapper;

    @Autowired
    private RobotService robotService;

    /**
     * 新增机器人配置
     *
     * @param robotConfig
     */
    public void add(RobotConfig robotConfig) {
        robotConfigMapper.insert(robotConfig);
    }

    /**
     * 修改机器人配置
     *
     * @param robotConfig
     */
    public int updateSelective(RobotConfig robotConfig) {
        return robotConfigMapper.updateByPrimaryKeySelective(robotConfig);
    }

    /**
     * 根据机器人ID查询配置
     *
     * @param robotId
     * @return
     */
    public RobotConfig getByRobotId(Long robotId) {
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setRobotId(robotId);
        return robotConfigMapper.selectOne(robotConfig);
    }

    @Override
    public Integer getLowBatteryThreshold(String code, long storeId) {
        Robot robot = robotService.getByCode(code, storeId);
        if (null == robot)
            return null;
        RobotConfig robotConfig = getByRobotId(robot.getId());
        if (null == robotConfig)
            return null;
        return robotConfig.getLowBatteryThreshold();
    }

    public void deleteByRobotId(Long id) {
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setRobotId(id);
        robotConfigMapper.delete(robotConfig);
    }
}
