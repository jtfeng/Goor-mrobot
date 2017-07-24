package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.*;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationRobotXREFService;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.assets.robot.service.RobotChargerMapPointXREFService;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotChargerMapPointXREFService robotChargerMapPointXREFService;

    @Autowired
    private PointService pointService;

    @Autowired
    private RobotMapper robotMapper;

    /**
     * 更新机器人
     *
     * @param robot
     */
    public void updateRobotAndBindChargerMapPoint(Robot robot) {
        List<MapPoint> list = robot.getChargerMapPointList();
        if (list != null && list.size() == 1) {
            bindChargerMapPoint(robot.getId(), robot.getChargerMapPointList());
        }
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
     *
     * @param stationId
     * @return
     */
    @Override
    public Robot getAvailableRobotByStationId(Long stationId, Integer typeId) {
        List<StationRobotXREF> list = stationRobotXREFService.getByStationId(stationId);
        Robot availableRobot = null;
        if (list != null && list.size() > 0) {
            for (StationRobotXREF xref : list) {
                Long robotId = xref.getRobotId();
                Robot robotDb = getById(robotId);
                if (robotDb != null && robotDb.getBusy() == false && robotDb.getTypeId().equals(typeId)) {
                    availableRobot = robotDb;
                    break;
                }
            }
        }
        if (availableRobot != null) {
            availableRobot.setBusy(true);
            updateRobotAndBindChargerMapPoint(availableRobot);
        }
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
     *
     * @param robotCode
     * @return
     * @author Ray.Fu
     */
    @Override
    public List<MapPoint> getChargerMapPointByRobotCode(String robotCode, Long storeId) {
        if (robotCode != null) {
            Robot robotDb = getByCode(robotCode, storeId);
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

    private List<Robot> listPageByStoreIdAndOrder(int page, int pageSize, Map map) {
        PageHelper.startPage(page, pageSize);
        return robotMapper.listRobot(map);
    }

    public List<Robot> listRobot(WhereRequest whereRequest) {
        Map map = Maps.newHashMap();
        if (!StringUtil.isNullOrEmpty(whereRequest.getQueryObj())) {
            JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
            String name = (String) jsonObject.get(SearchConstants.SEARCH_NAME);
            String sceneId = (String) jsonObject.get(SearchConstants.SEARCH_SCENE_ID);
            String sceneName = (String) jsonObject.get(SearchConstants.SEARCH_SCENE_NAME);
            int type = Integer.valueOf((String) jsonObject.get(SearchConstants.SEARCH_TYPE));
            map.put("name", name);
            map.put("sceneId", sceneId);
            map.put("sceneName", sceneName);
            map.put("type", type);
        }
        List<Robot> list = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), map);
        list.forEach(robot -> {
            Long robotId = robot.getId();
            RobotConfig robotConfigDb = robotConfigService.getByRobotId(robotId);
            robot.setBatteryThreshold(robotConfigDb != null ? robotConfigDb.getBatteryThreshold() : null);
            List<RobotPassword> robotPasswordList = robotPasswordService.listRobotPassword(robotId);
            robot.setPasswords(robotPasswordList);
            List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robotId);
            List<MapPoint> mapPointList = Lists.newArrayList();
            xrefList.forEach(xref -> {
                MapPoint mapPoint = pointService.findById(xref.getChargerMapPointId());
                if (mapPoint != null)
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


    public void saveRobotAndBindChargerMapPoint(Robot robot) {
        super.save(robot);
        Long robotNewId = robot.getId();
        List list = robot.getChargerMapPointList();
        if (list != null && list.size() == 1 && robotNewId != null) {
            bindChargerMapPoint(robotNewId, list);
        }
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setBatteryThreshold(robot.getBatteryThreshold());
        robotConfig.setRobotId(robot.getId());
        robotConfig.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        robotConfig.setCreateTime(new Date());
        robotConfig.setCreatedBy(1L);
        robotConfigService.add(robotConfig);
        robotPasswordService.saveRobotPassword(robot);
    }

    @Override
    public AjaxResult autoRegister(Robot robotNew) {
        try {
            Long robotId = robotNew.getId();
            Integer robotTypeId = robotNew.getTypeId();
            String robotName = robotNew.getName();
            String robotCode = robotNew.getCode();
            Long robotStoreId = robotNew.getStoreId();
            Robot robotDb = getByCode(robotCode, robotStoreId);
            if (robotId != null) {
                if (robotDb != null) {
                    robotNew.setOnline(true);
                    updateRobotAndBindChargerMapPoint(robotNew);
                    return AjaxResult.success(robotNew, "更新机器人状态成功");
                } else {
                    robotNew.setId(null);
                    saveRobotAndBindChargerMapPoint(robotNew);
                    return AjaxResult.success(robotNew, "注册成功");
                }
            } else {
                if (robotTypeId == null || robotTypeId <= 0 || robotTypeId > RobotTypeEnum.DRAWER.getCaption()) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人类型有误");
                }
                if (StringUtil.isNullOrEmpty(robotName) || StringUtil.isNullOrEmpty(robotCode)) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人名称或编号不能为空");
                }
                if (robotNew.getBatteryThreshold() == null) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人电量阈值不能为空");
                }
                //判断是否有重复的名称
                Robot robotDbByName = getByName(robotName);
                if (robotDbByName != null && !robotDbByName.getId().equals(robotId)) {
                    return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人名称重复");
                }
                //判断是否有重复的编号
                Robot robotDbByCode = getByCode(robotCode, robotStoreId);
                if (robotDbByCode != null && !robotDbByCode.getId().equals(robotId)) {
                    return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人编号重复");
                }
                saveRobotAndBindChargerMapPoint(robotNew);
                //往ros上透传电量阈值
                syncRosRobotConfig(robotNew);
                return AjaxResult.success(robotNew, "注册成功");
            }
        } catch (Exception e) {
//            log.error("注册失败, 错误日志 >>>> {}", e.getMessage());
            return AjaxResult.failed("注册失败");
        } finally {
        }
    }

    /**
     * 往ros上透传机器人配置信息（电量阈值，。。。）
     *
     * @param robotNew
     */
    private void syncRosRobotConfig(Robot robotNew) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.TOPIC_CLIENT_ROBOT_BATTERY_THRESHOLD);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(robotNew))));
        String robotCode = robotNew.getCode();
        MessageInfo info = new MessageInfo();
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotCode);
        info.setMessageType(MessageType.ROBOT_BATTERY_THRESHOLD);
        info.setMessageText(JSON.toJSONString(commonInfo));
        String noResultResourceRoutingKey = RabbitMqBean.getRoutingKey(robotCode, false, MessageType.EXECUTOR_COMMAND.name());
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultResourceRoutingKey, info);
    }

    public void deleteRobotById(Long id) {
        myMapper.deleteByPrimaryKey(id);
        robotPasswordService.delete(new RobotPassword(null, id));
    }

    @Override
    public void deleteRobotByCode(String code) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("CODE =", code);
        myMapper.deleteByExample(example);
    }

    public Robot getByName(String name) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("NAME =", name);
        List<Robot> list = myMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Robot getByCode(String code, Long storeId) {
        Example example = new Example(Robot.class);
        example.createCriteria().andCondition("CODE =", code);
        List<Robot> list = myMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
