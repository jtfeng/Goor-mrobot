package cn.muye.base.cache;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.state.*;
import cn.mrobot.utils.FileUtils;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.MessageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.List;

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
	/** uuid 的缓存 */
	private static ConcurrentHashMapCache<String, MessageInfo> UUIDCache = new ConcurrentHashMapCache<String, MessageInfo>();
    /** 机器人电量缓存 */
    private static ConcurrentHashMapCache<String, ChargeInfo> robotChargeInfoCache = new ConcurrentHashMapCache<>();

    /** 机器人同步时间的缓存 */
    private static ConcurrentHashMapCache<String, Long> robotAutoRegisterTimeCache = new ConcurrentHashMapCache<String, Long>();
    /** webSocket根据用户名来缓存Session 的缓存 */
    private static ConcurrentHashMapCache<String, Session> webSocketSessionCache = new ConcurrentHashMapCache<String, Session>();


//    //状态机缓存
    private static ConcurrentHashMapCache<String, StateCollectorAutoCharge> autoChargeCache = new ConcurrentHashMapCache<>();//自动回充状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> leftBaseDriverCache = new ConcurrentHashMapCache<>();//左驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> rightBaseDriverCache = new ConcurrentHashMapCache<>();//右驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseMicroSwitchAndAntiDropping> baseMicroSwitchAndAntiCache = new ConcurrentHashMapCache<>();//微动开关与防跌落状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseSystem> baseSystemCache = new ConcurrentHashMapCache<>();//底盘系统状态
    private static ConcurrentHashMapCache<String, StateCollectorNavigation> navigationCache = new ConcurrentHashMapCache<>();//导航状态

    static {

        // AppConfig对象缓存的最大生存时间，单位毫秒，永久保存
        messageCache.setMaxLifeTime(0);
        mapCurrentCache.setMaxLifeTime(0);
        mapOriginalCache.setMaxLifeTime(0);
        robotAutoRegisterTimeCache.setMaxLifeTime(0);
		UUIDCache.setMaxLifeTime(60*1000);//设置超时时间60秒

        robotChargeInfoCache.setMaxLifeTime(10*60*1000);
        webSocketSessionCache.setMaxLifeTime(0);

        //状态机缓存
        autoChargeCache.setMaxLifeTime(60 * 1000);
        leftBaseDriverCache.setMaxLifeTime(60 * 1000);
        rightBaseDriverCache.setMaxLifeTime(60 * 1000);
        baseMicroSwitchAndAntiCache.setMaxLifeTime(60 * 1000);
        baseSystemCache.setMaxLifeTime(60 * 1000);
        navigationCache.setMaxLifeTime(60 * 1000);
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

	public static void setUUIDCache(String uuId, MessageInfo messageInfo){
		UUIDCache.put(uuId, messageInfo);
	}

	public static MessageInfo getUUIDCache(String uuId){
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
        if (mapInfo == null) {
            mapInfoService = applicationContext.getBean(MapInfoService.class);
            String[] names = FileUtils.resolveMapAndSceneName(key);
            if(names.length != 3){
                return null;
            }
            List<MapInfo> mapInfoList = mapInfoService.getMapInfo(names[0], names[1], Long.parseLong(names[2]));
            if(mapInfoList.size() > 0){
                mapOriginalCache.put(key, mapInfo);
            }
            return mapInfo;
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
    public static void setRobotChargeInfoCache(String deviceId, ChargeInfo chargeInfo){
        robotChargeInfoCache.put(deviceId, chargeInfo);
    }

    public static ChargeInfo getRobotChargeInfoCache(String deviceId){
        return robotChargeInfoCache.get(deviceId);
    }

    public static void setWebSocketSessionCache(String userName, Session session){
        webSocketSessionCache.put(userName, session);
    }

    public static Session getWebSocketSessionCache(String userName){
        return webSocketSessionCache.get(userName);
    }

    public static int getWebSocketSessionCacheSize(){
        return webSocketSessionCache.size();
    }

    public static void removeWebSocketSessionCache(String userName){
        webSocketSessionCache.remove(userName);
    }
    //自动回充缓存
    public static void setAutoChargeCache(String deviceId, StateCollectorAutoCharge stateCollectorAutoCharge){
        autoChargeCache.put(deviceId, stateCollectorAutoCharge);
    }

    public static StateCollectorAutoCharge getAutoChargeCache(String deviceId){
        return autoChargeCache.get(deviceId);
    }

    //左驱状态缓存
    public static void setLeftBaseDriverCache(String deviceId, StateCollectorBaseDriver stateCollectorBaseDriver){
        leftBaseDriverCache.put(deviceId, stateCollectorBaseDriver);
    }

    public static StateCollectorBaseDriver getLeftBaseDriverCache(String deviceId){
        return leftBaseDriverCache.get(deviceId);
    }

    //右驱状态缓存
    public static void setRightBaseDriverCache(String deviceId, StateCollectorBaseDriver stateCollectorBaseDriver){
        leftBaseDriverCache.put(deviceId, stateCollectorBaseDriver);
    }

    public static StateCollectorBaseDriver getRightBaseDriverCache(String deviceId){
        return leftBaseDriverCache.get(deviceId);
    }

    //微动开关与防跌落状态缓存
    public static void setBaseMicroSwitchAndAntiCache(String deviceId, StateCollectorBaseMicroSwitchAndAntiDropping stateCollectorBaseMicroSwitchAndAntiDropping){
        baseMicroSwitchAndAntiCache.put(deviceId, stateCollectorBaseMicroSwitchAndAntiDropping);
    }

    public static StateCollectorBaseMicroSwitchAndAntiDropping getBaseMicroSwitchAndAntiCache(String deviceId){
        return baseMicroSwitchAndAntiCache.get(deviceId);
    }

    //底盘系统状态缓存
    public static void setBaseSystemCache(String deviceId, StateCollectorBaseSystem stateCollectorBaseSystem){
        baseSystemCache.put(deviceId, stateCollectorBaseSystem);
    }

    public static StateCollectorBaseSystem getBaseSystemCache(String deviceId){
        return baseSystemCache.get(deviceId);
    }

    //底盘系统状态缓存
    public static void setNavigationCache(String deviceId, StateCollectorNavigation stateCollectorNavigation){
        navigationCache.put(deviceId, stateCollectorNavigation);
    }

    public static StateCollectorNavigation getNavigationCache(String deviceId){
        return  navigationCache.get(deviceId);
    }
}
