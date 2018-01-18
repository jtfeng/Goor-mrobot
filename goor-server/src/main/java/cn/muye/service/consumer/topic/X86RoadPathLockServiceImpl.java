package cn.muye.service.consumer.topic;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.mission.task.JsonRoadPathLock;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.base.bean.MessageInfo;
import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        logger.info("handleX86RoadPathLock = "+ JsonUtils.toJson(
                messageInfo,
                new TypeToken<MessageInfo>(){}.getType()));
        String data = baseMessageService.getData(messageInfo);
        logger.info("data =" + data);
        if (!StringUtil.isEmpty(data)){
            JsonRoadPathLock jsonRoadPathLock =
                    (JsonRoadPathLock) JsonUtils.fromJson(data,
                            new TypeToken<JsonRoadPathLock>(){}.getType());
            boolean ret;
            logger.info("111111111111");
            if (jsonRoadPathLock != null && jsonRoadPathLock.getAction() != null){
                logger.info("jsonRoadPathLock = "+JSON.toJSONString(jsonRoadPathLock));
                //如果含有result字段，则不处理
                if(jsonRoadPathLock.getResult() != null){
                    logger.info("包含result字段。不处理");
                    return AjaxResult.success();
                }
                switch (jsonRoadPathLock.getAction()){
                    case "lock":
                        //判断和返回加锁
                        logger.info("2222222222  lock");
                        try {
                            Long x86_path_id = jsonRoadPathLock.getX86_path_id();
                            //TODO 原来的mission协议没有下发工控路径ID，路径锁方向无效
                            if(x86_path_id == null) {
                                ret = roadPathLockService.lock(
                                    jsonRoadPathLock.getRoadpath_id(),
                                    baseMessageService.getSenderId(messageInfo)
                                );
                            }
                            //新的mission协议增加下发工控路径ID，作为路径锁方向
                            else {
                                ret = roadPathLockService.lockDirection(
                                        jsonRoadPathLock.getRoadpath_id(),
                                        baseMessageService.getSenderId(messageInfo),
                                        x86_path_id
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.info("handleX86RoadPathLock error: " + e.getMessage());
                            return AjaxResult.failed();
                        }
                        break;
                    case "unlock":
                        logger.info("3333333  unlock");
                        try {
                            String robotCode = messageInfo.getSenderId();
                            Long lockId = jsonRoadPathLock.getRoadpath_id();
                            logger.info("锁ID为：" + lockId);
                            //如果lockId是我们定义的释放机器人资源，则执行释放资源
                            if(lockId != null && lockId.equals(Constant.RELEASE_ROBOT_LOCK_ID)) {
                                logger.info("开始执行释放" + robotCode +"机器人占用的所有路径锁、门锁任务。");
                                //执行释放机器人相关锁对象接口
                                ret = roadPathLockService.cloudReleaseRoadPathLock(robotCode);
                            }
                            //执行正常的解锁任务
                            else {
                                logger.info("开始执行解锁单个路径锁任务。");
                                ret = roadPathLockService.unlock(
                                        lockId,
                                        baseMessageService.getSenderId(messageInfo)
                                );
                            }

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
                    logger.info("加锁解锁操作成功"+ new Date());
                    jsonRoadPathLock.setResult(1);
                }else{
                    logger.info("加锁解锁操作失败"+ new Date());
                    jsonRoadPathLock.setResult(0);
                }
                jsonRoadPathLock.setSendTime(System.currentTimeMillis());

                return baseMessageService.sendRobotMessage(
                        baseMessageService.getSenderId(messageInfo),
                        TopicConstants.X86_ROADPATH_LOCK,
                        JsonUtils.toJson(jsonRoadPathLock,
                                new TypeToken<JsonRoadPathLock>(){}.getType())
                );
            }

        }
        return AjaxResult.failed();
    }
}
