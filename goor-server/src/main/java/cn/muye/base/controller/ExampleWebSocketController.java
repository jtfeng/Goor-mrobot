package cn.muye.base.controller;


import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.i18n.service.LocaleMessageSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin
@Controller
@Slf4j
@RequestMapping("sendWebSocket")
public class ExampleWebSocketController {

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = "demo",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pageList(WhereRequest whereRequest){
        try {

            webSocketSendMessage.sendWebSocketMessage(new WSMessage.Builder().messageType(WSMessageType.NOTIFICATION)
                    .body("test")
                    .build());
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_base_controller_ExampleWebSocketController_java_FSCG"));
        } catch (Exception e) {
            log.error("发送错误", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_base_controller_ExampleWebSocketController_java_XTNBCW"));
        }
    }

}