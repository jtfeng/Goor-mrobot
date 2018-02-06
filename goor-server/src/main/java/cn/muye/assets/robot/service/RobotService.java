package cn.muye.assets.robot.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.dijkstra.RobotRoadPathResult;
import cn.mrobot.bean.order.Order;
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
     * 由站点ID查询离订单第一个目的地点最近的可用在线的机器人
     * 当订单的设置里面装货站为空的时候，取目的地列表的第一个作为目的地；当订单设置的装货站不为空的时候，取装货点作为目的地。
     * @param typeId 点类型
     * @param order
     * @return
     * @throws Exception
     */
    RobotRoadPathResult getNearestAvailableRobotByOrder(Integer typeId, Order order) throws Exception;

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

    /**
     * 通过robotId获取 场景id
     * @param robotId
     * @return
     */
    Long getRobotSceneId(Long robotId);


}
