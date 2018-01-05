package cn.muye.erp.order.controller;

import cn.mrobot.bean.AjaxResult;
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
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.erp.bindmac.service.StationMacPasswordXREFService;
import cn.muye.erp.operation.service.OperationTypeService;
import cn.muye.erp.order.service.OperationOrderApplianceXREFService;
import cn.muye.erp.order.service.OperationOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/12
 */
@RestController
public class OperationOrderController {

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

    /**
     * 手术室下单，订单提交
     *
     * @param operationOrder
     * @return
     */
    @RequestMapping(value = "services/operation/order", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationOrder operationOrder) {
        List<OperationOrderApplianceXREF> applianceList = operationOrder.getApplianceList();
        if (null == applianceList || applianceList.isEmpty()) {
            return AjaxResult.failed("器械列表不能为空");
        }

        if (OperationOrder.Type.OPERATION_TYPE_ORDER.getCode() == operationOrder.getType()) {
            if (operationOrder.getOperationType().getId() == null) {
                return AjaxResult.failed("按手术类型申请手术类型ID不能为空");
            }
            //如果订单的type为1，则需要判断当前的器械列表是不是该手术类型的默认器械列表，如果不是，则将type置为3
            checkOperationType(operationOrder);
        }
        operationOrder.init();
        operationOrderService.saveOrder(operationOrder);
        //保存手术室订单和额外器械的关联关系
        saveOperationOrderApplianceXREF(operationOrder);

        //重新查询，带出额外器械信息
        OperationOrder operationOrderDB = operationOrderService.findOrderById(operationOrder.getId());
        //向无菌器械室发送消息
        return sendOrderToAsepticApparatusRoom(operationOrderDB);
    }

    private void checkOperationType(OperationOrder operationOrder) {
        List<OperationOrderApplianceXREF> operationOrderApplianceList = operationOrder.getApplianceList();
        OperationType operationType = operationTypeService.findOperationTypeById(operationOrder.getOperationType().getId());
        List<OperationDefaultApplianceXREF> operationDefaultApplianceXREFList = operationType.getApplianceList();
        boolean isEquals = isEqualsApplianceList(operationOrderApplianceList, operationDefaultApplianceXREFList);
        if (!isEquals) {
            operationOrder.setType(OperationOrder.Type.OPERATION_TYPE_ORDER_WITHOUT_DEFAULT_APPLIANCE.getCode());
        }
    }

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

    private boolean isEqualsApplianceXREF(OperationOrderApplianceXREF operationOrderApplianceXREF, OperationDefaultApplianceXREF operationDefaultApplianceXREF) {
        Appliance operationOrderAppliance = operationOrderApplianceXREF.getAppliance();
        Appliance operationDefaultAppliance = operationDefaultApplianceXREF.getAppliance();
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
            return AjaxResult.failed("ID不能为空");
        }
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        operationOrder.setHandleTime(new Date());
        operationOrder.setState(OperationOrder.State.ASEPTIC_APPARATUS_ROOM_HANDLED.getCode());
        //更新数据库处理时间和和订单状态
        operationOrderService.updateHandleTimeAndState(operationOrder);
        //向订单发起站发送处理通知
        sendWebSocketMessage(operationOrder.getStation().getId(), operationOrder);
        //对接调度系统，将订单发送给调度系统

        return AjaxResult.success(operationOrder, "操作成功");
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
            return AjaxResult.failed("ID不能为空");
        }
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        operationOrder.setReceiveTime(new Date());
        operationOrder.setState(OperationOrder.State.WAITING.getCode());
        //更新数据库处理时间和和订单状态
        operationOrderService.updateReceiveTimeAndState(operationOrder);
        //向订单发起站发送处理通知
        sendWebSocketMessage(operationOrder.getStation().getId(), operationOrder);
        //对接调度系统，将订单发送给调度系统

        return AjaxResult.success(operationOrder, "操作成功");
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
            return AjaxResult.failed("ID不能为空");
        }
        OperationOrder operationOrder = operationOrderService.findOrderById(id);
        return AjaxResult.success(operationOrder, "绑定成功");
    }

    @RequestMapping(value = "services/operation/order", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest) {
//        Integer pageNo = whereRequest.getPage();
//        Integer pageSize = whereRequest.getPageSize();
//
//        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
//        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
//        PageHelper.startPage(pageNo, pageSize);
        //用PageInfo对结果进行包装
        List<OperationOrder> operationTypeList = operationOrderService.listAllOperationOrder(whereRequest);
//        PageInfo<OperationOrder> page = new PageInfo<OperationOrder>(operationTypeList);

        return AjaxResult.success(operationTypeList, "查询成功");
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

    private AjaxResult sendOrderToAsepticApparatusRoom(OperationOrder operationOrder) {
        List<StationMacPasswordXREF> list = stationMacPasswordXREFService.findByType(StationMacPasswordXREF.Type.ASEPTIC_APPARATUS_ROOM);
        if (null == list || list.size() <= 0) {
            return AjaxResult.failed("未设置无菌器械室平板，请先设置");
        }
        StationMacPasswordXREF stationMacPasswordXREF = list.get(0);
        Long stationId = stationMacPasswordXREF.getStation().getId();
        //websocket将订单信息推送至无菌器械室
        sendWebSocketMessage(stationId, operationOrder);
        return AjaxResult.success(operationOrder, "下单成功");
    }

    private void sendWebSocketMessage(Long stationId, OperationOrder operationOrder) {
        WSMessage ws = new WSMessage.Builder()
                .messageType(WSMessageType.ORDER)
                .body(operationOrder)
                .deviceId(String.valueOf(stationId))
                .module(LogType.INFO_ORDER.getName()).build();
        webSocketSendMessage.sendWebSocketMessage(ws);
    }
}
