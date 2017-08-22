package cn.muye.base.cache;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.*;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.MessageInfo;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class CacheInfoManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static MapInfoService mapInfoService;

    protected final static Logger logger = Logger.getLogger(CacheInfoManager.class);

    /**
     * AppConfig 的缓存
     */
    private static ConcurrentHashMapCache<String, MessageInfo> messageCache = new ConcurrentHashMapCache<String, MessageInfo>();
    //机器人当前加载地图的缓存
    private static ConcurrentHashMapCache<String, MessageInfo> mapCurrentCache = new ConcurrentHashMapCache<String, MessageInfo>();
    //机器人原始地图的缓存
    private static ConcurrentHashMapCache<String, MapInfo> mapOriginalCache = new ConcurrentHashMapCache<String, MapInfo>();
    /**
     * uuid 的缓存
     */
    private static ConcurrentHashMapCache<String, MessageInfo> UUIDCache = new ConcurrentHashMapCache<String, MessageInfo>();
    /**
     * 机器人电量缓存
     */
    private static ConcurrentHashMapCache<String, ChargeInfo> robotChargeInfoCache = new ConcurrentHashMapCache<>();

    /**
     * 机器人同步时间的缓存
     */
    private static ConcurrentHashMapCache<String, Long> robotAutoRegisterTimeCache = new ConcurrentHashMapCache<String, Long>();
    /**
     * webSocket根据用户名来缓存Session 的缓存
     */
    private static ConcurrentHashMapCache<String, Session> webSocketSessionCache = new ConcurrentHashMapCache<String, Session>();
    /*场景和机器人列表，key为场景，value为机器人编号列表*/
    private static ConcurrentHashMapCache<String, List<String>> sceneRobotListCache = new ConcurrentHashMapCache<String, List<String>>();

    //    //状态机缓存
    private static ConcurrentHashMapCache<String, StateCollectorAutoCharge> autoChargeCache = new ConcurrentHashMapCache<>();//自动回充状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> leftBaseDriverCache = new ConcurrentHashMapCache<>();//左驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> rightBaseDriverCache = new ConcurrentHashMapCache<>();//右驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseMicroSwitchAndAntiDropping> baseMicroSwitchAndAntiCache = new ConcurrentHashMapCache<>();//微动开关与防跌落状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseSystem> baseSystemCache = new ConcurrentHashMapCache<>();//底盘系统状态
    private static ConcurrentHashMapCache<String, StateCollectorNavigation> navigationCache = new ConcurrentHashMapCache<>();//导航状态

    private static ConcurrentHashMapCache<String, String> persistMissionState = new ConcurrentHashMapCache<>();//已经存库的任务状态

    private static ConcurrentHashMapCache<String, Integer> userLoginStatusCache = new ConcurrentHashMapCache<>();//用户登录状态

    private static ConcurrentHashMapCache<String, Boolean> stopSendWebSocketDevice = new ConcurrentHashMapCache<>();//停止发送WebSocket机器编号

    static {

        // AppConfig对象缓存的最大生存时间，单位毫秒，永久保存
        messageCache.setMaxLifeTime(0);
        mapCurrentCache.setMaxLifeTime(0);
        mapOriginalCache.setMaxLifeTime(0);
        robotAutoRegisterTimeCache.setMaxLifeTime(0);
        UUIDCache.setMaxLifeTime(60 * 1000);//设置超时时间60秒

        robotChargeInfoCache.setMaxLifeTime(0);
        webSocketSessionCache.setMaxLifeTime(0);
        sceneRobotListCache.setMaxLifeTime(0);
        persistMissionState.setMaxLifeTime(0);

        //状态机缓存  存储端判断如果状态有改变则存入
        autoChargeCache.setMaxLifeTime(0);
        leftBaseDriverCache.setMaxLifeTime(0);
        rightBaseDriverCache.setMaxLifeTime(0);
        baseMicroSwitchAndAntiCache.setMaxLifeTime(0);
        baseSystemCache.setMaxLifeTime(0);
        navigationCache.setMaxLifeTime(0);

        //用户登录状态
        userLoginStatusCache.setMaxLifeTime(0);

        stopSendWebSocketDevice.setMaxLifeTime(0);
    }

    private CacheInfoManager() {

    }

    public static void setMessageCache(MessageInfo info) {
        messageCache.put(info.getSenderId(), info);
    }

    public static MessageInfo getMessageCache(String senderId) {
        return messageCache.get(senderId);
    }

    public static void removeMapOriginalCache(String key) {
        mapOriginalCache.remove(key);
    }

    public static void setUUIDCache(String uuId, MessageInfo messageInfo) {
        UUIDCache.put(uuId, messageInfo);
    }

    public static MessageInfo getUUIDCache(String uuId) {
        return UUIDCache.get(uuId);
    }


    public static void setMapCurrentCache(MessageInfo messageInfo) {
        mapCurrentCache.put(messageInfo.getSenderId(), messageInfo);
    }

    public static MessageInfo getMapCurrentCache(String deviceCode) {
        return mapCurrentCache.get(deviceCode);
    }

    /**
     * AppConfigCache
     */
    public static MapInfo getMapOriginalCache(String key) {
        if (key == null) {
            return null;
        }
        MapInfo mapInfo = mapOriginalCache.get(key);
        mapInfoService = applicationContext.getBean(MapInfoService.class);
        if (mapInfo == null) {
            String[] names = FileUtils.resolveMapAndSceneName(key);
            if (names.length != 3) {
                return null;
            }
            List<MapInfo> mapInfoList = mapInfoService.getMapInfo(names[0], names[1], Long.parseLong(names[2]));
            if (mapInfoList.size() > 0) {
                mapInfo = mapInfoList.get(0);
                mapOriginalCache.put(key, mapInfo);
            }
            return mapInfo;
        } else if (StringUtil.isNullOrEmpty(mapInfo.getPngDesigned())) {
            //如果美画图为空，进行数据库查询
            List<MapInfo> mapInfoList = mapInfoService.getMapInfo(mapInfo.getMapName(), mapInfo.getSceneName(), mapInfo.getStoreId());
            if (mapInfoList.size() > 0) {
                mapInfo.setPngDesigned(mapInfoList.get(0).getPngDesigned());
            }
        }
        return mapInfo;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CacheInfoManager.applicationContext = applicationContext;
    }

    public static Long getRobotAutoRegisterTimeCache(String robotCode) {
        return robotAutoRegisterTimeCache.get(Constant.ROBOT_AUTO_REGISTER_PREFIX + robotCode);
    }

    public static void setRobotAutoRegisterTimeCache(String robotCode, Long time) {
        robotAutoRegisterTimeCache.put(Constant.ROBOT_AUTO_REGISTER_PREFIX + robotCode, time);
    }

    //机器人电量缓存
    public static void setRobotChargeInfoCache(String deviceId, ChargeInfo chargeInfo) {
        robotChargeInfoCache.put(deviceId, chargeInfo);
    }

    public static ChargeInfo getRobotChargeInfoCache(String deviceId) {
        return robotChargeInfoCache.get(deviceId);
    }

    public static void setWebSocketSessionCache(String userName, Session session) {
        webSocketSessionCache.put(userName, session);
    }

    public static Session getWebSocketSessionCache(String userName) {
        return webSocketSessionCache.get(userName);
    }

    public static int getWebSocketSessionCacheSize() {
        return webSocketSessionCache.size();
    }

    public static void removeWebSocketSessionCache(String userName) {
        webSocketSessionCache.remove(userName);
    }

    //自动回充缓存
    public static void setAutoChargeCache(String deviceId, StateCollectorAutoCharge stateCollectorAutoCharge) {
        autoChargeCache.put(deviceId, stateCollectorAutoCharge);
    }

    public static StateCollectorAutoCharge getAutoChargeCache(String deviceId) {
        return autoChargeCache.get(deviceId);
    }

    //左驱状态缓存
    public static void setLeftBaseDriverCache(String deviceId, StateCollectorBaseDriver stateCollectorBaseDriver) {
        leftBaseDriverCache.put(deviceId, stateCollectorBaseDriver);
    }

    public static StateCollectorBaseDriver getLeftBaseDriverCache(String deviceId) {
        return leftBaseDriverCache.get(deviceId);
    }

    //右驱状态缓存
    public static void setRightBaseDriverCache(String deviceId, StateCollectorBaseDriver stateCollectorBaseDriver) {
        rightBaseDriverCache.put(deviceId, stateCollectorBaseDriver);
    }

    public static StateCollectorBaseDriver getRightBaseDriverCache(String deviceId) {
        return rightBaseDriverCache.get(deviceId);
    }

    //微动开关与防跌落状态缓存
    public static void setBaseMicroSwitchAndAntiCache(String deviceId, StateCollectorBaseMicroSwitchAndAntiDropping stateCollectorBaseMicroSwitchAndAntiDropping) {
        baseMicroSwitchAndAntiCache.put(deviceId, stateCollectorBaseMicroSwitchAndAntiDropping);
    }

    public static StateCollectorBaseMicroSwitchAndAntiDropping getBaseMicroSwitchAndAntiCache(String deviceId) {
        return baseMicroSwitchAndAntiCache.get(deviceId);
    }

    //底盘系统状态缓存
    public static void setBaseSystemCache(String deviceId, StateCollectorBaseSystem stateCollectorBaseSystem) {
        baseSystemCache.put(deviceId, stateCollectorBaseSystem);
    }

    public static StateCollectorBaseSystem getBaseSystemCache(String deviceId) {
        return baseSystemCache.get(deviceId);
    }

    //底盘系统状态缓存
    public static void setNavigationCache(String deviceId, StateCollectorNavigation stateCollectorNavigation) {
        navigationCache.put(deviceId, stateCollectorNavigation);
    }

    public static StateCollectorNavigation getNavigationCache(String deviceId) {
        return navigationCache.get(deviceId);
    }

    public static Map<String, List<String>> getSceneRobotListCache() {
        Map<String, List<String>> sceneRobotCodeList = new HashMap<>();
        Iterator iterator = sceneRobotListCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            String key = entry.getKey();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            List<String> robotCodeList = (List<String>) valueEntry.getValue();
            sceneRobotCodeList.put(key, robotCodeList);
        }
        return sceneRobotCodeList;
    }

    public static void setSceneRobotListCache(String sceneName, String robotCode) {
        Iterator iterator = sceneRobotListCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            List<String> robotCodeList = (List<String>) valueEntry.getValue();
            //判断机器是否在缓存中
            if (robotCodeList.contains(robotCode)) {
                robotCodeList.remove(robotCode);
            }
        }
        List<String> robotCodeList = sceneRobotListCache.get(sceneName);
        if (robotCodeList == null) {
            robotCodeList = Lists.newArrayList();
        }
        robotCodeList.add(robotCode);
        sceneRobotListCache.put(sceneName, robotCodeList);
    }

    //已经存库的任务状态
    public static void setPersistMissionState(String deviceId, String missionState) {
        persistMissionState.put(deviceId, missionState);
    }

    public static String getPersistMissionState(String deviceId) {
        return persistMissionState.get(deviceId);
    }

    public static Integer getUserLoginStatusCache(String key) {
        return userLoginStatusCache.get(key);
    }

    public static void removeUserLoginStatusCache(String key) {
        userLoginStatusCache.remove(key);
    }

    public static void setUserLoginStatusCache(String key, Integer status) {
        userLoginStatusCache.put(key, status);
    }

    public static void setStopSendWebSocketDevice(LogType logType, String deviceId) {
        String key = logType.getName() + "_" + deviceId;
        stopSendWebSocketDevice.put(key, true);
    }

    public static Boolean getStopSendWebSocketDevice(LogType logType, String deviceId) {
        String key = logType.getName() + "_" + deviceId;
        return stopSendWebSocketDevice.get(key);
    }
}
