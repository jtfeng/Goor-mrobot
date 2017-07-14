package cn.muye.log.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.bean.log.mission.LogMission.MissionLogType;
import cn.mrobot.utils.StringUtil;
import cn.muye.log.mission.service.LogMissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static cn.mrobot.bean.log.mission.LogMission.MissionLogType.MISSION;
import static cn.mrobot.bean.log.mission.LogMission.MissionLogType.MISSION_ITEM;
import static cn.mrobot.bean.log.mission.LogMission.MissionLogType.MISSION_LIST;

/**
 * Created by abel on 17-7-7.
 */
@RestController
@RequestMapping("log/mission")
@Api(
        value = "任务日志功能",
        description = "任务日志功能")
public class LogMissionController {

    @Autowired
    LogMissionService logMissionService;


    @PostMapping()
    @ApiOperation(
            value = "任务日志新增提交",
            notes = "任务日志新增提交")
    public AjaxResult addLogMission(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody LogMission body) {
        try {
            if (body.getMissionType() == null ||
                    body.getStoreId() == null ||
                    StringUtil.isEmpty(body.getRobotCode())){
                throw new Exception("参数不合法，请设置任务日志类型和店铺id以及机器人编号。");
            }
            switch (MissionLogType.valueOf(body.getMissionType())){
                case MISSION_LIST:
                    if (body.getMissionListId() == null ||
                            body.getMissionListRepeatTimes() == null ||
                            StringUtil.isEmpty(body.getMissionEvent())){
                        throw new Exception("参数不合法，请检查参数。");
                    }
                    break;
                case MISSION:
                    if (body.getMissionListId() == null ||
                            body.getMissionListRepeatTimes() == null ||
                            body.getMissionId() == null ||
                            body.getMissionRepeatTimes() == null ||
                            StringUtil.isEmpty(body.getMissionEvent())){
                        throw new Exception("参数不合法，请检查参数。");
                    }
                    break;
                case MISSION_ITEM:
                    if (body.getMissionListId() == null ||
                            body.getMissionListRepeatTimes() == null ||
                            body.getMissionId() == null ||
                            body.getMissionRepeatTimes() == null ||
                            body.getMissionItemId() == null ||
                            StringUtil.isEmpty(body.getMissionEvent())){
                        throw new Exception("参数不合法，请检查参数。");
                    }
                    break;
                default:
                    throw new Exception("参数不合法，任务日志类型错误。");
            }
            //设置其它的值
            if (body.getMissionDescription() == null){
                body.setMissionDescription("");
            }
            if (body.getCreateTime() == null){
                body.setCreateTime(new Date());
            }
            //保存记录
            int id = logMissionService.save(body);
            if (id <= 0){
                throw new Exception("任务日志保存失败。");
            }
            return AjaxResult.success("任务日志新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }
}
