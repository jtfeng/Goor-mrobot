package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.Message;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.MessageView;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.websoket.WebSocketSendMessage;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@Controller
@Slf4j
@RequestMapping("sendLog")
public class SendLogController {
    @Autowired
    private OffLineMessageService offLineMessageService;

    private boolean messageSave(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            return false;
        }
        OffLineMessage message = new OffLineMessage(messageInfo);
        offLineMessageService.save(message);//更新发送的消息
        return true;
    }

    /**
     * 分页查询发送消息
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "pageList",method = RequestMethod.POST)
    @ResponseBody
    private AjaxResult pageList(WhereRequest whereRequest){
        try {
            List<OffLineMessage> offLineMessageList = offLineMessageService.pageList(whereRequest.getPage(),whereRequest.getPageSize());
            List<MessageView> messageViewList = offLineMessageList.stream().map(offLineMessage -> new MessageView(offLineMessage)).collect(Collectors.toList());
            PageInfo<MessageView> pageList = new PageInfo<>(messageViewList);
            return AjaxResult.success(pageList,"发送日志查询成功");
        } catch (Exception e) {
            log.error("查询发送日志出现错误", e);
            return AjaxResult.failed("系统内部错误");
        }
    }

//    @RequestMapping(value = "save",method = RequestMethod.POST)
//    @ResponseBody
//    private AjaxResult save(WhereRequest whereRequest){
//        try {
//            OffLineMessage offLineMessage = new OffLineMessage();
//            offLineMessage.setUuId(UUID.randomUUID().toString().replace("-", ""));
//            offLineMessage.setSuccess(false);
//            offLineMessage.setSenderId("goor-server");
//            offLineMessageService.save(offLineMessage);
//            return AjaxResult.success("保存成功");
//        } catch (Exception e) {
//            log.error("保存错误", e);
//            return AjaxResult.failed("系统内部错误");
//        }
//    }

}
