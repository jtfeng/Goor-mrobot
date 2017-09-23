package cn.muye.order.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
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
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
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
    public void saveWaitOrder(Order order) {
        //保存订单
        preInject(order);
        orderMapper.saveOrder(order);
        //在此前保存起始站
        OrderSetting sqlSetting = orderSettingService.getById(order.getOrderSetting().getId());
        if(sqlSetting.getStartStation()!= null && sqlSetting.getStartStation().getId()!= null){
            OrderDetail startDetail = new OrderDetail();
            startDetail.setOrderId(order.getId());
            startDetail.setStationId(sqlSetting.getStartStation().getId());
            startDetail.setPlace(OrderConstant.ORDER_DETAIL_PLACE_START);
            startDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetailService.save(startDetail);
        }
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetail.setPlace(OrderConstant.ORDER_DETAIL_PLACE_MIDDLE);
            orderDetailService.save(orderDetail);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
        //之后保存末尾站
        if(sqlSetting.getEndStation()!=null && sqlSetting.getEndStation().getId()!= null){
            OrderDetail endDetail = new OrderDetail();
            endDetail.setOrderId(order.getId());
            endDetail.setStationId(sqlSetting.getEndStation().getId());
            endDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            endDetail.setPlace(OrderConstant.ORDER_DETAIL_PLACE_END);
            orderDetailService.save(endDetail);
        }
    }

    @Override
    public AjaxResult saveOrder(Order order) {
        saveWaitOrder(order);
        //在这里调用任务生成器
        //return AjaxResult.success();
        return generateMissionList(order.getId());
    }

    /**
     * 优先固定路径导航订单
     * @param order
     * @return
     */
    @Override
    public AjaxResult savePathOrder(Order order) {
        saveWaitOrder(order);
        return generateMissionListPathNav(order.getId());
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

    @Override
    public void changeOrderStatus(Long id, Integer status) {
        Order changeOrder = new Order(id);
        changeOrder.setStatus(status);
        orderMapper.updateOrder(changeOrder);
    }

    /**
     * 调用 任务生成器
     * @param orderId
     * @return
     */
    public AjaxResult generateMissionList(Long orderId){
        Order sqlOrder = getOrder(orderId);
        AjaxResult ajaxResult = missionFuncsService.createMissionLists(sqlOrder);
        /*if(!ajaxResult.isSuccess()){
            //修改该order属性,重新回到等待状态
            orderMapper.returnToWaitOrder(orderId, OrderConstant.ORDER_STATUS_WAIT);
            //订单失败，进入队列模式，前端返回提示变化
            ajaxResult = AjaxResult.failed("订单已接收，等待机器分配");
        }else {
            ajaxResult = AjaxResult.success("订单已接收，开始执行任务");
        }*/
        if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ajaxResult;

    }

    /**
     * 调用 任务生成器
     * @param orderId
     * @return
     */
    public AjaxResult generateMissionListPathNav(Long orderId){
        Order sqlOrder = getOrder(orderId);
        AjaxResult ajaxResult = missionFuncsService.createMissionListsPathNav(sqlOrder);
        /*if(!ajaxResult.isSuccess()){
            //修改该order属性,重新回到等待状态
            orderMapper.returnToWaitOrder(orderId, OrderConstant.ORDER_STATUS_WAIT);
            //订单失败，进入队列模式，前端返回提示变化
            ajaxResult = AjaxResult.failed("订单已接收，等待机器分配");
        }else {
            ajaxResult = AjaxResult.success("订单已接收，开始执行任务");
        }*/
        if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ajaxResult;

    }

    /**
     * 查询数据库内排列订单并处理
     */
    @Override
    public void checkWaitOrders() {
        Order domain = new Order();
        domain.setStatus(OrderConstant.ORDER_STATUS_WAIT);
        List<Order> waitOrders = orderMapper.listByDomain(domain);
        for (Order waitOrder : waitOrders) {
            logger.info("检测订单号为{} ",waitOrder.getId());
            Order sqlOrder = getOrder(waitOrder.getId());
            Robot availableRobot = robotService.getAvailableRobotByStationId(sqlOrder.getStartStation().getId(),sqlOrder.getOrderSetting().getRobotType().getId());
            if(availableRobot == null){
                //依旧无可用机器
                logger.info("未获取到可使用机器人");
                logger.info("本次订单号为{}检测结束", waitOrder.getId());
                continue;
            }else{
                logger.info("正在请求机器人,编号为{}",availableRobot.getCode());
                waitOrder.setRobot(availableRobot);
                waitOrder.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
                orderMapper.updateOrder(waitOrder);
                AjaxResult ajaxResult = generateMissionList(waitOrder.getId());
                if(!ajaxResult.isSuccess()){
                    availableRobot.setBusy(Boolean.FALSE);
                    robotService.updateSelective(availableRobot);
                    logger.info("请求机器人失败");
                }
            }
            logger.info("本次订单号为{}检测结束", waitOrder.getId());
        }
    }

    @Override
    public List<Order> listOrdersByStationAndStatus(Long stationId, Integer orderStatus) {
        Order order = new Order();
        order.setStatus(orderStatus);
        order.setStartStation(new Station(stationId));
        return orderMapper.listByDomain(order);
    }

    @Override
    public List<Order> listOrdersByStation(Long stationId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        return orderMapper.listOrdersByStation(stationId);
    }

    /**
     * AGV 中断当前任务反向返回出发地功能
     * @param mapPoint
     */
    @Override
    public void backToStartPoint(String robotCode, MapPoint mapPoint) {
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
