package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.mission.task.JsonRoadPathLock;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.base.bean.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel on 17-9-7.
 */
@Service
public class X86RoadPathLockServiceImpl implements X86RoadPathLockService {

    private Logger logger = Logger.getLogger(X86RoadPathLockServiceImpl.class);

    @Autowired
    BaseMessageService baseMessageService;

    @Autowired
    RoadPathLockService roadPathLockService;

    @Override
    public AjaxResult handleX86RoadPathLock(MessageInfo messageInfo) {
        logger.info(JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        if (!StringUtil.isEmpty(data)){
            JsonRoadPathLock jsonRoadPathLock =
                    (JsonRoadPathLock) JsonUtils.fromJson(data,
                            new TypeToken<JsonRoadPathLock>(){}.getType());
            Boolean ret;
            logger.info("111111111111");
            if (jsonRoadPathLock != null &&
                    jsonRoadPathLock.getAction() != null){
                switch (jsonRoadPathLock.getAction()){
                    case "lock":
                        //判断和返回加锁
                        logger.info("2222222222");
                        try {
                            ret = roadPathLockService.lock(
                                    jsonRoadPathLock.getRoadpath_id(),
                                    baseMessageService.getSenderId(messageInfo)
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.info("handleX86RoadPathLock error: " + e.getMessage());
                            return AjaxResult.failed();
                        }
                        break;
                    case "unlock":
                        logger.info("3333333");
                        try {
                            ret = roadPathLockService.unlock(
                                    jsonRoadPathLock.getRoadpath_id(),
                                    baseMessageService.getSenderId(messageInfo)
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.info("handleX86RoadPathLock error: " + e.getMessage());
                            return AjaxResult.failed();
                        }
                        break;
                    default:
                        ret = false;
                        break;
                }

                if (ret){
                    jsonRoadPathLock.setResult(1);
                }else{
                    jsonRoadPathLock.setResult(0);
                }
                jsonRoadPathLock.setSendTime(System.currentTimeMillis());

                return baseMessageService.sendRobotMessage(
                        baseMessageService.getSenderId(messageInfo),
                        TopicConstants.DIRECT_X86_ROADPATH_LOCK,
                        JsonUtils.toJson(jsonRoadPathLock,
                                new TypeToken<JsonRoadPathLock>(){}.getType())
                );
            }

        }
        return AjaxResult.failed();
    }
}
