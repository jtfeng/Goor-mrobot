package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.RobotMapZipXREF;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/15.
 */
public interface RobotMapZipXREFService {

    void saveOrUpdate(RobotMapZipXREF robotMapZipXREF);

    void updateByRobotId(RobotMapZipXREF robotMapZipXREF);

    List<RobotMapZipXREF> findByRobotId(Long robotId);
}
