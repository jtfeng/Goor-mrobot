package cn.muye.assets.robot.controller;

import cn.mrobot.bean.robot.Robot;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.AjaxResult;
import cn.muye.assets.robot.service.RobotService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Controller
public class RobotController {

    @Autowired
    private RobotService robotService;

    @RequestMapping(value = {"assets/robot"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询机器人列表", httpMethod = "GET", notes = "查询机器人列表")
    @ResponseBody
    public AjaxResult robotList(WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        List<Robot> list = robotService.listRobot();
        PageInfo<Robot> pageList = new PageInfo<>(list);
        return AjaxResult.success(pageList, "查询成功");
    }

    @RequestMapping(value = {"assets/robot/{id}"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询机器人详情", httpMethod = "GET", notes = "查询机器人详情")
    @ResponseBody
    public AjaxResult robotDetail(@ApiParam(value = "机器人ID") @PathVariable String id) {
        if (id != null) {
            Robot robot = robotService.getById(Long.valueOf(id));
            return AjaxResult.success(robot, "查询成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "查询失败");
        }
    }

    @RequestMapping(value = {"assets/robot"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改机器人", httpMethod = "POST", notes = "新增或修改机器人")
    @ResponseBody
    public AjaxResult addOrUpdateRobot(@ApiParam(value = "机器人")@RequestBody Robot robot) {
        if (robot.getTypeId() == null || robot.getTypeId() <= 0 || robot.getTypeId() > 3) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人类型有误");
        }
        if (StringUtil.isNullOrEmpty(robot.getName()) || StringUtil.isNullOrEmpty(robot.getCode())) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "机器人名称或编号不能为空");
        }
        //判断是否有重复的名称
        Robot robotDbByName = robotService.getByName(robot.getName());
        if (robotDbByName != null && robotDbByName.getId() != robot.getId()) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人名称重复");
        }
        //判断是否有重复的编号
        Robot robotDbByCode = robotService.getByCode(robot.getCode());
        if (robotDbByCode != null && robotDbByCode.getId() != robot.getId()) {
            return AjaxResult.failed(AjaxResult.CODE_FAILED, "机器人编号重复");
        }
        if (robot.getId() != null) { //修改
            Robot robotDb = robotService.getById(robot.getId());
            robotDb.setName(robot.getName());
            robotDb.setCode(robot.getCode());
            robotDb.setDescription(robot.getDescription());
            robotDb.setTypeId(robot.getTypeId());
            robotDb.setUpdateTime(new Date());
            robotDb.setBoxActivated(robot.getBoxActivated());
            robotService.update(robotDb);
            return AjaxResult.success(robotDb, "修改成功");
        } else if (robot.getId() == null){
            robot.setCreateTime(new Date());
            robotService.save(robot);
            return AjaxResult.success(robot, "新增成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误，查询失败");
        }
    }

    @RequestMapping(value = {"assets/robot/{id}"}, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机器人", httpMethod = "DELETE", notes = "删除机器人")
    @ResponseBody
    public AjaxResult deleteRobot(@ApiParam(value = "机器人")@PathVariable String id) {
        if (id != null) {
            Robot robot = robotService.getById(Long.valueOf(id));
            robotService.deleteById(robot.getId());
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误，查询失败");
        }
    }

//    @RequestMapping(value = {"robotType"}, method = RequestMethod.GET)
//    @ApiOperation(value = "查询机器人类型列表", httpMethod = "GET", notes = "查询机器人类型列表")
//    @ResponseBody
//    public AjaxResult robotTypeList() {
//        List<RobotType> list = robotTypeService.listType();
//        return AjaxResult.success(list, "查询成功");
//    }
}
