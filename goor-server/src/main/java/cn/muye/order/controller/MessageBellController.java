package cn.muye.order.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.order.MessageBell;
import cn.mrobot.bean.order.OrderConstant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.controller.BaseController;
import cn.muye.order.bean.MessageBellVO;
import cn.muye.order.service.MessageBellService;
import cn.muye.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/10/10.
 */
@RestController
@RequestMapping(value = "messageBell")
public class MessageBellController extends BaseController{

    @Autowired
    private MessageBellService messageBellService;
    @Autowired
    private UserUtil userUtil;

    /**
     * 获取 未读消息内容
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public AjaxResult listMessageBell(WhereRequest whereRequest){
        try {
            Long stationId = userUtil.getStationId();
            MessageBell queryBell = new MessageBell();
            queryBell.setStatus(OrderConstant.MESSAGE_BELL_UNREAD);
            queryBell.setStationId(stationId);
            List<MessageBell> messageBellList = messageBellService.listQueryPageByStoreIdAndOrder(whereRequest.getPage(),OrderConstant.MESSAGE_DEFAULT_MAX_NUM,queryBell,"CREATE_TIME DESC");
            boolean hasReceive = false;
            boolean hasSend = false;
            for (MessageBell messageBell : messageBellList) {
                if(messageBell.getType() == OrderConstant.MESSAGE_BELL_RECEIVE){
                    hasReceive = true;
                }else if(messageBell.getType() == OrderConstant.MESSAGE_BELL_SEND){
                    hasReceive = true;
                }
            }

            MessageBellVO messageBellVO = new MessageBellVO();
            messageBellVO.setMessageBellList(messageBellList);
            messageBellVO.setHasReceive(hasReceive);
            messageBellVO.setHasSend(hasSend);
            return AjaxResult.success(messageBellVO, "读取信息列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("读取信息出错");
        }
    }

    /**
     * 清除未读 消息
     * @return
     */
    @RequestMapping(value = "clearMessages", method = RequestMethod.GET)
    public AjaxResult clearUnreadMessageBell(@RequestParam("clearDate") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date clearDate){
        try {
            Long stationId = userUtil.getStationId();
            messageBellService.updateByStationIdAndClearDate(stationId, clearDate);
            return AjaxResult.success("消息清除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed("清除信息出错");
        }
    }
}
