package cn.muye.area.pose.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.FileValidCreateUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.pose.service.CurrentPoseService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.mapper.SceneMapper;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jelynn on 2017/8/2.
 */
@Service
public class CurrentPoseServiceImpl implements CurrentPoseService {

    private static Logger LOGGER = LoggerFactory.getLogger(CurrentPoseServiceImpl.class);

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private RobotService robotService;

    @Override
    public void sendCurrentPose() {
        Map<String, List<String>> sceneRobotCodeList = CacheInfoManager.getSceneRobotListCache();
        if (sceneRobotCodeList == null || sceneRobotCodeList.size() <= 0)
            return;
        Iterator iterator = sceneRobotCodeList.entrySet().iterator();
        while (iterator.hasNext()) {
            JSONArray jsonArray = new JSONArray();
            Map.Entry entry = (Map.Entry) iterator.next();
            String sceneName = (String) entry.getKey();
            List<String> robotCodeList = (List<String>) entry.getValue();
            for (int i = 0; i < robotCodeList.size(); i++) {
                String code = robotCodeList.get(i);
                Robot robot = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);

                //获取机器人所在地图名称
                String mapName = getMapName(code);
                //未获取到地图信息，循环下一个
                if (StringUtil.isNullOrEmpty(mapName))
                    continue;

                MessageInfo currentPose = CacheInfoManager.getMessageCache(code);
                //未获取到当前位置信息信息，循环下一个
                if (null == currentPose || StringUtil.isNullOrEmpty(currentPose.getMessageText())) {
                    continue;
                }

                JSONObject currentPoseObject = JSON.parseObject(currentPose.getMessageText());
                currentPoseObject.put(TopicConstants.SCENE_NAME, sceneName);
                currentPoseObject.put(TopicConstants.MAP_NAME, mapName);
                currentPoseObject.put(TopicConstants.CODE, code);
                currentPoseObject.put("id", robot.getRobotIdForElevator());
                jsonArray.add(currentPoseObject);
            }
            //将消息通过mq发送给agent,由agent透传给ros topic
            ExecutorService executorService = Executors.newCachedThreadPool();
            Runnable runnable = new MyThread(robotCodeList, jsonArray);
            executorService.execute(runnable);
        }
    }

    /**
     * 获取机器人所在地图信息
     * @param code  机器人编号
     * @return 地图名
     */
    private String getMapName(String code) {
        MessageInfo messageInfo = CacheInfoManager.getMapCurrentCache(code);
        //未获取到地图信息，循环下一个
        if (messageInfo == null || StringUtil.isNullOrEmpty(messageInfo.getMessageText())) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
        String data = jsonObject.getString(TopicConstants.DATA);
        JSONObject object = JSON.parseObject(data);
        Integer currentMapCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
        //未获取到当前地图的正常数据，循环下一个
        if (currentMapCode == null || currentMapCode != 0) {
            return null;
        }
        String mapData = object.getString(TopicConstants.DATA);
        JSONObject mapObject = JSON.parseObject(mapData);
        String mapName = mapObject.getString(TopicConstants.MAP_NAME);
        return mapName;
    }


    /**
     * 向list中的机器人发送当前位置信息
     * @param robotCodeList 机器人列表
     * @param jsonArray 发送的数据
     */
    public void sendCurrentPose(List<String> robotCodeList, JSONArray jsonArray) {
        try {
            if (jsonArray.size() <= 0 || robotCodeList == null || robotCodeList.size() <= 0) {
                return;
            }
            //循环给每个机器人下发
            for (int i = 0; i < robotCodeList.size(); i++) {
                String code = robotCodeList.get(i);

<<<<<<< HEAD
=======
//                LOGGER.info("robot {} sendCurrentPose start", code);

>>>>>>> a692758... 添加地图信息删除时删除本地文件操作
                //封装robots_current_pose数据
                SlamBody slamBody = new SlamBody();
                slamBody.setPubName(TopicConstants.ROBOTS_CURRENT_POSE);
                slamBody.setErrorCode("0");
                slamBody.setMsg("success");
                slamBody.setData(jsonArray);
                slamBody.setUuid(UUID.randomUUID().toString().replace("-", ""));

<<<<<<< HEAD
=======
                String slamBodyString = JSON.toJSONString(slamBody);
//                LOGGER.info("sendCurrentPose slamBody {}", slamBodyString);

>>>>>>> a692758... 添加地图信息删除时删除本地文件操作
                //封装命令消息
                CommonInfo commonInfo = new CommonInfo();
                commonInfo.setTopicName(TopicConstants.AGENT_PUB);
                commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
                messageInfo.setSendTime(new Date());
                messageInfo.setReceiverId(code);
                messageInfo.setSenderId("goor-server");
                messageInfo.setMessageType(MessageType.EXECUTOR_COMMAND);

                messageSendHandleService.sendCommandMessage(false, false, code, messageInfo);
            }
        } catch (Exception e) {
            LOGGER.error("发送实时坐标信息失败", e);
        }
    }

    class MyThread implements Runnable {
        private List<String> robotCodeList;
        private JSONArray jsonArray;

        public MyThread(List<String> robotCodeList, JSONArray jsonArray) {
            this.robotCodeList = robotCodeList;
            this.jsonArray = jsonArray;
        }

        @Override
        public void run() {
            sendCurrentPose(robotCodeList, jsonArray);
        }
    }
}
