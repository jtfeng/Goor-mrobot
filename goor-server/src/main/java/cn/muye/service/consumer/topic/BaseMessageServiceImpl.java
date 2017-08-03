package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.service.MessageSendHandleService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by abel on 17-7-11.
 */
@Service
public class BaseMessageServiceImpl implements BaseMessageService {

    private Logger logger = Logger.getLogger(BaseMessageServiceImpl.class);

    @Autowired
    private MsgSendAsyncTask msgSendAsyncTask;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    /**
     * 获取data
     * @param messageInfo
     * @return
     */
    @Override
    public String getData(MessageInfo messageInfo){
        JSONObject requestDataObject =
                JSON.parseObject(messageInfo.getMessageText());
        if (requestDataObject == null){
            return "";
        }
        String ret = null;
        try {
            ret = requestDataObject.getString(TopicConstants.DATA);
        } catch (Exception e) {
            logger.error(e);
        }
        return ret;
    }

    /**
     * 获取消息里的data
     * @param messageInfo
     * @return
     */
    @Override
    public String getPubData(MessageInfo messageInfo){
        JSONObject requestDataObject =
                JSON.parseObject(messageInfo.getMessageText());
        if (requestDataObject == null){
            return "";
        }
        String ret = null;
        try {
            ret = requestDataObject.getString(TopicConstants.DATA);
            JSONObject jsonObjectData = JSON.parseObject(ret);
            if (jsonObjectData != null){
                ret = jsonObjectData.getString(TopicConstants.DATA);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return ret;
    }

    /**
     * 获取消息里的MessageName
     * @param messageInfo
     * @return
     */
    @Override
    public String getMessageName(MessageInfo messageInfo){
        if (messageInfo == null){
            return "";
        }

        JSONObject requestDataObject =
                JSON.parseObject(messageInfo.getMessageText());
        if (requestDataObject == null){
            return "";
        }

        JSONObject jsonObjectData = JSON.parseObject(
                requestDataObject.getString(TopicConstants.DATA));

        String ret = "";
        try {
            ret = jsonObjectData.getString(TopicConstants.PUB_NAME);
        } catch (Exception e) {
            logger.error(e);
        }

        return ret;
    }

    /**
     * 获取消息里的senderId
     * @param messageInfo
     * @return
     */
    @Override
    public String getSenderId(MessageInfo messageInfo){
        if (messageInfo == null ||
                StringUtil.isEmpty(messageInfo.getSenderId())){
            return "";
        }

        return messageInfo.getSenderId();
    }

    /**
     * 给指定机器人发送消息
     * @param robotCode
     * @param slamResponseBody
     */
    @Override
    public AjaxResult sendRobotMessage(String robotCode, SlamResponseBody slamResponseBody){
        return sendRobotMessage(robotCode, TopicConstants.AGENT_SUB, slamResponseBody);
    }

    @Override
    public AjaxResult sendRobotMessage(String robotCode, String topic, SlamResponseBody slamResponseBody) {
        if (slamResponseBody == null){
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(slamResponseBody));

        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(topic);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(messageObject.toJSONString());

        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotCode);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
        info.setMessageText(JSON.toJSONString(commonInfo));//TODO 发送资源及rostopic命令

//        String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey(
//                robotCode,true, MessageType.EXECUTOR_COMMAND.name());

        //单机器命令发送（带回执）
        try {
           return messageSendHandleService.sendCommandMessage(true, true, robotCode, info);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }
    }

    /**
     * 给指定机器人发送消息
     * @param robotCode
     * @param data
     */
    @Override
    public AjaxResult sendRobotMessage(String robotCode, String data){
        return sendRobotMessage(TopicConstants.AGENT_SUB, data);
    }

    @Override
    public AjaxResult sendRobotMessage(String robotCode, String topic, String data) {
        if (data == null){
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"数据为空");
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, data);

        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(topic);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(messageObject.toJSONString());

        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId(robotCode);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
        info.setMessageText(JSON.toJSONString(commonInfo));//TODO 发送资源及rostopic命令

//        String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey(
//                robotCode,true, MessageType.EXECUTOR_COMMAND.name());

        //单机器命令发送（带回执）
        try {
            return messageSendHandleService.sendCommandMessage(true, true, robotCode, info);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }
    }

    @Override
    public AjaxResult sendAllRobotMessage(String data) {
        return sendAllRobotMessage(TopicConstants.AGENT_SUB, data);
    }

    @Override
    public AjaxResult sendAllRobotMessage(String topic, String data) {
        if (data == null){
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }

        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, data);

        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(topic);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(messageObject.toJSONString());

        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
        info.setMessageText(JSON.toJSONString(commonInfo));//TODO 发送资源及rostopic命令

        //单机器命令发送（带回执）
        try {
            return messageSendHandleService.sendCommandMessageAndAll(false, info);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return AjaxResult.failed(AjaxResult.CODE_FAILED,"出错");
        }
    }

}
