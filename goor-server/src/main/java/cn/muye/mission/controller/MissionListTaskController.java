package cn.muye.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.service.missiontask.MissionFuncsService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    MissionFuncsService missionFuncsService;

    @Autowired
    MissionListTaskService missionListTaskService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @PostMapping("/createMissionListTask")
    @ApiOperation(
            value = "任务列表创建",
            notes = "任务列表创建")
    public AjaxResult createMissionListTask() {
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
            orderSetting.setStartPoint(a);
            orderSetting.setEndPoint(b);
            order.setOrderSetting(orderSetting);

            List<OrderDetail> orderDetails = new ArrayList<>();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setStationId(1L);
            orderDetails.add(orderDetail);
            order.setDetailList(orderDetails);

            boolean ret =
                    missionFuncsService.createMissionLists(order);
            if (ret){
                return AjaxResult.success("任务列表创建成功");
            }else{
                throw new Exception("任务列表创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return AjaxResult.success(pageList, "查询成功");
    }

    @RequestMapping(value = "/controlState/{robotCode}/{command}", method = RequestMethod.GET)
    public Object controlState(@PathVariable("robotCode") String robotCode, @PathVariable("command") String command){
        try {
            checkArgument(!("".equals(robotCode.trim())), "机器人编号不能为空串!");
            checkArgument(!("".equals(robotCode.trim())), "传入的指令不能为空串!");
            checkArgument(ImmutableList.of("pause", "resume", "clear").indexOf(command) != -1, "您传入的指令不正确，合法的指令包括：（ pause、resume、clear ）");
            String uuid = UUID.randomUUID().toString().replace("-", "");
            CommonInfo commonInfo = new CommonInfo();
            commonInfo.setTopicName(TopicConstants.X86_MISSION_INSTANT_CONTROL);
            commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
            commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(new HashMap<String, String>(){{
                put("command", command);put("uuid", uuid);put("sendTime", String.valueOf(new Date().getTime()));
            }}))));
            MessageInfo info = new MessageInfo();
            info.setUuId(uuid);info.setSendTime(new Date());info.setSenderId("goor-server");info.setReceiverId(robotCode);
            info.setMessageType(MessageType.EXECUTOR_COMMAND);info.setMessageText(JSON.toJSONString(commonInfo));
            AjaxResult result = messageSendHandleService.sendCommandMessage(true,true,robotCode,info);
            if(!result.isSuccess()){
                return AjaxResult.failed(String.format("%s 指令调用失败，请重试", command));
            }
            for (int i=0; i<500; i++) {
                Thread.sleep(1000);
                MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(info.getUuId());
                if(messageInfo1.isSuccess()){
                    info.setSuccess(true);

                    break;
                }
            }
            if (info.isSuccess()) {
                return AjaxResult.success(String.format("%s 指令调用成功!", command));
            }else{
                return AjaxResult.failed(String.format("%s 指令调用失败，请重试", command));
            }
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }
}
