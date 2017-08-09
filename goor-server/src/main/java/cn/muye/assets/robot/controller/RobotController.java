package cn.muye.assets.robot.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotConfigService robotConfigService;

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
        return AjaxResult.success(pageList, "查询成功");
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
        Integer robotIdForElevator = robot.getRobotIdForElevator();
        if (robotIdForElevator != null) {
            String robotIdForElevatorStr = String.valueOf(robotIdForElevator);
            String regex = "^[10]{8}";
            boolean flag = robotIdForElevatorStr.matches(regex);
            if (!flag) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "电梯编号必须为8位二进制");
            }
        }
        List list = robot.getChargerMapPointList();
        //判断是否有重复的名称
        Robot robotDbByName = robotService.getByName(robotName);
        if (robotDbByName != null && !robotDbByName.getId().equals(robotId)) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人名称重复");
        }
        //判断是否有重复的编号
        Robot robotDbByCode = robotService.getByCode(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (robotDbByCode != null && !robotDbByCode.getId().equals(robotId)) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人编号重复");
        }
        AjaxResult ajaxResult = null;
        if (robotId != null) { //修改
            Robot robotDb = robotService.getById(robotId);
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
                    robotDb.setChargerMapPointList(robot.getChargerMapPointList());
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
                    return AjaxResult.failed("修改同步失败，回滚操作");
                } finally {
                }
            } else if (robotDb != null && robotCode != null && !robotCode.equals(robotCodeDb)) {
                return AjaxResult.failed(robot, "不能修改机器人的编号");
            } else {
                return AjaxResult.failed("不存在的机器人");
            }
        } else {
            return AjaxResult.failed("不允许新增机器人");
        }
        return ajaxResult;
    }

    @RequestMapping(value = {"assets/robot/{id}"}, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机器人", httpMethod = "DELETE", notes = "删除机器人")
    @ResponseBody
    public AjaxResult deleteRobot(@ApiParam(value = "机器人") @PathVariable String id) {
        if (id != null) {
            robotService.deleteRobotById(Long.valueOf(id));
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误，查询失败");
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
                    return AjaxResult.failed("密码必须为4位数字");
                }
            }
            robotPasswordService.batchUpdateRobotPwdList(robot.getPasswords());
            return AjaxResult.success("修改密码成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("修改密码出错");
        }
    }

    //整合到新增和修改接口了
//    @RequestMapping(value = {"assets/robot/bindChargerMapPoint"}, method = RequestMethod.POST)
//    @ApiOperation(value = "机器人绑充电桩", httpMethod = "POST", notes = "机器人绑充电桩")
//    @ResponseBody
//    public AjaxResult bindChargerMapPoint(@RequestBody Robot robot) {
//
//    }

}
