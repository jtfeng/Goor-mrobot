package cn.muye.order.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.order.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.area.station.mapper.StationRobotXREFMapper;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BasePreInject;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.order.mapper.GoodsInfoMapper;
import cn.muye.order.mapper.OrderMapper;
import cn.muye.order.service.*;
import cn.muye.service.missiontask.MissionFuncsService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    private StationRobotXREFMapper stationRobotXREFMapper;
    @Autowired
    private MissionItemTaskService missionItemTaskService;
    @Autowired
    private MissionListTaskService missionListTaskService;
    @Autowired
    private MissionTaskService missionTaskService;
    @Autowired
    private MissionWarningService missionWarningService;
    @Autowired
    private MessageBellService messageBellService;
    @Autowired
    private StationService stationService;
    @Autowired
    private ApplyOrderService applyOrderService;

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
            if(orderDetail.getGoodsInfoList()!=null){
                orderDetail.getGoodsInfoList().forEach(goodsInfo -> {
                    goodsInfo.setOrderDetailId(orderDetail.getId());
                    goodsInfoMapper.insert(goodsInfo);
                });
            }
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
        //判定是否 存在applyOrderID，若存在 修改状态
        if(order.getApplyOrderId()!= null){
            ApplyOrder applyOrder = new ApplyOrder();
            applyOrder.setId(order.getApplyOrderId());
            applyOrder.setOrderId(order.getId());
            applyOrder.setStatus(OrderConstant.APPLY_ORDER_STATUS_ACCEPT);
            applyOrder.setDealDate(new Date());
            applyOrderService.updateSelective(applyOrder);
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
            if(getOrder.getShelf()!= null && getOrder.getShelf().getId()!= null){
                getOrder.setShelf(shelfService.getById(getOrder.getShelf().getId()));
            }
            Station startStation = stationService.findById(getOrder.getStartStation().getId());
            if(startStation !=null){
                getOrder.setResscene(startStation.getResscene());
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
        if(!ajaxResult.isSuccess()){
            //修改该order属性,重新回到等待状态
            orderMapper.returnToWaitOrder(orderId, OrderConstant.ORDER_STATUS_WAIT);
            //订单失败，进入队列模式，前端返回提示变化
            ajaxResult = AjaxResult.failed("订单已接收，等待机器分配");
        }else {
            ajaxResult = AjaxResult.success("订单已接收，开始执行任务");
        }
        /*if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }*/
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
        if(!ajaxResult.isSuccess()){
            //修改该order属性,重新回到等待状态
            orderMapper.returnToWaitOrder(orderId, OrderConstant.ORDER_STATUS_WAIT);
            //订单失败，进入队列模式，前端返回提示变化
            ajaxResult = AjaxResult.failed("订单已接收，等待机器分配");
        }else {
            ajaxResult = AjaxResult.success("订单已接收，开始执行任务");
        }
        /*if(!ajaxResult.isSuccess()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }*/
        return ajaxResult;

    }

    /**
     * 查询数据库内排列订单并处理,暂只处理一个
     */
    @Override
    public void checkWaitOrders() {
        Order domain = new Order();
        domain.setStatus(OrderConstant.ORDER_STATUS_WAIT);
        List<Order> waitOrders = orderMapper.listByDomain(domain);
        for (Order waitOrder : waitOrders) {
            logger.info("检测订单号为{} ",waitOrder.getId());
            Order sqlOrder = getOrder(waitOrder.getId());
            GoodsType goodsType = sqlOrder.getOrderSetting().getGoodsType();
            Robot availableRobot = null;
            if(goodsType == null){
                availableRobot = robotService.getAvailableRobotByStationId(sqlOrder.getStartStation().getId(),null);
            }else {
                availableRobot = robotService.getAvailableRobotByStationId(sqlOrder.getStartStation().getId(),goodsType.getRobotTypeId());
            }
            if(availableRobot == null){
                //依旧无可用机器
                logger.info("未获取到可使用机器人");
                logger.info("本次订单号为{}检测结束", waitOrder.getId());
            }else{
                logger.info("正在请求机器人,编号为{}",availableRobot.getCode());
                waitOrder.setRobot(availableRobot);
                waitOrder.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
                orderMapper.updateOrder(waitOrder);
                AjaxResult ajaxResult = generateMissionListPathNav(waitOrder.getId());
                //AjaxResult ajaxResult = AjaxResult.success();
                if(!ajaxResult.isSuccess()){
                    availableRobot.setBusy(Boolean.FALSE);
                    robotService.updateSelective(availableRobot);
                    logger.info("请求机器人失败");
                }
                logger.info("本次订单号为{}检测结束", waitOrder.getId());
                break;
            }

        }
    }

    /**
     * 机器人 提供SN号 主动请求队列任务 执行
     * @param robotSn
     */
    @Override
    public void robotRequestWaitOrder(String robotSn) {
        logger.info("本次主动请求的机器人为{}", robotSn);
        Robot robot = robotService.getByCode(robotSn, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if(robot!=null){
            logger.info("获取到该机器人");
            StationRobotXREF queryStationRobotXREF = new StationRobotXREF();
            queryStationRobotXREF.setRobotId(robot.getId());
            List<StationRobotXREF> stationRobotXREFs = stationRobotXREFMapper.select(queryStationRobotXREF);
            List<Long> stationList = stationRobotXREFs.stream().map(stationRobotXREF -> stationRobotXREF.getStationId()).collect(Collectors.toList());
            Order waitOrder = orderMapper.findFirstWaitOrder(stationList);
            if(waitOrder!= null){
                logger.info("获取到队列订单号为{}", waitOrder.getId());
                waitOrder.setRobot(robot);
                waitOrder.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
                orderMapper.updateOrder(waitOrder);
                //调用任务生成器
                AjaxResult ajaxResult = generateMissionListPathNav(waitOrder.getId());
                if(!ajaxResult.isSuccess()){
                    logger.info("请求机器人失败");
                }else {
                    //请求成功，修改机器人为忙碌状态
                    robot.setBusy(Boolean.TRUE);
                    robotService.updateSelective(robot);
                    logger.info("请求机器人成功");
                }
            }else {
                logger.info("队列中没有该机器人可调度的任务");
            }
        }else {
            logger.info("系统后台未检测到该SN号的机器人");
        }

    }

    /**
     * 根据订单id 中止任务（同时释放锁等等）
     * @param orderId
     */
    @Override
    public void stopAllMissions(Long orderId) {
        //修改订单状态设置为废弃
        changeOrderStatus(orderId, OrderConstant.ORDER_STATUS_EXPIRE);
        //释放电梯及路径锁
        MissionListTask missionListTask = missionListTaskService.findByOrderId(orderId);
        if(missionListTask!= null){
            List<MissionItemTask> missionItemList = missionItemTaskService.findByListIdAndItemNameEqualToUnlock(missionListTask.getId());
            missionItemList.forEach(missionItemTask -> {
                if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_elevator_unlock)){
                    missionItemTask.getData();

                }else if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_roadpath_unlock)){

                }else {

                }
            });
        }


    }

    /**
     * 检测运行订单执行任务的超时警报 添加至Message_Bell内
     */
    @Override
    public void checkOrderMissionOverTime() {
        Date currentTime = new Date();
        long oneDayBefore = currentTime.getTime() - 24*60*60*1000;
        Date beforeTime = new Date(oneDayBefore);
        List<Long> processingOrderIdsToday = orderMapper.listProcessingOrderIdsToday(beforeTime, currentTime);
        if(processingOrderIdsToday == null || processingOrderIdsToday.size() == 0){
            return;
        }
        List<MissionListTask> missionListTasks = missionListTaskService.findByOrderIds(processingOrderIdsToday);
        for (MissionListTask missionListTask : missionListTasks) {
            List<MissionItemTask> missionItemTaskList = missionItemTaskService.findByListIdAndItemName(missionListTask.getId());
            Date lastFinishData = null;
            Long lastStationId = null;
            Order order = orderMapper.getById(missionListTask.getOrderId());
            Long sendStationId = order.getStartStation().getId();
            Robot currentRobot = new Robot();
            if(order!=null){
                currentRobot = robotService.findById(order.getRobot().getId());
            }
            for (MissionItemTask missionItemTask : missionItemTaskList) {
                if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_load)
                        ||missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)){
                    //获取取货的结束时间
                    Date finishDate = missionItemTask.getFinishDate();
                    //若无，则直接跳出,无警报信息
                    if(finishDate == null){
                        break;
                    }else {
                        lastFinishData = finishDate;
                        lastStationId = getStationIdByMissionId(missionItemTask.getMissionId());
                    }
                }else if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_unload)
                        ||missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_finalUnload)){
                    Date finishDate = missionItemTask.getFinishDate();
                    Long stationId = getStationIdByMissionId(missionItemTask.getMissionId());
                    if(lastFinishData!=null){
                        //无结束时间，开始报警时间测定
                        if(finishDate == null){
                            Long warningTime = missionWarningService.getWarningTime(lastStationId, stationId);
                            if(warningTime == null){
                                logger.info("{}到{}站id的无超时时间设置", lastStationId, stationId);
                            }else {
                                //超时系数1.5 暂写死
                                Date overTimeDate = new Date(lastFinishData.getTime() + warningTime*1500L);
                                if(currentTime.compareTo(overTimeDate) > 0){
                                    //已超时，需要警报
                                    int overTimeMins = DateTimeUtils.getTimeGap(currentTime, overTimeDate);
                                    MessageBell messageBell = messageBellService.findByMissionItemId(missionItemTask.getId());
                                    String lastStationName = "";
                                    String nowStationName = "";
                                    Station lastStation = stationService.findById(lastStationId);
                                    if(lastStation!=null){
                                        lastStationName = lastStation.getName();
                                    }
                                    Station nowStation = stationService.findById(stationId);
                                    if(nowStation!=null){
                                        nowStationName = nowStation.getName();
                                    }
                                    String message = "运送从" +lastStationName+"站至"+ nowStationName+"站已超时"+ overTimeMins + "分钟";
                                    logger.info("超时内容为："+ message);
                                    if(messageBell== null){
                                        //新增
                                        MessageBell newMessageBell = new MessageBell(message, currentRobot.getCode(), OrderConstant.MESSAGE_BELL_OVERTIME_WARNING, sendStationId, OrderConstant.MESSAGE_BELL_UNREAD);
                                        newMessageBell.setMissionItemId(missionItemTask.getId());
                                        messageBellService.save(newMessageBell);
                                    }else {
                                        //修改超时时间
                                        messageBell.setMessage(message);
                                        messageBellService.updateSelective(messageBell);
                                    }
                                }
                            }
                            break;
                        }else{
                            //有结束时间，不再超时判定，继续遍历
                            lastFinishData = finishDate;
                            lastStationId = stationId;
                        }
                    }
                }
            }
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

    @Override
    public List<Order> listPageOrderLogsByRobotId(Long robotId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Order> sqlOrders = orderMapper.listDoneOrdersByRobotIdDateDesc(robotId);
        return sqlOrders.stream().map(order -> getOrder(order.getId())).collect(Collectors.toList());
    }

    @Override
    public List<Order> listPageOrderLogsByStationId(Long stationId, Integer page, Integer pageSize) {
        StationRobotXREF query = new StationRobotXREF();
        query.setStationId(stationId);
        List<StationRobotXREF> stationRobotXREFList = stationRobotXREFMapper.select(query);
        List<Long> robotIdList = stationRobotXREFList.stream().map(stationRobotXREF -> stationRobotXREF.getRobotId()).collect(Collectors.toList());
        PageHelper.startPage(page, pageSize);
        List<Order> sqlOrders = orderMapper.listDoneOrdersByRobotIdListDateDesc(robotIdList);
        return sqlOrders.stream().map(order -> getOrder(order.getId())).collect(Collectors.toList());
    }

    //通过missionId 获取对应站id
    public Long getStationIdByMissionId(Long missionId){
        MissionTask missionTask = missionTaskService.findById(missionId);
        if(missionTask == null){
            return null;
        }else {
            OrderDetail orderDetail = orderDetailService.findById(Long.parseLong(missionTask.getOrderDetailMission()));
            return orderDetail == null ? null: orderDetail.getStationId();
        }
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
