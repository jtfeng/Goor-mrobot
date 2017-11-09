package cn.muye.base.service.batch;

import cn.mrobot.utils.DateTimeUtils;
import cn.muye.area.pose.service.CurrentPoseService;
import cn.muye.base.export.service.ExportService;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import cn.muye.log.base.service.LogCollectService;
import cn.muye.order.service.OrderDetailService;
import cn.muye.order.service.OrderService;
import cn.muye.service.consumer.topic.X86MissionCommonRequestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by enva on 2017/05/12.
 */
@Component
@Configurable
//@EnableScheduling
public class ScheduleTasks {

    private Logger logger = Logger.getLogger(ScheduleTasks.class);

    @Autowired
    private OffLineMessageService offLineMessageService;

    @Autowired
    private ReceiveMessageService receiveMessageService;

    @Autowired
    private LogCollectService logCollectService;

    @Autowired
    private CurrentPoseService currentPoseService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ExportService exportService;

    private final static Object lock = new Object();

    //每10s发送未成功的消息
//    @Scheduled(cron = "*/5 * *  * * * ")
//    public void sendMessageSchedule() {
//        logger.info("Scheduled send message start");
//        try {
//            List<OffLineMessage> list = offLineMessageService.listByIsSuccess(false);
//            for (OffLineMessage message : list) {
//                MessageInfo info = new MessageInfo(message);
//                messageSendService.sendMessage(message.getReceiverId(), info);
//            }
//
////            OffLineMessage message = offLineMessageService.getByIsSuccess(false);
////            if(message != null && message.getId() != null){
////                MessageInfo info = new MessageInfo(message);
////                messageSendService.sendMessage(message.getReceiverId(), info);
////            }
//        } catch (Exception e) {
//            logger.error("Scheduled send message error", e);
//        }
//    }

    //每10s发送回执消息
//    @Scheduled(cron = "*/5 * *  * * * ")
//    public void replyMessageSchedule() {
//        logger.info("Scheduled send reply message start");
//        try {
//            List<ReceiveMessage> list = receiveMessageService.listByIsSuccess(false);
//            for (ReceiveMessage message : list) {
//                MessageInfo info = new MessageInfo(message);
//                messageSendService.sendReplyMessage(message.getSenderId(), info);
//                message.setSuccess(true);
//                receiveMessageService.update(message);
//            }
////            ReceiveMessage message = receiveMessageService.getByIsSuccess(false);
////            if(message != null && message.getId() != null){
////                MessageInfo info = new MessageInfo(message);
////                messageSendService.sendReplyMessage(message.getSenderId(), info);
////                message.setIsSuccess(true);
////                receiveMessageService.update(message);
////            }
//        } catch (Exception e) {
//            logger.error("Scheduled send reply message error", e);
//        }
//    }

    //每分钟执行一次
    //@Scheduled(cron = "0 */1 * * * ?")
    public void scanWaitOrders() {
        synchronized (lock){
            logger.info("开启订单等待队列扫描");
            try {
                orderService.checkWaitOrders();
                logger.info("订单扫描结束");
            } catch (Exception e) {
                logger.error("订单扫描出现异常", e);
            }
        }
    }

    //每分钟执行一次
    //@Scheduled(cron = "0/30 * * * * ?")
    public void testWsOrder() {
        logger.info("开启ws 推送");
        try {
            orderDetailService.finishedDetailTask(316L,1);
            logger.info("ws 推送结束");
        } catch (Exception e) {
            logger.error("ws 推送失败", e);
        }
    }

    //每天晚上23:5触发
    @Scheduled(cron = "0 5 23 * * ?")
//    @Scheduled(cron = "*/30 * *  * * * ") //test cron
    public void clearMessageSchedule() {
        logger.info("Scheduled clear message start");
        try {
            ReceiveMessage receiveMessage = new ReceiveMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            receiveMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }


    //TODO 添加定时任务，当定时任务出现未执行情况时，查看数据库，重新new ScheduledHandle(scheduledExecutor)的未执行的方法;两个重要1：定时任务，2：删除历史数据

    //每分钟触发,记录底盘，自动导航，电量信息日志
    @Scheduled(cron = "0 */3 * * * ?")
    public void collectBaseState() {
        logger.info("Scheduled log");
        try {
            logCollectService.startCollectLog();
        } catch (Exception e) {
            logger.error("Scheduled collect base state error", e);
        }
    }

    //每30秒触发,向任务管理器异步查询当前执行任务状态，任务管理器收到后异步上报信息
    @Autowired
    X86MissionCommonRequestService x86MissionCommonRequestService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void missionStateCommonRequest() {
        logger.debug("missionStateCommonRequest Scheduled send");
        try {
//            logger.info("result is: " + x86MissionCommonRequestService.sendX86MissionStateCommonRequest());
            x86MissionCommonRequestService.sendX86MissionStateCommonRequest();
        } catch (Exception e) {
            logger.error("Scheduled collect base state error", e);
        }
    }

    /**
     * 每秒发送机器人当前的位置信息给工控，工控做机器人排队(暂定2s/次)
     */
    @Scheduled(cron = "*/2 * * * * ?")
    public void sendCurrentPose() {
        try {
            logger.debug("Scheduled send robots current pose start");
            currentPoseService.sendCurrentPose();
        } catch (Exception e) {
            logger.error("Scheduled send robots current pose error", e);
        }
    }

    /**
     * 添加定时任务，每周星期日导出日志表数据到文件中，包括LOG_CHARGE_INFO，LOG_INFO，LOG_MISSION
     */
    @Scheduled(cron = "0 30 23 ? * 7")
    public void exportLogToFile() {
        try {
            logger.info("Scheduled exportLogToFile LOG_CHARGE_INFO，LOG_INFO，LOG_MISSION");
            exportService.exportLogToFile();
        } catch (Exception e) {
            logger.error("Scheduled exportLogToFile LOG_CHARGE_INFO，LOG_INFO，LOG_MISSION", e);
        }
    }
}
