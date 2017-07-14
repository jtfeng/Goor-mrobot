package cn.muye.area.map.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jelynn on 2017/7/7.
 */
@Service
public class MapSyncService implements ApplicationContextAware{

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSyncService.class);
    private static ApplicationContext applicationContext;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotService robotService;

    @Autowired
    private MapZipService mapZipService;

    public String syncMap(long storeId) {
        MapZip mapZip = mapZipService.latestZip(storeId);
        return syncMap(mapZip, storeId);
    }

    public String syncMap(MapZip mapZip, long storeId) {
        List<Robot> robotList = robotService.listRobot(storeId);
        return syncMap(mapZip, robotList);
    }

    public String syncMap(MapZip mapZip, List<Robot> robotList) {
        //TODO 向机器人发送消息，更新地图
        StringBuffer stringBuffer = new StringBuffer();
        sendMapSyncMessage(robotList, mapZip);
        return stringBuffer.toString();
    }

    public void sendMapSyncMessage(List<Robot> robotList , MapZip mapZip){
        try {
            if(null == applicationContext){
                    return;
            }
            rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            if(null == rabbitTemplate){
                return;
            }
            //封装文件下载数据
            SlamResponseBody slamResponseBody = new SlamResponseBody();
            slamResponseBody.setSubName(TopicConstants.AGENT_LOCAL_MAP_UPLOAD);
            slamResponseBody.setData(mapZip);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessageText(JSON.toJSONString(slamResponseBody));
            for(int i = 0; i <robotList.size(); i ++ ){
                String code = robotList.get(i).getCode();
                String backResultClientRoutingKey = RabbitMqBean.getRoutingKey(code,true, MessageType.EXECUTOR_CLIENT.name());
                AjaxResult ajaxClientResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultClientRoutingKey, messageInfo);
            }
        }catch (Exception e){
            LOGGER.error("发送地图更新信息失败",e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapSyncService.applicationContext = applicationContext;
    }
}
