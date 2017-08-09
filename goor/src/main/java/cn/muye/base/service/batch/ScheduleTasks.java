package cn.muye.base.service.batch;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.slam.SlamRequestBody;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import cn.muye.publisher.AppSubService;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
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
    private Ros ros;

    @Autowired
    private AppSubService appSubService;

    @Value("${local.robot.SN}")
    private String localRobotSN;

    @Value("${server.mapPath")
    private String mapPath;

    @Value(TopicConstants.TOPIC_RECEIVE_COMMAND)
    private String topicCommandAndReceiveSN;

    //没5s发送一次心跳消息
    @Scheduled(cron = "*/5 * *  * * * ")
    public void mqHealthCheckScheduled() {
        logger.info("Scheduled mqHealthCheckScheduled task start");
        try {
                ScheduledHandleService service = new ScheduledHandleServiceImp();
                service.mqHealthCheck(topicCommandAndReceiveSN);
                logger.info("schedule mqHealthCheckScheduled task end");
        } catch (Exception e) {
            logger.error("Scheduled send message error", e);
        }
    }

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
            receiveMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            receiveMessageService.deleteBySendTime(receiveMessage);//删除昨天的数据
            OffLineMessage offLineMessage = new OffLineMessage();//TODO 增加删除文件前，查询(DateTimeUtils.getInternalDateByDay(new Date(), -1))，将删除文件写入log或历史库，供查阅
            offLineMessage.setSendTime(DateTimeUtils.getInternalDateByDay(new Date(), -1));
            offLineMessageService.deleteBySendTime(offLineMessage);//删除昨天的数据
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }


    //每30秒触发  获取电量信息，存入数据库
    @Scheduled(cron = "0 */2 * * * *") //test cron
    public void getChargeAndPosition() {
        try {
            logger.info("定时获取电量信息");
            SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.CHARGING_STATUS_INQUIRY);
            appSubService.sendTopic(TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING, slamRequestBody);
        } catch (Exception e) {
            logger.error("获取电量信息或当前位置信息出错", e);
        }
    }

    //默认开机10分钟后请求时间同步，不关机情况下每天同步一次
    @Scheduled(initialDelay = 600000, fixedRate = 24 * 60 * 60 * 1000)
    public void timeSynchronized() {
        logger.info("Scheduled clear message start");
        try {
            ScheduledHandleService service = new ScheduledHandleServiceImp();
            service.timeSynchronized(localRobotSN);
            logger.info("schedule rosHealthCheckScheduled");
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }

    //每天检查一次地图文件夹下是否有zip文件，有就删掉
    @Scheduled(cron = "0 0 12 * * *") //test cron
    public void deleteZipMapFile() {
        logger.info("Scheduled delete Zip map file");
        try {
            File mapPathDir = new File(mapPath);
            if (mapPathDir.exists() && mapPathDir.isDirectory()) {
                File parentDir = new File(mapPathDir.getParent());
                File[] files = parentDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.getName().lastIndexOf(".zip") >= 0 || file.getName().lastIndexOf(".flags") >= 0) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Scheduled clear message error", e);
        }
    }

    //TODO 添加定时任务，当定时任务出现未执行情况时，查看数据库，重新new ScheduledHandle(scheduledExecutor)的未执行的方法;两个重要1：定时任务，2：删除历史数据




}
