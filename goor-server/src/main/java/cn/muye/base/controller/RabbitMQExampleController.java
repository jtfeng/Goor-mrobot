package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.*;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.imp.BlockingCell;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
//import org.redisson.RedissonBlockingQueue;
//import org.redisson.api.RMap;
//import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@Controller
@Slf4j
public class RabbitMQExampleController {
    private Logger logger = Logger.getLogger(RabbitMQExampleController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    RedissonClient redissonClient;

    @Autowired
    private OffLineMessageService offLineMessageService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;


    /**
     * 调用统一发送消息例子
     * @param request
     * @return
     */
    @RequestMapping(value = "testSendMessage",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult testSendMessage(HttpServletRequest request){
        try {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
            messageInfo.setReceiverId("enva_test0101");
            messageInfo.setSenderId("goor-server");
            messageInfo.setMessageType(MessageType.EXECUTOR_COMMAND);
            messageInfo.setMessageText("sssssssssssss");
            AjaxResult result = messageSendHandleService.sendCommandMessage(true,true,"enva_test0101",messageInfo);
            if(!result.isSuccess()){
                return AjaxResult.failed();
            }
            long startTime = System.currentTimeMillis();
            log.info("start time"+ startTime);
            for (int i=0; i<500; i++) {
                Thread.sleep(1000);
                MessageInfo messageInfo1 = CacheInfoManager.getUUIDCache(messageInfo.getUuId());
                if(messageInfo1.isSuccess()){
                    messageInfo.setSuccess(true);
                    break;
                }
            }
            long endTime = System.currentTimeMillis();
            log.info("end time"+ (endTime - startTime));
            if (messageInfo.isSuccess()) {
                return AjaxResult.success();
            }else{
                return AjaxResult.failed();
            }
        } catch (Exception e) {
            log.error("发送错误", e);
            return AjaxResult.failed("系统内部错误");
        }
    }


    /**
     * 调用rabbitMQ发送消息demo
     * @param request
     */
    @RequestMapping(value = "testRabbitMQ", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult testRabbitMQ(HttpServletRequest request) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setLocalFileName("robotFileName");
        commonInfo.setLocalPath("robotFilePath");
        commonInfo.setMD5("sendFileMD5");
        commonInfo.setRemoteFileUrl("remoteServerFileDownloadUrl");
        commonInfo.setTopicName("rosTopicName");
        commonInfo.setTopicType("rosTopicType");
        commonInfo.setPublishMessage("sendToRosJsonMessage");
        //如：
        commonInfo.setPublishMessage(TopicConstants.GET_CURRENT_MAP_PUB_MESSAGE);//发送到ros的json数据

        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setUuId(UUID.randomUUID().toString().replace("-", ""));
        info.setSendTime(new Date());
        info.setSenderId("goor-server");
        info.setReceiverId("SNabc0010");
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
//        info.setMessageType(MessageType.EXECUTOR_RESOURCE);//TODO 如果发送资源,将此行注释去掉，注释此行上面第一行
//        info.setMessageType(MessageType.EXECUTOR_LOG);//TODO 针对 x86 agent 业务逻辑,不接收发送到ros的信息，如：发送命令要求上传log等
        info.setMessageText(JSON.toJSONString(commonInfo));//TODO 发送资源及rostopic命令

        //获取当前需要发送的的routingKey,其中"SNabc001"为机器人SN号
        String noResultCommandRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_COMMAND.name());
        String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",true, MessageType.EXECUTOR_COMMAND.name());

        String noResultResourceRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_RESOURCE.name());
        String backResultResourceRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",true, MessageType.EXECUTOR_RESOURCE.name());

        String noResultClientRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",false, MessageType.EXECUTOR_CLIENT.name());
        String backResultClientRoutingKey = RabbitMqBean.getRoutingKey("SNabc0010",true, MessageType.EXECUTOR_CLIENT.name());

        //单机器命令发送（不带回执）
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultCommandRoutingKey, info);

        //单机器命令发送（带回执）
        AjaxResult ajaxCommandResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultCommandRoutingKey, info);

        //全部机器命令发送
        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_COMMAND_EXCHANGE, "", info);

        //单机器资源发送（不带回执）
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultResourceRoutingKey, info);

        //单机器资源发送（带回执）
        AjaxResult ajaxResourceResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultResourceRoutingKey, info);

        //全部机器资源发送
        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_RESOURCE_EXCHANGE, "", info);

        //单机器发送，仅供x86 agent 处理业务逻辑，不发ros消息（不带回执）
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultClientRoutingKey, info);

        //单机器发送，仅供x86 agent 处理业务逻辑，不发ros消息（带回执）
        AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, info);

        //全部机器x86 agent发送,仅供x86 agent 处理业务逻辑，不发ros消息
        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_CLIENT_EXCHANGE, "", info);

        try {
            this.messageSave(info);
        } catch (Exception e) {
            log.error("save message error", e);
        }

        //从缓存中获取发送次消息是否收到ros返回消息
        MessageInfo messageInfo = CacheInfoManager.getUUIDCache(info.getUuId());//获得ros返回的当前uuid值是否成功，
        //messageInfo为null为不成功， messageInfo.isSuccess()为false也是不成功
        messageInfo.getMessageStatusType().getName();//获取成功失败的提示语
        messageInfo.isSuccess();//是否成功 true和false

        return ajaxCommandResult;
//        return ajaxResourceResult;
//        return ajaxClientResult;

    }

    private boolean messageSave(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            return false;
        }
        OffLineMessage message = new OffLineMessage(messageInfo);
        offLineMessageService.save(message);//更新发送的消息
        return true;
    }

    @RequestMapping(value = "getPosition", method= RequestMethod.GET)
    @ResponseBody
    public AjaxResult getPosition(HttpServletRequest request,@RequestParam("num")String num) {
        try {
            MessageInfo info = CacheInfoManager.getMessageCache(num);
            if(null != info){
                return AjaxResult.success(info.getMessageText(), info.getSendTime().getTime()+"");
            }
        } catch (Exception e) {
            logger.error("getPosition exception",e);
        }
        return AjaxResult.failed();
    }

//    @RequestMapping(value = "testRedisson", method= RequestMethod.POST)
//    @ResponseBody
//    public AjaxResult testRedisson(HttpServletRequest request,@RequestParam("num")String num) {
//        try {
//            RMap<Integer, String> users = redissonClient.<Integer, String>getMap("users");
//            users.put(1, "linux_china");
//            System.out.println(users.get(1));
//            users.remove(1);
//
////            RMap<String, MessageInfo> messageInfoRMap = redissonClient.getMap("messageInfo");
////            MessageInfo messageInfo = new MessageInfo();
////            messageInfo.setMessageText("testEnva");
////            messageInfo.setSenderId("ssssssssssssssssss");
////            messageInfo.setSendTime(new Date());
////            messageInfoRMap.put("testenva",messageInfo);
////            System.out.println(messageInfoRMap.get("testenva"));
////            messageInfoRMap.remove("testenva");
//
//            RMap<String, List<MessageInfo>> messageInfoRMapList = redissonClient.getMap("messageInfoList");
//            MessageInfo messageInfo1 = new MessageInfo();
//            messageInfo1.setMessageText("testEnva");
//            messageInfo1.setSenderId("ssssssssssssssssss");
//            messageInfo1.setSendTime(new Date());
//
//            MessageInfo messageInfo2 = new MessageInfo();
//            messageInfo2.setMessageText("testEnva1");
//            messageInfo2.setSenderId("ssssssssssssssssss1");
//            messageInfo2.setSendTime(new Date());
//
//            List<MessageInfo> messageInfos  = new ArrayList<>();
//            messageInfos.add(messageInfo1);
//            messageInfos.add(messageInfo2);
//            messageInfoRMapList.put("testenva",messageInfos);
//            System.out.println(messageInfoRMapList.get("testenva"));
//            messageInfoRMapList.remove("testenva");
//
//        } catch (Exception e) {
//            logger.error("getPosition exception",e);
//        }
//        return AjaxResult.failed();
//    }


//    //http://localhost:8080/ws
//    @MessageMapping("/welcome")//浏览器发送请求通过@messageMapping 映射/welcome 这个地址。
//    @SendTo("/topic/getResponse")//服务器端有消息时,会订阅@SendTo 中的路径的浏览器发送消息。
//    public Response say(Message message) throws Exception {
//        Thread.sleep(1000);
//        return new Response("Welcome, " + message.getName() + "!");
//    }
//
//    @MessageMapping("/chat")//在springmvc 中可以直接获得principal,principal 中包含当前用户的信息
//    public void handleChat(Principal principal, Message message) {
////      public void handleChat(Message message) {
//
//        /**
//         * 此处是一段硬编码。如果发送人是wyf 则发送给 wisely 如果发送人是wisely 就发送给 wyf。
//         * 通过当前用户,然后查找消息,如果查找到未读消息,则发送给当前用户。
//         */
////        if (principal.getName().equals("admin")) {
////            //通过convertAndSendToUser 向用户发送信息,
////            // 第一个参数是接收消息的用户,第二个参数是浏览器订阅的地址,第三个参数是消息本身
////
////            messagingTemplate.convertAndSendToUser("abel",
////                    "/queue/notifications", principal.getName() + "-send:"
////                            + message.getName());
////            messagingTemplate.convertAndSendToUser("admin",
////                    "/queue/notifications", principal.getName() + "-send:"
////                            + message.getName());
////        } else {
////            messagingTemplate.convertAndSendToUser("admin",
////                    "/queue/notifications", principal.getName() + "-send:"
////                            + message.getName());
////            messagingTemplate.convertAndSendToUser("abel",
////                    "/queue/notifications", principal.getName() + "-send:"
////                            + message.getName());
////        }
//        //通过convertAndSendToUser 向用户发送信息,
//        // 第一个参数是接收消息的用户,第二个参数是浏览器订阅的地址,第三个参数是消息本身
//
//        messagingTemplate.convertAndSendToUser("abel",
//                "/queue/notifications", "abel" + "-send:"
//                        + message.getName());
//        messagingTemplate.convertAndSendToUser("admin",
//                "/queue/notifications", "admin" + "-send:"
//                        + message.getName());
//    }

}
