package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.controller.BaseController;
import cn.muye.order.bean.OrderDetailVO;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.order.service.OrderSettingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Selim on 2017/7/8.
 */
@Controller
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private RobotService robotService;


    /**
     * id 获取订单详情
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getOrder(@RequestParam("id") Long id){
        try {
            Order findOrder = orderService.getOrder(id);
            return AjaxResult.success(findOrder,"获取订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取订单失败");
        }
    }

    /**
     * 保存一个订单
     * @param order
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveOrder(@RequestBody Order order,HttpSession session ){
        Robot arrangeRobot = null;
        try {
            Long stationId = (Long)session.getAttribute("stationId");
            OrderSetting setting = orderSettingService.getById(order.getOrderSetting().getId());
            Integer robotTypeId = setting.getRobotType().getId();
            //根据 站点id 和 机器人类型 自动选择机器人 todo
            arrangeRobot = robotService.getAvailableRobotByStationId(stationId,robotTypeId);
            if(arrangeRobot == null){
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"现在暂无可调用机器");
            }
            order.setRobot(arrangeRobot);
            order.setStartStation(new Station(stationId));

            //现在orderSetting后台默认注入默认配置
            OrderSetting orderSetting = orderSettingService.getDefaultSetting(stationId);
            order.setOrderSetting(orderSetting);

            orderService.saveOrder(order);

            return AjaxResult.success("保存订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            //若失败 机器人状态回滚
            if(arrangeRobot != null){
                arrangeRobot.setBusy(Boolean.FALSE);
                robotService.updateSelective(arrangeRobot);
            }
            return AjaxResult.failed("保存订单失败");
        }
    }

    /**
     * 获取当前用户 查询到的任务列表
     * @param session
     * @return
     */
    @RequestMapping(value = "listStationTasks", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listStationTasks(HttpSession session,WhereRequest whereRequest){
        try {
            Long stationId = (Long)session.getAttribute("stationId");
            List<OrderDetail> orderDetailList = orderDetailService.listStationTasks(stationId, whereRequest);
            List<OrderDetailVO> orderDetailVOs = orderDetailList.stream().map(orderDetail -> generateOrderDetailVO(orderDetail)).collect(Collectors.toList());
            PageInfo<OrderDetailVO> detailPageInfo = new PageInfo<>(orderDetailVOs);
            return AjaxResult.success(detailPageInfo, "查询任务列表进展成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("查询任务列表进展失败");
        }

    }

    //转化为试图任务列表
    private OrderDetailVO generateOrderDetailVO(OrderDetail orderDetail){
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setBeginDate(orderDetail.getCreateTime());
        orderDetailVO.setFinishDate(orderDetail.getFinishDate());
        orderDetailVO.setGoodsInfoList(orderDetail.getGoodsInfoList());
        Order findOrder = orderService.getOrder(orderDetail.getOrderId());
        orderDetailVO.setNeedSign(findOrder.getOrderSetting().getNeedSign());
        orderDetailVO.setRobot(findOrder.getRobot());
        orderDetailVO.setStartStation(findOrder.getStartStation());
        orderDetailVO.setStatus(orderDetail.getStatus());
        return orderDetailVO;
    }

}
