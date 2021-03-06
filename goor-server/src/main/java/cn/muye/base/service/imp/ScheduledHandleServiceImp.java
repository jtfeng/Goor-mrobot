package cn.muye.base.service.imp;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.model.message.ReceiveMessage;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.service.mapper.message.ReceiveMessageService;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);

    private static ApplicationContext applicationContext;

    private OffLineMessageService offLineMessageService;

    private ReceiveMessageService receiveMessageService;

    private RobotService robotService;

    private RabbitTemplate rabbitTemplate;

    public ScheduledHandleServiceImp() {

    }

    @Override
    public void mqHealthCheck() throws Exception {
        try {
            logger.info("Scheduled mqHealthCheck start");
            if(!getRabbitTemplate()){
                return;
            }
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setSendTime(new Date());
            messageInfo.setSenderId("server");
            messageInfo.setMessageType(MessageType.RABBITMQ_HEARTBEAT);
            logger.info("开始发送goor-server心跳消息");
            rabbitTemplate.convertAndSend(TopicConstants.TOPIC_SERVER_COMMAND, messageInfo);
        } catch (final Exception e) {
            logger.error("Scheduled mqHealthCheck exception", e);
        }
    }

    @Override
    public void executeTwentyThreeAtNightPerDay() {
        logger.info("Scheduled clear message start");
        try {
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
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

    @Override
    public void executeRobotHeartBeat() {
        robotService = applicationContext.getBean(RobotService.class);
        //拿sendTime跟内存里的同步时间
        Long currentTime = new Date().getTime();
        List<Robot> list = robotService.listRobot(SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (list != null && list.size() > 0) {
            for (Robot robot : list) {
                String code = robot.getCode();
                Long sendTime = CacheInfoManager.getRobotAutoRegisterTimeCache(code);
                Robot robotDb = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);
                if (robotDb != null) {
                    //如果大于1分钟
                    if (sendTime == null || (currentTime - sendTime > Constant.CHECK_IF_OFFLINE_TIME)) {
                        robotDb.setOnline(false);
                    } else {
                        robotDb.setOnline(true);
                    }
                    robotService.updateSelective(robotDb);
                }

            }
        }
    }

    private boolean getRabbitTemplate(){
        if(null == applicationContext){
            logger.error("getRabbitTemplate applicationContext is null error");
            return false;
        }
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        if(null == rabbitTemplate){
            logger.error("getRabbitTemplate rabbitTemplate is null error ");
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }
}
