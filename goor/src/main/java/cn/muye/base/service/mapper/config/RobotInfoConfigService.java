package cn.muye.base.service.mapper.config;

import cn.muye.base.mapper.config.RobotInfoConfigMapper;
import cn.muye.base.model.config.RobotInfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by enva on 2017/5/11.
 */

@Service
@Transactional
public class RobotInfoConfigService {

    @Autowired
    private RobotInfoConfigMapper robotInfoConfigMapper;

    public Integer update(RobotInfoConfig robotInfoConfig){
        return robotInfoConfigMapper.update(robotInfoConfig);
    }

    public RobotInfoConfig get() {
        List<RobotInfoConfig> list = robotInfoConfigMapper.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
