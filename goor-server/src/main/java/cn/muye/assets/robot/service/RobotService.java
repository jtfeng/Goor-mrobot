package cn.muye.assets.robot.service;

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

    void saveRobot(Robot robot);

    void deleteRobotById(Long id);

    Robot getByName(String name);

    Robot getByCode(String code);

    void updateRobot(Robot robot);

    Robot getAvailableRobotByStationId(Long stationId);

    void bindChargerMapPoint(Long robotId, List<MapPoint> list);

    List<MapPoint> getChargerMapPointByRobotCode(String robotCode);
}
