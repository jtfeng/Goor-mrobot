package cn.muye.base.cache;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationRobotXREF;
import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.dijkstra.RoadPathMaps;
import cn.mrobot.bean.state.*;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.elevator.service.ElevatorNoticeService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.mission.bean.RobotPositionRecord;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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

    /*场景和机器人列表，key为场景，value为机器人编号列表*/
    private static ConcurrentHashMapCache<String, CopyOnWriteArraySet<String>> sceneRobotListCache = new ConcurrentHashMapCache<String, CopyOnWriteArraySet<String>>();

    //    //状态机缓存
    private static ConcurrentHashMapCache<String, StateCollectorAutoCharge> autoChargeCache = new ConcurrentHashMapCache<>();//自动回充状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> leftBaseDriverCache = new ConcurrentHashMapCache<>();//左驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseDriver> rightBaseDriverCache = new ConcurrentHashMapCache<>();//右驱状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseMicroSwitchAndAntiDropping> baseMicroSwitchAndAntiCache = new ConcurrentHashMapCache<>();//微动开关与防跌落状态
    private static ConcurrentHashMapCache<String, StateCollectorBaseSystem> baseSystemCache = new ConcurrentHashMapCache<>();//底盘系统状态
    private static ConcurrentHashMapCache<String, StateCollectorNavigation> navigationCache = new ConcurrentHashMapCache<>();//导航状态

    private static ConcurrentHashMapCache<String, String> persistMissionState = new ConcurrentHashMapCache<>();//已经存库的任务状态

    private static ConcurrentHashMapCache<String, Integer> userLoginStatusCache = new ConcurrentHashMapCache<>();//用户登录状态

    private static ConcurrentHashMapCache<String, Boolean> robotOnlineCache = new ConcurrentHashMapCache<>();//机器人是否在线缓存

    private static ConcurrentHashMapCache<String, Boolean> robotBusyCache = new ConcurrentHashMapCache<>();//机器人是否在线缓存

    private static ConcurrentHashMapCache<String, String> robotMissionAlertStatusCache = new ConcurrentHashMapCache<>();//当前机器人任务状态缓存

    /**
     * webSocket根据用户名来缓存Session 的缓存
     */
    private static ConcurrentHashMapCache<String, Set<Session>> webSocketSessionCache = new ConcurrentHashMapCache<String, Set<Session>>(); //key ： 机器人code
    private static ConcurrentHashMapCache<Session, Map<String, Set<String>>> webSocketClientReceiveModuleCache = new ConcurrentHashMapCache<>(); //key ：客户端session value为指定类型的列表

    /**
     * 云端场景的路径图的缓存，用于动态规划路径
     * //key: 地图场景名 storeId + sceneName
     */
    private static ConcurrentHashMapCache<String, RoadPathMaps> roadPathMapsCache = new ConcurrentHashMapCache<String, RoadPathMaps>();

    /**
     * 云端场景的路径的缓存，用于动态规划路径
     * //key: 地图场景名 storeId + sceneName + pathType
     */
    private static ConcurrentHashMapCache<String, List<RoadPathDetail>> roadPathDetailsCache = new ConcurrentHashMapCache<String, List<RoadPathDetail>>();

    /**
     * 云端场景的点的缓存，用于动态规划路径
     * //key: 地图场景名 storeId + sceneName
     */
    private static ConcurrentHashMapCache<String, List<MapPoint>> mapPointsCache = new ConcurrentHashMapCache<String, List<MapPoint>>();

    /**
     * 固定路径获取的uuid缓存
     * //key: UUID
     */
    private static ConcurrentHashMapCache<String, Boolean> fixpathSceneNameCache = new ConcurrentHashMapCache<String, Boolean>();

    /**
     * 电梯pad消息通知缓存
     * //key: elevatorNoticeId
     */
    private static ConcurrentHashMapCache<Long, Boolean> elevatorNoticeCache = new ConcurrentHashMapCache<Long, Boolean>();

    /**
     * 机器人位置缓存
     */
    private static ConcurrentHashMapCache<String, LinkedList<RobotPositionRecord>> robotPositionRecordsCache = new ConcurrentHashMapCache<>();

    /**
     * 机器人List缓存
     */
    private static ConcurrentHashMapCache<String, List<Robot>> robotListCache = new ConcurrentHashMapCache<>();

    /**
     * 机器人信息缓存(ID做key)
     */
    private static ConcurrentHashMapCache<Long, Robot> robotInfoCacheById = new ConcurrentHashMapCache<>();

    /**
     * 机器人信息缓存(CODE做key)
     */
    private static ConcurrentHashMapCache<String, Robot> robotInfoCacheByCode = new ConcurrentHashMapCache<>();

    /**
     * 站List缓存
     */
    private static ConcurrentHashMapCache<String, List<Station>> stationListCache = new ConcurrentHashMapCache<>();

    /**
     * 站信息缓存
     */
    private static ConcurrentHashMapCache<Long, Station> stationInfoCache = new ConcurrentHashMapCache<>();

    /**
     * 站机器人关系缓存
     */
    private static ConcurrentHashMapCache<Long, List<StationRobotXREF>> stationRobotIdXrefListCache = new ConcurrentHashMapCache<>();

    /**
     * 场景Id与工控场景名缓存
     */
    private static ConcurrentHashMapCache<Long, String> sceneMapRelationCache = new ConcurrentHashMapCache<>();

    /**
     * 到站信息发送缓存
     */
    private static ConcurrentHashMapCache<Long, CopyOnWriteArrayList<ElevatorNotice>> arrivalStationNoticeCache = new ConcurrentHashMapCache<>();

    static {

        // AppConfig对象缓存的最大生存时间，单位毫秒，永久保存
        messageCache.setMaxLifeTime(0);
        mapCurrentCache.setMaxLifeTime(0);
        mapOriginalCache.setMaxLifeTime(0);
        robotAutoRegisterTimeCache.setMaxLifeTime(0);
        UUIDCache.setMaxLifeTime(60 * 1000);//设置超时时间60秒

        robotChargeInfoCache.setMaxLifeTime(0);
        sceneRobotListCache.setMaxLifeTime(0);
        persistMissionState.setMaxLifeTime(0);
        arrivalStationNoticeCache.setMaxLifeTime(0);

        //状态机缓存  存储端判断如果状态有改变则存入
        autoChargeCache.setMaxLifeTime(0);
        leftBaseDriverCache.setMaxLifeTime(0);
        rightBaseDriverCache.setMaxLifeTime(0);
        baseMicroSwitchAndAntiCache.setMaxLifeTime(0);
        baseSystemCache.setMaxLifeTime(0);
        navigationCache.setMaxLifeTime(0);
        fixpathSceneNameCache.setMaxLifeTime(10 * 60 * 1000);
        robotPositionRecordsCache.setMaxLifeTime(0);
        robotListCache.setMaxLifeTime(0);
        robotInfoCacheById.setMaxLifeTime(0);
        robotInfoCacheByCode.setMaxLifeTime(0);
        stationListCache.setMaxLifeTime(0);
        stationInfoCache.setMaxLifeTime(0);
        stationRobotIdXrefListCache.setMaxLifeTime(0);
        sceneMapRelationCache.setMaxLifeTime(0);
        //用户登录状态
        userLoginStatusCache.setMaxLifeTime(0);

        webSocketSessionCache.setMaxLifeTime(0);
        webSocketClientReceiveModuleCache.setMaxLifeTime(0);

        //设置超时时间60*60秒=1小时
        roadPathMapsCache.setMaxLifeTime(3 * 3600 * 1000);
        roadPathDetailsCache.setMaxLifeTime(3 * 3600 * 1000);
        mapPointsCache.setMaxLifeTime(3 * 3600 * 1000);
    }

    private CacheInfoManager() {

    }

    public static void setMessageCache(MessageInfo info) {
        if (null == info || null == info.getSenderId()) {
            return;
        }
        messageCache.put(info.getSenderId(), info);
    }

    public static void removeMessageCache(String code) {
        messageCache.remove(code);
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
        Set<Session> sessionSet = getWebSocketSessionCache(userName);
        if (null == sessionSet) {
            sessionSet = Sets.newHashSet();
        }
        sessionSet.add(session);
        webSocketSessionCache.put(userName, sessionSet);
    }

    public static Set<Session> getWebSocketSessionCache(String userName) {
        if (userName == null)
            return null;
        return webSocketSessionCache.get(userName);
    }

    public static Map<String, Set<Session>> getWebSocketSessionCache() {
        Map<String, Set<Session>> webSocketSessionList = new HashMap<>();
        Iterator iterator = webSocketSessionCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            String key = entry.getKey();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            Set<Session> sessionSet = (Set<Session>) valueEntry.getValue();
            webSocketSessionList.put(key, sessionSet);
        }
        return webSocketSessionList;
    }

    public static void setWebSocketClientReceiveModule(Session session, String userId, String module) {
        Map<String, Set<String>> userModuleXREFs = webSocketClientReceiveModuleCache.get(session);
        if (null == userModuleXREFs) {
            userModuleXREFs = Maps.newHashMap();
        }
        Set<String> modules = userModuleXREFs.get(userId);
        if (null == modules) {
            modules = Sets.newHashSet();
        }
        modules.add(module);
        userModuleXREFs.put(userId, modules);
        webSocketClientReceiveModuleCache.put(session, userModuleXREFs);
    }

    public static boolean isWebSocketClientReceiveModule(Session session, String userId, String module) {
        Map<String, Set<String>> userModuleXREFs = webSocketClientReceiveModuleCache.get(session);
        if (null == userModuleXREFs)
            return false;
        Set<String> modules = userModuleXREFs.get(userId);
        return modules != null && modules.contains(module);
    }

    public static void removeWebSocketClientReceiveModule(Session session, String userId, String module) {
        if (null == session)
            return;
        Map<String, Set<String>> userModuleXREFs = webSocketClientReceiveModuleCache.get(session);
        if (null == userModuleXREFs)
            return;
        Set<String> modules = userModuleXREFs.get(userId);
        if (modules != null && modules.size() > 0) {
            modules.remove(module);
        }
    }

    public static void removeWebSocketClientFromModule(Session session) {
        if (null == session)
            return;
        webSocketClientReceiveModuleCache.remove(session);
    }

    public static int getWebSocketSessionCacheSize() {
        return webSocketSessionCache.size();
    }

    public static void removeWebSocketSessionCache(Session session) {
        if (null == session)
            return;
        Iterator iterator = webSocketSessionCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            String key = entry.getKey();
            Set<Session> sessionCache = (Set<Session>) valueEntry.getValue();
            if (sessionCache.contains(session)) {
                sessionCache.remove(session);
                webSocketSessionCache.put(key, sessionCache);
            }
        }
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

    public static CopyOnWriteArraySet<String> getSceneRobotListCache(String sceneMapName) {
        return sceneRobotListCache.get(sceneMapName);
    }

    public static Map<String, CopyOnWriteArraySet<String>> getSceneRobotListCache() {
        Map<String, CopyOnWriteArraySet<String>> sceneRobotCodeList = new HashMap<>();
        Iterator iterator = sceneRobotListCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            String key = entry.getKey();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            CopyOnWriteArraySet<String> robotCodeset = (CopyOnWriteArraySet<String>) valueEntry.getValue();
            sceneRobotCodeList.put(key, robotCodeset);
        }
        return sceneRobotCodeList;
    }

    public static void setSceneRobotListCache(String sceneName, String robotCode) {
        Iterator iterator = sceneRobotListCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            CopyOnWriteArraySet<String> robotCodeSet = (CopyOnWriteArraySet<String>) valueEntry.getValue();
            //判断机器是否在缓存中
            if (robotCodeSet.contains(robotCode)) {
                robotCodeSet.remove(robotCode);
                sceneRobotListCache.put(entry.getKey(), robotCodeSet);
            }
        }
        CopyOnWriteArraySet<String> sceneRobotCodeSet = sceneRobotListCache.get(sceneName);
        if (sceneRobotCodeSet == null) {
            sceneRobotCodeSet = new CopyOnWriteArraySet<String>();
        }
        sceneRobotCodeSet.add(robotCode);
        sceneRobotListCache.put(sceneName, sceneRobotCodeSet);
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

    public static Boolean getRobotOnlineCache(String robotSn) {
        return robotOnlineCache.get(robotSn);
    }

    public static void setRobotOnlineCache(String robotSn, Boolean online) {
        robotOnlineCache.put(robotSn, online);
    }

    public static String getRobotMissionAlertStatusCache(String code) {
        return robotMissionAlertStatusCache.get(code);
    }

    public static void setRobotMissionAlertStatusCache(String code, String missionStatus) {
        robotMissionAlertStatusCache.put(code, missionStatus);
    }

    /**
     * RoadPathMaps 获取、设置、删除
     *
     * @param storeId
     * @param sceneName
     * @param roadPathService
     * @return
     */
    public static RoadPathMaps getRoadPathMapsCache(Long storeId, String sceneName, RoadPathService roadPathService) {
        RoadPathMaps roadPathMaps = roadPathMapsCache.get(storeId + sceneName);
        //如果缓存中没有，则先写入一遍，再返回
        if (roadPathMaps == null) {
            roadPathMaps = setRoadPathMapsCache(storeId, sceneName, roadPathService);
        }
        return roadPathMaps;
    }

    public static RoadPathMaps setRoadPathMapsCache(Long storeId, String sceneName, RoadPathService roadPathService) {
        List<RoadPath> roadPathList = roadPathService.listRoadPathsBySceneNamePathTypeOrderByStart(sceneName, null, storeId);
        RoadPathMaps roadPathMaps = null;
        if (roadPathList != null && roadPathList.size() > 0) {
            roadPathMaps = new RoadPathMaps();
            roadPathMaps.init(roadPathList);
        }
        roadPathMapsCache.put(storeId + sceneName, roadPathMaps);
        return roadPathMaps;
    }

    public static void removeRoadPathMapsCache(Long storeId, String sceneName) {
        roadPathMapsCache.remove(storeId + sceneName);
    }

    /**
     * List<RoadPathDetail> 获取、设置、删除
     *
     * @param storeId
     * @param sceneName
     * @param pathType
     * @param roadPathService
     * @return
     */
    public static List<RoadPathDetail> getRoadPathDetailsCache(Long storeId, String sceneName, Integer pathType, RoadPathService roadPathService) {
        List<RoadPathDetail> roadPathDetails = roadPathDetailsCache.get(storeId + sceneName + pathType);
        //如果缓存中没有，则先写入一遍，再返回
        if (roadPathDetails == null) {
            roadPathDetails = setRoadPathDetailsCache(storeId, sceneName, pathType, roadPathService);
        }
        return roadPathDetails;
    }

    public static List<RoadPathDetail> setRoadPathDetailsCache(Long storeId, String sceneName, Integer pathType, RoadPathService roadPathService) {
        List<RoadPathDetail> roadPathDetails = roadPathService.listRoadPathDetailsBySceneNamePathType(sceneName, pathType, storeId);
        roadPathDetailsCache.put(storeId + sceneName + pathType, roadPathDetails);
        return roadPathDetails;
    }

    public static void removeRoadPathDetailsCache(Long storeId, String sceneName, Integer pathType) {
        roadPathDetailsCache.remove(storeId + sceneName + pathType);
    }

    /**
     * List<MapPoint> 获取、设置、删除
     *
     * @param storeId
     * @param sceneName
     * @param pointService
     * @return
     */
    public static List<MapPoint> getMapPointsCache(Long storeId, String sceneName, PointService pointService) {
        List<MapPoint> mapPoints = mapPointsCache.get(storeId + sceneName);
        //如果缓存中没有，则先写入一遍，再返回
        if (mapPoints == null) {
            mapPoints = setMapPointsCache(storeId, sceneName, pointService);
        }
        return mapPoints;
    }

    public static List<MapPoint> setMapPointsCache(Long storeId, String sceneName, PointService pointService) {
        List<MapPoint> mapPoints = pointService.listByMapSceneNameAndPointType(sceneName, null, storeId);
        mapPointsCache.put(storeId + sceneName, mapPoints);
        return mapPoints;
    }

    public static void removeMapPointsCache(Long storeId, String sceneName) {
        mapPointsCache.remove(storeId + sceneName);
    }

    public static Boolean getFixpathSceneNameCache(String sceneName) {
        return fixpathSceneNameCache.get(sceneName);
    }

    public static void removeFixpathSceneNameCache(String sceneName) {
        fixpathSceneNameCache.remove(sceneName);
    }

    public static void setFixpathSceneNameCache(String sceneName) {
        CacheInfoManager.fixpathSceneNameCache.put(sceneName, true);
    }

    public static List<Long> getElevatorNoticeCache() {
        Iterator iterator = elevatorNoticeCache.iterator();
        List<Long> elevatorNoticeIdList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<Long, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<Long, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            Long key = entry.getKey();
            if (null != key) {
                elevatorNoticeIdList.add(key);
            }
        }
        return elevatorNoticeIdList;
    }

    public static void setElevatorNoticeCache(Long elevatorNoticeId) {
        elevatorNoticeCache.put(elevatorNoticeId, true);
    }

    public static void removeElevatorNoticeCache(Long elevatorNoticeId) {
        elevatorNoticeCache.remove(elevatorNoticeId);
    }

    public static void setRobotPositionRecordsCache(String robotId, LinkedList<RobotPositionRecord> robotPositionRecordList) {
        robotPositionRecordsCache.put(robotId, robotPositionRecordList);
    }

    public static void removeRobotPositionRecordsCache(String robotId) {
        robotPositionRecordsCache.remove(robotId);
    }

    public static LinkedList<RobotPositionRecord> getRobotPositionRecordsCache(String key) {
        LinkedList<RobotPositionRecord> recordsLinkedList = robotPositionRecordsCache.get(key);
        return recordsLinkedList;
    }

    public static void removeUnBusyRobotsCache(List<String> busyRobotCode) {
        Iterator iterator = robotPositionRecordsCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<String, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            String key = entry.getKey();
            if (!busyRobotCode.contains(key)) {
                removeRobotPositionRecordsCache(key);
            }
        }
    }

    public static Boolean getRobotBusyCache(String robotCode) {
        return robotBusyCache.get(robotCode);
    }

    public static void setRobotBusyCache(String robotCode, Boolean busy) {
        robotBusyCache.put(robotCode, busy);
    }

    public static List<Robot> getRobotListCache(String key) {
        return robotListCache.get(key);
    }

    public static void setRobotListCache(String key, List<Robot> robotList) {
        robotListCache.put(key, robotList);
    }

    public static Robot getRobotInfoCacheById(Long id) {
        return robotInfoCacheById.get(id);
    }

    public static void setRobotInfoCacheById(Long id, Robot robot) {
        robotInfoCacheById.put(id, robot);
    }

    public static List<Station> getStationListCache(String key) {
        return stationListCache.get(key);
    }

    public static void setStationListCache(String key, List<Station> stationList) {
        stationListCache.put(key, stationList);
    }

    public static Station getStationInfoCache(Long id) {
        return stationInfoCache.get(id);
    }

    public static void setStationInfoCache(Long id, Station stationInfo) {
        stationInfoCache.put(id, stationInfo);
    }

    public static List<StationRobotXREF> getStationRobotIdXrefListCache(Long stationId) {
        return stationRobotIdXrefListCache.get(stationId);
    }

    public static void setStationRobotIdXrefListCache(Long stationId, List<StationRobotXREF> stationRobotIdXrefList) {
        stationRobotIdXrefListCache.put(stationId, stationRobotIdXrefList);
    }

    public static void removeStationRobotIdXrefListCache(Long stationId) {
        stationRobotIdXrefListCache.remove(stationId);
    }

    public static String getSceneMapRelationCache(Long sceneId) {
        return sceneMapRelationCache.get(sceneId);
    }

    public static void setSceneMapRelationCache(Long sceneId, String mapSceneName) {
        sceneMapRelationCache.put(sceneId, mapSceneName);
    }

    public static void removeSceneMapRelationCache(Long sceneId) {
        sceneMapRelationCache.remove(sceneId);
    }

    public static List<ElevatorNotice> getArrivalStationNoticeCache(Long stationId) {
        return arrivalStationNoticeCache.get(stationId);
    }

    public static void setArrivalStationNoticeCache(Long stationId, ElevatorNotice elevatorNotice) {
        CopyOnWriteArrayList<ElevatorNotice> elevatorNoticeList = arrivalStationNoticeCache.get(stationId);
        if (null == elevatorNoticeList) {
            elevatorNoticeList = new CopyOnWriteArrayList(Lists.newArrayList());
        }
        if (!elevatorNoticeList.contains(elevatorNotice)) {
            elevatorNoticeList.add(elevatorNotice);
        }
        arrivalStationNoticeCache.put(stationId, elevatorNoticeList);
    }

    /**
     * 建议使用 elevatorNoticeService.removeArrivalStationNoticeCacheByOrderDetailId(id);
     * 避免多线程出现 ConcurrentModificationException
     *
     * @param orderDetailId
     */
    public static void removeArrivalStationNoticeCacheByOrderDetailId(Long orderDetailId) {
        Iterator iterator = arrivalStationNoticeCache.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<Long, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            Long key = entry.getKey();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            CopyOnWriteArrayList<ElevatorNotice> elevatorNotices = (CopyOnWriteArrayList<ElevatorNotice>) valueEntry.getValue();
            for (ElevatorNotice elevatorNotice : elevatorNotices) {
                if (orderDetailId.equals(elevatorNotice.getOrderDetailId())) {
                    //并且修改elevatorNotice的状态
                    ElevatorNoticeService elevatorNoticeService = applicationContext.getBean(ElevatorNoticeService.class);
                    elevatorNoticeService.updateState(elevatorNotice.getId(), ElevatorNotice.State.RECEIVED);
                    //从列表中移除
                    elevatorNotices.remove(elevatorNotice);
                    arrivalStationNoticeCache.put(key, elevatorNotices);
                }
            }
        }
    }

    public static Map<Long, List<ElevatorNotice>> getAllArrivalStationNoticeCache() {
        Iterator iterator = arrivalStationNoticeCache.iterator();
        Map<Long, List<ElevatorNotice>> noticeMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Long, ConcurrentHashMapCache.ValueEntry> entry = (Map.Entry<Long, ConcurrentHashMapCache.ValueEntry>) iterator.next();
            Long key = entry.getKey();
            ConcurrentHashMapCache.ValueEntry valueEntry = entry.getValue();
            CopyOnWriteArrayList<ElevatorNotice> elevatorNotices = (CopyOnWriteArrayList<ElevatorNotice>) valueEntry.getValue();
            noticeMap.put(key, elevatorNotices);
        }
        return noticeMap;
    }

    public static Robot getRobotInfoCacheByCode(String code) {
        return robotInfoCacheByCode.get(code);
    }

    public static void setRobotInfoCacheByCode(String code, Robot robot) {
        robotInfoCacheByCode.put(code, robot);
    }

}
