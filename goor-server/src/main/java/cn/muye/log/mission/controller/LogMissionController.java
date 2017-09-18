package cn.muye.log.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.mission.LogMission;
import cn.mrobot.bean.log.mission.LogMission.MissionLogType;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.MessageInfo;
import cn.muye.log.mission.service.LogMissionService;
import cn.muye.service.consumer.topic.BaseMessageService;
import cn.muye.service.consumer.topic.X86ElevatorLockService;
import cn.muye.service.consumer.topic.X86MissionStateResponseService;
import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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


    //{"senderId":"SNtest","messageText":"","sendTime":"Aug 4, 2017 5:40:00 PM","success":true}
    //{"data":""}
    //data就是约定的json对象字串

    //{"action": "lock","elevator_id": 1,"sendTime": 1501748933017,"uuid": "e7981721296445c9865e3dfcbbcaf98d"}




    @PostMapping()
    @ApiOperation(
            value = "任务日志新增提交",
            notes = "任务日志新增提交")
    public AjaxResult addLogMission(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody LogMission body, HttpServletRequest request) {
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

    @Autowired
    BaseMessageService baseMessageService;
    @Autowired
    X86MissionStateResponseService x86MissionStateResponseService;

    @PostMapping("/test")
    @ApiOperation(
            value = "test",
            notes = "test")
    public AjaxResult test(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody String body) {
        try {
            body = "{\"senderId\":\"SNtest\",\"messageText\":\"{\\\"data\\\":\\\"{\\\\\\\"state\\\\\\\": \\\\\\\"executing\\\\\\\", \\\\\\\"mission_list_id\\\\\\\": 319, \\\\\\\"uuid\\\\\\\": \\\\\\\"-1\\\\\\\", \\\\\\\"repeat_times\\\\\\\": 1, \\\\\\\"mission_list\\\\\\\": [{\\\\\\\"mission_id\\\\\\\": 3329, \\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3391}], \\\\\\\"repeat_times\\\\\\\": 1}, {\\\\\\\"mission_id\\\\\\\": 3330, \\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3392}], \\\\\\\"repeat_times\\\\\\\": 1}, {\\\\\\\"mission_id\\\\\\\": 3331, \\\\\\\"state\\\\\\\": \\\\\\\"canceled\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"canceled\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3393}], \\\\\\\"repeat_times\\\\\\\": 1}, {\\\\\\\"mission_id\\\\\\\": 3332, \\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"finished\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3394}], \\\\\\\"repeat_times\\\\\\\": 1}, {\\\\\\\"mission_id\\\\\\\": 3333, \\\\\\\"state\\\\\\\": \\\\\\\"executing\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"executing\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3395}], \\\\\\\"repeat_times\\\\\\\": 1}, {\\\\\\\"mission_id\\\\\\\": 3334, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3396}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3335, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3397}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3336, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3398}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3337, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3399}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3338, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3400}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3339, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3401}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3340, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3402}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3341, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3403}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3342, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3404}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3343, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3405}], \\\\\\\"repeat_times\\\\\\\": 0}, {\\\\\\\"mission_id\\\\\\\": 3344, \\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_set\\\\\\\": [{\\\\\\\"state\\\\\\\": \\\\\\\"waiting\\\\\\\", \\\\\\\"mission_item_id\\\\\\\": 3406}], \\\\\\\"repeat_times\\\\\\\": 0}]}\\\"}\",\"sendTime\":\"Aug 4, 2017 5:40:00 PM\",\"success\":false}";
            MessageInfo messageInfo =
                    (MessageInfo) JsonUtils.fromJson(
                            body,
                            new TypeToken<MessageInfo>(){}.getType());
            if (messageInfo != null){
//                String data = baseMessageService.getData(messageInfo);
//                if (!StringUtil.isEmpty(data)){
//                    JsonMissionStateResponse jsonMissionStateResponse =
//                            (JsonMissionStateResponse) JsonUtils.fromJson(data,
//                                    new TypeToken<JsonMissionStateResponse>(){}.getType());
//                    String a = "";
//                }
                x86MissionStateResponseService.handleX86MissionStateResponse(messageInfo);
            }
            return AjaxResult.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    @Autowired
    X86ElevatorLockService x86ElevatorLockService;

    @PostMapping("/elevator/lock")
    @ApiOperation(
            value = "test",
            notes = "test")
    public AjaxResult elevatorLock(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody String body) {
        try {
            body = "{\"senderId\":\"SNtest\",\"messageText\":\"{\\\"data\\\":\\\"{\\\\\\\"action\\\\\\\": \\\\\\\"lock\\\\\\\",\\\\\\\"elevator_id\\\\\\\": 1,\\\\\\\"sendTime\\\\\\\": 1501748933017,\\\\\\\"uuid\\\\\\\": \\\\\\\"e7981721296445c9865e3dfcbbcaf98d\\\\\\\"}\\\"}\",\"sendTime\":\"Aug 4, 2017 5:40:00 PM\",\"success\":true}";
            MessageInfo messageInfo =
                    (MessageInfo) JsonUtils.fromJson(
                            body,
                            new TypeToken<MessageInfo>(){}.getType());
            if (messageInfo != null){
                return x86ElevatorLockService.handleX86ElevatorLock(messageInfo);
            }
            return AjaxResult.failed("失败");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    @PostMapping("/elevator/unlock")
    @ApiOperation(
            value = "test",
            notes = "test")
    public AjaxResult elevatorUnlock(
            @ApiParam(
                    required = true,
                    name = "body",
                    value = "入参对象")
            @RequestBody String body) {
        try {
            body = "{\"senderId\":\"SNtest\",\"messageText\":\"{\\\"data\\\":\\\"{\\\\\\\"action\\\\\\\": \\\\\\\"unlock\\\\\\\",\\\\\\\"elevator_id\\\\\\\": 1,\\\\\\\"sendTime\\\\\\\": 1501748933017,\\\\\\\"uuid\\\\\\\": \\\\\\\"e7981721296445c9865e3dfcbbcaf98d\\\\\\\"}\\\"}\",\"sendTime\":\"Aug 4, 2017 5:40:00 PM\",\"success\":true}";
            MessageInfo messageInfo =
                    (MessageInfo) JsonUtils.fromJson(
                            body,
                            new TypeToken<MessageInfo>(){}.getType());
            if (messageInfo != null){
                return x86ElevatorLockService.handleX86ElevatorLock(messageInfo);
            }
            return AjaxResult.failed("失败");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 查看任务日志
     * @param whereRequest
     * @return
     */
    @GetMapping()
    @ApiOperation(
            value = "任务日志列表",
            notes = "任务日志列表")
    public AjaxResult list(WhereRequest whereRequest) {
        if (whereRequest != null) {
            int page = whereRequest.getPage();
            int pageSize = whereRequest.getPageSize();
            List<LogMission> list = logMissionService.listPageByStoreIdAndOrder(page, pageSize, LogMission.class, "CREATE_TIME DESC");
            PageInfo<LogMission> pageInfo = new PageInfo<LogMission>(list);
            return AjaxResult.success(pageInfo, "查询成功");
        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
        }
    }
}
