package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.order.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.controller.BaseController;
import cn.muye.order.bean.*;
import cn.muye.order.service.GoodsService;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.order.service.OrderSettingService;
import cn.muye.service.consumer.topic.X86MissionDispatchService;
import cn.muye.util.SessionUtil;
import cn.muye.util.UserUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Selim on 2017/7/8.
 * 订单管理
 *
 */
@Controller
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderSettingService orderSettingService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private RobotPasswordService robotPasswordService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsTypeService goodsTypeService;
    @Autowired
    private StationService stationService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private PointService pointService;
    @Autowired
    private SceneService sceneService;

    @Autowired
    X86MissionDispatchService x86MissionDispatchService;

    /**
     * test
     * @param
     * @return
     */
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult test(@RequestParam("id")Long id){
        try {
            orderDetailService.finishedDetailTask(id,1);
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed();
        }
    }




    /**
     * id 获取订单详情
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getOrder(@RequestParam("id") Long id){
        try {
            Order findOrder = orderService.getOrder(id);
            return AjaxResult.success(findOrder,"获取订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取订单失败");
        }
    }

    /**
     * 测试发送发货卸货取货任务，不要提交
     * @return
     */
    @RequestMapping(value = "sendLoadUnload", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult testLoadUnloadEvent(@RequestBody String json, @RequestParam("robotSn") String robotSn) {
        AjaxResult ajaxResult = x86MissionDispatchService.sendX86MissionDispatch(
                robotSn,
                json
        );
        return ajaxResult;
    }

    /**
     * 保存一个订单
     * @param order
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult saveOrder(@RequestBody Order order,@RequestParam(required = false) String orderNavType){
        Robot arrangeRobot = null;
        try {
            //注入发起站
            Long stationId = userUtil.getStationId();
            order.setStartStation(new Station(stationId));
            //注入场景
            Scene scene = SessionUtil.getScene();
            order.setScene(scene);
            //现在orderSetting后台默认注入默认配置
            if(order.getOrderSetting() == null){
                OrderSetting orderSetting = orderSettingService.getDefaultSetting(stationId);
                order.setOrderSetting(orderSetting);
            }
            OrderSetting setting = orderSettingService.getById(order.getOrderSetting().getId());
            //货架判定
            if(setting.getNeedShelf()){
                if(order.getShelf()== null || order.getShelf().getId() == null){
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "默认配置必须传入货架编号");
                }else {
                    //todo 货架是否被占用？
                }
            }
            Integer robotTypeId = setting.getRobotType().getId();
            //根据 站点id 和 机器人类型 自动选择机器人
            arrangeRobot = robotService.getAvailableRobotByStationId(stationId,robotTypeId);
            if(arrangeRobot == null){
                //暂无可用机器人，反馈成功
                logger.info("本次请求未获取到可用机器人");
               /* order.setStatus(OrderConstant.ORDER_STATUS_WAIT);
                orderService.saveWaitOrder(order);
                return AjaxResult.success("订单已接收，等待机器分配");*/
                return AjaxResult.failed("暂无可用机器人");
            }
            //存在机器人，订单直接下单
            logger.info("获取到机器人，机器人编号为{}", arrangeRobot.getCode());
            order.setRobot(arrangeRobot);
            order.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
            AjaxResult ajaxResult = AjaxResult.failed("订单生成异常");
            if(orderNavType != null && orderNavType.equals(Constant.ORDER_NAV_TYPE_PATH)) {
                ajaxResult = orderService.savePathOrder(order);
            }
            else {
                ajaxResult = orderService.saveOrder(order);
            }
            //若未成功， 机器人状态也回滚
            if(!ajaxResult.isSuccess()){
                if(arrangeRobot != null){
                    logger.info("请求机器人{}失败，订单重新分配", arrangeRobot.getCode());
                    arrangeRobot.setBusy(Boolean.FALSE);
                    robotService.updateSelective(arrangeRobot);
                }
            }else{
                ajaxResult = AjaxResult.success("下单成功，分配到机器人编号为" + arrangeRobot.getCode());
            }
            logger.info("订单申请结束");
            return ajaxResult;
        } catch (Exception e) {
            e.printStackTrace();
            //若失败 机器人状态回滚
            if(arrangeRobot != null){
                arrangeRobot.setBusy(Boolean.FALSE);
                robotService.updateSelective(arrangeRobot);
            }
            return AjaxResult.failed("提交订单出现异常，订单失败");
        }

    }

    /**
     * 获取在等待分配的订单
     * @param
     * @return
     */
    @RequestMapping(value = "listWaitOrderByStation", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listWaitOrdersByStation(){
        try {
            Long stationId = userUtil.getStationId();
            List<Order> waitOrders = orderService.listWaitOrdersByStation(stationId, OrderConstant.ORDER_STATUS_WAIT);
            List<Order> detailWaitOrders = waitOrders.stream().map(order -> orderService.getOrder(order.getId())).collect(Collectors.toList());
            return AjaxResult.success(detailWaitOrders, "获取等待订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取等待订单出错");
        }
    }



    /**
     * 获取下单页面的信息
     * @param
     * @return
     */
    @RequestMapping(value = "getOrderPageInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getOrderPageInfo(@RequestParam("type") Long type){
        try {
            Station station = stationService.findById(userUtil.getStationId());
            if(station == null){
                return AjaxResult.failed("未获取到用户的关联站点");
            }
            List<Goods> goodsList = goodsService.listGoodsByType(type);
            List<Station> stationList = stationService.listAccessStationByStationId(station.getId(),station.getSceneId());
            List<Shelf> shelfList = shelfService.listBySceneAndStoreId(station.getSceneId());
            OrderPageInfoVO orderPageInfoVO = new OrderPageInfoVO();
            orderPageInfoVO.setGoodsList(goodsList);
            orderPageInfoVO.setShelfList(shelfList);
            orderPageInfoVO.setStationList(stationList);
            return AjaxResult.success(orderPageInfoVO, "查询下单页面信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取下单页面的信息失败");
        }

    }


    /**
     * 获取下单设置页面选择的信息
     * @param
     * @return
     */
    @RequestMapping(value = "getOrderSettingPageInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getOrderSettingPageInfo(){
        try {
            Station station = stationService.findById(userUtil.getStationId());
            if(station == null){
                return AjaxResult.failed("未获取到用户的关联站点");
            }
            String mapSceneName = sceneService.getRelatedMapNameBySceneId(station.getSceneId());
            List<GoodsType> goodsTypes = goodsTypeService.listAllByStoreId(GoodsType.class);
            List<MapPoint> startPoints = pointService.listByMapSceneNameAndPointType(mapSceneName, MapPointType.LOAD.getCaption(), SearchConstants.FAKE_MERCHANT_STORE_ID);
            List<MapPoint> endPoints = pointService.listByMapSceneNameAndPointType(mapSceneName, MapPointType.FINAL_UNLOAD.getCaption(), SearchConstants.FAKE_MERCHANT_STORE_ID);
            OrderSettingPageInfoVO orderSettingPageInfoVO = new OrderSettingPageInfoVO();
            orderSettingPageInfoVO.setGoodsTypes(goodsTypes);
            orderSettingPageInfoVO.setStartMapPoints(startPoints);
            orderSettingPageInfoVO.setEndMapPoints(endPoints);
            return AjaxResult.success(orderSettingPageInfoVO, "查询订单设置页面信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("获取订单设置页面的信息失败");
        }

    }

    /**
     * 获取当前用户 查询到的任务列表
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listStationTasks", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listStationTasks(HttpSession session,WhereRequest whereRequest){
        try {
            Long stationId = userUtil.getStationId();
            List<OrderDetail> orderDetailList = orderDetailService.listStationTasks(stationId, whereRequest);
            List<OrderDetailVO> orderDetailVOs = orderDetailList.stream().map(orderDetail -> generateOrderDetailVO(orderDetail)).collect(Collectors.toList());
            PageInfo<OrderDetailVO> detailPageInfo = new PageInfo<>(orderDetailVOs);
            return AjaxResult.success(detailPageInfo, "查询任务列表进展成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("查询任务列表进展失败");
        }

    }

    //转化为试图任务列表
    private OrderDetailVO generateOrderDetailVO(OrderDetail orderDetail){
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        Order findOrder = orderService.getOrder(orderDetail.getOrderId());
        if(findOrder!= null){
            orderDetailVO.setStartStationName(stationService.findById(findOrder.getStartStation().getId()).getName());
            orderDetailVO.setRobotCode(findOrder.getRobot().getCode());
            //根据order封装
            List<OrderDetail> detailList = findOrder.getDetailList();
            List<OrderTransferVO> transferVOs = detailList.stream()
                    .filter(detail -> detail.getId() <= orderDetail.getId())
                    .map(detail -> generateTransferVO(detail))
                    .collect(Collectors.toList());
            orderDetailVO.setTransferVOList(transferVOs);
            //获取标头
            int index = -1;
            StringBuffer transferInfo = new StringBuffer();
            for(int i = 0 ; i < transferVOs.size(); i++){
                if(transferVOs.get(i).getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    index = i;
                    break;
                }
            }
            OrderTransferVO orderTransferVO = null;
            if(index == -1){
                //无运输状态的点，取最后一个点
                orderTransferVO = transferVOs.get(transferVOs.size()-1);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前已签收,站点为" + orderTransferVO.getStationName());
                }
            }else if(index == 0){
                //起始即为未运输，取第一个点
                orderTransferVO = transferVOs.get(0);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    transferInfo.append("当前小车正在前往" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前已签收,站点为" + orderTransferVO.getStationName());
                }
            }else{
                //取上一个点进行判断
                orderTransferVO = transferVOs.get(index - 1);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    transferInfo.append("当前小车正在前往" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前小车正在前往" + transferVOs.get(index).getStationName());
                }
            }
            orderDetailVO.setTransferInfo(transferInfo.toString());
        }
        OrderDetail orderDetailInfo = orderDetailService.getOrderDetailInfo(orderDetail.getId());
        if(orderDetailInfo!= null && orderDetailInfo.getGoodsInfoList()!=null){
            List<GoodsInfoVO> goodsInfoVOList = orderDetailInfo.getGoodsInfoList().stream()
                    .map(goodsInfo -> generateGoodsInfoVO(goodsInfo, findOrder))
                    .collect(Collectors.toList());
            orderDetailVO.setGoodsInfoList(goodsInfoVOList);
        }
        orderDetailVO.setStatus(orderDetailInfo.getStatus());
        return orderDetailVO;
    }

    private GoodsInfoVO generateGoodsInfoVO(GoodsInfo goodsInfo, Order order){
        GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
        goodsInfoVO.setBoxNum(goodsInfo.getBoxNum());
        goodsInfoVO.setGoods(goodsInfo.getGoods());
        goodsInfoVO.setNum(goodsInfo.getNum());
        if(order.getOrderSetting().getNeedShelf()){
            goodsInfoVO.setPassword(robotPasswordService.getPwdByRobotIdAndBoxNum(order.getRobot().getId(), goodsInfo.getBoxNum()));
        }else {
            goodsInfoVO.setPassword(robotPasswordService.getPwdByRobotId(order.getRobot().getId()));
        }
        return goodsInfoVO;
    }

    private OrderTransferVO generateTransferVO(OrderDetail detail){
        OrderTransferVO orderTransferVO = new OrderTransferVO();
        Station station = stationService.findById(detail.getStationId());
        orderTransferVO.setStationName(station == null ? null : station.getName());
        orderTransferVO.setStatus(detail.getStatus());
        if(detail.getFinishDate()!=null){
            orderTransferVO.setFinishDate(DateTimeUtils.getDefaultDateString(detail.getFinishDate()));
        }
        return orderTransferVO;
    }

}
