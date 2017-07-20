package cn.muye.base.controller;


import cn.mrobot.bean.AjaxResult;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.websoket.WebSocketSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@CrossOrigin
@Controller
@Slf4j
@RequestMapping("sendWebSocket")
public class ExampleWebSocketController {

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @RequestMapping(value = "demo",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pageList(WhereRequest whereRequest){
        try {
            webSocketSendMessage.sendWebSocketMessage("1", "sendMessageText");
            return AjaxResult.success("发送成功");
        } catch (Exception e) {
            log.error("发送错误", e);
            return AjaxResult.failed("系统内部错误");
        }
    }

}