package cn.muye.service.imp;

import cn.muye.bean.MessageInfo;
import cn.muye.service.MessageHandleService;
import cn.muye.service.MessageSendService;
import cn.muye.service.ScheduledHandleService;
import cn.muye.service.mapper.message.ReceiveMessageService;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MessageHandleServiceImp implements MessageHandleService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(MessageHandleServiceImp.class);

    private static ApplicationContext applicationContext;

    private ScheduledExecutorService scheduledExecutorService;

    private MessageSendService messageSendService;

    private ReceiveMessageService receiveMessageService;

    private final Lock lockUpgrade = new ReentrantLock();

    private void getScheduledExecutorService(){
        scheduledExecutorService = applicationContext.getBean(ScheduledExecutorService.class);
    }

    @Override
    public void executorCommandMessage(final Ros ros, final Client client, final MessageInfo messageInfo) {
        getScheduledExecutorService();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("executorCommandMessage start");
                    ScheduledHandleService service = new ScheduledHandleServiceImp();
                    service.publishMessage(ros, client, messageInfo);
                    logger.info("executorCommandMessage end");
                } catch (Exception e) {
                    logger.error("schedule executorCommandMessage exception", e);
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void executorLogMessage(Ros ros, Client client, MessageInfo messageInfo) {

    }

    @Override
    public void executorResourceMessage(Ros ros, Client client, MessageInfo messageInfo) {

    }

    @Override
    public void executorUpgradeMessage(final Ros ros, final Client client, final MessageInfo messageInfo) {
        if(messageInfo.isFailResend()){//已经入库的发送，不走以下流程，只有无状态发送才走
            return;
        }
        getScheduledExecutorService();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("-->> executorUpgradeMessage start");
                    ScheduledHandleService service = new ScheduledHandleServiceImp();
                    service.downloadResource(ros, client, messageInfo);
                    logger.info("-->> executorUpgradeMessage end");
                } catch (Exception e) {
                    logger.error("schedule executorUpgradeMessage exception", e);
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void replyMessage(Ros ros, Client client, MessageInfo messageInfo) {

    }

    private void test(Ros ros, Client client, MessageInfo messageInfo){
        logger.info("sssssssssss===="+ros+",client==="+client+",messageInfo==="+messageInfo);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageHandleServiceImp.applicationContext = applicationContext;
    }
}
