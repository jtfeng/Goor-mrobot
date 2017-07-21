package cn.muye.base.mapper.config;

import cn.muye.base.model.config.RobotInfoConfig;

import java.util.List;

/**
 * Created by ray on 2017/7/21.
 */
public interface RobotInfoConfigMapper {

    int update(RobotInfoConfig robotInfoConfig);

    List<RobotInfoConfig> list();
}
