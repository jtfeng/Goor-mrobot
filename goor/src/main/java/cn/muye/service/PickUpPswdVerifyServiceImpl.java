package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by abel on 17-7-11.
 */
@Service
public class PickUpPswdVerifyServiceImpl
        implements PickUpPswdVerifyService {

    private Logger logger = Logger.getLogger(PickUpPswdVerifyServiceImpl.class);

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

    @Override
    public AjaxResult handlePickUpPswdVerify(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)) {
            PickUpPswdVerifyBean pickUpPswdVerifyBean =
                    (PickUpPswdVerifyBean) JsonUtils.fromJson(data,
                            new TypeToken<PickUpPswdVerifyBean>() {
                            }.getType());
            if (pickUpPswdVerifyBean != null &&
                    !StringUtil.isEmpty(pickUpPswdVerifyBean.getPswd()) &&
                    pickUpPswdVerifyBean.getBoxNum() != null){
                //验证密码，如果密码相符，则返回OK
                if (true){
                    return AjaxResult.success();
                }else{
                    return AjaxResult.failed();
                }

            }else {
                return AjaxResult.failed();
            }
        }
        return AjaxResult.failed();
    }
}
