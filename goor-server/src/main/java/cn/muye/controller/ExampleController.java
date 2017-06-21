package cn.muye.controller;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.DeviceType;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.muye.bean.*;
import cn.muye.service.MessageSendService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class ExampleController {
    private Logger logger = Logger.getLogger(ExampleController.class);

    @Autowired
    private MessageSendService messageSendService;

    @RequestMapping(value = "test1", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult test1(@RequestParam("aa")String aa) {
        logger.info("sssssssssssssssssss======" + messageSendService);
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
        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_RESOURCE, text, b);
        info.setMessageStatusType(MessageStatusType.FILE_NOT_DOWNLOADED);
        info.setReceiptWebSocket(true);
        info.setWebSocketId("user-9");
        info.setSendDeviceType(DeviceType.GOOR_SERVER);
        info.setReceiverDeviceType(DeviceType.GOOR);
        info.setMessageKind(0);
        info.setMessageType(MessageType.EXECUTOR_UPGRADE);
        info.setMessageStatusType(MessageStatusType.INIT);
        info.setSendTime(new Date());
        info.setUpdateTime(new Date());
        info.setSendCount(0);
//        info.setMessageKind(1);

        messageSendService.sendMessage("goor-server", info);
//        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test2", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult test2(@RequestParam("aa")String aa) {
        logger.info("sssssssssssssssssss======" + messageSendService);
        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
        byte[] b = text.getBytes();
        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_LOG, text, b);
        info.setMessageStatusType(MessageStatusType.FILE_NOT_DOWNLOADED);
        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test3", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult test3(@RequestParam("aa")String aa) {
        logger.info("sssssssssssssssssss======" + messageSendService);
        String text = JSON.toJSONString(new MessageInfo(MessageType.REPLY, null, null));
        byte[] b = text.getBytes();
        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_RESOURCE, text, b);
        messageSendService.sendNoStatusMessage("cookyPlus1301_test1", info);
        return AjaxResult.success();
    }

    @RequestMapping(value = "test4", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult test4(@RequestParam("aa")String aa) {
        return AjaxResult.success(aa);
    }

    @RequestMapping(value = "testSendStationInfo", method= RequestMethod.POST)
    @ResponseBody
    public AjaxResult abcd(@RequestParam("aa")String aa) {
        logger.info("sssssssssssssssssss======" + messageSendService);

        //TODO 现在是假数据，到时候得应用发送机器主板编号，从数据库查该机器人可见的站点信息，返回给应用
        //TODO X86 agent告知应用机器人调度状态信息接口。——通过app_sub发
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TopicConstants.SUB_NAME, TopicConstants.STATUS_DISPATCH);
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(jsonObject));


        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.APP_SUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        commonInfo.setPublishMessage(messageObject.toJSONString());
        String text = JSON.toJSONString(commonInfo);
        byte[] b = text.getBytes();
        //用EXECUTOR_COMMAND就会发topic
        MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
        messageSendService.sendNoStatusMessage("cookyPlus1301chay", info);

        return AjaxResult.success();
    }

}
