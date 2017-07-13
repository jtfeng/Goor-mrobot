package cn.muye.base.consumer.service;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by abel on 17-7-11.
 */
@Service
public class PickUpPswdVerifyServiceImpl
        implements PickUpPswdVerifyService {

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void sendPickUpPswdVerify() {

        PickUpPswdVerifyBean bean =
                new PickUpPswdVerifyBean();
        bean.setRobotCode("");
        bean.setPswd("123456");
        bean.setBoxNum(1);
        bean.setUuid(UUID.randomUUID().toString().replace("-", ""));

        baseMessageService.sendCloudMessage(TopicConstants.PICK_UP_PSWD_VERIFY, bean);
    }

    @Override
    public void sendPickUpPswdVerify(PickUpPswdVerifyBean bean) {

        if (bean == null){
            bean =
                    new PickUpPswdVerifyBean();
            bean.setRobotCode("");
            bean.setPswd("123456");
            bean.setBoxNum(1);
        }
        bean.setUuid(UUID.randomUUID().toString().replace("-", ""));

        baseMessageService.sendCloudMessage(TopicConstants.PICK_UP_PSWD_VERIFY, bean);
    }
}
