package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.muye.assets.robot.mapper.RobotConfigMapper;
import cn.muye.assets.robot.service.RobotConfigService;
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

    public void deleteByRobotId(Long id) {
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setRobotId(id);
        robotConfigMapper.delete(robotConfig);
    }
}
