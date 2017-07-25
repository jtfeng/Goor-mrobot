package cn.muye.base.consumer;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubBean;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.state.StateCollectorAutoCharge;
import cn.mrobot.bean.state.StateCollectorResponse;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.aes.AES;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.state.StateCollectorService;
import cn.muye.service.consumer.topic.PickUpPswdVerifyService;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.log.charge.service.ChargeInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);

//    @Autowired
//    private ReceiveMessageService receiveMessageService;

    @Autowired
    private OffLineMessageService offLineMessageService;

    @Autowired
    PickUpPswdVerifyService pickUpPswdVerifyService;

    @Autowired
    private ChargeInfoService chargeInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotService robotService;

    @Autowired
    private GoodsTypeService goodsTypeService;

    @Autowired
    private StateCollectorService stateCollectorService;

    /**
     * 透传ros发布的topic：agent_pub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_PUB)
    public void directAgentPub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
//                else if(){
//
//                }
                if (!StringUtil.isEmpty(messageName)) {
                    switch (messageName) {
                        case TopicConstants.PICK_UP_PSWD_VERIFY:
                            /* 17.7.5 Add By Abel. 取货密码验证。根据机器人编号，密码和货柜编号*/
                            pickUpPswdVerifyService.handlePickUpPswdVerify(messageInfo);
                            break;
                        case TopicConstants.FETCH_DETAIL_GOODSTYPE:
                            //Server Answer the detail GoodsType messages.
                            goodsTypeService.syncGoodsTypeMessage(messageInfo, messageName);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAgentPub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：agent_sub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_SUB)
    public void directAgentSub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
//                else if(){
//
//                }
            }
        } catch (Exception e) {
            logger.error("consumer directAgentSub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：app_pub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_APP_PUB)
    public void directAppPub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    if (TopicConstants.DEBUG)
                    logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }

//                else if(){
//
//                }
            }
        } catch (Exception e) {
            logger.error("consumer directAppPub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：app_sub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_APP_SUB)
    public void directAppSub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                String messageData = jsonObjectData.getString(TopicConstants.DATA);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    //将当前加载的地图信息存入缓存
                    CacheInfoManager.setMapCurrentCache(messageInfo);
                } else if (!StringUtils.isEmpty(messageName) && messageName.equals(TopicConstants.CHARGING_STATUS_INQUIRY)) {
                    //保存电量信息
                    String deviceId = messageInfo.getSenderId();
                    ChargeInfo chargeInfo = JSON.parseObject(messageData, ChargeInfo.class);
                    chargeInfo.setDeviceId(deviceId);
                    chargeInfo.setCreateTime(messageInfo.getSendTime());
                    chargeInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                    StateCollectorAutoCharge autoCharge = CacheInfoManager.getAutoChargeCache(deviceId);
                    if(null != autoCharge){
                        chargeInfo.setAutoCharging(autoCharge.getPluginStatus());
                    }
                    CacheInfoManager.setRobotChargeInfoCache(deviceId, chargeInfo);
                    chargeInfoService.save(chargeInfo);
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAppSub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：state_collector
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_STATE_COLLECTOR)
    public void directStateCollector(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                StateCollectorResponse stateCollectorResponse = JSON.parseObject(data, StateCollectorResponse.class);
                stateCollectorResponse.setTime(messageInfo.getSendTime());
                stateCollectorResponse.setSenderId(messageInfo.getSenderId());
                stateCollectorService.handleStateCollector(stateCollectorResponse);
            }
        } catch (Exception e) {
            logger.error("consumer directAgentPub exception", e);
        }
    }


    /**
     * 透传ros发布的topic：current_pose
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_CURRENT_POSE)
    public void directCurrentPose(@Payload MessageInfo messageInfo) {
        try {
            CacheInfoManager.setMessageCache(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCurrentPose exception", e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT)
    public void directCommandReport(@Payload MessageInfo messageInfo) {
        try {
            if (MessageType.TIME_SYNCHRONIZED.equals(messageInfo.getMessageType())) {
                clientTimeSynchronized(messageInfo);
                return;
            }

            messageSaveOrUpdate(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCommandReport exception", e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE)
    public AjaxResult directCommandReportAndReceive(@Payload MessageInfo messageInfo) {
        try {
            messageSaveOrUpdate(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCommandReport exception", e);
        }
        return AjaxResult.success();
    }


    private boolean messageSaveOrUpdate(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            return false;
        }
        if (MessageType.EXECUTOR_LOG.equals(messageInfo.getMessageType())) {
            //TODO 此处可以添加日志及状态上报存储


        } else if (MessageType.REPLY.equals(messageInfo.getMessageType())) {
            OffLineMessage message = new OffLineMessage();
            message.setMessageStatusType(messageInfo.getMessageStatusType().getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setUuId(messageInfo.getUuId());//更新的主键
            message.setUpdateTime(new Date());//更新时间
            offLineMessageService.update(message);//更新发送的消息
        }
        return true;
    }

    /**
     * x86 请求与云端时间同步
     *
     * @param messageInfo
     */
    private void clientTimeSynchronized(MessageInfo messageInfo) {
        //监听上行X86时间同步消息，获得X86时间，与服务器时间比较，如果差值大于10s（默认），进行时间同步，否则不处理
        Date date = new Date();
        long upTime = messageInfo.getSendTime().getTime();
        long downTime = date.getTime();
        if ((downTime - upTime) < 10) {
            return;
        } else {
            //发送带响应同步消息，获得10次时间平均延迟
            int sum = 0;

            try {
                //todo:rabbitmq响应超时问题，
                MessageInfo sendMessageInfo = new MessageInfo();
                for (int i = 0; i < 10; i++) {
                    long startTime = System.currentTimeMillis();
                    sendMessageInfo.setMessageType(MessageType.TIME_SYNCHRONIZED);
                    AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);//后期带上机器编码进行区分
                    logger.info("the delay time :" + result.toString());
                    long endTime = System.currentTimeMillis();
                    sum += (endTime - startTime);
                }

                long avg = sum / 20; //只需要考虑单向时间误差

                //给指定X86发送时间同步消息
                sendMessageInfo.setMessageText(String.valueOf(new Date().getTime() + avg));
                AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);
                logger.info("the time synchronized result :" + result);
            } catch (Exception e) {
                logger.error("time synchronized failure : " + e.toString());
            }
        }
        return;
    }

    /**
     * 接收goor发布的topic：direct.command_robot_info
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_ROBOT_INFO)
    public void subscribeRobotInfo(@Payload MessageInfo messageInfo) {
        try {
            logger.info("subscribeRobotInfo start");
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                String robotStr = AES.decryptFromBase64(messageInfo.getMessageText(), Constant.AES_KEY);
                Robot robotNew = JSON.parseObject(robotStr, Robot.class);
                CacheInfoManager.setRobotAutoRegisterTimeCache(robotNew.getCode(), messageInfo.getSendTime().getTime());
                robotService.autoRegister(robotNew);
            }
        } catch (Exception e) {
            logger.error("consumer robotInfo exception", e);
        }
    }

}
