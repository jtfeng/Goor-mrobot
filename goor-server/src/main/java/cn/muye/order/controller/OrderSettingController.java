package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.order.OrderSetting;
import cn.muye.base.controller.BaseController;
import cn.muye.order.service.OrderSettingService;
import cn.muye.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
@Controller
@RequestMapping(value = "orderSetting")
public class OrderSettingController extends BaseController{

    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private UserUtil userUtil;

    /**
     * 通过id获取 订单配置
     * @param id
     * @return
     */
     @RequestMapping(method = RequestMethod.GET)
     @ResponseBody
     public AjaxResult getOrderSetting(@RequestParam("id") Long id){
        try {
            OrderSetting orderSetting = orderSettingService.getById(id);
            return AjaxResult.success(orderSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("查询配置内部出错");
        }
    }

    /**
     * 获取该站 可用配置
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listAvailableOrderSetting(){
        try {
            Long stationId = userUtil.getStationId();
            List<OrderSetting> orderSetting = orderSettingService.listAvailableOrderSettingByStationId(stationId);
            return AjaxResult.success(orderSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("查询配置列表内部出错");
        }
    }

    /**
     * 添加 订单配置
     * @param orderSetting
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveOrderSetting(@RequestBody OrderSetting orderSetting){
        try {
            Long stationId = userUtil.getStationId();
            if(stationId == null){
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "session内无法获取站id");
            }
            boolean hasDefaultSetting = orderSettingService.hasDefaultSetting(stationId);
            if(!hasDefaultSetting){
                orderSetting.setDefaultSetting(Boolean.TRUE);
            }
            orderSetting.setStationId(stationId);
            orderSettingService.saveOrderSetting(orderSetting);
            return AjaxResult.success("添加配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("添加配置内部出错");
        }
    }

    /**
     * 修改 订单配置
     * @param orderSetting
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public AjaxResult updateOrderSetting(@RequestBody OrderSetting orderSetting){
        try {
            if(orderSetting.getId() == null){
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "修改订单必须带有id");
            }
            orderSettingService.updateOrderSetting(orderSetting);
            return AjaxResult.success("修改配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("修改配置内部出错");
        }
    }


    /**
     * 修改 订单默认配置
     * @param id
     * @return
     */
    @RequestMapping(value = "updateDefault" , method = RequestMethod.PUT)
    @ResponseBody
    public AjaxResult updateDefaultOrderSetting(@RequestParam("id") Long id){
        try {
            Long stationId = userUtil.getStationId();
            List<OrderSetting> settingList = orderSettingService.listAvailableOrderSettingByStationId(stationId);
            settingList.forEach(orderSetting -> {
               if(orderSetting.getId() == id ){
                   orderSetting.setDefaultSetting(Boolean.TRUE);
               }else {
                   orderSetting.setDefaultSetting(Boolean.FALSE);
               }
                orderSettingService.updateOrderSetting(orderSetting);
            });
            return AjaxResult.success("修改默认配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("修改默认配置内部出错");
        }
    }

    /**
     * 删除 订单配置
     * 做逻辑删除
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public AjaxResult deleteOrderSetting(@RequestParam("id") Long id){
        try {
            orderSettingService.deleteOrderSetting(id);
            return AjaxResult.success("删除配置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("删除配置内部出错");
        }
    }


}
