package cn.muye.base.service.imp;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.Base64;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.MessageSendService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import com.alibaba.fastjson.JSON;
import com.mpush.api.Client;
import com.mpush.api.http.HttpRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class MessageSendServiceImp implements MessageSendService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(MessageSendServiceImp.class);

    private static ApplicationContext applicationContext;

    private OffLineMessageService offLineMessageService;

    private Client client;

    @Override
    public Integer sendMessage(String toUserId, MessageInfo messageInfo) {
        try {
            this.getOffLineMessageService();
            if (StringUtil.isEmpty(messageInfo.getMessageStatusType())) {
                messageInfo.setMessageStatusType(MessageStatusType.INIT);//初始化
            }
            OffLineMessage message = new OffLineMessage(messageInfo);
            message.setReceiverId(toUserId);//设置接收者
            message.setFailResend(true);
            if (message != null && message.getId() != null) {
                OffLineMessage messageUpdate = new OffLineMessage();
                messageUpdate.setSendCount(message.getSendCount() + 1);//每更新一次加一
                messageUpdate.setId(message.getId());
                messageUpdate.setUpdateTime(new Date());
                messageUpdate.setFailResend(message.isFailResend());//需要设置，不设置默认为false
                messageUpdate.setFinish(message.isFinish());//需要设置，不设置默认为false
                messageUpdate.setReceiptWebSocket(message.isReceiptWebSocket());//需要设置，不设置默认为false
                offLineMessageService.update(messageUpdate);
            } else {
                message.setSendCount(1);//第一次保存为1
                offLineMessageService.save(message);
                messageInfo.setId(message.getId());//设置发送消息的ID
            }
            return this.send(toUserId, messageInfo);
        }catch (Exception e){
            return 0;
        }
    }


    @Override
    public Integer sendReplyMessage(String toUserId, MessageInfo messageInfo) {
        messageInfo.setFailResend(true);
        messageInfo.setRelyMessage("success receive message");
        messageInfo.setMessageType(MessageType.REPLY);
        return this.send(toUserId, messageInfo);
    }

    @Override
    public Integer sendWebSocketMessage(MessageInfo messageInfo, MessageStatusType messageStatusType, String replyMessage){
        //TODO 把发送webSocket消息单独提取出来，1.失败消息，2.成功消息，3.参数不对，提示消息，4.正在处理的提示消息,
        if(StringUtil.isEmpty(messageInfo)
                || StringUtil.isNullOrEmpty(messageInfo.getWebSocketId())
                || !messageInfo.isReceiptWebSocket()){//当设置了需要发送webSocket回执时，发送回执
            return 0;
        }
        if(!StringUtil.isEmpty(messageStatusType)){
            messageInfo.setMessageStatusType(messageStatusType);
        }
        if(!StringUtil.isNullOrEmpty(replyMessage)){
            messageInfo.setRelyMessage(replyMessage);
        }
        return this.send(messageInfo.getWebSocketId(), messageInfo);
    }

    @Override
    public Integer sendNoStatusMessage(String toUserId, MessageInfo messageInfo) {
        return this.send(toUserId, messageInfo);
    }

	private Integer send(String toUserId, MessageInfo messageInfo){
        try {
            this.getClient();
            String jsonString = JSON.toJSONString(messageInfo);
            String content = Base64.encode(jsonString);
            logger.info("start sendMessage parameter: jsonString=" + jsonString + ",content=" + content + ", userId=" + toUserId);
            StringBuffer sb = new StringBuffer();
            sb.append(CacheInfoManager.getAppConfigCache(1L).getMpushPushServer());
            sb.append(Constant.QUESTION_SYMBOL);
            sb.append(Constant.USER_ID);
            sb.append(Constant.EQUAL_SYMBOL);
            sb.append(toUserId);
            sb.append(Constant.WITH_SYMBOL);
            sb.append(Constant.CONTENT);
            sb.append(Constant.EQUAL_SYMBOL);
            sb.append(content);
            if(!client.isRunning()){
                return 0;//发送失败
            }
            client.sendHttp(HttpRequest.buildPost(sb.toString()));//TODO 添加云端某台机器是否在线检查，如果不在线，则找在线的发送
            return 1;
        }catch (Exception e){
            logger.error("-->> send error", e);
            return null;
        }
    }

    private void getClient(){
        client = applicationContext.getBean(Client.class);
    }

    private void getOffLineMessageService(){
        offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageSendServiceImp.applicationContext = applicationContext;
    }
}
