package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotChargerMapPointXREF;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationRobotXREFService;
import cn.muye.assets.robot.service.RobotChargerMapPointXREFService;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl extends BaseServiceImpl<Robot> implements RobotService {

    @Autowired
    private RobotPasswordService robotPasswordService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private StationRobotXREFService stationRobotXREFService;

    @Autowired
    private RobotService robotService;

    @Autowired
    private RobotChargerMapPointXREFService robotChargerMapPointXREFService;

    @Autowired
    private PointService pointService;

    /**
     * 更新机器人
     * @param robot
     */
    public void updateRobot(Robot robot) {
        //更新机器人信息
        updateByStoreId(robot);
        //更新机器人配置信息
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (robotConfig != null && robot.getBatteryThreshold() != null) {
            robotConfig.setBatteryThreshold(robot.getBatteryThreshold());
            robotConfigService.update(robotConfig);
        }
    }

    /**
     * 由站点ID查询可用的机器人
     * @param stationId
     * @return
     */
    @Override
    public Robot getAvailableRobotByStationId(Long stationId) {
        List<StationRobotXREF> list = stationRobotXREFService.getByStationId(stationId);
        Robot availableRobot = null;
        if (list != null && list.size() > 0) {
            for (StationRobotXREF xref : list) {
                Long robotId = xref.getRobotId();
                Robot robotDb = robotService.getById(robotId);
                if (robotDb != null && robotDb.getBusy() == false) {
                    availableRobot = robotDb;
                    break;
                }
            }
        }
        availableRobot.setBusy(true);
        updateRobot(availableRobot);
        return availableRobot;
    }

    @Override
    public void bindChargerMapPoint(Long robotId, List<MapPoint> list) {
        if (robotId != null) {
            robotChargerMapPointXREFService.deleteByRobotId(robotId);
            if (list != null && list.size() > 0) {
                for (MapPoint mapPoint : list) {
                    RobotChargerMapPointXREF xref = new RobotChargerMapPointXREF();
                    xref.setRobotId(robotId);
                    xref.setChargerMapPointId(mapPoint.getId());
                    robotChargerMapPointXREFService.save(xref);
                }
            }
        }
    }

    /**
     * 由机器人编号获取绑定的充电桩List
     * @author Ray.Fu
     * @param robotCode
     * @return
     */
    @Override
    public List<MapPoint> getChargerMapPointByRobotCode(String robotCode) {
        if (robotCode != null) {
            Robot robotDb = robotService.getByCode(robotCode);
            if (robotDb != null) {
                Long robotId = robotDb.getId();
                List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robotId);
                List<MapPoint> mapPointList = Lists.newArrayList();
                if (xrefList != null && xrefList.size() > 0) {
                    for (RobotChargerMapPointXREF xref : xrefList) {
                        MapPoint point = pointService.findById(xref.getChargerMapPointId());
                        mapPointList.add(point);
                    }
                }
                return mapPointList;
            }
        } else {
            return null;
        }
        return null;
    }

    private List<Robot> listPageByStoreIdAndOrder(int page, int pageSize, String name, Integer type, Class<Robot> clazz, String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(clazz);
        Example.Criteria criteria = example.createCriteria();
        criteria = criteria.andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (name != null) {
            criteria.andCondition("NAME like", "%" + name + "%");
        }
        if (type != null) {
            criteria.andCondition("TYPE_ID =", type);
        }
        if (!StringUtil.isNullOrEmpty(order)) {
            example.setOrderByClause(order);
        }
        return myMapper.selectByExample(example);
    }

    public List<Robot> listRobot(WhereRequest whereRequest) {
        List<Robot> list = null;
        if (whereRequest != null && !StringUtil.isNullOrEmpty(whereRequest.getQueryObj())) {
            JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
            String name = (String) jsonObject.get(SearchConstants.SEARCH_NAME);
            int type = Integer.valueOf((String) jsonObject.get(SearchConstants.SEARCH_TYPE));
            list = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), name, type, Robot.class, "ID DESC");
        } else {
            list = super.listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Robot.class, "ID DESC");
        }
        list.forEach(robot -> {
            robot.setBatteryThreshold(robotConfigService.getByRobotId(robot.getId()).getBatteryThreshold());
            robot.setPasswords(robotPasswordService.listRobotPassword(robot.getId()));
            List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robot.getId());
            List<MapPoint> mapPointList = Lists.newArrayList();
            xrefList.forEach(xref -> {
                MapPoint mapPoint = pointService.findById(xref.getChargerMapPointId());
                mapPointList.add(mapPoint);
            });
            robot.setChargerMapPointList(mapPointList);
        });
        return list;
    }

    @Override
    public List<Robot> listRobot(Long storeId) {
        Example example = new Example(Robot.class);
       example.createCriteria().andCondition("STORE_ID =", storeId);
        return myMapper.selectByExample(example);
    }

    public Robot getById(Long id) {
        return myMapper.selectByPrimaryKey(id);
    }


    public void saveRobot(Robot robot) {
        super.save(robot);
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setBatteryThreshold(robot.getBatteryThreshold());
        robotConfig.setRobotId(robot.getId());
        robotConfig.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        robotConfig.setCreateTime(new Date());
        robotConfig.setCreatedBy(1L);
        robotConfigService.add(robotConfig);
        robotPasswordService.saveRobotPassword(robot);
    }

    public void deleteRobotById(Long id) {
        myMapper.deleteByPrimaryKey(id);
        robotPasswordService.delete(new RobotPassword(null, id));
    }

    public Robot getByName(String name) {
        Robot robot = new Robot();
        robot.setName(name);
        return myMapper.selectOne(robot);
    }

    public Robot getByCode(String code) {
        Robot robot = new Robot();
        robot.setCode(code);
        return myMapper.selectOne(robot);
    }
}
