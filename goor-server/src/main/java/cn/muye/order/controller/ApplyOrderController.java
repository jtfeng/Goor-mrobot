package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationStationXREF;
import cn.mrobot.bean.order.ApplyOrder;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.muye.area.station.service.StationService;
import cn.muye.area.station.service.StationStationXREFService;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.service.ApplyOrderService;
import cn.muye.util.UserUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Selim on 2017/11/9.
 * 申请 他站送货 请求
 */
@RestController
@RequestMapping(value = "applyOrder")
public class ApplyOrderController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyOrderController.class);
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private ApplyOrderService applyOrderService;
    @Autowired
    private StationStationXREFService stationStationXREFService;
    @Autowired
    private StationService stationService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 新增一个申请送货请求
     * @param applyOrder
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public AjaxResult saveApplyOrder(@RequestBody ApplyOrder applyOrder){
        try {
            //发起站注入
            applyOrder.setApplyStationId(userUtil.getStationId());
            applyOrder.setStatus(OrderConstant.APPLY_ORDER_STATUS_WAITING);
            applyOrderService.save(applyOrder);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_SQCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_SQCXYC"));
        }
    }

    /**
     * 获取可申请站列表
     * @return
     */
    @RequestMapping(value = "applyStations", method = RequestMethod.GET)
    public AjaxResult listApplyStations(){
        try {
            Long stationId = userUtil.getStationId();
            if(stationId == null){
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_WHQDYHZ"));
            }else {
                List<StationStationXREF> stationStationXREFs = stationStationXREFService.listByDestinationStationId(stationId);
                List<Station> stationList = stationStationXREFs.stream().map(stationStationXREF -> stationService.findById(stationStationXREF.getOriginStationId())).collect(Collectors.toList());
                return AjaxResult.success(stationList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_HQKSQYHZLBCG"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_XTNBCW"));
        }

    }


    /**
     * 查看待接收的列表
     * @return
     */
    @RequestMapping(value="waitingList", method = RequestMethod.GET)
    public AjaxResult listWaitingApplyOrders(){
        try {
            Long stationId = userUtil.getStationId();
            ApplyOrder queryApplyOrder = new ApplyOrder();
            queryApplyOrder.setSendStationId(stationId);
            queryApplyOrder.setStatus(OrderConstant.APPLY_ORDER_STATUS_WAITING);
            List<ApplyOrder> applyOrderList = applyOrderService.listQueryPageByStoreIdAndOrder(0,0,queryApplyOrder,"CREATE_TIME DESC");
            applyOrderList.forEach(applyOrder -> {
                Station station = stationService.findById(applyOrder.getApplyStationId());
                applyOrder.setApplyStationName(station == null? "": station.getName());
            });
            return AjaxResult.success(applyOrderList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_CKDJSDLBCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_CKDJSDLBCXYC"));
        }
    }

    /**
     * 接收端 点击确认领取, 包装成order对象 跳转至下单页面回显
     * @return
     */
    @RequestMapping(value="dealApplyOrder", method = RequestMethod.POST)
    public AjaxResult dealApplyOrder(@RequestParam("id")Long applyId){
        try {
            ApplyOrder applyOrder = applyOrderService.findById(applyId);
            Order order = null;
            if(applyOrder!= null){
                order = new Order();
                List<OrderDetail> orderDetailList = Lists.newArrayList();
                OrderDetail orderDetail = new OrderDetail();
                Long sendStationId = applyOrder.getSendStationId();
                orderDetail.setStationId(sendStationId);
                orderDetailList.add(orderDetail);
                order.setDetailList(orderDetailList);
                order.setApplyOrderId(applyId);
            }
            return AjaxResult.success(order, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_HQDDXX"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_ApplyOrderController_java_HQDDXXCC"));
        }
    }
}
