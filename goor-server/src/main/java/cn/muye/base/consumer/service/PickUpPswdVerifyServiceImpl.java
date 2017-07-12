package cn.muye.base.consumer.service;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.message.data.PickUpPswdVerifyBean;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-7-11.
 */
@Service
public class PickUpPswdVerifyServiceImpl
        implements PickUpPswdVerifyService {

    @Autowired
    private RobotPasswordService robotPasswordService;

    @Autowired
    private RobotService robotService;

    @Autowired
    BaseMessageService baseMessageService;

    @Override
    public void handlePickUpPswdVerify(MessageInfo messageInfo) {
        /* 17.7.5 Add By Abel. 取货密码验证。根据机器人编号，密码和货柜编号*/
        //转换对象
        PickUpPswdVerifyBean bean =
                (PickUpPswdVerifyBean) JsonUtils.fromJson(baseMessageService.getPubData(messageInfo),
                        new TypeToken<PickUpPswdVerifyBean>(){}.getType());

        SlamResponseBody slamResponseBody = new SlamResponseBody();
        slamResponseBody.setSubName(TopicConstants.PICK_UP_PSWD_VERIFY);

        if (bean == null ||
//                StringUtil.isEmpty(bean.getRobotCode()) ||
                StringUtil.isEmpty(bean.getPswd())){
            // TODO: 17-7-5 检索参数不合法，返回错误
            if (!StringUtil.isEmpty(bean.getRobotCode())){
                bean.setRetCode(PickUpPswdVerifyBean.RET_CODE_ERROR_PARA);
                slamResponseBody.setData(JsonUtils.toJson(bean,
                        new TypeToken<PickUpPswdVerifyBean>(){}.getType()));
                baseMessageService.sendRobotMessage(bean.getRobotCode(), slamResponseBody);
            }
            return;
        }

        bean.setRobotCode(baseMessageService.getSenderId(messageInfo));

        //判断参数是否合法
        if (bean.getBoxNum() == null ||
                bean.getBoxNum() <= 0){
            bean.setBoxNum(1);
        }
        //进行业务逻辑
        //首先由robot code 查询机器人的记录
        Robot robot = robotService.getByCode(bean.getRobotCode());
        if (robot == null ||
                robot.getId() == null){
            // TODO: 17-7-5 没有查到机器人记录，返回错误
            if (!StringUtil.isEmpty(bean.getRobotCode())){
                bean.setRetCode(PickUpPswdVerifyBean.RET_CODE_ERROR_ROBOT);
                slamResponseBody.setData(JsonUtils.toJson(bean,
                        new TypeToken<PickUpPswdVerifyBean>(){}.getType()));
                baseMessageService.sendRobotMessage(bean.getRobotCode(), slamResponseBody);
            }
            return;
        }

        //查询对应密码记录是否存在
        RobotPassword robotPassword = new RobotPassword();
        robotPassword.setBoxNum(bean.getBoxNum());
        robotPassword.setPassword(bean.getPswd());
        robotPassword.setRobotId(robot.getId());
        robotPassword = robotPasswordService.findByRobotIdAndBoxNumAndPswd(robotPassword);
        if (robotPassword == null){
            //没有查询到记录，验证失败
            bean.setRetCode(PickUpPswdVerifyBean.RET_CODE_ERROR_NO_RECORD);
        }else{
            //查询到记录了，验证成功
            bean.setRetCode(PickUpPswdVerifyBean.RET_CODE_SUCCESS);
        }

        if (!StringUtil.isEmpty(bean.getRobotCode())){
            slamResponseBody.setData(JsonUtils.toJson(bean,
                    new TypeToken<PickUpPswdVerifyBean>(){}.getType()));
            baseMessageService.sendRobotMessage(bean.getRobotCode(), slamResponseBody);
        }
    }
}
