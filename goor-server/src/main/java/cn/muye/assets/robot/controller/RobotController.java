package cn.muye.assets.robot.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.assets.robot.RobotTypeEnum;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.util.aes.AES;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
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
     * 新增或修改机器人
     *
     * @param robot
     * @return
     */
    @RequestMapping(value = {"assets/robot"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改机器人", httpMethod = "POST", notes = "新增或修改机器人")
    @ResponseBody
    public AjaxResult addOrUpdateRobot(@ApiParam(value = "机器人") @RequestBody Robot robot) {
        if (robot.getTypeId() == null || robot.getTypeId() <= 0 || robot.getTypeId() > RobotTypeEnum.DRAWER.getCaption()) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人类型有误");
        }
        if (StringUtil.isNullOrEmpty(robot.getName()) || StringUtil.isNullOrEmpty(robot.getCode())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人名称或编号不能为空");
        }
        if (robot.getBatteryThreshold() == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人电量阈值不能为空");
        }
        //判断是否有重复的名称
        Robot robotDbByName = robotService.getByName(robot.getName());
        if (robotDbByName != null && !robotDbByName.getId().equals(robot.getId())) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人名称重复");
        }
        //判断是否有重复的编号
        Robot robotDbByCode = robotService.getByCode(robot.getCode());
        if (robotDbByCode != null && !robotDbByCode.getId().equals(robot.getId())) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人编号重复");
        }
        if (robot.getId() != null) { //修改
            Robot robotDb = robotService.getById(robot.getId());
            robotDb.setName(robot.getName());
            robotDb.setCode(robot.getCode());
            robotDb.setDescription(robot.getDescription());
            robotDb.setUpdateTime(new Date());
            robotDb.setBoxActivated(robot.getBoxActivated());
            robotDb.setBatteryThreshold(robot.getBatteryThreshold());
            robotService.updateRobot(robotDb);
            return AjaxResult.success(robotDb, "修改成功");
        } else if (robot.getId() == null) {
            robot.setBoxActivated(true);
            robotService.saveRobot(robot);
            return AjaxResult.success(robot, "新增成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误，查询失败");
        }
    }

    /**
     * 自动注册接口
     * @param robot
     * @return
     */
    @RequestMapping(value = {"assets/robot/register"}, method = RequestMethod.POST)
    @ApiOperation(value = "自动注册机器人", httpMethod = "POST", notes = "自动注册机器人")
    @ResponseBody
    public AjaxResult registerRobot(@RequestParam("robot") byte[] robot) {
        try {
            byte[] robotByte = AES.decrypt(robot, Constant.AES_KEY.getBytes());
            Robot robotNew = JSON.parseObject(robotByte, Robot.class);
            if (robotNew.getId() != null) {
                return AjaxResult.failed("注册失败，配置文件有误");
            }
            if (robotNew.getTypeId() == null || robotNew.getTypeId() <= 0 || robotNew.getTypeId() > RobotTypeEnum.DRAWER.getCaption()) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人类型有误");
            }
            if (StringUtil.isNullOrEmpty(robotNew.getName()) || StringUtil.isNullOrEmpty(robotNew.getCode())) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人名称或编号不能为空");
            }
            if (robotNew.getBatteryThreshold() == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人电量阈值不能为空");
            }
            //判断是否有重复的名称
            Robot robotDbByName = robotService.getByName(robotNew.getName());
            if (robotDbByName != null && !robotDbByName.getId().equals(robotNew.getId())) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人名称重复");
            }
            //判断是否有重复的编号
            Robot robotDbByCode = robotService.getByCode(robotNew.getCode());
            if (robotDbByCode != null && !robotDbByCode.getId().equals(robotNew.getId())) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人编号重复");
            }
            robotService.saveRobot(robotNew);
            return AjaxResult.success("注册成功");
        } catch (Exception e) {
            LOGGER.error("注册失败, 错误日志 >>>> {}", e.getMessage());
            return AjaxResult.failed("注册失败");
        } finally {
        }
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

    @RequestMapping(value = {"assets/robot/bindChargerMapPoint"}, method = RequestMethod.POST)
    @ApiOperation(value = "机器人绑充电桩", httpMethod = "POST", notes = "机器人绑充电桩")
    @ResponseBody
    public AjaxResult bindChargerMapPoint(@RequestBody Robot robot) {
        List<MapPoint> list = robot.getChargerMapPointList();
        Long robotId = robot.getId();
        if (list != null && list.size() == 1 && robotId != null) {
            robotService.bindChargerMapPoint(robotId, list);
            return AjaxResult.success(robot,"绑定成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
        }
    }

}
