package cn.muye.base.cache;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.constant.Constant;
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

    protected final static Logger logger = Logger.getLogger(CacheInfoManager.class);

    /**
     * AppConfig 的缓存
     */
    private static ConcurrentHashMapCache<Long, AppConfig> appConfigCache = new ConcurrentHashMapCache<Long, AppConfig>();

    /**
     * topicHeartCheck 的缓存
     */
    private static ConcurrentHashMapCache<Integer, Long> topicHeartCheckCache = new ConcurrentHashMapCache<Integer, Long>();

    /**
     * sub_name 的缓存
     */
    private static ConcurrentHashMapCache<String, Integer> nameSubCache = new ConcurrentHashMapCache<String, Integer>();

    /**
     * lsub_name 的缓存
     */
    private static ConcurrentHashMapCache<String, Integer> nameLSubCache = new ConcurrentHashMapCache<String, Integer>();

    /** 机器人信息的缓存(读配置文件的信息放入缓存) */
    private static ConcurrentHashMapCache<String, Robot> robotInfoCache = new ConcurrentHashMapCache<>();

    /**
     * uuid 对应的操作是否已处理
     */
    private static ConcurrentHashMapCache<String, Boolean> uuidHandledCache = new ConcurrentHashMapCache<String, Boolean>();


    static {
        appConfigCache.setMaxLifeTime(0);
        topicHeartCheckCache.setMaxLifeTime(0);
        nameSubCache.setMaxLifeTime(0);
        nameLSubCache.setMaxLifeTime(0);
        robotInfoCache.setMaxLifeTime(0);
        topicHeartCheckCache.put(1, System.currentTimeMillis());

        uuidHandledCache.setMaxLifeTime(0);
    }

    private CacheInfoManager() {

    }

    public static void removeAppConfigCache(Long id) {
        appConfigCache.remove(id);
    }

    public static void setNameSubCache(String nameSub) {
        nameSubCache.put(nameSub, 1);
    }

    public static boolean getNameSubCache(String nameSub){
        Integer value = nameSubCache.get(nameSub);
        return value != null && value > 0;
    }

    public static void setNameLSubCache(String nameSub) {
        nameLSubCache.put(nameSub, 1);
    }

    public static boolean getNameLSubCache(String nameSub) {
        Integer value = nameLSubCache.get(nameSub);
        return value != null && value > 0;
    }

    public static void setTopicHeartCheckCache() {
        topicHeartCheckCache.remove(1);
        topicHeartCheckCache.put(1, System.currentTimeMillis());
    }

    public static Long getTopicHeartCheckCache() {
        return topicHeartCheckCache.get(1);
    }

    public static Robot getRobotInfoCache() {
        return robotInfoCache.get(Constant.ROBOT_CACHE_KEY);
    }

    public static void setRobotInfoCache(Robot robotInfo) {
        robotInfoCache.put(Constant.ROBOT_CACHE_KEY, robotInfo);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CacheInfoManager.applicationContext = applicationContext;
    }


    public static boolean getUUIDHandledCache(String uuid) {
        Boolean flag = uuidHandledCache.get(uuid);
        return null == flag ? false :flag;
    }

    public static void setUUIDHandledCache(String uuid) {
        if(uuidHandledCache.ContainsKey(uuid)){
            return;
        }
        uuidHandledCache.put(uuid, true); //set默认为true
    }
}
