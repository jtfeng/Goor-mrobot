package cn.muye.order.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.order.Order;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.bean.order.OrderSetting;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.order.service.OrderSettingService;
import cn.muye.service.missiontask.MissionFuncsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

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

    @Override
    public void saveWaitOrder(Order order) {
        //保存订单
        preInject(order);
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetailService.save(orderDetail);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
    }

    @Override
    public AjaxResult saveOrder(Order order) {
        saveWaitOrder(order);
        //在这里调用任务生成器
        return generateMissionList(order.getId());
    }

    /**
     * 优先固定路径导航订单
     * @param order
     * @return
     */
    @Override
    public AjaxResult savePathOrder(Order order) {
        //保存订单
        preInject(order);
//        order.setStatus(OrderConstant.ORDER_STATUS_UNDONE);
        orderMapper.saveOrder(order);
        //保存订单详情
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderConstant.ORDER_DETAIL_STATUS_TRANSFER);
            orderDetailService.save(orderDetail);
            //保存货物信息
            orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                goodsInfo.setOrderDetailId(orderDetail.getId());
                goodsInfoMapper.insert(goodsInfo);
            });
        });
        //在这里调用任务生成器
        Order sqlOrder = getOrder(order.getId());
//        AjaxResult ajaxResult = missionFuncsService.createMissionLists(sqlOrder);
        AjaxResult ajaxResult = missionFuncsService.createMissionListsPathNav(sqlOrder);
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
    public AjaxResult generateMissionList(Long orderId){
        Order sqlOrder = getOrder(orderId);
        AjaxResult ajaxResult = missionFuncsService.createMissionLists(sqlOrder);
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
            Order sqlOrder = getOrder(waitOrder.getId());
            Robot availableRobot = robotService.getAvailableRobotByStationId(sqlOrder.getStartStation().getId(),sqlOrder.getOrderSetting().getRobotType().getId());
            if(availableRobot == null){
                //依旧无可用机器
                continue;
            }else{
                waitOrder.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
                orderMapper.updateOrder(waitOrder);
                generateMissionList(waitOrder.getId());
            }
        }
    }
}
