package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.alert.LogAlert;
import cn.mrobot.bean.slam.SlamBody;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.service.FileUpladService;
import cn.muye.publisher.AppSubService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExampleController {
    private Logger logger = Logger.getLogger(ExampleController.class);

    @Autowired
    private Ros ros;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FileUpladService fileUpladService;

    @Autowired
    private AppSubService appSubService;

    @RequestMapping(value = "test1", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult test1(@RequestParam("aa") String aa) {
//        logger.info("sssssssssssssssssss======" + messageSendService);
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName("/enva_test");
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setLocalFileName("test.apk");
        commonInfo.setLocalPath("E:/TEMP/TEST/aaa/ccc/test");
        commonInfo.setRemoteFileUrl("http://myee7.com/push_test/105/app/Gaea/Gaea_1.1.0_11069_20160816.apk");
        commonInfo.setMD5("e3d9ef05786e10c1fdd4e55633c12c99");
        commonInfo.setPublishMessage(JSON.toJSONString(commonInfo));
        String text = JSON.toJSONString(commonInfo);
        byte[] b = text.getBytes();
//        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_RESOURCE, text, b);
//        info.setMessageStatusType(MessageStatusType.FILE_NOT_DOWNLOADED);
//        info.setReceiptWebSocket(true);
//        info.setWebSocketId("user-9");
//        info.setMessageKind(1);

//        messageSendService.sendMessage("cookyPlus1301_test1", info);
//        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test2", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult test2(@RequestParam("aa") String aa) {
//        logger.info("sssssssssssssssssss======" + messageSendService);
//        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
//        byte[] b = text.getBytes();
//        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, text, b);
//        info.setMessageStatusType(MessageStatusType.FILE_NOT_DOWNLOADED);
//        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test3", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult test3(@RequestParam("aa") String aa) {
//        logger.info("sssssssssssssssssss======" + messageSendService);
//        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
//        byte[] b = text.getBytes();
//        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_RESOURCE, text, b);
//        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }


    /**
     * 模拟发送导航的topic
     *
     * @param aa
     * @return
     */
    @RequestMapping(value = "test4", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult test4(@RequestParam("aa") String aa) {
//		logPublishService.publishMessage();

//        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
//        byte[] b = text.getBytes();
//        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
////        messageSendService.messageSend("cookyPlus1301chay", info);
//        //模拟像云端发送一条消息
//        messageSendService.messageSend("goor-server-chay", info);

//        //TODO 发布通用命令类型，topic名称由云端定义，发布消息也由云端定义
        /*Topic echo = new Topic(ros, "/app_sub", "std_msgs/String");
        Message toSend = new Message("{\"data\": \"hello, world,appSub!"+ new Date()+aa +"\"}");
        echo.publish(toSend);*/

        //        //TODO 模拟应用发布查询云端站数据接口
        Topic echo = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_SUB);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TopicConstants.PUB_NAME, "station_list_get");
        jsonObject.put(TopicConstants.DATA, "{\"robot_code\": \"cookyPlus1301chay\"}");
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
        Message toSend = new Message(JSON.toJSONString(messageObject));
//        Message toSend = new Message("{\"data\":{\"pub_name\": \"station_list_get\",\"data\": {\"robot_code\": \"cookyPlus1301chay\"} } }");
//        Message toSend = new Message("{\"data\": \"hello, world,appSub!"+ new Date()+aa +"\"}");
        echo.publish(toSend);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test5", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult test5(@RequestParam("aa") String aa) {
//        logger.info("sssssssssssssssssss======" + messageSendService);
//        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
//        byte[] b = text.getBytes();
//        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
//        messageSendService.sendMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    /**
     * 向机器人发送指令
     * 载入地图和场景的导航点
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/map/upload", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult mapUpload(@RequestParam("uuid") String uuid) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TopicConstants.SUB_NAME, TopicConstants.AGENT_LOCAL_MAP_UPLOAD);
            jsonObject.put(TopicConstants.UUID, uuid);
            logger.info("发送地图上传topic");
            appSubService.sendTopic(TopicConstants.AGENT_SUB, TopicConstants.TOPIC_TYPE_STRING, jsonObject);
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed();
        }
    }

    /**
     * 模拟应用发送校验员工工号
     *
     * @return
     */
    @RequestMapping(value = "testVerifyEmpNo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult test6(@RequestParam("missionItemId") Long missionItemId, @RequestParam("empNo") String empNo) {
        //        //TODO 模拟应用发布查询云端站数据接口
        Topic echo = TopicHandleInfo.getTopic(ros, TopicConstants.AGENT_SUB);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TopicConstants.SUB_NAME, "verify_emplyee_number");
        jsonObject.put(TopicConstants.DATA, "{\"missionItemId\": " + missionItemId +", \"empNo\": "+ empNo +"}");
        jsonObject.put(TopicConstants.UUID, "fldslfjlsajflsdjfljdslkfjlkdsjfl");
        jsonObject.put("msg", "");
        jsonObject.put("error_code", "");
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
        Message toSend = new Message(JSON.toJSONString(messageObject));
//        Message toSend = new Message("{\"data\":{\"pub_name\": \"station_list_get\",\"data\": {\"robot_code\": \"cookyPlus1301chay\"} } }");
//        Message toSend = new Message("{\"data\": \"hello, world,appSub!"+ new Date()+aa +"\"}");
        echo.publish(toSend);
        return AjaxResult.success();
    }

    /**
     * 模拟应用发送报警事件
     *
     * @return
     */
    @RequestMapping(value = "testAlert", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult testAlert(@RequestParam(value = "robotCode",required = false) String robotCode, @RequestParam("uuId") String uuId, @RequestParam("sendTime") Long sendTime, @RequestParam("alertCode") Integer alertCode, @RequestParam("msg") String msg, @RequestParam("missionItemId") Long missionItemId) {
        //        //TODO 模拟应用发布查询云端站数据接口
        Topic echo = TopicHandleInfo.getTopic(ros, TopicConstants.X86_MISSION_ALERT);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TopicConstants.SUB_NAME, "mission_alert");
        jsonObject.put(TopicConstants.DATA, "   {\"alert_time\":"+ sendTime + ", \"alert_code\":"+ alertCode+ ", \"msg\":\""+ msg + "\", \"mission_item_id\":"+ missionItemId +"}" );
        jsonObject.put(TopicConstants.UUID, uuId);
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
        logger.info("测试alert的Data:" + jsonObject.get(TopicConstants.DATA));
        Message toSend = new Message(JSON.toJSONString(messageObject));
//        Message toSend = new Message("{\"data\":{\"pub_name\": \"station_list_get\",\"data\": {\"robot_code\": \"cookyPlus1301chay\"} } }");
//        Message toSend = new Message("{\"data\": \"hello, world,appSub!"+ new Date()+aa +"\"}");
        echo.publish(toSend);
        return AjaxResult.success();
    }

    /**
     * 发送获取工控固定路径的topic
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/fixpath", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult fixpath(@RequestParam("uuid") String uuid) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TopicConstants.PUB_NAME, "fixpath_query");

            JSONObject dataObject = new JSONObject();
            dataObject.put(TopicConstants.SCENE_NAME, "scene2");
            jsonObject.put(TopicConstants.DATA, dataObject);
            logger.info("发送获取工控固定路径的topic");
            appSubService.sendTopic(TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING, jsonObject);
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed();
        }
    }

    /**
     * 模拟mission发送加锁请求
     *
     * @return
     */
    @RequestMapping(value = "test7", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult test7(@RequestParam("command") String command) {
        //
        Topic echo = TopicHandleInfo.getTopic(ros, TopicConstants.X86_ELEVATOR_LOCK);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", command);//lock，unlock
        jsonObject.put("elevator_id", 1);
        jsonObject.put(TopicConstants.UUID, "fldslfjlsajflsdjfljdslkfjlkdsjfl");
        jsonObject.put("sendTime", 1501748933017L);
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));
        Message toSend = new Message(JSON.toJSONString(messageObject));
//        Message toSend = new Message("{\"data\":{\"pub_name\": \"station_list_get\",\"data\": {\"robot_code\": \"cookyPlus1301chay\"} } }");
//        Message toSend = new Message("{\"data\": \"hello, world,appSub!"+ new Date()+aa +"\"}");
        logger.info("测试alert的Data:" + messageObject.get(TopicConstants.DATA));
        echo.publish(toSend);
        return AjaxResult.success();
    }
}
