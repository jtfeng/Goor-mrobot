package cn.muye.base.controller;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.base.bean.*;
import cn.muye.base.cache.CacheInfoManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@Controller
public class RabbitMQExampleController {
    private Logger logger = Logger.getLogger(RabbitMQExampleController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;

    /**
     * 调用rabbitMQ发送消息demo
     * @param request
     */
    @RequestMapping(value = "testRabbitMQ", method= RequestMethod.POST)
    @ResponseBody
    public void testRabbitMQ(HttpServletRequest request) {
        MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
        info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
//        info.setMessageType(MessageType.EXECUTOR_RESOURCE);//TODO 如果发送资源,将此行注释去掉，注释此行上面第一行

        //获取当前需要发送的的routingKey,其中"SNabc001"为机器人SN号
        String noResultRoutingKey = RabbitMqBean.getRoutingKey("SNabc001",false, info);
        String backResultRoutingKey = RabbitMqBean.getRoutingKey("SNabc001",true, info);

        //单机器命令发送（不带回执）
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultRoutingKey, info);

        //单机器命令发送（带回执）
        AjaxResult ajaxResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultRoutingKey, info);

        //全部机器命令发送
        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_COMMAND_EXCHANGE, "", info);

        //单机器资源发送（不带回执）
        rabbitTemplate.convertAndSend(TopicConstants.TOPIC_EXCHANGE, noResultRoutingKey, info);

        //单机器资源发送（带回执）
        AjaxResult ajaxResourceResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultRoutingKey, info);

        //全部机器资源发送
        rabbitTemplate.convertAndSend(TopicConstants.FANOUT_RESOURCE_EXCHANGE, "", info);

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
