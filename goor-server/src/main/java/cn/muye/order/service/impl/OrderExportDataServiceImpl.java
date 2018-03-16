package cn.muye.order.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.task.JsonMissionItemDataRoadPathUnlock;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.order.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.order.bean.export.DestinationAnalysisVO;
import cn.muye.order.bean.export.ElevatorUseAnalysisVO;
import cn.muye.order.bean.export.OrderExportData;
import cn.muye.order.bean.export.TransferTaskAnalysisVO;
import cn.muye.order.service.OrderExportDataService;
import cn.muye.order.service.OrderService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2018/3/12.
 */
@Service
public class OrderExportDataServiceImpl implements OrderExportDataService{

    @Autowired
    private OrderService orderService;
    @Autowired
    private MissionItemTaskService missionItemTaskService;
    @Autowired
    private MissionListTaskService missionListTaskService;

    @Override
    public OrderExportData getOrderExportData(Long stationId, Date startDate, Date endDate) {
        OrderExportData orderExportData = new OrderExportData();
        List<TransferTaskAnalysisVO> transferTaskAnalysisVOs = Lists.newArrayList();
        List<ElevatorUseAnalysisVO> elevatorUseAnalysisVOs = Lists.newArrayList();
        List<DestinationAnalysisVO> destinationAnalysisVOs = Lists.newArrayList();
        List<Order> finishedOrderList = orderService.listOrdersByStationAndDate(stationId, startDate, endDate);
        for (Order order : finishedOrderList) {
            Order sqlOrder = orderService.getOrder(order.getId());
            transferTaskAnalysisVOs.add(generateTransferTaskAnalysisVO(sqlOrder));
            destinationAnalysisVOs = generateDestinationAnalysisVO(destinationAnalysisVOs, sqlOrder);
        }
        orderExportData.setTransferTaskAnalysisVOList(transferTaskAnalysisVOs);
        orderExportData.setDestinationAnalysisVOList(destinationAnalysisVOs);
        orderExportData.setElevatorUseAnalysisVOList(elevatorUseAnalysisVOs);
        transfer_index = 1;
        destination_index = 1;
        return orderExportData;
    }

    //序号标示
    private static int transfer_index = 1;
    private static int destination_index = 1;

    //生成运送任务excel表属性
    private TransferTaskAnalysisVO generateTransferTaskAnalysisVO(Order order){
        //为了比较时间差
        Date arriveStartPlaceTimeCompare = null;
        Date startPlaceTimeCompare = null;
        Date backEndPlaceTimeCompare = null;
        Date orderDateCompare = null;
        //实体类建设
        TransferTaskAnalysisVO transferTaskAnalysisVO = new TransferTaskAnalysisVO();
        transferTaskAnalysisVO.setIndex(transfer_index);
        transferTaskAnalysisVO.setOrderDate(DateTimeUtils.getDateString(order.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN_SHORT));
        orderDateCompare = order.getCreateTime();
        transferTaskAnalysisVO.setOrderStation(order.getStartStation() != null ? order.getStartStation().getName() : "");
        transferTaskAnalysisVO.setOrderId(order.getId());
        transferTaskAnalysisVO.setRobotCode(order.getRobot()!= null? order.getRobot().getCode() : "");
        //提取orderDetail内容
        List<OrderDetail> orderDetails = order.getDetailList();
        int num = 0;
        StringBuffer destinationsSb = new StringBuffer("");
        StringBuffer goodsInfoSb = new StringBuffer("");
        for (OrderDetail orderDetail : orderDetails) {
            if(orderDetail.getPlace().equals(OrderConstant.ORDER_DETAIL_PLACE_MIDDLE)){
                num ++;
                destinationsSb.append(orderDetail.getStationName() + ",");
                List<GoodsInfo> goodsInfoList = orderDetail.getGoodsInfoList();
                goodsInfoList.forEach(goodsInfo -> {
                    int goodsNum = goodsInfo.getNum();
                    Goods goods = goodsInfo.getGoods();
                    if(goods != null){
                        goodsInfoSb.append(goodsNum + goods.getUnit() + goods.getName() + ",");
                    }
                });
            }else if(orderDetail.getPlace().equals(OrderConstant.ORDER_DETAIL_PLACE_START)){
                //时间统计
                List<MissionItemTask> missionItemTaskList = missionItemTaskService.listByOrderDetailId(orderDetail.getId());
                for (MissionItemTask missionItemTask : missionItemTaskList) {
                    if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_load)
                            || missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)){
                        //取货时间
                        transferTaskAnalysisVO.setArriveStartPlaceTime(DateTimeUtils.getDateString(missionItemTask.getStartDate(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                        transferTaskAnalysisVO.setStartPlaceTime(DateTimeUtils.getDateString(missionItemTask.getFinishDate(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                        arriveStartPlaceTimeCompare = missionItemTask.getStartDate();
                        startPlaceTimeCompare = missionItemTask.getFinishDate();
                        break;
                    }
                }
            }
        }
        //去除最后一个逗号
        if(destinationsSb.length() > 0){
            destinationsSb.deleteCharAt(destinationsSb.length() -1);
        }
        if(goodsInfoSb.length() > 0){
            goodsInfoSb.deleteCharAt(goodsInfoSb.length() -1);
        }
        transferTaskAnalysisVO.setGoodsInfo(goodsInfoSb.toString());
        transferTaskAnalysisVO.setDestinations(destinationsSb.toString());
        transferTaskAnalysisVO.setNumOfDestination(num);
        transferTaskAnalysisVO.setOrderDateTime(DateTimeUtils.getDateString(order.getCreateTime(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
        //订单执行和结束时间
        MissionListTask missionListTask = missionListTaskService.findByOrderId(order.getId());
        if(missionListTask != null){
            transferTaskAnalysisVO.setExecuteOrderDateTime(DateTimeUtils.getDateString(new Date(missionListTask.getStartTime()), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
            //结束时间不准确，放弃
            //transferTaskAnalysisVO.setBackEndPlaceTime(DateTimeUtils.getDateString(new Date(missionListTask.getStopTime()), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
            //backEndPlaceTimeCompare = new Date(missionListTask.getStopTime());
        }
        //重新获取结束时间点（机器人最后释放锁item任务）
        List<MissionItemTask> missionItemTasks = missionItemTaskService.findByListIdAndItemNameOrderByIdDesc(missionListTask.getId(), MissionFuncsServiceImpl.MissionItemName_roadpath_unlock);
        for (MissionItemTask missionItemTask : missionItemTasks) {
            JsonMissionItemDataRoadPathUnlock jsonMissionItemDataRoadPathUnlock = JSON.parseObject(missionItemTask.getData(), JsonMissionItemDataRoadPathUnlock.class);
            if(jsonMissionItemDataRoadPathUnlock != null){
                if(jsonMissionItemDataRoadPathUnlock.getRoadpath_id().equals(Constant.RELEASE_ROBOT_LOCK_ID)){
                    //优先取结束时间
                    if(missionItemTask.getFinishDate() != null){
                        transferTaskAnalysisVO.setBackEndPlaceTime(DateTimeUtils.getDateString(missionItemTask.getFinishDate(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                        backEndPlaceTimeCompare = missionItemTask.getFinishDate();
                    }else {
                        //结束时间没有的 次取开始时间
                        if(missionItemTask.getStartDate() != null){
                            transferTaskAnalysisVO.setBackEndPlaceTime(DateTimeUtils.getDateString(missionItemTask.getStartDate(), DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                            backEndPlaceTimeCompare = missionItemTask.getStartDate();
                        }
                    }
                }
            }
        }
        //比较数据加入
        if(backEndPlaceTimeCompare!=null && startPlaceTimeCompare!=null){
            transferTaskAnalysisVO.setTotalTransferTime(DateTimeUtils.getTimeGapSecond(backEndPlaceTimeCompare, startPlaceTimeCompare));
        }
        if(startPlaceTimeCompare!=null && arriveStartPlaceTimeCompare!=null){
            transferTaskAnalysisVO.setStartTime(DateTimeUtils.getTimeGapSecond(startPlaceTimeCompare, arriveStartPlaceTimeCompare));
        }
        if(backEndPlaceTimeCompare!=null && orderDateCompare!=null){
            transferTaskAnalysisVO.setTotalTime(DateTimeUtils.getTimeGapSecond(backEndPlaceTimeCompare, orderDateCompare));
        }
        //序列号加1
        transfer_index ++;
        return transferTaskAnalysisVO;
    }

    //目的地人机交互分析 excel输出类
    private List<DestinationAnalysisVO> generateDestinationAnalysisVO(List<DestinationAnalysisVO> destinationAnalysisVOList, Order order){
        List<OrderDetail> orderDetailList = order.getDetailList();
        orderDetailList.forEach(orderDetail -> {
            //若是中途卸货站
            if(orderDetail.getPlace().equals(OrderConstant.ORDER_DETAIL_PLACE_MIDDLE)){
                DestinationAnalysisVO destinationAnalysisVO = new DestinationAnalysisVO();
                destinationAnalysisVO.setIndex(destination_index);
                destinationAnalysisVO.setOrderDate(DateTimeUtils.getDateString(order.getCreateTime(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN_SHORT));
                destinationAnalysisVO.setOrderStation(order.getStartStation() != null ? order.getStartStation().getName() : "");
                destinationAnalysisVO.setOrderId(order.getId());
                destinationAnalysisVO.setRobotCode(order.getRobot() != null ? order.getRobot().getCode() : "");
                destinationAnalysisVO.setDestinationStation(orderDetail.getStationName());
                List<MissionItemTask> missionItemTaskList = missionItemTaskService.listByOrderDetailId(orderDetail.getId());
                for (MissionItemTask missionItemTask : missionItemTaskList) {
                    if(missionItemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_unload)){
                        Date startDate = missionItemTask.getStartDate();
                        Date finishDate = missionItemTask.getFinishDate();
                        destinationAnalysisVO.setArriveDestinationDate(DateTimeUtils.getDateString(startDate, DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                        destinationAnalysisVO.setLeaveDestinationDate(DateTimeUtils.getDateString(finishDate, DateTimeUtils.DEFAULT_TIME_FORMAT_PATTERN_SHORT));
                        if(startDate !=null && finishDate != null){
                            destinationAnalysisVO.setDestinationInteractiveTime(DateTimeUtils.getTimeGapSecond(finishDate, startDate));
                        }
                        break;
                    }
                }
                destinationAnalysisVOList.add(destinationAnalysisVO);
                destination_index ++;
            }
        });
        return destinationAnalysisVOList;
    }
}
