package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.mission.task.JsonElevatorLock;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
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
            if (jsonElevatorLock != null &&
                    jsonElevatorLock.getAction() != null){
                switch (jsonElevatorLock.getAction()){
                    case "lock":
                        //判断和返回加锁

                        break;
                    case "unlock":

                        break;
                        default:
                            break;
                }
            }

        }
        return null;
    }
}
