package cn.muye.service.batch;

import cn.muye.model.message.OffLineMessage;
import cn.muye.model.message.ReceiveMessage;
import cn.muye.service.MessageSendService;
import cn.muye.service.mapper.message.OffLineMessageService;
import cn.muye.service.mapper.message.ReceiveMessageService;
import edu.wpi.rail.jrosbridge.Ros;
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
    private MessageSendService messageSendService;

    @Autowired
    private Ros ros;

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

    //每天晚上23:5触发
    @Scheduled(cron = "0 5 23 * * ?")
//    @Scheduled(cron = "*/30 * *  * * * ") //test cron
    public void clearMessageSchedule() {
        logger.info("Scheduled clear message start");
        try {
            ReceiveMessage receiveMessage = new ReceiveMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            receiveMessage.setSendTime(new Date());
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(new Date());
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }


    //TODO 添加定时任务，当定时任务出现未执行情况时，查看数据库，重新new ScheduledHandle(scheduledExecutor)的未执行的方法;两个重要1：定时任务，2：删除历史数据



}
