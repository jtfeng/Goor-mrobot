package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.robot.*;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.mission.task.JsonMissionItemDataLaserNavigation;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.utils.JsonUtils;
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
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl extends BaseServiceImpl<Robot> implements RobotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotServiceImpl.class);

    @Autowired
    private RobotPasswordService robotPasswordService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private StationRobotXREFService stationRobotXREFService;

    @Autowired
    private RobotChargerMapPointXREFService robotChargerMapPointXREFService;

    @Autowired
    private PointService pointService;

    @Autowired
    private RobotMapper robotMapper;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    public static final Lock lock1 = new ReentrantLock();

    /**
     * 更新机器人
     *
     * @param robot
     */
    public AjaxResult updateRobotAndBindChargerMapPoint(Robot robot, Integer lowBatteryThresholdDb, Integer sufficientBatteryThresholdDb, Integer lowRobotBatteryThreshold, Integer sufficientBatteryThreshold, String robotCodeDb) throws RuntimeException {
        List<MapPoint> list = robot.getOriginChargerMapPointList();
        if (list != null && list.size() == 1) {
            list = bindChargerMapPoint(robot.getId(), robot.getOriginChargerMapPointList());
            robot.setOriginChargerMapPointList(list);
        }
        if (lowBatteryThresholdDb == null || lowBatteryThresholdDb != null && !lowBatteryThresholdDb.equals(lowRobotBatteryThreshold)) {
            robot.setLowBatteryThreshold(lowRobotBatteryThreshold);
        }
        if (sufficientBatteryThresholdDb == null || sufficientBatteryThresholdDb != null && !sufficientBatteryThresholdDb.equals(sufficientBatteryThreshold)) {
            robot.setSufficientBatteryThreshold(sufficientBatteryThreshold);
        }
        //更新机器人信息
        updateSelectiveByStoreId(robot);
        //更新机器人配置信息
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (robotConfig != null && robot.getLowBatteryThreshold() != null) {
            robotConfig.setLowBatteryThreshold(robot.getLowBatteryThreshold());
            robotConfig.setSufficientBatteryThreshold(robot.getSufficientBatteryThreshold());
            robotConfigService.updateSelective(robotConfig);
        }
        //向X86上同步修改后的机器人电量阈值信息
        if (robot.getOnline() != null && robot.getOnline() == true && lowBatteryThresholdDb != null && lowRobotBatteryThreshold != null && sufficientBatteryThresholdDb != null && sufficientBatteryThreshold != null) {
            if (lowBatteryThresholdDb != null && !lowBatteryThresholdDb.equals(lowRobotBatteryThreshold)) {
                robot.setLowBatteryThreshold(lowRobotBatteryThreshold);
            }
            if (sufficientBatteryThresholdDb != null && !sufficientBatteryThresholdDb.equals(sufficientBatteryThreshold)) {
                robot.setSufficientBatteryThreshold(sufficientBatteryThreshold);
            }
            syncRobotBatteryThresholdToRos(robot);
        }
        return AjaxResult.success(robot, "修改成功");
    }

    /**
     * 向X86上同步修改后的机器人电量阈值信息
     *
     * @param robotDb
     * @return
     */
    private void syncRobotBatteryThresholdToRos(Robot robotDb) throws RuntimeException {
        //给应用和任务管理器下发topic
        for (int i = 0; i < 2; i++) {
            try {
                CommonInfo commonInfo = new CommonInfo();
                commonInfo.setTopicName(TopicConstants.AGENT_PUB);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                String uuid = UUID.randomUUID().toString().replace("-", "");
                robotDb.setUuid(UUID.randomUUID().toString().replace("-", ""));
                SlamBody slamBody = new SlamBody();
                slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
                slamBody.setUuid(uuid);
                convertChargerMapPointToJsonMissionItemDataLaserNavigation(robotDb);
                slamBody.setData(JsonUtils.toJson(robotDb,
                        new TypeToken<Robot>() {
                        }.getType()));
                LOGGER.info("下发机器人信息json串=>", slamBody.getData());
                slamBody.setErrorCode("0");
                slamBody.setMsg("success");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
                messageInfo.setReceiverId(robotDb.getCode());
                messageInfo.setSenderId("goor-server");
                messageInfo.setMessageType(MessageType.ROBOT_INFO);
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotDb.getCode(), messageInfo);
            } catch (Exception e) {
                LOGGER.error("发送错误", e);
            }
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
            super.updateSelective(availableRobot);
        }
        return availableRobot;
    }

    @Override
    public List<MapPoint> bindChargerMapPoint(Long robotId, List<MapPoint> list) {
        List listMapPoint = null;
        if (robotId != null) {
            robotChargerMapPointXREFService.deleteByRobotId(robotId);
            if (list != null && list.size() > 0) {
                for (MapPoint mapPoint : list) {
                    Long mapPointId = mapPoint.getId();
                    RobotChargerMapPointXREF xref = new RobotChargerMapPointXREF();
                    xref.setRobotId(robotId);
                    xref.setChargerMapPointId(mapPointId);
                    robotChargerMapPointXREFService.save(xref);
                    MapPoint mapPointDb = pointService.findById(mapPointId);
                    listMapPoint = Lists.newArrayList(mapPointDb);
                }
            }
        }
        return listMapPoint;
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
            robot.setLowBatteryThreshold(robotConfigDb != null ? robotConfigDb.getLowBatteryThreshold() : null);
            robot.setSufficientBatteryThreshold(robotConfigDb != null ? robotConfigDb.getSufficientBatteryThreshold() : null);
            List<RobotPassword> robotPasswordList = robotPasswordService.listRobotPassword(robotId);
            robot.setPasswords(robotPasswordList);
            List<RobotChargerMapPointXREF> xrefList = robotChargerMapPointXREFService.getByRobotId(robotId);
            List<MapPoint> mapPointList = Lists.newArrayList();
            xrefList.forEach(xref -> {
                MapPoint mapPoint = pointService.findById(xref.getChargerMapPointId());
                if (mapPoint != null)
                    mapPointList.add(mapPoint);
            });
            robot.setOriginChargerMapPointList(mapPointList);
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


    /**
     * 新增机器人信息
     *
     * @param robot
     * @throws RuntimeException
     */
    public void saveRobotAndBindChargerMapPoint(Robot robot) throws RuntimeException {
        super.save(robot);
        Long robotNewId = robot.getId();
        List list = robot.getChargerMapPointList();
        if (list != null && list.size() == 1 && robotNewId != null) {
            bindChargerMapPoint(robotNewId, list);
        }
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setLowBatteryThreshold(robot.getLowBatteryThreshold());
        robotConfig.setSufficientBatteryThreshold(robot.getSufficientBatteryThreshold());
        robotConfig.setRobotId(robot.getId());
        robotConfig.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        robotConfig.setCreateTime(new Date());
        robotConfig.setCreatedBy(1L);
        robotConfigService.add(robotConfig);
        if (robot.getTypeId() != null) {
            robotPasswordService.saveRobotPassword(robot);
        }
    }

    /**
     * 自动注册
     *
     * @param robotNew
     * @return
     */
    @Override
    public AjaxResult autoRegister(Robot robotNew) throws RuntimeException {
        if (lock1.tryLock()) {
            try {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                if (lock1.tryLock()) {
                    try {
                        return doingAutoRegister(robotNew);
                    } finally {
                        lock1.unlock();
                    }
                }
            } finally {
                lock1.unlock();
            }
        } else {
            return AjaxResult.failed("其他线程正在注册，请稍后...");
        }
        return null;
    }

    private AjaxResult doingAutoRegister(Robot robotNew) {
        try {
            if (robotNew != null) {
                Long robotId = robotNew.getId();
                String robotCode = robotNew.getCode();
                Integer robotTypeId = robotNew.getTypeId();
                String robotName = robotNew.getName();
                Long robotStoreId = robotNew.getStoreId();
                Integer lowBatteryThreshold = robotNew.getLowBatteryThreshold();
                Integer sufficientBatteryThreshold = robotNew.getSufficientBatteryThreshold();
                List<RobotPassword> passwordList = robotNew.getPasswords();
                //todo 按照robotCode的规范来赋值typeId,本次暂时写死
                if (!StringUtil.isNullOrEmpty(robotCode) && robotTypeId == null) {
                    robotNew.setTypeId(Constant.ROBOT_PRIMARY_TYPE_ID);
                }
                if (passwordList == null || passwordList.size() == 0) {
                    passwordList = Lists.newArrayList();
                    RobotPassword robotPassword = new RobotPassword();
                    robotPassword.setPassword(Constant.PRIMARY_PWD);
                    passwordList.add(robotPassword);
                    robotNew.setPasswords(passwordList);
                }
                if (!StringUtil.isNullOrEmpty(robotCode) && StringUtil.isNullOrEmpty(robotName)) {
                    robotNew.setName(robotCode);
                }
                if (lowBatteryThreshold == null) {
                    robotNew.setLowBatteryThreshold(Constant.ROBOT_LOW_BATTERY_THRESHOLD_DEFAULT);
                }
                if (sufficientBatteryThreshold == null) {
                    robotNew.setSufficientBatteryThreshold(Constant.ROBOT_SUFFICIENT_BATTERY_THRESHOLD_DEFAULT);
                }
                if (StringUtil.isNullOrEmpty(robotCode)) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人编号不能为空");
                }
                Robot robotDb = getByCode(robotCode, robotStoreId);
                if (robotDb == null) {
                    saveRobotAndBindChargerMapPoint(robotNew);
                    //往ros上透传电量阈值,机器人注册同步往应用下发消息，不需要回执，发不成功，应用那边会有查询请求，再给其反馈机器人信息
                    syncRosRobotConfig(robotNew);
                    return AjaxResult.success(robotNew, "注册成功");
                } else {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人编号重复,注册失败");
                }
            } else {
                return AjaxResult.failed("注册失败");
            }
        } catch (Exception e) {
            LOGGER.error("注册失败, 错误日志 >>>> {}", e);
            return AjaxResult.failed("注册失败");
        } finally {
        }
    }

    /**
     * 往ros上透传机器人配置信息（电量阈值，机器人编号。。。）
     *
     * @param robotNew
     */
    private void syncRosRobotConfig(Robot robotNew) {
        for (int i = 0; i < 2; i++) {
            try {
                CommonInfo commonInfo = new CommonInfo();
                commonInfo.setTopicName(TopicConstants.AGENT_PUB);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                //todo 暂时用唐林的SlamBody的结构，之后如果可复用，建议把名字换成通用的
                String uuid = UUID.randomUUID().toString().replace("-", "");
                robotNew.setUuid(uuid);
                SlamBody slamBody = new SlamBody();
                slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
                slamBody.setUuid(uuid);
                convertChargerMapPointToJsonMissionItemDataLaserNavigation(robotNew);
                slamBody.setData(JsonUtils.toJson(robotNew,
                        new TypeToken<Robot>() {
                        }.getType()));
                slamBody.setErrorCode("0");
                slamBody.setMsg("success");
                slamBody.setMsg("机器人" + robotNew.getCode() + "注册成功");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
                messageInfo.setReceiverId(robotNew.getCode());
                messageInfo.setSenderId("goor-server");
                messageInfo.setMessageType(MessageType.ROBOT_INFO);
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotNew.getCode(), messageInfo);
            } catch (Exception e) {
                LOGGER.error("发送错误{}", e);
            } finally {
            }
        }
    }

    /**
     * 充电桩点List转换导航点的List
     * @param robotNew
     */
    private void convertChargerMapPointToJsonMissionItemDataLaserNavigation(Robot robotNew) {
        if (robotNew != null && robotNew.getOriginChargerMapPointList() != null) {
            List<MapPoint> originChargerMapPointList = robotNew.getOriginChargerMapPointList();
            List<JsonMissionItemDataLaserNavigation> list = Lists.newArrayList();
            if (originChargerMapPointList != null && originChargerMapPointList.size() > 0) {
                for (MapPoint mapPoint : originChargerMapPointList) {
                    String mapName = mapPoint.getMapName();
                    JsonMissionItemDataLaserNavigation jsonMissionItemDataLaserNavigation = new JsonMissionItemDataLaserNavigation();
                    jsonMissionItemDataLaserNavigation.setMap(mapName);
                    jsonMissionItemDataLaserNavigation.setMap_name(mapName);
                    jsonMissionItemDataLaserNavigation.setScene_name(mapPoint.getSceneName());
                    jsonMissionItemDataLaserNavigation.setX(mapPoint.getX());
                    jsonMissionItemDataLaserNavigation.setY(mapPoint.getY());
                    jsonMissionItemDataLaserNavigation.setTh(mapPoint.getTh());
                    list.add(jsonMissionItemDataLaserNavigation);
                }
            }
            robotNew.setChargerMapPointList(null);
            robotNew.setChargerMapPointList(list);
        }
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

    @Override
    public Robot getByCodeByXml(String code, Long storeId, Long robotId) {
        Map map = Maps.newHashMap();
        map.put("code", code);
        map.put("storeId", storeId);
        map.put("robotId", robotId);
        Robot robot = robotMapper.getRobotByCode(map);
        return robot;
    }
}
