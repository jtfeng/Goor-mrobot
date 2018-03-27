package cn.muye.assets.robot.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.assets.robot.RobotTypeEnum;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Controller
@Api(value = "机器人模块", description = "机器人模块")
public class RobotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotController.class);

    @Autowired
    private RobotService robotService;
    @Autowired
    private RobotPasswordService robotPasswordService;
    @Autowired
    private RobotConfigService robotConfigService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = {"assets/robotOnlineStatus"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询机器人在线状态", httpMethod = "GET", notes = "查询机器人在线状态")
    @ResponseBody
    public AjaxResult robotList(WhereRequest whereRequest, @RequestParam(value = "robotSn") String robotSn) {
        if (CacheInfoManager.getRobotOnlineCache(robotSn)) {
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_JQRZX"));
        } else {
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_JQRBZX"));
        }
    }

    @RequestMapping(value = {"assets/robotBusyStatus"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询机器人忙碌状态", httpMethod = "GET", notes = "查询机器人忙碌状态")
    @ResponseBody
    public AjaxResult robotBusyStatus(WhereRequest whereRequest, @RequestParam(value = "robotSn") String robotSn) {
        if (CacheInfoManager.getRobotBusyCache(robotSn)) {
            return AjaxResult.success("机器人忙碌");
        } else {
            return AjaxResult.success("机器人空闲");
        }
    }

    /**
     * 查询机器人列表
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = {"assets/robot"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询机器人列表", httpMethod = "GET", notes = "查询机器人列表")
    @ResponseBody
    public AjaxResult robotList(WhereRequest whereRequest) {
        List<Robot> list = robotService.listRobot(whereRequest);
        PageInfo<Robot> pageList = new PageInfo<>(list);
        return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_CXCG"));
    }

    /**
     * 修改机器人
     *
     * @param robot
     * @return
     */
    @RequestMapping(value = {"assets/robot"}, method = {RequestMethod.PUT})
    @ApiOperation(value = "修改机器人", httpMethod = "PUT", notes = "修改机器人")
    @ResponseBody
    public AjaxResult updateRobot(@ApiParam(value = "机器人") @RequestBody Robot robot) {
        Long robotId = robot.getId();
        String robotName = robot.getName();
        String robotCode = robot.getCode();
        Integer lowRobotBatteryThreshold = robot.getLowBatteryThreshold();
        Integer sufficientBatteryThreshold = robot.getSufficientBatteryThreshold();
        String robotIdForElevator = robot.getRobotIdForElevator();
        /*if (!StringUtil.isNullOrEmpty(robotIdForElevator)) {
            String regex = "^[10]{8}";
            boolean flag = robotIdForElevator.matches(regex);
            if (!flag) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_DTBHBXW8WEJZ"));
            }
        }*/
        List list = robot.getOriginChargerMapPointList();
        //TODO 将来要能绑定多个充电桩，现在只能绑定一个20171104
        if(list != null && list.size() > 1) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_ZNBDYGCDZ"));
        }

        //判断是否有重复的名称
        Robot robotDbByName = robotService.getByName(robotName);
        if (robotDbByName != null && !robotDbByName.getId().equals(robotId)) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_JQRMCZF"));
        }
        //判断是否有重复的编号
        Robot robotDbByCode = robotService.getByCode(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (robotDbByCode != null && !robotDbByCode.getId().equals(robotId)) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_JQRBHZF"));
        }
        AjaxResult ajaxResult;
        if (robotId != null) { //修改
            Robot robotDb = robotService.getByCodeByXml(null, SearchConstants.FAKE_MERCHANT_STORE_ID, robotId);
            RobotConfig robotConfig = robotConfigService.getByRobotId(robotId);
            Integer lowBatteryThresholdDb = null;
            Integer sufficientBatteryThresholdDb = null;
            if (robotConfig != null) {
                lowBatteryThresholdDb = robotConfig.getLowBatteryThreshold();
                sufficientBatteryThresholdDb = robotConfig.getSufficientBatteryThreshold();
            }
            String robotCodeDb = robotDb.getCode();
            if (robotDb != null && robotCode == null) {
                if (list != null) {
                    robotDb.setOriginChargerMapPointList(robot.getOriginChargerMapPointList());
                }
                if (robotName != null) {
                    robotDb.setName(robotName);
                }
                if (robot.getDescription() != null) {
                    robotDb.setDescription(robot.getDescription());
                }
                if (robot.getBoxActivated() != null) {
                    robotDb.setBoxActivated(robot.getBoxActivated());
                }
                robotDb.setUpdateTime(new Date());
                if (lowRobotBatteryThreshold != null) {
                    robotDb.setLowBatteryThreshold(lowRobotBatteryThreshold);
                }
                robotDb.setRobotIdForElevator(robotIdForElevator);
                try {
                    ajaxResult = robotService.updateRobotAndBindChargerMapPoint(robotDb, lowBatteryThresholdDb, sufficientBatteryThresholdDb, lowRobotBatteryThreshold, sufficientBatteryThreshold, robotCodeDb);
                } catch (Exception e) {
                    LOGGER.error("{}", e);
                    return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_XGTBSBHGCZ"));
                } finally {
                }
            } else if (robotDb != null && robotCode != null && !robotCode.equals(robotCodeDb)) {
                return AjaxResult.failed(robot, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_BNXGJQRDBH"));
            } else {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_BCZDJQR"));
            }
        } else {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_BYXXZJQR"));
        }
        return ajaxResult;
    }

    @RequestMapping(value = {"assets/robot/{id}"}, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机器人", httpMethod = "DELETE", notes = "删除机器人")
    @ResponseBody
    public AjaxResult deleteRobot(@ApiParam(value = "机器人") @PathVariable String id) {
        if (id != null) {
            robotService.deleteRobotById(Long.valueOf(id));
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_SCCG"));
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_CSYWCXSB"));
        }
    }

    @RequestMapping(value = {"assets/robotPassword"}, method = RequestMethod.PUT)
    @ApiOperation(value = "修改机器人密码", httpMethod = "DELETE", notes = "修改机器人密码")
    @ResponseBody
    public AjaxResult changeRobotPwd(@RequestBody Robot robot) {
        try {
            for (RobotPassword robotPassword : robot.getPasswords()) {
                String password = robotPassword.getPassword();
                String regex = "^\\d{4}$";
                boolean flag = password.matches(regex);
                if (!flag) {
                    return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_MMBXW4WSZ"));
                }
            }
            robotPasswordService.batchUpdateRobotPwdList(robot.getPasswords());
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_XGMMCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_XGMMCC"));
        }
    }

    /**
     * 所有设置机器人的通用密码（当前的处理模式是 设置过程可能不成功，若设置失败，则继续使用旧密码）
     * @param newPassword
     * @return
     */
    @RequestMapping(value = {"assets/setNewRobotPassword"}, method = RequestMethod.GET)
    @ApiOperation(value = "设置所有机器人操作的通用密码", httpMethod = "GET", notes = "设置所有机器人操作的通用密码")
    @ResponseBody
    public AjaxResult setRobotPassword(String newPassword) {
        try {
            this.robotService.setRobotPassword(newPassword);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    //整合到新增和修改接口了
//    @RequestMapping(value = {"assets/robot/bindChargerMapPoint"}, method = RequestMethod.POST)
//    @ApiOperation(value = "机器人绑充电桩", httpMethod = "POST", notes = "机器人绑充电桩")
//    @ResponseBody
//    public AjaxResult bindChargerMapPoint(@RequestBody Robot robot) {
//    }

    /**
     * 获取所有可用机器人数量
     * @param stationId
     * @return
     */
    @RequestMapping(value = {"assets/robot/getAvailableRobotCount/{stationId}"}, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有可用机器人数量", httpMethod = "GET", notes = "获取所有可用机器人数量")
    @ResponseBody
    public AjaxResult getAvailableRobotCount(@PathVariable Long stationId) {
        try {
            Map<String, Integer> availableRobotCountMap = CacheInfoManager.getAvailableRobotCountCache(stationId);
            return AjaxResult.success(availableRobotCountMap, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_CXCG"));
        } catch (Exception e){
            LOGGER.error("############## getAvailableRobotCount error ############# {}", e);
            return AjaxResult.failed(e.getMessage());
        } finally {
        }
    }

    /**
     * 模拟可用机器人数量
     * @param count
     * @return
     */
    @GetMapping(value = {"assets/robot/testAvailableRobotCount"})
    @ApiOperation(value = "模拟可用机器人数量", httpMethod = "GET", notes = "模拟可用机器人数量")
    @ResponseBody
    public AjaxResult mockAvailableRobotCount(@RequestParam Long stationId, @RequestParam int count) {
        try {
            Map<String, Integer> availableRobotCountMap = Maps.newHashMap();
            availableRobotCountMap.put(RobotTypeEnum.TRAILER.name(), count);
            CacheInfoManager.setAvailableRobotCountCache(stationId, availableRobotCountMap);
            return AjaxResult.success(CacheInfoManager.getAvailableRobotCountCache(stationId), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_robot_controller_RobotController_java_CXCG"));
        } catch (Exception e){
            LOGGER.error("############## mockAvailableRobotCount error ############# {}", e);
            return AjaxResult.failed(e.getMessage());
        } finally {
        }
    }

}
