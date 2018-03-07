package cn.muye.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.base.LogInfoUtils;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.service.missiontask.MissionFuncsService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by abel on 17-7-14.
 */
@RestController
@RequestMapping("mission/task")
@Api(
        value = "任务列表功能测试",
        description = "任务列表功能测试")
public class MissionListTaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionListTaskController.class);

    @Autowired
    MissionFuncsService missionFuncsService;

    @Autowired
    MissionListTaskService missionListTaskService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @PostMapping("/createMissionListTask")
    @ApiOperation(
            value = "任务列表创建",
            notes = "任务列表创建")
    public AjaxResult createMissionListTask(HttpServletRequest request) {
        try {
            Order order = new Order();
            order.setId(1L);

            Robot robot = new Robot();
            robot.setId(1L);
            robot.setCode("testrobot");
            order.setRobot(robot);

            MapPoint a = new MapPoint();
            a.setPointName("a");
            a.setSceneName("a");
            MapPoint b = new MapPoint();
            b.setPointName("b");
            b.setSceneName("b");

            OrderSetting orderSetting = new OrderSetting();
            //orderSetting.setStartPoint(a);
            //orderSetting.setEndPoint(b);
            order.setOrderSetting(orderSetting);

            List<OrderDetail> orderDetails = new ArrayList<>();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setStationId(1L);
            orderDetails.add(orderDetail);
            order.setDetailList(orderDetails);

            AjaxResult ret =
                    missionFuncsService.createMissionLists(order);
            return ret;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 查询指定场景（必选）和状态（可选）的任务列表
     *
     * @param whereRequest
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(
            value = "查询任务列表数据",
            notes = "查询任务列表数据")
    public AjaxResult getMissionListTaskList(WhereRequest whereRequest) {
        List<MissionListTask> list = missionListTaskService.tasksList(whereRequest);
        PageInfo<MissionListTask> pageList = new PageInfo<>(list);
        return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_CXCG"));
    }

    //SNabc013_jelynn
    @RequestMapping(value = "/controlState/{robotCode}/{command}", method = RequestMethod.GET)
    public Object controlState(@PathVariable("robotCode") String robotCode, @PathVariable("command") String command) {
        try {

            checkArgument(!("".equals(robotCode.trim())), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_JQRBHBNWKC"));
            checkArgument(!("".equals(robotCode.trim())), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_CRDZLBNWKC"));
            checkArgument(ImmutableList.of("pause", "resume", "clear", "skipMissionList", "startNextMission").indexOf(command) != -1, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_NCRDZLBZQHFDZLBKPAUSERESUMECLEARSKIPMISSIONLISTSTARTNEXTMISSION"));

            String uuid = UUID.randomUUID().toString().replace("-", "");
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName(TopicConstants.X86_MISSION_INSTANT_CONTROL);
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, Object>() {{
                put("command", command);
                put("uuid", uuid);
                put("sendTime", System.currentTimeMillis());
            }}))));
            MessageInfo info = new MessageInfo();
            info.setUuId(uuid);
            info.setSendTime(new Date());
            info.setSenderId("goor-server");
            info.setReceiverId(robotCode);
            info.setMessageType(MessageType.EXECUTOR_COMMAND);
            info.setMessageText(JSON.toJSONString(commonInfo));
            AjaxResult result = messageSendHandleService.sendCommandMessage(true, true, robotCode, info);
            if (!result.isSuccess()) {
                return AjaxResult.failed(String.format(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_SZLDYSBQZS"), command));
            }
            for (int i = 0; i < 500; i++) {
                Thread.sleep(1000);
                MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(info.getUuId());
                if (messageInfo1 != null && messageInfo1.isSuccess()) {
                    info.setSuccess(true);
                    break;
                }
            }
            if (info.isSuccess()) {
                LogInfoUtils.info(robotCode, ModuleEnums.MISSION, LogType.INFO_USER_OPERATE, String.format(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_SZLDYCG"), command));
                return AjaxResult.success(String.format(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_SZLDYCG"), command));
            } else {
                return AjaxResult.failed(String.format(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_SZLDYSBQZS"), command));
            }

        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    @PostMapping("/missionTaskCancel")
    @ApiOperation(
            value = "场景任务Task终止",
            notes = "场景任务Task终止")
    public AjaxResult missionTaskCancel(@RequestParam("missionTaskId") int missionTaskId) {
        MissionListTask missionListTask = missionListTaskService.findById(Long.valueOf(missionTaskId));
        if (missionListTask != null) {
            String robotCode = missionListTask.getRobotCode();
            AjaxResult ajaxResult = sendMissionCommand(robotCode, missionTaskId);
            return ajaxResult;
        } else {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_BCZDRWID"));
        }

    }

    private AjaxResult sendMissionCommand(String robotSn, Integer missionListId) {
        try {
            MessageInfo messageInfo = new MessageInfo();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            messageInfo.setUuId(uuid);
            messageInfo.setSendTime(new Date());
            messageInfo.setSenderId("goor-server");
            messageInfo.setReceiverId(robotSn);
            messageInfo.setMessageType(MessageType.EXECUTOR_COMMAND);
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName(TopicConstants.X86_MISSION_QUEUE_CANCEL);
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, Object>() {
                {
                    put("uuid", uuid);
                    put("missionListIds", Lists.newArrayList(
                            new HashMap<String, Object>() {{
                                put("missionListId", missionListId);
                            }}
                    ));

                }
            }))));
            messageInfo.setMessageText(JSON.toJSONString(commonInfo));
            AjaxResult ajaxResult = messageSendHandleService.sendCommandMessage(true, true, robotSn, messageInfo);
            if (ajaxResult != null && !ajaxResult.isSuccess()) {
                return AjaxResult.failed();
            }
            long startTime = System.currentTimeMillis();
            LOGGER.info("start time" + startTime);
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                //获取ROS的回执消息
                MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(messageInfo.getUuId());
                if (messageInfo1 != null && messageInfo1.isSuccess()) {
                    messageInfo.setSuccess(true);
                    break;
                }
            }
            long endTime = System.currentTimeMillis();
            LOGGER.info("end time" + (endTime - startTime));
            if (messageInfo.isSuccess()) {
                return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_ZLFSCG"));
            } else {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_ZLFSSB"));
            }
        } catch (
                Exception e)

        {
            LOGGER.error("{}", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_mission_controller_MissionListTaskController_java_ZLFSSB"));
        } finally

        {
        }
    }
}
