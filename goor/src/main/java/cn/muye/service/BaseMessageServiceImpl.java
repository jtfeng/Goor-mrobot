package cn.muye.service;

import cn.mrobot.bean.base.TCPubBean;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${local.robot.SN}")
    private String localRobotSN;

    /**
     * 给云端发送消息
     * @param pubName
     * @param data
     */
    @Override
    public void sendCloudMessage(String pubName, Object data){
        if (data == null){
            return;
        }

        JSONObject jsonObject = new JSONObject();

        TCPubBean tcPubBean = new TCPubBean();
        tcPubBean.setTopicName(TopicConstants.AGENT_PUB);
        tcPubBean.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        tcPubBean.setData(JSON.toJSONString(data));
        tcPubBean.setPub_name(pubName);

        jsonObject.put(TopicConstants.DATA, JSON.toJSONString(tcPubBean));

        MessageInfo info = new MessageInfo();
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId(localRobotSN);
        info.setMessageType(MessageType.EXECUTOR_COMMAND);
        info.setMessageText(JSON.toJSONString(jsonObject));//TODO 发送资源及rostopic命令
        //往云端推送（无回执）
//        rabbitTemplate.convertAndSend(TopicConstants.DIRECT_COMMAND_REPORT, info);
        //往云端推送（有回执）
        try {
            msgSendAsyncTask.taskMsgSend(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
