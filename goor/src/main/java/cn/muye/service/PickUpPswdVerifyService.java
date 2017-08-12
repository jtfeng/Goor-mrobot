package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import cn.muye.base.bean.MessageInfo;

/**
 * Created by abel on 17-7-11.
 */
public interface PickUpPswdVerifyService {

    void sendPickUpPswdVerify();
    void sendPickUpPswdVerify(PickUpPswdVerifyBean bean);
    AjaxResult handlePickUpPswdVerify(MessageInfo messageInfo);
}
