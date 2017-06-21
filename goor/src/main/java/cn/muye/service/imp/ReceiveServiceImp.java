package cn.muye.service.imp;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.Base64;
import cn.mrobot.utils.StringUtil;
import cn.muye.bean.MessageInfo;
import cn.muye.model.message.OffLineMessage;
import cn.muye.model.message.ReceiveMessage;
import cn.muye.service.*;
import cn.muye.service.mapper.message.OffLineMessageService;
import cn.muye.service.mapper.message.ReceiveMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mpush.api.Client;
import edu.wpi.rail.jrosbridge.Ros;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReceiveServiceImp implements ReceiveService, ApplicationContextAware {
    private static Logger logger = Logger.getLogger(ReceiveServiceImp.class);

    private static ApplicationContext applicationContext;

    private ReceiveMessageService receiveMessageService;

    private OffLineMessageService offLineMessageService;

    private Ros ros;

    public ReceiveServiceImp(){

    }

    public ReceiveServiceImp(Client client, byte[] content) {
        this.analysis(client, content);
    }

    @Override
    public void analysis(Client client, byte[] content){
        logger.info("analysis client====" + client + ",content====" + content);
        try {
            JSONObject jsonObject = JSON.parseObject(new String(content, Constant.UTF_8));
            String messageAll = jsonObject.getString(Constant.CONTENT);
            String messageInfo = JSON.parseObject(messageAll).getString(Constant.CONTENT);
            String messageInfoDecode = Base64.decode(messageInfo);
            JSONObject messageInfoJson = JSON.parseObject(messageInfoDecode);
            //此处为打印日志，请勿删除
            logger.info("analysis jsonObject====" + jsonObject + ",messageAll====" + messageAll + ",messageInfo====" + messageInfo + ",messageInfoDecode====" + messageInfoDecode + ",messageInfoJson====" + messageInfoJson);
			this.navigation(client, JSON.toJavaObject(messageInfoJson, MessageInfo.class));
        } catch (Exception e) {
            logger.error("analysis error", e);
        }
    }

    public void navigation(Client client, MessageInfo messageInfo) throws Exception{
        logger.info("navigation messageType====" + messageInfo.getMessageType());
        this.sendMessageSave(messageInfo);//保存接收的消息
        getRos();
        MessageHandleService service = new MessageHandleServiceImp();
        if(MessageType.REPLY.equals(messageInfo.getMessageType())){//接收发送后回执的消息，更新数据库
            service.replyMessage(ros, client, messageInfo);
        }else if(MessageType.EXECUTOR_COMMAND.equals(messageInfo.getMessageType())){
            service.executorCommandMessage(ros, client, messageInfo);
        }else if(MessageType.EXECUTOR_LOG.equals(messageInfo.getMessageType())){
            service.executorLogMessage(ros, client, messageInfo);
        }else if(MessageType.EXECUTOR_RESOURCE.equals(messageInfo.getMessageType())){
            service.executorUpgradeMessage(ros, client, messageInfo);
        }else if(MessageType.EXECUTOR_UPGRADE.equals(messageInfo.getMessageType())) {
            service.executorUpgradeMessage(ros, client, messageInfo);
        }else{
            logger.info("No find messageType matching====");
        }
    }

    private boolean sendMessageSave(MessageInfo messageInfo) throws Exception{
        //保存发送方消息至数据库，处理完业务后以便回执
        if(messageInfo == null
                || messageInfo.getMessageType() == null
                || !messageInfo.isFailResend()
                || StringUtil.isEmpty(messageInfo.getId() + "")){
            return false;
        }
        if(!MessageType.REPLY.equals(messageInfo.getMessageType())){
            if(StringUtil.isEmpty(messageInfo.getSenderId())){
                return false;
            }
            receiveMessageService = applicationContext.getBean(ReceiveMessageService.class);
            ReceiveMessage message = new ReceiveMessage(messageInfo);
            List<ReceiveMessage> messages = receiveMessageService.listByIdAndSenderId(message);//查询当前发送者有没有发送此记
            if(messages.size() <= 0){
                //TODO 直接回执，再存库
                receiveMessageService.save(message);//未发送保存
            }else{
                //TODO 直接回执，再更新库
                ReceiveMessage messageUpdate = new ReceiveMessage();
                messageUpdate.setSuccess(false);//说明发送方未收到回执，需要重新发送回执
                messageUpdate.setId(message.getId());//更新的联合主键
                messageUpdate.setSenderId(message.getSenderId());//更新的联合主键
                messageUpdate.setUpdateTime(new Date());//更新时间
                messageUpdate.setFailResend(message.isFailResend());//需要设置，不设置默认为false
                messageUpdate.setFinish(message.isFinish());//需要设置，不设置默认为false
                messageUpdate.setReceiptWebSocket(message.isReceiptWebSocket());//需要设置，不设置默认为false
                receiveMessageService.update(messageUpdate);//发送过了，又发送，说明没收到回执，需要重新发送回执
            }
        }else if(MessageType.REPLY.equals(messageInfo.getMessageType())){
            offLineMessageService = applicationContext.getBean(OffLineMessageService.class);
            OffLineMessage message = new OffLineMessage();
            message.setMessageStatusType(messageInfo.getMessageStatusType().getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setId(messageInfo.getId());//更新的主键
            message.setUpdateTime(new Date());//更新时间
            message.setFailResend(messageInfo.isFailResend());//需要设置，不设置默认为false
            message.setFinish(messageInfo.isFinish());//需要设置，不设置默认为false
            message.setReceiptWebSocket(messageInfo.isReceiptWebSocket());//需要设置，不设置默认为false
            offLineMessageService.update(message);//更新发送的消息
        }
        return true;
    }

    private void getRos(){
        ros = applicationContext.getBean(Ros.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ReceiveServiceImp.applicationContext = applicationContext;
    }
}
