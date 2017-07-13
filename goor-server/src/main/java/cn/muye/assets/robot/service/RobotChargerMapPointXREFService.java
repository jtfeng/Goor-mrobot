package cn.muye.assets.robot.service;


import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.RobotChargerMapPointXREF;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/7/13.
 */
public interface RobotChargerMapPointXREFService {

    int deleteByRobotId(Long id);

    int save(RobotChargerMapPointXREF robotChargerMapPointXREF);

    List<RobotChargerMapPointXREF> getByRobotId(Long id);
}
