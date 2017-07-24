package cn.muye.assets.robot.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/21.
 */
public interface RobotService extends BaseService<Robot>{

    List<Robot> listRobot(WhereRequest whereRequest);

    List<Robot> listRobot(Long storeId);

    Robot getById(Long id);

    void saveRobotAndBindChargerMapPoint(Robot robot);

    AjaxResult autoRegister(Robot robotNew);

    void deleteRobotById(Long id);

    void deleteRobotByCode(String code);

    Robot getByName(String name);

    Robot getByCode(String code, Long storeId);

    AjaxResult updateRobotAndBindChargerMapPoint(Robot robot, Integer batteryThresholdDb, Integer robotBatteryThreshold, String robotCodeDb);

    Robot getAvailableRobotByStationId(Long stationId, Integer typeId);

    void bindChargerMapPoint(Long robotId, List<MapPoint> list);

    List<MapPoint> getChargerMapPointByRobotCode(String robotCode, Long storeId);
}
