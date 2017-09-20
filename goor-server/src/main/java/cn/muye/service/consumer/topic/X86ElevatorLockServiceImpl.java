package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.mission.task.JsonElevatorLock;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.elevator.service.ElevatorShaftService;
import cn.muye.base.bean.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-8-3.
 */
@Service
public class X86ElevatorLockServiceImpl implements X86ElevatorLockService {

    private Logger logger = Logger.getLogger(X86ElevatorLockServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    ElevatorShaftService elevatorShaftService;

    @Override
    public AjaxResult handleX86ElevatorLock(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)){
            JsonElevatorLock jsonElevatorLock =
                    (JsonElevatorLock) JsonUtils.fromJson(data,
                            new TypeToken<JsonElevatorLock>(){}.getType());
            Boolean ret;
            logger.info("111111111111");
            if (jsonElevatorLock != null &&
                    jsonElevatorLock.getAction() != null){
                switch (jsonElevatorLock.getAction()){
                    case "lock":
                        //判断和返回加锁
                        logger.info("2222222222");
                        ret = elevatorShaftService.updateElevatorShaftLockStateWithRobotCode(
                                jsonElevatorLock.getElevator_id(),
                                ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_LOCK,
                                baseMessageService.getSenderId(messageInfo)
                        );
                        break;
                    case "unlock":
                        logger.info("3333333");
                        ret = elevatorShaftService.updateElevatorShaftLockStateWithRobotCode(
                                jsonElevatorLock.getElevator_id(),
                                ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_UNLOCK,
                                baseMessageService.getSenderId(messageInfo)
                        );
                        break;
                        default:
                            ret = false;
                            break;
                }

                if (ret){
                    jsonElevatorLock.setResult(1);
                }else{
                    jsonElevatorLock.setResult(0);
                }
                jsonElevatorLock.setSendTime(System.currentTimeMillis());

                return baseMessageService.sendRobotMessage(
                        baseMessageService.getSenderId(messageInfo),
                        TopicConstants.X86_ELEVATOR_LOCK,
                        JsonUtils.toJson(jsonElevatorLock,
                                new TypeToken<JsonElevatorLock>(){}.getType())
                );
            }

        }
        return AjaxResult.failed();
    }
}
