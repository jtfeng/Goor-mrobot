package cn.muye.base.cache;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.FileUtils;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.model.config.AppConfig;
import cn.muye.base.service.mapper.config.AppConfigService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheInfoManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static AppConfigService appConfigService;

    private static MapInfoService mapInfoService;

    protected final static Logger logger = Logger.getLogger(CacheInfoManager.class);

    /**
     * AppConfig 的缓存
     */
    private static ConcurrentHashMapCache<Long, AppConfig> appConfigCache = new ConcurrentHashMapCache<Long, AppConfig>();
    private static ConcurrentHashMapCache<String, MessageInfo> messageCache = new ConcurrentHashMapCache<String, MessageInfo>();
    //机器人当前加载地图的缓存
    private static ConcurrentHashMapCache<String, MessageInfo> mapCurrentCache = new ConcurrentHashMapCache<String, MessageInfo>();
    //机器人原始地图的缓存
    private static ConcurrentHashMapCache<String, MapInfo> mapOriginalCache = new ConcurrentHashMapCache<String, MapInfo>();
	/** uuid 的缓存 */
	private static ConcurrentHashMapCache<String, MessageInfo> UUIDCache = new ConcurrentHashMapCache<String, MessageInfo>();

    /** 机器人同步时间的缓存 */
    private static ConcurrentHashMap<String, Long> robotAutoRegisterTimeCache = new ConcurrentHashMap<>();

    static {

        // AppConfig对象缓存的最大生存时间，单位毫秒，永久保存
        appConfigCache.setMaxLifeTime(0);
        messageCache.setMaxLifeTime(0);
        mapCurrentCache.setMaxLifeTime(0);
        mapOriginalCache.setMaxLifeTime(0);
		UUIDCache.setMaxLifeTime(60*1000);//设置超时时间60秒
	}

    private CacheInfoManager() {

    }

    public static void removeAppConfigCache(Long id) {
        appConfigCache.remove(id);
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

    /**
     * AppConfigCache
     */
    public static AppConfig getAppConfigCache(Long id) {
        if (id == null) {
            return null;
        }
        AppConfig appConfigInfo = appConfigCache.get(id);
        if (appConfigInfo == null) {
            appConfigService = applicationContext.getBean(AppConfigService.class);
            AppConfig appConfig = appConfigService.get(1);
            appConfigCache.put(id, appConfig);
            return appConfig;
        }
        return appConfigInfo;
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
            mapInfo = mapInfoService.getMapInfo(names[0], names[1], Long.parseLong(names[2]));
            mapOriginalCache.put(key, mapInfo);
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
}
