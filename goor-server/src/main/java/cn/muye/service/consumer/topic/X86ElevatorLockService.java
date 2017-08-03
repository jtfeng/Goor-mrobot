package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by abel on 17-7-11.
 */
public interface X86ElevatorLockService {

    AjaxResult handleX86ElevatorLock(MessageInfo messageInfo);
}
