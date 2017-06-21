package cn.muye.service.imp;

import cn.muye.bean.MessageInfo;
import cn.muye.model.message.OffLineMessage;
import cn.muye.model.message.ReceiveMessage;
import cn.muye.service.MessageSendService;
import cn.muye.service.ScheduledHandleService;
import cn.muye.service.mapper.message.OffLineMessageService;
import cn.muye.service.mapper.message.ReceiveMessageService;
import com.mpush.api.Client;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);

    private static ApplicationContext applicationContext;

    private final Lock lockUpgrade = new ReentrantLock();

    private OffLineMessageService offLineMessageService;

    private ReceiveMessageService receiveMessageService;

    private MessageSendService messageSendService;

    private Client client;

    public ScheduledHandleServiceImp(){

    }

    @Override
    public void sendMessage() {
            try {
                logger.info("Scheduled send message start");
                offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
                messageSendService = applicationContext.getBean(MessageSendService.class);
                List<OffLineMessage> list = offLineMessageService.listByIsSuccess(false);//限制发送超过200次的，不再发送,后续改xml在查询时过滤
                for (OffLineMessage message : list) {
//                    if(message.getSendCount() > 200){//限制发送超过200次的，不再发送,后续改xml在查询时过滤
//                        continue;
//                    }
                    MessageInfo info = new MessageInfo(message);
                    messageSendService.sendMessage(message.getReceiverId(), info);
                }
            } catch (final Exception e) {
                logger.error("Scheduled sendMessage exception", e);
            }
    }

    @Override
    public void receiveMessage() {
            try {
                logger.info("Scheduled send reply message start");
                messageSendService = applicationContext.getBean(MessageSendService.class);
                receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
                List<ReceiveMessage> list = receiveMessageService.listByMessageSuccess(new ReceiveMessage(false));//多次回执，未成功和未publish的消息都回执,限制发送超过200次的，不再发送
                for (ReceiveMessage message : list) {
//                    if(message.getSendCount() > 200){// 限制发送超过200次的，不再发送,后续改xml在查询时过滤
//                        continue;
//                    }
                    MessageInfo info = new MessageInfo(message);
                    messageSendService.sendReplyMessage(message.getSenderId(), info);
                    message.setSuccess(true);
                    message.setSendCount(message.getSendCount()+1);
                    receiveMessageService.update(message);
                }
            } catch (final Exception e) {
                logger.error("Scheduled receiveMessage exception", e);
            }
    }

    @Override
    public void executeTwentyThreeAtNightPerDay(){
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }
}
