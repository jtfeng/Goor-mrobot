package cn.muye.base.consumer;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.base.CommonInfo;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageStatusType;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.mission.task.JsonElevatorNotice;
import cn.mrobot.bean.mission.task.JsonMissionItemDataLaserNavigation;
import cn.mrobot.bean.slam.SlamBody;
import cn.mrobot.bean.state.StateCollectorAutoCharge;
import cn.mrobot.bean.state.StateCollectorResponse;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.aes.AES;
import cn.muye.account.employee.service.EmployeeService;
import cn.muye.area.fixpath.service.FixPathService;
import cn.muye.area.map.bean.CurrentInfo;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.assets.elevator.service.ElevatorNoticeService;
import cn.muye.assets.goods.service.GoodsTypeService;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.model.message.OffLineMessage;
import cn.muye.base.service.MessageSendHandleService;
import cn.muye.base.service.mapper.message.OffLineMessageService;
import cn.muye.base.websoket.WebSocketSendMessage;
import cn.muye.log.charge.service.ChargeInfoService;
import cn.muye.log.state.service.StateCollectorService;
import cn.muye.service.consumer.topic.PickUpPswdVerifyService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class ConsumerCommon {
    private static Logger logger = Logger.getLogger(ConsumerCommon.class);

    private static final int MAGIC_NUM_THREE = 3;
//    @Autowired
//    private ReceiveMessageService receiveMessageService;

    @Autowired
    private OffLineMessageService offLineMessageService;

    @Autowired
    PickUpPswdVerifyService pickUpPswdVerifyService;

    @Autowired
    private ChargeInfoService chargeInfoService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RobotService robotService;

    @Autowired
    private RobotConfigService robotConfigService;

    @Autowired
    private GoodsTypeService goodsTypeService;

    @Autowired
    private StateCollectorService stateCollectorService;

    @Autowired
    private MessageSendHandleService messageSendHandleService;

    @Autowired
    private MapInfoService mapInfoService;

    @Autowired
    private WebSocketSendMessage webSocketSendMessage;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FixPathService fixPathService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ElevatorNoticeService elevatorNoticeService;
    /**
     * 透传ros发布的topic：agent_pub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_PUB)
    public void directAgentPub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtil.isEmpty(messageName)) {
                    switch (messageName) {
                        case TopicConstants.PICK_UP_PSWD_VERIFY:
                            /* 17.7.5 Add By Abel. 取货密码验证。根据机器人编号，密码和货柜编号*/
                            pickUpPswdVerifyService.handlePickUpPswdVerify(messageInfo);
                            break;
                        case TopicConstants.FETCH_DETAIL_GOODSTYPE:
                            //Server Answer the detail GoodsType messages.
                            goodsTypeService.syncGoodsTypeMessage(messageInfo, messageName);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAgentPub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：agent_sub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_AGENT_SUB)
    public void directAgentSub(@Payload MessageInfo messageInfo) {
        try {
            logger.info("messageInfo : " + JSON.toJSONString(messageInfo));
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                String uuid = jsonObjectData.getString(TopicConstants.UUID);
                String robotCode = messageInfo.getSenderId();
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals(TopicConstants.PUB_SUB_NAME_ROBOT_INFO)) {
                    //订阅应用发出的查询机器人信息(暂时只拿电量阈值和sn)请求,回执给其所需的机器人信息
                    Robot robotDb = robotService.getByCodeByXml(robotCode, SearchConstants.FAKE_MERCHANT_STORE_ID, null);
                    if (!StringUtil.isNullOrEmpty(uuid)) {
                        syncRosRobotConfig(robotDb, uuid);
                    }
                } else if (!StringUtils.isEmpty(messageName) && (messageName.equals(TopicConstants.VERIFY_EMPLYEE_NUMBER)
                        || messageName.equals(TopicConstants.VERIFY_ELEVATOR_ADMIN_NUMBER))) {
                    // 员工工号取货校验
                    String jsonData = jsonObjectData.getString(TopicConstants.DATA);
                    logger.info(" verify_emplyee_number  jsonData : " + JSON.toJSONString(jsonData));
                    JSONObject employeeObj = JSON.parseObject(jsonData);
                    String empNo = employeeObj.getString("empNo");
                    Long missionItemId = employeeObj.getLong("missionItemId");
                    AjaxResult ajaxResult = employeeService.verifyEmplyeeNumber(empNo, missionItemId, messageName);
                    replyVerification(robotCode, ajaxResult.getMessage(), ajaxResult.getCode(), uuid, messageName);
                } else if (!StringUtils.isEmpty(messageName) &&
                        messageName.equals(TopicConstants.PUB_SUB_NAME_CHECK_OPERATE_PWD)) {
                    //PUB AND SUB NAME : check_operate_pwd
                    robotService.checkPasswordIsValid(uuid, robotCode,
                            jsonObjectData.getString("input_pwd"));
                } else if (!StringUtils.isEmpty(messageName) &&
                        messageName.equals(TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_QUERY)) {
                    logger.info("   -| 机器人编号为：" + robotCode + "、发送的消息格式类型为：" + TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_QUERY);
                    // 机器人开机获取云端相关资源
                    sceneService.replyGetRobotStartAssets(uuid, robotCode);
                } else if (!StringUtils.isEmpty(messageName) &&
                        messageName.equals(TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_UPDATE)) {
                    logger.info("   -| 机器人编号为：" + robotCode + "、发送的消息格式类型为：" + TopicConstants.PUB_SUB_NAME_CLOUD_ASSETS_UPDATE);
                    logger.info("       -| 接收到的实际 JSON 字符串内容为：" + JSONObject.toJSONString(jsonObjectData));
                    // 机器人开机重新修改与指定机器人的绑定关系
                    try {
                        sceneService.updateGetRobotStartAssets(robotCode, JSONObject.parseObject(jsonObjectData.getString(TopicConstants.DATA)));
                        sceneService.replyUpdateCloudAssetsResult(uuid, robotCode, true);
                    }catch (Exception e) {
                        sceneService.replyUpdateCloudAssetsResult(uuid, robotCode, false);
                        logger.error(e.getMessage(), e);
                    }
                } else if (!StringUtils.isEmpty(messageName) && messageName.equals(TopicConstants.ELEVATOR_NOTICE)) {
                    // 电梯pad消息通知,websocket消息通知电梯pad,pad接收成功后通知mission
                    logger.info("电梯pad消息通知,jsonObjectData=" + jsonObjectData);
                    ElevatorNotice elevatorNotice = parseToElevatorNotice(robotCode, jsonObjectData);
                    //收到消息后立即反馈给mission
                    elevatorNoticeService.sendElevatorNoticeToX86(elevatorNotice, TopicConstants.ERROR_CODE_SUCCESS, null, null);
                    elevatorNoticeService.sendElevatorNoticeToWebSocket(elevatorNotice);
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAgentSub exception", e);
        }
    }

    private ElevatorNotice parseToElevatorNotice(String robotCode, JSONObject jsonObjectData) {
        JsonElevatorNotice jsonElevatorNotice = JSON.parseObject(jsonObjectData.getString(TopicConstants.DATA), JsonElevatorNotice.class);
        String uuid = jsonObjectData.getString(TopicConstants.UUID);
        //将消息存入数据库
        ElevatorNotice elevatorNotice = new ElevatorNotice();
        elevatorNotice.init();
        elevatorNotice.setRobotCode(robotCode);
        elevatorNotice.setCallFloor(jsonElevatorNotice.getCallFloor());
        elevatorNotice.setElevatorId(jsonElevatorNotice.getElevatorId());
        elevatorNotice.setTargetFloor(jsonElevatorNotice.getTargetFloor());
        elevatorNotice.setUuid(uuid);
        return elevatorNotice;
    }

    /**
     * 给应用回执校验员工工号结果
     *
     * @param robotCode
     * @param msg
     * @param code
     * @param uuid
     */
    private void replyVerification(String robotCode, String msg, Integer code, String uuid, String subName) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_PUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        SlamBody slamBody = new SlamBody();
        slamBody.setPubName(subName);
        slamBody.setUuid(uuid);
        slamBody.setMsg(msg);
        slamBody.setErrorCode(String.valueOf(code));
        slamBody.setData("");
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setReceiverId(robotCode);
        messageInfo.setSenderId("goor-server");
        messageInfo.setMessageType(MessageType.ROBOT_INFO);
        messageInfo.setMessageText(JSON.toJSONString(commonInfo));
        try {
            messageSendHandleService.sendCommandMessage(true, false, robotCode, messageInfo);
            logger.info("下发机器人" + robotCode + "员工校验信息成功");
        } catch (Exception e) {
            logger.error("发送错误", e);
            try {
                slamBody.setMsg("查询错误");
                slamBody.setErrorCode("1");
                slamBody.setData("");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotCode, messageInfo);
            } catch (Exception e1) {
                logger.error("错误{}", e1);
            }
        } finally {
        }
    }


    /**
     * 同步往ros的agent_pub扔机器人信息消息
     *
     * @param robotNew
     */
    private void syncRosRobotConfig(Robot robotNew, String uuid) {
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTopicName(TopicConstants.AGENT_PUB);
        commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
        robotNew.setUuid(uuid);
        SlamBody slamBody = new SlamBody();
        slamBody.setPubName(TopicConstants.PUB_SUB_NAME_ROBOT_INFO);
        slamBody.setUuid(uuid);
        slamBody.setMsg("success");
        slamBody.setErrorCode("0");
        convertChargerMapPointToJsonMissionItemDataLaserNavigation(robotNew);
        slamBody.setData(JsonUtils.toJson(robotNew,
                new TypeToken<Robot>() {
                }.getType()));
        commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuId(UUID.randomUUID().toString().replace("-", ""));
        messageInfo.setReceiverId(robotNew.getCode());
        messageInfo.setSenderId("goor-server");
        messageInfo.setMessageType(MessageType.ROBOT_INFO);
        messageInfo.setMessageText(JSON.toJSONString(commonInfo));
        try {
            messageSendHandleService.sendCommandMessage(true, false, robotNew.getCode(), messageInfo);
            logger.info("下发机器人" + robotNew.getCode() + "电量阈值信息成功");
        } catch (Exception e) {
            logger.error("发送错误", e);
            try {
                slamBody.setMsg("查询错误");
                slamBody.setErrorCode("1");
                slamBody.setData("");
                commonInfo.setPublishMessage(JSON.toJSONString(new PubData(JSON.toJSONString(slamBody))));
                messageInfo.setMessageText(JSON.toJSONString(commonInfo));
                messageSendHandleService.sendCommandMessage(true, false, robotNew.getCode(), messageInfo);
            } catch (Exception e1) {
                logger.error("错误{}", e1);
            }
        } finally {
        }
    }

    /**
     * 充电桩点List转换导航点的List
     *
     * @param robotNew
     */
    private void convertChargerMapPointToJsonMissionItemDataLaserNavigation(Robot robotNew) {
        if (robotNew != null && robotNew.getOriginChargerMapPointList() != null) {
            List<MapPoint> originChargerMapPointList = robotNew.getOriginChargerMapPointList();
            List<JsonMissionItemDataLaserNavigation> list = Lists.newArrayList();
            if (originChargerMapPointList != null && originChargerMapPointList.size() > 0) {
                for (MapPoint mapPoint : originChargerMapPointList) {
                    String mapName = mapPoint.getMapName();
                    JsonMissionItemDataLaserNavigation jsonMissionItemDataLaserNavigation = new JsonMissionItemDataLaserNavigation();
                    jsonMissionItemDataLaserNavigation.setMap(mapName);
                    jsonMissionItemDataLaserNavigation.setMap_name(mapName);
                    jsonMissionItemDataLaserNavigation.setScene_name(mapPoint.getSceneName());
                    jsonMissionItemDataLaserNavigation.setX(mapPoint.getX());
                    jsonMissionItemDataLaserNavigation.setY(mapPoint.getY());
                    jsonMissionItemDataLaserNavigation.setTh(mapPoint.getTh());
                    list.add(jsonMissionItemDataLaserNavigation);
                }
            }
            robotNew.setChargerMapPointList(null);
            robotNew.setChargerMapPointList(list);
        }
    }

    /**
     * 透传ros发布的topic：app_pub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_APP_PUB)
    public void directAppPub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    if (TopicConstants.DEBUG)
                        logger.info(" ====== message.toString()===" + messageInfo.getMessageText());
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAppPub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：app_sub
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_APP_SUB)
    public void directAppSub(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                JSONObject jsonObjectData = JSON.parseObject(data);
                String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
                String messageData = jsonObjectData.getString(TopicConstants.DATA);
                Integer errorCode = jsonObjectData.getInteger(SearchConstants.SEARCH_ERROR_CODE);
                //TODO 根据不同的pub_name或者sub_name,处理不同的业务逻辑，如下获取当前地图信息
                if (!StringUtils.isEmpty(messageName) && messageName.equals("map_current_get")) {
                    //将当前加载的地图信息存入缓存
                    CacheInfoManager.setMapCurrentCache(messageInfo);
                    //将场景和对应的机器人放置在缓存中
                    if (errorCode != null && errorCode == 0) {
                        String mapData = jsonObjectData.getString(TopicConstants.DATA);
                        JSONObject mapObject = JSON.parseObject(mapData);
                        String sceneName = mapObject.getString(TopicConstants.SCENE_NAME);
                        String mapName = mapObject.getString(TopicConstants.MAP_NAME);
                        String sceneKey = sceneName + "_" + mapName;
                        CacheInfoManager.setSceneRobotListCache(sceneKey, messageInfo.getSenderId());
                    }
                } else if (!StringUtils.isEmpty(messageName) && messageName.equals(TopicConstants.FIXPATH_FILE_QUERY)) {
                    //fixPathService.saveFixpathQuery(messageInfo.getSenderId(), messageInfo.getSendTime(), messageData);
                    //fixPathService.dealFixpathFileQuery(messageInfo.getSenderId(), messageInfo.getSendTime(), messageData);
                }
            }
        } catch (Exception e) {
            logger.error("consumer directAppSub exception", e);
        }
    }

    /**
     * 透传ros发布的topic：state_collector
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_STATE_COLLECTOR)
    public void directStateCollector(@Payload MessageInfo messageInfo) {
        try {
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
                String data = jsonObject.getString(TopicConstants.DATA);
                StateCollectorResponse stateCollectorResponse = JSON.parseObject(data, StateCollectorResponse.class);
                stateCollectorResponse.setTime(messageInfo.getSendTime());
                stateCollectorResponse.setSenderId(messageInfo.getSenderId());
                stateCollectorService.handleStateCollector(stateCollectorResponse);
            }
        } catch (Exception e) {
            logger.error("consumer directAgentPub exception", e);
        }
    }


    /**
     * 透传ros发布的topic：current_pose
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_CURRENT_POSE)
    public void directCurrentPose(@Payload MessageInfo messageInfo) {
        try {
            CacheInfoManager.setMessageCache(messageInfo);
            //当前位置信息改变，向WebSocket推送消息
            sendCurrentInfoWebSocket(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCurrentPose exception", e);
        }
    }

    /**
     * 透传ros发布的topic：power
     * 按照 ChargeInfo 解析数据
     * <p>
     * power status store in a 8-bit coded unsigned int
     * charging status store in the 5th bit
     * 1 = on charging
     * 0 = not charging
     *
     * @param messageInfo goor上传的信息
     */
    @RabbitListener(queues = TopicConstants.DIRECT_APP_SUB_POWER)
    public void directPower(@Payload MessageInfo messageInfo) {
        try {
            String messageText = messageInfo.getMessageText();
            JSONObject jsonObject = JSON.parseObject(messageText);
            String raw_data = jsonObject.getString("data");
            byte[] bytes = Base64Utils.decode(raw_data.getBytes());
            int[] ret = new int[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                ret[i] = bytes[i] & 0xFF;
            }

            //屏蔽有些电源板上传数据数组长度为2
            if(ret.length >= MAGIC_NUM_THREE){
                ChargeInfo chargeInfo = new ChargeInfo();
                chargeInfo.setPowerPercent(ret[0]);
                String binStr = StringUtil.intToBit(ret[1], 8); //转成8位二进制数据
                chargeInfo.setChargingStatus(Integer.parseInt(binStr.charAt(3) + ""));
                chargeInfo.setDeviceId(messageInfo.getSenderId());
                chargeInfo.setCreateTime(messageInfo.getSendTime());
                chargeInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                chargeInfo.setAutoCharging(ret[2]); //byte 2：系统状态ex： BIT0 = 0  自动回充口没有充电；BIT0 = 1 自动回冲口正在充电
                saveAndCheckChargeInfo(messageInfo.getSenderId(), chargeInfo);
            }
        } catch (Exception e) {
            logger.error("consumer directAppSubPower exception", e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT)
    public void directCommandReport(@Payload MessageInfo messageInfo) {
        try {
            if (MessageType.TIME_SYNCHRONIZED.equals(messageInfo.getMessageType())) {
                clientTimeSynchronized(messageInfo);
                return;
            }

            messageSaveOrUpdate(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCommandReport exception", e);
        }
    }

    /**
     * 接收 x86 agent 发布过来的消息，理论不接收ros消息，牵涉到ros消息的，请使用topic透传，只和agent通信（无回执）
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_REPORT_RECEIVE)
    public AjaxResult directCommandReportAndReceive(@Payload MessageInfo messageInfo) {
        try {
            messageSaveOrUpdate(messageInfo);
        } catch (Exception e) {
            logger.error("consumer directCommandReport exception", e);
        }
        return AjaxResult.success();
    }

    /**
     * 接收 x86 agent 在线信息监测
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.ROBOT_ONLINE_QUERY)
    public AjaxResult robotOnlineQuery(@Payload MessageInfo messageInfo) {
        if (messageInfo != null) {
            return AjaxResult.success();
        }
        return AjaxResult.success();
    }

    /**
     * 消费掉topic.server消息
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.TOPIC_SERVER_COMMAND)
    public void topicServerCommand(@Payload MessageInfo messageInfo) {
        try {
            logger.debug("receive heartBeat message topicServerCommand receive message");
        } catch (Exception e) {
            logger.error("consumer topicServerCommand exception", e);
        }
    }

    /**
     * 消费掉fanout.server消息
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.FANOUT_SERVER_COMMAND)
    public void fanoutServerCommand(@Payload MessageInfo messageInfo) {
        try {
            logger.debug("fanoutServerCommand receive message");
        } catch (Exception e) {
            logger.error("consumer fanoutServerCommand exception", e);
        }
    }


    private boolean messageSaveOrUpdate(MessageInfo messageInfo) throws Exception {
        if (messageInfo == null
                || StringUtil.isEmpty(messageInfo.getUuId() + "")) {
            return false;
        }
        if (MessageType.EXECUTOR_LOG.equals(messageInfo.getMessageType())) {
            //TODO 此处可以添加日志及状态上报存储


        } else if (MessageType.REPLY.equals(messageInfo.getMessageType())) {
            OffLineMessage message = new OffLineMessage();
            MessageStatusType messageStatusType = messageInfo.getMessageStatusType();
            message.setMessageStatusType(messageStatusType == null ? null : messageStatusType.getIndex());//如果是回执，将对方传过来的信息带上
            message.setRelyMessage(messageInfo.getRelyMessage());//回执消息入库
            message.setSuccess(true);//接收到回执，发送消息成功
            message.setUuId(messageInfo.getUuId());//更新的主键
            message.setUpdateTime(new Date());//更新时间
            offLineMessageService.update(message);//更新发送的消息
        }
        return true;
    }

    /**
     * x86 请求与云端时间同步
     *
     * @param messageInfo
     */
    private void clientTimeSynchronized(MessageInfo messageInfo) {
        //监听上行X86时间同步消息，获得X86时间，与服务器时间比较，如果差值大于10s（默认），进行时间同步，否则不处理
        Date date = new Date();
        long upTime = messageInfo.getSendTime().getTime();
        long downTime = date.getTime();
        if ((downTime - upTime) < 10 * 1000 || (upTime - downTime) < 10 * 1000) {
            return;
        } else {
            //发送带响应同步消息，获得10次时间平均延迟
            int sum = 0;
            try {
                //todo:rabbitmq响应超时问题，
                MessageInfo sendMessageInfo = new MessageInfo();
                for (int i = 0; i < 10; i++) {
                    long startTime = System.currentTimeMillis();
                    sendMessageInfo.setMessageType(MessageType.TIME_SYNCHRONIZED);
                    AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);//后期带上机器编码进行区分
                    logger.info("the delay time :" + result.toString());
                    long endTime = System.currentTimeMillis();
                    sum += (endTime - startTime);
                }

                long avg = sum / 20; //只需要考虑单向时间误差

                //给指定X86发送时间同步消息
                sendMessageInfo.setMessageText(String.valueOf(System.currentTimeMillis() + avg));
                AjaxResult result = (AjaxResult) rabbitTemplate.convertSendAndReceive("topic.command.receive." + messageInfo.getSenderId(), sendMessageInfo);
                logger.info("the time synchronized result :" + result);
            } catch (Exception e) {
                logger.error("time synchronized failure : " + e.toString());
            }
        }
        return;
    }

    /**
     * 接收goor发布的topic：direct.command_robot_info
     *
     * @param messageInfo
     */
    @RabbitListener(queues = TopicConstants.DIRECT_COMMAND_ROBOT_INFO)
    public void subscribeRobotInfo(@Payload MessageInfo messageInfo) {
        try {
            logger.info("#############subscribeRobotInfo start##############");
            if (null != messageInfo && !StringUtils.isEmpty(messageInfo.getMessageText())) {
                String robotStr = AES.decryptFromBase64(messageInfo.getMessageText(), Constant.AES_KEY);
                Robot robotNew = JSON.parseObject(robotStr, Robot.class);
                //判断缓存里有没有
                Long lastRegTime = CacheInfoManager.getRobotAutoRegisterTimeCache(robotNew.getCode());
                if (lastRegTime == null) {
                    robotService.autoRegister(robotNew);
                } else {
                    //已经有了，更新缓存
                    CacheInfoManager.setRobotAutoRegisterTimeCache(robotNew.getCode(), System.currentTimeMillis());
                }
            }
        } catch (RuntimeException e) {
            logger.error("机器人注册失败，回滚，错误信息==>{}", e);
        } catch (Exception e) {
            logger.error("consumer robotInfo exception", e);
        }
    }

    private void saveAndCheckChargeInfo(String code, ChargeInfo chargeInfo) {
        StateCollectorAutoCharge autoCharge = CacheInfoManager.getAutoChargeCache(code);
        if (null != autoCharge) {
            chargeInfo.setAutoCharging(autoCharge.getPluginStatus());
        }
        CacheInfoManager.setRobotChargeInfoCache(code, chargeInfo);
        chargeInfoService.save(chargeInfo);

        //保存低电量警告
        int powerPercent = chargeInfo.getPowerPercent();
        Robot robot = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (null == robot) {
            return;
        }

        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (null == robotConfig) {
            return;
        }

        Integer lowBatteryThreshold = robotConfig.getLowBatteryThreshold();
        if (lowBatteryThreshold == null) {
            return;
        }

        if (powerPercent <= lowBatteryThreshold) {
            //更新机器人低电量状态
            Robot saveRobot = new Robot();
            robot.setId(robot.getId());
            robot.setLowPowerState(true);
            robotService.updateSelective(saveRobot);
            //向websocket推送低电量警告
            //判断是否接收到前端停止发送的请求
            String body = "机器人" + code + "当前电量：" + powerPercent + ",电量阈值:" + lowBatteryThreshold;
            WSMessage ws = new WSMessage.Builder().
                    title(LogType.WARNING_LOWER_POWER.getValue())
                    .messageType(WSMessageType.WARNING)
                    .body(body)
                    .deviceId(code)
                    .module(LogType.WARNING_LOWER_POWER.getName()).build();
            try {
                webSocketSendMessage.sendWebSocketMessage(ws);
            } catch (Exception e) {
                logger.error("发送低电量报警异常", e);
            }

        }
    }

    /**
     * 向WebSocket推送当前位置
     *
     * @param messageInfo
     */
    private void sendCurrentInfoWebSocket(MessageInfo messageInfo) {
        try {
            String deviceId = messageInfo.getSenderId();
            CurrentInfo currentInfo = mapInfoService.getCurrentInfo(deviceId);
            WSMessage ws = new WSMessage.Builder()
                    .messageType(WSMessageType.POSE)
                    .body(currentInfo)
                    .deviceId(deviceId)
                    .module(LogType.INFO_CURRENT_POSE.getName()).build();
            webSocketSendMessage.sendWebSocketMessage(ws);
        } catch (Exception e) {
            logger.error("发送当前位置信息出错", e);
        }
    }
}
