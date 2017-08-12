package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.message.data.PickUpPswdUpdateBean;
import cn.mrobot.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by abel on 17-8-7.
 */
@Service
public class PickUpPswdUpdateServiceImpl
        implements PickUpPswdUpdateService {

    private Logger logger = Logger
            .getLogger(PickUpPswdUpdateServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public AjaxResult sendPickUpPswdUpdate(
            String robotCode,
            List<RobotPassword> list) {

        PickUpPswdUpdateBean bean = new PickUpPswdUpdateBean();
        bean.setList(list);
        bean.setUuid(UUID.randomUUID().toString().replace("-", ""));

        return baseMessageService.sendRobotMessage(
                robotCode,
                TopicConstants.PICK_UP_PSWD_UPDATE,
                JsonUtils.toJson(list,
                        new TypeToken<PickUpPswdUpdateBean>(){}.getType())
        );
    }
}
