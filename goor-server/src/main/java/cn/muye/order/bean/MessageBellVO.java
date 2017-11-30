package cn.muye.order.bean;

import cn.mrobot.bean.order.ApplyOrder;
import cn.mrobot.bean.order.MessageBell;

import java.util.List;

/**
 * Created by Selim on 2017/10/10.
 */
public class MessageBellVO {

    private boolean hasReceive;

    private boolean hasSend;

    private List<MessageBell> messageBellList;

    private List<ApplyOrder> waitApplyOrders;  //为了PAD端请求接口合并

    public boolean isHasReceive() {
        return hasReceive;
    }

    public void setHasReceive(boolean hasReceive) {
        this.hasReceive = hasReceive;
    }

    public boolean isHasSend() {
        return hasSend;
    }

    public void setHasSend(boolean hasSend) {
        this.hasSend = hasSend;
    }

    public List<MessageBell> getMessageBellList() {
        return messageBellList;
    }

    public void setMessageBellList(List<MessageBell> messageBellList) {
        this.messageBellList = messageBellList;
    }

    public List<ApplyOrder> getWaitApplyOrders() {
        return waitApplyOrders;
    }

    public void setWaitApplyOrders(List<ApplyOrder> waitApplyOrders) {
        this.waitApplyOrders = waitApplyOrders;
    }
}
