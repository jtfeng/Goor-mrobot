package cn.muye.mission.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.muye.funcs.service.MissionFuncsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

            MissionListTask missionListTask =
                    missionFuncsService.createMissionLists(order);
            if (missionListTask != null &&
                    missionListTask.getId() != null){
                return AjaxResult.success("任务列表创建成功");
            }else{
                throw new Exception("任务列表创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }
}
