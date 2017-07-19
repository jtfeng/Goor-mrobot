package cn.muye.service.consumer.topic;

import cn.muye.base.bean.MessageInfo;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by abel on 17-7-11.
 */
public interface PickUpPswdVerifyService {

    void handlePickUpPswdVerify(MessageInfo messageInfo);
}
