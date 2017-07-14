package cn.muye.base.consumer.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Created by abel on 17-7-10.
 */
public class MsgSendAsyncTask {

    protected static final Logger logger = LoggerFactory.getLogger(MsgSendAsyncTask.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async("msgSendAsync")
    public Future<String> taskMsgSend(String backResultCommandRoutingKey, MessageInfo info) throws InterruptedException{
        logger.info("msgSendAsync. One task execing...");
        int count = 0;
        AjaxResult ajaxCommandResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(
                TopicConstants.TOPIC_EXCHANGE, backResultCommandRoutingKey, info);
        while (count < 10 && !ajaxCommandResult.isSuccess()){
            count++;
            ajaxCommandResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(
                    TopicConstants.TOPIC_EXCHANGE, backResultCommandRoutingKey, info);
        }
        logger.info("msgSendAsync. task exec finished...");
        return new AsyncResult<>("taskMsgSend finished and exit!");
    }


}
