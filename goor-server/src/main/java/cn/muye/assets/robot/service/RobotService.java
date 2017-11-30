package cn.muye.assets.robot.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;
import java.util.Map;

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

    Robot getByCodeByXml(String code, Long storeId, Long robotId);

    AjaxResult updateRobotAndBindChargerMapPoint(Robot robot, Integer lowBatteryThresholdDb, Integer sufficientBatteryThresholdDb, Integer lowRobotBatteryThreshold, Integer sufficientRobotBatteryThreshold, String robotCodeDb);

    Robot getAvailableRobotByStationId(Long stationId, Integer typeId) throws RuntimeException;

    /**
     * 根据站ID获取可用机器人数量
     * @param stationId
     * @return
     */
    Map getCountAvailableRobotByStationId(Long stationId);

    List<MapPoint> bindChargerMapPoint(Long robotId, List<MapPoint> list);

    List<MapPoint> getChargerMapPointByRobotCode(String robotCode, Long storeId);

    /**
     * 为机器人设置通用密码
     */
    void setRobotPassword(String newPassword);

    boolean checkPasswordIsValid(String uuid, String robotCode, String password);

    void setRobotBusyAndOnline(String robotCode, Boolean busy, Boolean online);


}
