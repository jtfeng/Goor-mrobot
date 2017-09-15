package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by admin on 2017/9/11.
 */
public interface X86MissionAlertService {

    AjaxResult handleX86MissionAlert(MessageInfo messageInfo);
}
