package cn.muye.order.service.impl;

import cn.mrobot.bean.order.MessageBell;
import cn.mrobot.bean.order.OrderConstant;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.order.service.MessageBellService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/10/10.
 */
@Service
@Transactional
public class MessageBellServiceImpl extends BaseServiceImpl<MessageBell> implements MessageBellService{

    @Override
    public void updateByStationIdAndClearDate(Long stationId, Date clearDate) {
        //先获取所有此站关联的未读信息
        MessageBell messageBell = new MessageBell();
        messageBell.setStationId(stationId);
        messageBell.setStatus(OrderConstant.MESSAGE_BELL_UNREAD);
        List<MessageBell> unreadMessageBell = myMapper.select(messageBell);
        for (MessageBell bell : unreadMessageBell) {
            if(bell.getCreateTime().compareTo(clearDate) <= 0){
                bell.setStatus(OrderConstant.MESSAGE_BELL_READ);
                myMapper.updateByPrimaryKeySelective(bell);
            }
        }
    }
}
