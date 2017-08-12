package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.message.data.PickUpPswdUpdateBean;

import java.util.List;

/**
 * Created by abel on 17-7-11.
 */
public interface PickUpPswdUpdateService {

    AjaxResult sendPickUpPswdUpdate(
            String robotCode,
            List<RobotPassword> list);
}
