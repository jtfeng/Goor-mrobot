package cn.muye.erp.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.erp.ApplianceXREF;
import cn.mrobot.bean.erp.appliance.Appliance;
import cn.mrobot.bean.erp.bindmac.StationMacPasswordXREF;
import cn.mrobot.bean.erp.operation.OperationDefaultApplianceXREF;
import cn.mrobot.bean.erp.operation.OperationType;
import cn.mrobot.bean.erp.order.OperationOrder;
import cn.mrobot.bean.erp.order.OperationOrderApplianceXREF;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.erp.appliance.service.ApplianceService;
import cn.muye.erp.bindmac.service.StationMacPasswordXREFService;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.erp.order.service.OperationOrderApplianceXREFService;
import cn.muye.erp.order.service.OperationOrderService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.websocket.Session;
import java.util.*;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
@RestController
public class OperationOrderController {

    private static Logger logger = LoggerFactory.getLogger(OperationOrderController.class);

    @Autowired
    private OperationOrderService operationOrderService;

    @Autowired
    private OperationTypeService operationTypeService;

    @Autowired
    private OperationOrderApplianceXREFService operationOrderApplianceXREFService;

    @Autowired
    private StationMacPasswordXREFService stationMacPasswordXREFService;

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @Autowired
    private ApplianceService applianceService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    //缓存手术室下单的订单编号，用做重发机制检测
    private List<Long> operationOrderIds = Lists.newArrayList();
    /**
     * 手术室下单，订单提交
     *
     * @param operationOrder
     * @return
     */
    @RequestMapping(value = "services/operation/order", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationOrder operationOrder) {
        //检测是否配置有无菌器械室
        AjaxResult asepticApparatusRoomCheckResult = checkHasAsepticApparatusRoom();
        if (!asepticApparatusRoomCheckResult.isSuccess()) {
            return asepticApparatusRoomCheckResult;
        }
        StationMacPasswordXREF asepticApparatusRoomXREF = (StationMacPasswordXREF) asepticApparatusRoomCheckResult.getData();
        //添加无菌器械室在线监测,不在线提示“无菌器械包室系统离线，请联系管理员或稍后再试”
        AjaxResult onlineCheckAsepticApparatusRoomResult = onlineCheckAsepticApparatusRoom(asepticApparatusRoomXREF);
        if (!onlineCheckAsepticApparatusRoomResult.isSuccess()) {
            return onlineCheckAsepticApparatusRoomResult;
        }
        if (OperationOrder.Type.OPERATION_TYPE_ORDER.getCode() == operationOrder.getType()) {
            if (operationOrder.getOperationType().getId() == null) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_ASSLXSQSSLXIDBNWK"));
            }
            //如果订单的type为1，则需要判断当前的器械列表是不是该手术类型的默认器械列表，如果不是，则将type置为3
            checkOperationType(operationOrder);
        }
        operationOrder.init();
        operationOrderService.saveOrder(operationOrder);
        //校验订单的额外器械列表和额外器是否可用
        AjaxResult checkResult = checkApplianceList(operationOrder);
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        //保存手术室订单和额外器械的关联关系
        saveOperationOrderApplianceXREF(operationOrder);
        //重新查询，带出额外器械信息
        OperationOrder operationOrderDB = operationOrderService.findOrderById(operationOrder.getId());
        //缓存手术室下单的订单编号，用做重发机制检测
        operationOrderIds.add(operationOrderDB.getId());
        //向无菌器械室发送消息
        return sendOrderToAsepticApparatusRoom(operationOrderDB, asepticApparatusRoomXREF);
    }

    /**
     * 检测无菌器械室是否在线，无菌器械室登录后会通过websocket向后台发送注册信息，注册时将信息存放在cache中，
     * 当页面关闭时会调用websocket的onclose方法移除cache
     * 所以可以通过websocket的cache校验是否在线
     *
     * @param asepticApparatusRoomXREF
     * @return
     */
    private AjaxResult onlineCheckAsepticApparatusRoom(StationMacPasswordXREF asepticApparatusRoomXREF) {
        Station station = asepticApparatusRoomXREF.getStation();
        Long stationId = station.getId();
        Set<Session> sessionSet = CacheInfoManager.getWebSocketSessionCache(stationId + "");
        if (null == sessionSet || sessionSet.size() <= 0) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_WJQXBSXTLXQLXGLYHSHZS"));
        }
        return AjaxResult.success();
    }

    /**
     * 检验订单中的器械是否都是可用
     *
     * @param operationOrder
     * @return
     */
    private AjaxResult checkApplianceList(OperationOrder operationOrder) {
        List<OperationOrderApplianceXREF> list = operationOrder.getApplianceList();
        for (OperationOrderApplianceXREF xref : list) {
            Appliance appliance = applianceService.findById(xref.getAppliance().getId());
            if (appliance.getDeleteFlag() == 1) {
                //更新订单状态为手术室下单失败
                String msg = localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_XDSB") + appliance.getName() + localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_YJSC");
                operationOrder.setComment(msg);
                updateOperationOrderState(operationOrder, OperationOrder.State.ORDER_FAIL);
                return AjaxResult.failed(msg);
            }
        }
        return AjaxResult.success();
    }

    /**
     * 校验订单类型，确定订单是type 2 还是type 3
     *
     * @param operationOrder
     */
    private void checkOperationType(OperationOrder operationOrder) {
        List<OperationOrderApplianceXREF> operationOrderApplianceList = operationOrder.getApplianceList();
        OperationType operationType = operationTypeService.findOperationTypeById(operationOrder.getOperationType().getId());
        if (operationType == null) {
            return;
        }
        List<OperationDefaultApplianceXREF> operationDefaultApplianceXREFList = operationType.getApplianceList();
        boolean isEquals = isEqualsApplianceList(operationOrderApplianceList, operationDefaultApplianceXREFList);
        if (!isEquals) {
            operationOrder.setType(OperationOrder.Type.OPERATION_TYPE_ORDER_WITHOUT_DEFAULT_APPLIANCE.getCode());
        }
    }

    /**
     * 检验订单的器械列表是否和手术类型的默认器械列表一致
     *
     * @param operationOrderApplianceList
     * @param operationDefaultApplianceXREFList
     * @return
     */
    private boolean isEqualsApplianceList(List<OperationOrderApplianceXREF> operationOrderApplianceList,
                                          List<OperationDefaultApplianceXREF> operationDefaultApplianceXREFList) {
        if (operationOrderApplianceList.size() != operationDefaultApplianceXREFList.size()) {
            return false;
        }
        sort(operationOrderApplianceList);
        sort(operationDefaultApplianceXREFList);
        for (int i = 0; i < operationOrderApplianceList.size(); i++) {
            OperationOrderApplianceXREF operationOrderApplianceXREF = operationOrderApplianceList.get(i);
            OperationDefaultApplianceXREF operationDefaultApplianceXREF = operationDefaultApplianceXREFList.get(i);
            boolean flag = isEqualsApplianceXREF(operationOrderApplianceXREF, operationDefaultApplianceXREF);
            if (!flag) {
                return false;
            }
            continue;
        }
        return true;
    }

    /**
     * 校验关联关系中的额外器械包是否一致
     *
     * @param operationOrderApplianceXREF
     * @param operationDefaultApplianceXREF
     * @return
     */
    private boolean isEqualsApplianceXREF(OperationOrderApplianceXREF operationOrderApplianceXREF, OperationDefaultApplianceXREF operationDefaultApplianceXREF) {
        Appliance operationOrderAppliance = operationOrderApplianceXREF.getAppliance();
        Appliance operationDefaultAppliance = operationDefaultApplianceXREF.getAppliance();
        if (operationOrderAppliance == null && operationDefaultAppliance == null) {
            return true;
        } else if ((operationOrderAppliance == null && operationDefaultAppliance != null) ||
                (operationOrderAppliance != null && operationDefaultAppliance == null)) {
            return false;
        }
        return operationOrderAppliance.getId().equals(operationDefaultAppliance.getId()) &&
                operationOrderApplianceXREF.getNumber() == operationDefaultApplianceXREF.getNumber();
    }

    public <T extends ApplianceXREF> List<T> sort(List<T> list) {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.getAppliance().getId().compareTo(o2.getAppliance().getId());
            }
        });
        return list;
    }

    /**
     * 受理完毕按钮，后台更新订单状态，将订单受理完毕信息推送到发货站，通知发货站准备签收
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "services/operation/order/handle/{id}", method = RequestMethod.GET)
    public AjaxResult handle(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_IDBNWK"));
        }
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        operationOrder.setHandleTime(new Date());
        operationOrder.setState(OperationOrder.State.ASEPTIC_APPARATUS_ROOM_HANDLED.getCode());
        //更新数据库处理时间和和订单状态
        operationOrderService.updateHandleTimeAndState(operationOrder);
        //向订单发起站发送处理通知
        sendWebSocketMessage(operationOrder.getStation().getId(), operationOrder);
        //对接调度系统，将订单发送给调度系统

        return AjaxResult.success(operationOrder, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_CZCG"));
    }

    /**
     * 无菌器械室收到订单数据，通过接口的形式通知云端
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "services/operation/order/receive/{id}", method = RequestMethod.GET)
    public AjaxResult receiveData(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_IDBNWK"));
        }
        //移除缓存手术室下单的订单编号
        operationOrderIds.remove(id);
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        operationOrder.setReceiveTime(new Date());
        operationOrder.setState(OperationOrder.State.WAITING.getCode());
        //更新数据库处理时间和和订单状态
        operationOrderService.updateReceiveTimeAndState(operationOrder);
        //向订单发起站发送处理通知
        sendWebSocketMessage(operationOrder.getStation().getId(), operationOrder);
        //对接调度系统，将订单发送给调度系统

        return AjaxResult.success(operationOrder, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_CZCG"));
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "services/operation/order/{id}", method = RequestMethod.GET)
    public AjaxResult findById(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_IDBNWK"));
        }
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        return AjaxResult.success(operationOrder, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_BDCG"));
    }

    @RequestMapping(value = "services/operation/order", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
//        Integer pageNo = whereRequest.getPage();
//        Integer pageSize = whereRequest.getPageSize();
//
//        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
//        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
//        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装`
        List<OperationOrder> operationTypeList = operationOrderService.listAllOperationOrder(whereRequest);
        //处理订单中的state和type枚举类，将类型改成中文类型以便显示
        operationTypeList = handleEnums(operationTypeList);
//      PageInfo<OperationOrder> page = new PageInfo<OperationOrder>(operationTypeList);
        return AjaxResult.success(operationTypeList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_CXCG"));
    }

    private List<OperationOrder> handleEnums(List<OperationOrder> operationTypeList) {
        for (int i = 0; i < operationTypeList.size(); i++) {
            OperationOrder operationOrder = operationTypeList.get(i);
            OperationOrder.Type type = OperationOrder.Type.getByCode(operationOrder.getType());
            String typeCH = type != null ? localeMessageSourceService.getMessage(type.getName()) : "";
            operationOrder.setTypeCH(typeCH);
            OperationOrder.State state = OperationOrder.State.getByCode(operationOrder.getState());
            String stateCH = state != null ? localeMessageSourceService.getMessage(state.getName()) : "";
            operationOrder.setStateCH(stateCH);
            //替换原数据
            operationTypeList.set(i, operationOrder);
        }
        return operationTypeList;
    }

    private void saveOperationOrderApplianceXREF(OperationOrder operationOrder) {
        Long operationOrderId = operationOrder.getId();
        if (operationOrderId != null) {
            List<OperationOrderApplianceXREF> list = operationOrder.getApplianceList();
            if (null == list || list.size() <= 0) {
                return;
            }
            for (OperationOrderApplianceXREF xref : list) {
                xref.setOperationOrderId(operationOrderId);
                operationOrderApplianceXREFService.save(xref);
            }
        }
    }

    /**
     * 检测是否配置有无菌器械室
     *
     * @return
     */
    private AjaxResult checkHasAsepticApparatusRoom() {
        StationMacPasswordXREF stationMacPasswordXREF = getAsepticApparatusRoomXREF();
        return null == stationMacPasswordXREF ? AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_WSZWJQXSPBQXSZ")) : AjaxResult.success(stationMacPasswordXREF);
    }

    private StationMacPasswordXREF getAsepticApparatusRoomXREF() {
        List<StationMacPasswordXREF> list = stationMacPasswordXREFService.findByType(StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM);
        return (null == list || list.size() <= 0) ? null : list.get(0);
    }

    /**
     * 将订单发送至无菌器械室
     *
     * @param operationOrder
     * @param asepticApparatusRoomXREF
     * @return
     */
    private AjaxResult sendOrderToAsepticApparatusRoom(OperationOrder operationOrder, StationMacPasswordXREF asepticApparatusRoomXREF) {
        Long stationId = asepticApparatusRoomXREF.getStation().getId();
        //websocket将订单信息推送至无菌器械室
        sendWebSocketMessage(stationId, operationOrder);
        return AjaxResult.success(operationOrder, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_erp_order_controller_OperationOrderController_java_XDCG"));
    }

    private void updateOperationOrderState(OperationOrder operationOrder, OperationOrder.State state) {
        operationOrder.setState(state.getCode());
        operationOrderService.update(operationOrder);
    }

    private void sendWebSocketMessage(Long stationId, OperationOrder operationOrder) {
        WSMessage ws = new WSMessage.Builder()
                .messageType(WSMessageType.NOTIFICATION)
                .body(operationOrder)
                .deviceId(String.valueOf(stationId))
                .module(LogType.INFO_ORDER.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(ws);
    }


    /**
     * 添加定时任务，每10秒检查一次有没有手术室订单消息，有，则发送给无菌器械室
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void sendOperationOrderCache() {
        try {
            if (operationOrderIds.size() != 0){
                for (Long operationOrderId : operationOrderIds){
                    OperationOrder operationOrder = operationOrderService.findOrderById(operationOrderId);
                    sendWebSocketMessage(operationOrder.getStation().getId(), operationOrder);
                }
            }
        } catch (Exception e) {
            logger.error("Scheduled sendOperationOrderCache  error", e);
        }
    }

}
