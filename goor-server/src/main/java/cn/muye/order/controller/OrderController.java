package cn.muye.order.controller;

import cn.mrobot.bean.order.Order;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.controller.BaseController;
import cn.muye.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Selim on 2017/7/8.
 */
@Controller
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

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
    public AjaxResult saveOrder(@RequestBody Order order){
        try {
            orderService.saveOrder(order);
            return AjaxResult.success("保存订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("保存订单失败");
        }
    }
}
