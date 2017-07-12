package cn.muye.base.consumer.service;

import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;

/**
 * Created by abel on 17-7-11.
 */
public interface PickUpPswdVerifyService {

    void sendPickUpPswdVerify();
    void sendPickUpPswdVerify(PickUpPswdVerifyBean bean);
}
