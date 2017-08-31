package cn.muye.base.cache;

import cn.mrobot.bean.constant.Constant;
import cn.muye.base.model.config.RobotInfoConfig;
import cn.muye.base.service.mapper.config.RobotInfoConfigService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CacheInfoManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static RobotInfoConfigService robotInfoConfigService;

    protected final static Logger logger = Logger.getLogger(CacheInfoManager.class);

    /**
     * topicHeartCheck 的缓存
     */
    private static ConcurrentHashMapCache<Integer, Long> topicHeartCheckCache = new ConcurrentHashMapCache<Integer, Long>();

    /**
     * x86MissionTopicHeartCheck 的缓存
     */
    private static ConcurrentHashMapCache<Integer, Long> x86MissionTopicHeartCheckCache = new ConcurrentHashMapCache<Integer, Long>();

    /**
     * 降低获取机器人当前位置信息 的缓存
     */
    private static ConcurrentHashMapCache<Integer, Long> currentPoseSendTime = new ConcurrentHashMapCache<Integer, Long>();

    /**
     * 降低获取机器人当前电量 的缓存
     */
    private static ConcurrentHashMapCache<Integer, Long> powerSendTime = new ConcurrentHashMapCache<Integer, Long>();

    /**
     * sub_name 的缓存
     */
    private static ConcurrentHashMapCache<String, Integer> nameSubCache = new ConcurrentHashMapCache<String, Integer>();

    /**
     * lsub_name 的缓存
     */
    private static ConcurrentHashMapCache<String, Integer> nameLSubCache = new ConcurrentHashMapCache<String, Integer>();

    /** 机器人信息的缓存*/
    private static ConcurrentHashMapCache<String, RobotInfoConfig> robotInfoConfigCache = new ConcurrentHashMapCache<>();

    /**
     * uuid 对应的操作是否已处理
     */
    private static ConcurrentHashMapCache<String, Boolean> uuidHandledCache = new ConcurrentHashMapCache<String, Boolean>();

    /**
     * 状态机 module 的缓存
     */
    private static ConcurrentHashMapCache<String, Long> stateModuleCache = new ConcurrentHashMapCache<String, Long>();


    static {
        topicHeartCheckCache.setMaxLifeTime(0);
        x86MissionTopicHeartCheckCache.setMaxLifeTime(0);
        currentPoseSendTime.setMaxLifeTime(0);
        nameSubCache.setMaxLifeTime(0);
        nameLSubCache.setMaxLifeTime(0);
        robotInfoConfigCache.setMaxLifeTime(0);
        topicHeartCheckCache.put(1, System.currentTimeMillis());
        x86MissionTopicHeartCheckCache.put(1, System.currentTimeMillis());
        currentPoseSendTime.put(1, System.currentTimeMillis());
        powerSendTime.put(1, System.currentTimeMillis());
        uuidHandledCache.setMaxLifeTime(0);

        stateModuleCache.setMaxLifeTime(60 * 1000); //存活时间 2s
    }

    private CacheInfoManager() {

    }

    public static void removeRobotInfoConfigCache() {
        robotInfoConfigCache.remove(Constant.ROBOT_CACHE_KEY);
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

    public static void setX86MissionTopicHeartCheckCache() {
        x86MissionTopicHeartCheckCache.remove(1);
        x86MissionTopicHeartCheckCache.put(1, System.currentTimeMillis());
    }

    public static Long getX86MissionTopicHeartCheckCache() {
        return x86MissionTopicHeartCheckCache.get(1);
    }

    public static void setCurrentPoseSendTime() {
        currentPoseSendTime.put(1, System.currentTimeMillis());
    }

    public static Long getCurrentPoseSendTime() {
        return currentPoseSendTime.get(1);
    }

    public static void setPowerSendTime() {
        powerSendTime.put(1, System.currentTimeMillis());
    }

    public static Long getPowerSendTime() {
        return powerSendTime.get(1);
    }
    /**
     * RobotInfoConfigCache
     */
    public static RobotInfoConfig getRobotInfoConfigCache() {
        RobotInfoConfig robotInfoConfig = robotInfoConfigCache.get(Constant.ROBOT_CACHE_KEY);
        return robotInfoConfig;
    }

    public static void setRobotInfoConfigCache(RobotInfoConfig robotInfoConfig) {
        robotInfoConfigCache.put(Constant.ROBOT_CACHE_KEY, robotInfoConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CacheInfoManager.applicationContext = applicationContext;
    }

    public static Long getStateModuleCache(String module) {
       return stateModuleCache.get(module);
    }

    public static void setStateModuleCache(String module) {
        stateModuleCache.put(module, System.currentTimeMillis()); //set默认为true
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
