package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.order.OrderSetting;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.service.OrderSettingService;
import cn.muye.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Selim on 2017/7/6.
 */
@Controller
@RequestMapping(value = "orderSetting")
public class OrderSettingController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSettingController.class);
    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private GoodsTypeService goodsTypeService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

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
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_CXPZNBCC"));
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
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_CXPZLBNBCC"));
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
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_SESSIONNWFHQZID"));
            }
            boolean hasDefaultSetting = orderSettingService.hasDefaultSetting(stationId);
            if(!hasDefaultSetting){
                orderSetting.setDefaultSetting(Boolean.TRUE);
            }
            orderSetting.setStationId(stationId);
            orderSettingService.saveOrderSetting(orderSetting);
            return AjaxResult.success(orderSetting, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_TJPZCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_TJPZNBCC"));
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
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_XGDDBXDYID"));
            }
            orderSettingService.updateOrderSetting(orderSetting);
            return AjaxResult.success(orderSetting, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_XGPZCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_XGPZNBCC"));
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
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_XGMRPZCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_XGMRPZNBCC"));
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
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_SCPZCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_OrderSettingController_java_SCPZNBCC"));
        }
    }


}
