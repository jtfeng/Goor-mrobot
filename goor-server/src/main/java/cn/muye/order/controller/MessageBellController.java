package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.order.ApplyOrder;
import cn.mrobot.bean.order.MessageBell;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.StationService;
import cn.muye.base.controller.BaseController;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.order.bean.MessageBellVO;
import cn.muye.order.service.ApplyOrderService;
import cn.muye.order.service.MessageBellService;
import cn.muye.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/10/10.
 */
@RestController
@RequestMapping(value = "messageBell")
public class MessageBellController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBellController.class);
    @Autowired
    private MessageBellService messageBellService;
    @Autowired
    private ApplyOrderService applyOrderService;
    @Autowired
    private StationService stationService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 获取 未读消息内容 & 订单申请列表
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public AjaxResult listMessageBell(WhereRequest whereRequest){
        try {
            Long stationId = userUtil.getStationId();
            MessageBell queryBell = new MessageBell();
            queryBell.setStatus(OrderConstant.MESSAGE_BELL_UNREAD);
            queryBell.setStationId(stationId);
            List<MessageBell> messageBellList = messageBellService.listQueryPageByStoreIdAndOrder(whereRequest.getPage(),OrderConstant.MESSAGE_DEFAULT_MAX_NUM,queryBell,"CREATE_TIME DESC");
            boolean hasReceive = false;
            boolean hasSend = false;
            for (MessageBell messageBell : messageBellList) {
                if(messageBell.getType() == OrderConstant.MESSAGE_BELL_RECEIVE){
                    hasReceive = true;
                }else if(messageBell.getType() == OrderConstant.MESSAGE_BELL_SEND){
                    hasReceive = true;
                }
            }

            MessageBellVO messageBellVO = new MessageBellVO();
            messageBellVO.setMessageBellList(messageBellList);
            messageBellVO.setHasReceive(hasReceive);
            messageBellVO.setHasSend(hasSend);
            //注入申请订单信息 同时请求
            ApplyOrder queryApplyOrder = new ApplyOrder();
            queryApplyOrder.setSendStationId(stationId);
            queryApplyOrder.setStatus(OrderConstant.APPLY_ORDER_STATUS_WAITING);
            List<ApplyOrder> applyOrderList = applyOrderService.listQueryPageByStoreIdAndOrder(0,0,queryApplyOrder,"CREATE_TIME DESC");
            applyOrderList.forEach(applyOrder -> {
                Station station = stationService.findById(applyOrder.getApplyStationId());
                applyOrder.setApplyStationName(station == null? "": station.getName());
            });
            messageBellVO.setWaitApplyOrders(applyOrderList);
            return AjaxResult.success(messageBellVO, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_MessageBellController_java_DQXXLBCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_MessageBellController_java_DQXXCC"));
        }
    }

    /**
     * 清除未读 消息
     * @return
     */
    @RequestMapping(value = "clearMessages", method = RequestMethod.GET)
    public AjaxResult clearUnreadMessageBell(@RequestParam("clearDate") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date clearDate){
        try {
            Long stationId = userUtil.getStationId();
            messageBellService.updateByStationIdAndClearDate(stationId, clearDate);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_MessageBellController_java_XXQCCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_order_controller_MessageBellController_java_QCXXCC"));
        }
    }
}
