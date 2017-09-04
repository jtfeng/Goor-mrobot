package cn.muye.order.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.order.service.OrderSettingService;
import cn.muye.service.missiontask.MissionFuncsService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * Created by Selim on 2017/7/8.
 */
@Service
@Transactional
public class OrderServiceImpl extends BasePreInject<Order> implements OrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private RobotService robotService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private MissionFuncsService missionFuncsService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void saveWaitOrder(Order order, HttpServletRequest request) {
        //保存订单
        preInject(order, request);
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetailService.save(orderDetail, request);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
    }

    @Override
    public AjaxResult saveOrder(Order order, HttpServletRequest request) {
        saveWaitOrder(order, request);
        //在这里调用任务生成器
        return generateMissionList(order.getId(), request);
    }

    /**
     * 优先固定路径导航订单
     * @param order
     * @return
     */
    @Override
    public AjaxResult savePathOrder(Order order, HttpServletRequest request) {
        //保存订单
        preInject(order, request);
//        order.setStatus(OrderConstant.ORDER_STATUS_UNDONE);
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetailService.save(orderDetail, request);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
        //在这里调用任务生成器
        Order sqlOrder = getOrder(order.getId());
//        AjaxResult ajaxResult = missionFuncsService.createMissionLists(sqlOrder);
        AjaxResult ajaxResult = missionFuncsService.createMissionListsPathNav(sqlOrder, request);
        if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ajaxResult;
    }

    @Override
    public Order getOrder(Long id) {
        Order getOrder = orderMapper.getById(id);
        if(getOrder != null){
            if(getOrder.getRobot()!= null){
                getOrder.setRobot(robotService.getById(getOrder.getRobot().getId()));
            }
            getOrder.setDetailList(orderDetailService.listOrderDetailByOrderId(getOrder.getId()));
            OrderSetting findSetting = orderSettingService.getById(getOrder.getOrderSetting().getId());
            getOrder.setOrderSetting(findSetting);
            if(findSetting != null && findSetting.getNeedShelf()){
                getOrder.setShelf(shelfService.getById(getOrder.getShelf().getId()));
            }
        }
        return getOrder;
    }

    /**
     * 调用 任务生成器
     * @param orderId
     * @return
     */
    public AjaxResult generateMissionList(Long orderId, HttpServletRequest request){
        Order sqlOrder = getOrder(orderId);
        AjaxResult ajaxResult = missionFuncsService.createMissionLists(sqlOrder, request);
        if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ajaxResult;

    }

    /**
     * 查询数据库内排列订单并处理
     */
    @Override
    public void checkWaitOrders(HttpServletRequest request) {
        Order domain = new Order();
        domain.setStatus(OrderConstant.ORDER_STATUS_WAIT);
        List<Order> waitOrders = orderMapper.listByDomain(domain);
        for (Order waitOrder : waitOrders) {
            Order sqlOrder = getOrder(waitOrder.getId());
            Robot availableRobot = robotService.getAvailableRobotByStationId(sqlOrder.getStartStation().getId(),sqlOrder.getOrderSetting().getRobotType().getId());
            if(availableRobot == null){
                //依旧无可用机器
                continue;
            }else{
                waitOrder.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
                orderMapper.updateOrder(waitOrder);
                generateMissionList(waitOrder.getId(), request);
            }
        }
    }

    /**
     * AGV 中断当前任务反向返回出发地功能
     * @param mapPoint
     */
    @Override
    public void backToStartPoint(String robotCode, MapPoint mapPoint, HttpServletRequest request) {
        //发送指定消息，任务中断的时候可以返回出发点
        //TODO: 需要确定这个任务事件的相关 Topic 名称和类型的相关信息
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName("");
        commonInfo.setTopicType("");
        commonInfo.setPublishMessage("");
        MessageInfo info = new MessageInfo();
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotCode);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);
        info.setMessageText(JSON.toJSONString(commonInfo));
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, RabbitMqBean.getRoutingKey(robotCode, false,
                MessageType.EXECUTOR_COMMAND.name()), info);
    }
}
