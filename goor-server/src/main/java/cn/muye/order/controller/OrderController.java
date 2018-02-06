package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.assets.good.GoodsType;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.dijkstra.RobotRoadPathResult;
import cn.mrobot.bean.order.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.assets.shelf.service.ShelfService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.controller.BaseController;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.order.bean.*;
import cn.muye.order.service.*;
import cn.muye.service.consumer.topic.X86MissionDispatchService;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
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
    private ElevatorService elevatorService;
    @Autowired
    private ApplyOrderService applyOrderService;
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private MapInfoService mapInfoService;

    @Autowired
    X86MissionDispatchService x86MissionDispatchService;

    @Autowired
    private MissionWarningService missionWarningService;

    /**
     * test
     * @param
     * @return
     */
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult test(){
        try {
            orderDetailService.finishedDetailTask(2095L, OrderConstant.ORDER_DETAIL_STATUS_GET);
            return AjaxResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
            LOGGER.error(e.getMessage(), e);
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
            Station station = stationService.findById(stationId);
            order.setStartStation(station);
//            order.setStartStation(new Station(stationId));
            //注入场景
//            Scene scene = SessionUtil.getScene();
            Scene scene = sceneService.findById(station.getSceneId());
            order.setScene(scene);
            //现在orderSetting后台默认注入默认配置
//            if(order.getOrderSetting() == null){
                OrderSetting orderSetting = orderSettingService.getDefaultSetting(stationId);
                order.setOrderSetting(orderSetting);
//            }
//            OrderSetting setting = orderSettingService.getById(order.getOrderSetting().getId());
            OrderSetting setting = orderSetting;
            //货架判定
            if(setting.getNeedShelf()){
                if(order.getShelf()== null || order.getShelf().getId() == null){
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "默认配置必须传入货架编号");
                }else {
                    //todo 货架是否被占用？
                }
            }
            //根据物品类型注入对应的选取机器人类型
            Integer robotTypeId = null;
            if(setting.getGoodsType()!= null && setting.getGoodsType().getId()!= null){
                GoodsType goodsType = goodsTypeService.findById(setting.getGoodsType().getId());
                robotTypeId = goodsType.getRobotTypeId();
            }
            //根据 站点id 和 机器人类型 自动选择机器人
//            arrangeRobot = robotService.getAvailableRobotByStationId(stationId, robotTypeId);
            RobotRoadPathResult robotRoadPathResult = robotService.getNearestAvailableRobotByOrder(robotTypeId, order);
            order.setRobotRoadPathResult(robotRoadPathResult);
            arrangeRobot = robotRoadPathResult == null ? null : robotRoadPathResult.getRobot();
//            arrangeRobot = robotService.findById(323L);
            if(arrangeRobot == null){
                //暂无可用机器人，反馈成功
                logger.info("本次请求未获取到可用机器人");
                order.setStatus(OrderConstant.ORDER_STATUS_WAIT);
                orderService.saveWaitOrder(order);
                return AjaxResult.success("订单已接收，等待机器分配");
            }
            //存在机器人，订单直接下单
            logger.info("获取到机器人，机器人编号为{}", arrangeRobot.getCode());
            order.setRobot(arrangeRobot);
            order.setStatus(OrderConstant.ORDER_STATUS_BEGIN);
            AjaxResult ajaxResult;
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
            LOGGER.error(e.getMessage(), e);
            //若失败 机器人状态回滚
            if(arrangeRobot != null){
                arrangeRobot.setBusy(Boolean.FALSE);
                robotService.updateSelective(arrangeRobot);
            }
            return AjaxResult.failed("提交订单出现异常，订单失败");
        }

    }

    /**
     * 获取依据状态的订单列表
     * status  0为处理订单中  1为完成订单 2为等待分配订单 3为订单取消
     * @param
     * @return
     */
    @RequestMapping(value = "listOrderByStationAndStatus", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listOrderByStationAndStatus(@RequestParam("status") Integer status){
        try {
            Long stationId = userUtil.getStationId();
            List<Order> waitOrders = orderService.listOrdersByStationAndStatus(stationId, status);
            List<Order> detailWaitOrders = waitOrders.stream().map(order -> orderService.getOrder(order.getId())).collect(Collectors.toList());
            return AjaxResult.success(detailWaitOrders, "获取等待订单成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("获取等待订单出错");
        }
    }


    /**
     * 获取所有的订单
     * @param
     * @return
     */
    @RequestMapping(value = "listOrderByStation", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listOrdersByStation(WhereRequest whereRequest){
        try {
            Long stationId = userUtil.getStationId();
            List<Order> waitOrders = orderService.listOrdersByStation(stationId, whereRequest.getPage(), whereRequest.getPageSize());
            List<Order> detailWaitOrders = waitOrders.stream().map(order -> orderService.getOrder(order.getId())).collect(Collectors.toList());
            PageInfo<Order> pageWaitOrders = new PageInfo<>(detailWaitOrders);
            return AjaxResult.success(pageWaitOrders, "获取所有订单成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("获取所有订单出错");
        }
    }

    /**
     * 取消 等待订单执行
     * @param
     * @return
     */
    @RequestMapping(value = "cancelWaitOrder", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult cancelWaitOrder(@RequestParam("id")Long id){
        try {
            Order sqlOrder = orderService.getOrder(id);
            if(!(OrderConstant.ORDER_STATUS_WAIT.equals(sqlOrder.getStatus()))){
                return AjaxResult.failed("暂无法取消，订单并非处于等待阶段");
            }
            orderService.changeOrderStatus(id, OrderConstant.ORDER_STATUS_EXPIRE);
            return AjaxResult.success( "取消该订单成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("取消该订单出错");
        }
    }



    /**
     * 获取下单页面的信息
     * @param
     * @return
     */
    @RequestMapping(value = "getOrderPageInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getOrderPageInfo(@RequestParam(value = "type", required = false) Long type){
        try {
            Station station = stationService.findById(userUtil.getStationId());
            if(station == null){
                return AjaxResult.failed("未获取到用户的关联站点");
            }
            List<Goods> goodsList = Lists.newArrayList();
            if(type != null){
                goodsList = goodsService.listGoodsByType(type);
            }
            List<Station> stationList = stationService.listAccessStationByStationId(station.getId(),station.getSceneId());
            List<Shelf> shelfList = shelfService.listBySceneAndStoreId(station.getSceneId());
            OrderPageInfoVO orderPageInfoVO = new OrderPageInfoVO();
            orderPageInfoVO.setGoodsList(goodsList);
            orderPageInfoVO.setShelfList(shelfList);
            orderPageInfoVO.setStationList(stationList);
            return AjaxResult.success(orderPageInfoVO, "查询下单页面信息成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
            List<Station> startStations = stationService.listStationsBySceneAndMapPointType(station.getSceneId(), MapPointType.LOAD.getCaption());
            List<Station> endStations = stationService.listStationsBySceneAndMapPointType(station.getSceneId(), MapPointType.FINAL_UNLOAD.getCaption());
            OrderSettingPageInfoVO orderSettingPageInfoVO = new OrderSettingPageInfoVO();
            orderSettingPageInfoVO.setGoodsTypes(goodsTypes);
            orderSettingPageInfoVO.setStartStations(startStations);
            orderSettingPageInfoVO.setEndStations(endStations);
            return AjaxResult.success(orderSettingPageInfoVO, "查询订单设置页面信息成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
            //地图模式 注释上2行，打开下面注释
            /*List<OrderDetailNewVO> orderDetailVOs = orderDetailList.stream().map(orderDetail -> {
                try {
                    return generateOrderDetailNewVO(orderDetail);
                } catch (Exception e) {
                    log.error(e, e.getMessage());
                    return null;
                }
            }).collect(Collectors.toList());
            PageInfo<OrderDetailNewVO> detailPageInfo = new PageInfo<>(orderDetailVOs);*/
            return AjaxResult.success(detailPageInfo, "查询任务列表进展成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("查询任务列表进展失败");
        }

    }


    //转化为视图任务列表，图片版
    private OrderDetailNewVO generateOrderDetailNewVO(OrderDetail orderDetail) throws Exception {
        OrderDetailNewVO orderDetailNewVO = new OrderDetailNewVO();
        Order findOrder = orderService.getOrder(orderDetail.getOrderId());
        if(findOrder != null){
            Station startStation = stationService.findById(findOrder.getStartStation().getId());
            orderDetailNewVO.setStartStationName(startStation == null ? "" : startStation.getName());
            orderDetailNewVO.setRobotCode(findOrder.getRobot().getCode());
            orderDetailNewVO.setOrderYear(DateTimeUtils.getDateString(findOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN_SHORT));
            orderDetailNewVO.setOrderHour(DateTimeUtils.getDateString(findOrder.getCreateTime(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT_NO_SECOND));
            MapPathInfoVO mapPathInfoVO = new MapPathInfoVO();
            //根据order封装
            List<OrderDetail> detailList = findOrder.getDetailList().stream()
                    .filter(detail -> detail.getId() <= orderDetail.getId())
                    .collect(Collectors.toList());
            MapPoint lastMapPoint = null;
            //封装bean
            MapPathInfoDetailVO mapPathInfoDetailVO = new MapPathInfoDetailVO();
            for (int i = 0; i < detailList.size(); i++) {
                 if(i == 0){
                     //起始取货点
                     OrderDetail beginOrderDetail = detailList.get(i);
                     MapPoint startPoint = pointService.findMapPointByStationIdAndCloudType(beginOrderDetail.getStationId(), MapPointType.LOAD.getCaption());
                     mapPathInfoVO.setStartMapPoint(translateToMapPointVO(startPoint));
                     lastMapPoint = startPoint;
                 }else if(i == detailList.size() -1){
                     //最后结束点
                     OrderDetail endOrderDetail = detailList.get(detailList.size()-1);
                     MapPoint endPoint = null;
                     if(endOrderDetail.getPlace() == OrderConstant.ORDER_DETAIL_PLACE_END){
                         endPoint = pointService.findMapPointByStationIdAndCloudType(endOrderDetail.getStationId(), MapPointType.FINAL_UNLOAD.getCaption());
                     }else {
                         Station station = stationService.findById(endOrderDetail.getStationId(), endOrderDetail.getStoreId(), null);
                         for (MapPoint mapPoint : station.getMapPoints()) {
                             endPoint = mapPoint;
                             break;
                         }
                     }
                     mapPathInfoVO.setEndMapPoint(translateToMapPointVO(endPoint));
                     //计算路径点
                     calculateRoadPath(endPoint, lastMapPoint, mapPathInfoDetailVO);
                 }else {
                     OrderDetail midOrderDetail = detailList.get(i);
                     Station station = stationService.findById(midOrderDetail.getStationId(), midOrderDetail.getStoreId(), null);
                     MapPoint midMapPoint = null;
                     for (MapPoint mapPoint : station.getMapPoints()) {
                         midMapPoint = mapPoint;
                         break;
                     }
                     //计算路劲点
                     calculateRoadPath(midMapPoint, lastMapPoint, mapPathInfoDetailVO);
                     //更改为 上个点
                     lastMapPoint = midMapPoint;
                 }
            }
            //获取transfer信息
            //获取标头
            int index = -1;
            StringBuffer transferInfo = new StringBuffer();
            for(int i = 0 ; i < detailList.size(); i++){
                if(detailList.get(i).getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    index = i;
                    break;
                }
            }
            OrderDetail orderTransferVO = null;
            if(index == -1){
                //无运输状态的点，取最后一个点
                orderTransferVO = detailList.get(detailList.size()-1);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前已签收,站点为" + orderTransferVO.getStationName());
                }
            }else if(index == 0){
                //起始即为未运输，取第一个点
                orderTransferVO = detailList.get(0);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    transferInfo.append("当前小车正在前往" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前已签收,站点为" + orderTransferVO.getStationName());
                }
            }else{
                //取上一个点进行判断
                orderTransferVO = detailList.get(index - 1);
                if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_TRANSFER){
                    transferInfo.append("当前小车正在前往" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_GET){
                    transferInfo.append("当前小车已经到达" + orderTransferVO.getStationName());
                }else if(orderTransferVO.getStatus() == OrderConstant.ORDER_DETAIL_STATUS_SIGN){
                    transferInfo.append("当前小车正在前往" + detailList.get(index).getStationName());
                }
            }
            mapPathInfoVO.setMapPathInfoDetail(mapPathInfoDetailVO);
            orderDetailNewVO.setMapPathInfoVO(mapPathInfoVO);
            orderDetailNewVO.setTransferInfo(transferInfo.toString());
            OrderDetail orderDetailInfo = orderDetailService.getOrderDetailInfo(orderDetail.getId());
            if(orderDetailInfo!= null && orderDetailInfo.getGoodsInfoList()!=null){
                List<GoodsInfoVO> goodsInfoVOList = orderDetailInfo.getGoodsInfoList().stream()
                        .map(goodsInfo -> generateGoodsInfoVO(goodsInfo, findOrder))
                        .collect(Collectors.toList());
                orderDetailNewVO.setGoodsInfoList(goodsInfoVOList);
            }
            orderDetailNewVO.setStatus(orderDetailInfo.getStatus());
        }
        return orderDetailNewVO;
    }

    //计算路径提供的抽取方法
    private MapPathInfoDetailVO calculateRoadPath(MapPoint current, MapPoint last, MapPathInfoDetailVO mapPathInfoDetailVO) throws Exception {
        if(current!= null && last!= null){
            List<RoadPathDetail> roadPathDetail = roadPathService.listRoadPathDetailByStartAndEndPointType(last.getId(), current.getId(), null, null, Constant.PATH_TYPE_CLOUD);
            for (RoadPathDetail pathDetail : roadPathDetail) {
                List<MapPoint> roadPathPointList = pathDetail.getRelatePoints();
                for (MapPoint mapPoint : roadPathPointList) {
                    String mapName = mapPoint.getMapName();
                    String sceneName = mapPoint.getSceneName();
                    String key = sceneName + "_" + mapName;
                    //查看map是否已存在此key
                    if(!mapPathInfoDetailVO.getMapInfo().containsKey(key)){
                        List<MapInfo> mapInfos = mapInfoService.getMapInfo(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
                        if(mapInfos!= null && mapInfos.size()> 0){
                            MapInfo mapInfo1= mapInfos.get(0);
                            mapPathInfoDetailVO.getMapInfo().put(key, transferToMapInfoVO(mapInfo1));
                        }
                    }
                    mapPathInfoDetailVO.getMapPointList().add(translateToMapPointVO(mapPoint));
                }
                break;
            }
        }
        return mapPathInfoDetailVO;
    }

    // 地图信息转化为显示类
    private MapInfoVO transferToMapInfoVO(MapInfo mapInfo){
        MapInfoVO mapInfoVO = new MapInfoVO();
        mapInfoVO.setSceneName(mapInfo.getSceneName());
        mapInfoVO.setMapName(mapInfo.getMapName());
        mapInfoVO.setRosMapUrl(mapInfo.getPngImageHttpPath());
        mapInfoVO.setBeautifyMapUrl(null); //后续若存在美化图片即注入
        JSONObject jsonObject = JSON.parseObject(mapInfo.getRos());
        JSONArray originData = jsonObject.getJSONArray("origin");
        if(originData!= null && originData.size()== 3){
            MapPointVO mapPointVO = new MapPointVO();
            mapPointVO.setX(originData.getDouble(0));
            mapPointVO.setY(originData.getDouble(1));
            mapPointVO.setTh(originData.getDouble(2));
            mapPointVO.setMapName(mapInfo.getMapName());
            mapPointVO.setSceneName(mapInfo.getSceneName());
            mapInfoVO.setOriginMapPoint(mapPointVO);
        }
        return mapInfoVO;
    };

    // 地图点转化为显示类
    private MapPointVO translateToMapPointVO(MapPoint mapPoint){
        MapPointVO mapPointVO = new MapPointVO();
        mapPointVO.setX(mapPoint.getX());
        mapPointVO.setY(mapPoint.getY());
        mapPointVO.setTh(mapPoint.getTh());
        mapPointVO.setName(mapPoint.getPointAlias());
        mapPointVO.setMapName(mapPoint.getMapName());
        mapPointVO.setSceneName(mapPoint.getSceneName());
        return mapPointVO;
    }

    //转化为视图任务列表, 文字版
    private OrderDetailVO generateOrderDetailVO(OrderDetail orderDetail){
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        Order findOrder = orderService.getOrder(orderDetail.getOrderId());
        if(findOrder!= null){
            Station startStation = stationService.findById(findOrder.getStartStation().getId());
            orderDetailVO.setStartStationName(startStation == null? "": startStation.getName());
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
            //注入时间
            orderDetailVO.setOrderYear(DateTimeUtils.getDateString(findOrder.getCreateTime(),DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN_SHORT));
            orderDetailVO.setOrderHour(DateTimeUtils.getDateString(findOrder.getCreateTime(),DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT_NO_SECOND));
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



    // ---------- PAD 检测日志 --------------

    /**
     * PAD 日志模块 任务记录
     * @param robotId
     * @return
     */
    @RequestMapping(value = "orderLogs",method = RequestMethod.GET)
    @ResponseBody
    private AjaxResult orderLogs(@RequestParam(value = "robotId", required = false)Long robotId,
                                 WhereRequest whereRequest){
        try {
            List<Order> orderList = Lists.newArrayList();
            if(robotId == null){
                //查询所有该站下所有机器人
                Long stationId = userUtil.getStationId();
                orderList = orderService.listPageOrderLogsByStationId(stationId, whereRequest.getPage(), whereRequest.getPageSize());
            }else {
                orderList = orderService.listPageOrderLogsByRobotId(robotId, whereRequest.getPage(), whereRequest.getPageSize());
            }
            List<OrderLogVO> orderLogVOList = orderList.stream().map(order -> generateOrderLogVO(order)).collect(Collectors.toList());
            PageInfo<OrderLogVO> pageInfo = new PageInfo<>(orderLogVOList);
            return AjaxResult.success(pageInfo, "获取日志任务列表成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统内部出错");
        }
    }

    //注入OrderLogVO
    private OrderLogVO generateOrderLogVO(Order order){
        OrderLogVO orderLogVO = new OrderLogVO();
        orderLogVO.setBeginTime(order.getCreateTime());
        orderLogVO.setEndTime(order.getFinishDate());
        orderLogVO.setRobotName(order.getRobot().getName());
        Integer status = order.getStatus();
        StringBuffer sb = new StringBuffer();
        if(status == OrderConstant.ORDER_STATUS_EXPIRE){
            sb.append("本次送货请求已被中断");
        }else {
            sb.append("去往");
            List<OrderDetail> orderDetailList = order.getDetailList();
            List<OrderLogDetailVO> orderLogDetailVOList = orderDetailList.stream().map(orderDetail -> {
                OrderLogDetailVO orderLogDetailVO = new OrderLogDetailVO();
                orderLogDetailVO.setCheckTime(orderDetail.getFinishDate());
                List<GoodsInfoVO> goodsInfoVOList = orderDetail.getGoodsInfoList().stream()
                        .map(goodsInfo -> generateGoodsInfoVO(goodsInfo, order))
                        .collect(Collectors.toList());
                orderLogDetailVO.setGoodsInfoList(goodsInfoVOList);
                orderLogDetailVO.setLocation(orderDetail.getStationName());
                if (orderDetail.getPlace() == OrderConstant.ORDER_DETAIL_PLACE_START) {
                    orderLogDetailVO.setEventDetail("起始装货");
                } else if (orderDetail.getPlace() == OrderConstant.ORDER_DETAIL_PLACE_MIDDLE) {
                    orderLogDetailVO.setEventDetail("签收货物");
                    sb.append(orderDetail.getStationName() + ",");
                } else if (orderDetail.getPlace() == OrderConstant.ORDER_DETAIL_PLACE_END) {
                    orderLogDetailVO.setEventDetail("终点卸货");
                }
                return orderLogDetailVO;
            }).collect(Collectors.toList());
            sb.deleteCharAt(sb.length()-1);
            sb.append("的货物已送达");
            orderLogVO.setOrderLogDetailVOList(orderLogDetailVOList);
        }
        orderLogVO.setEvent(sb.toString());
        return orderLogVO;
    }

}
