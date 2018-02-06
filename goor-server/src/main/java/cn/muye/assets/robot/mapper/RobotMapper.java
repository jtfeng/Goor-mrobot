package cn.muye.assets.robot.mapper;


import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.util.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Component
public interface RobotMapper extends MyMapper<Robot> {

    List<Robot> listRobot(Map map);

    Robot getRobotByCode(Map map);

    Long getRobotSceneId(Long robotId);
}
