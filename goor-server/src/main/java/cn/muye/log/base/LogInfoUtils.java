package cn.muye.log.base;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.utils.StringUtil;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.base.service.LogInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Jelynn on 2017/8/11.
 */
@Service
public class LogInfoUtils implements ApplicationContextAware {

    public static ApplicationContext applicationContext;


    public static Long info(String deviceId, ModuleEnums module, LogType logType, String message) {
        return log(LogLevel.INFO, module, deviceId, logType, message);
    }

    public static Long warn(String deviceId, ModuleEnums module, LogType logType, String message) {
        return log(LogLevel.WARNING, module, deviceId, logType, message);
    }

    public static Long error(String deviceId, ModuleEnums module, LogType logType, String message) {
        return log(LogLevel.ERROR, module, deviceId, logType, message);
    }

    /**
     * 记录警告处理日志
     *
     * @param id
     * @param handlePerson
     */
    public static void handleWarning(Long id, String handlePerson) {
        LogInfoService logInfoService = applicationContext.getBean(LogInfoService.class);
        LogInfo logInfo = new LogInfo();
        logInfo.setId(id);
        logInfo.setHandlePerson(handlePerson);
        logInfo.setHandleTime(new Date());
        logInfoService.update(logInfo);
    }

    private static Long log(LogLevel level, ModuleEnums module, String deviceId, LogType logType, String message) {

        LogInfo logInfo = new LogInfo();
        logInfo.setDeviceId(deviceId);
        logInfo.setLogLevel(level.getName());
        logInfo.setModule(module.getModuleId());
        logInfo.setLogType(logType.getName());
        logInfo.setMessage(message);

        logInfo.setCreateTime(new Date());
        logInfo.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(deviceId);
        if (currentMap != null && !StringUtil.isNullOrEmpty(currentMap.getMessageText())) {
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            Integer currentMapCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
            if (currentMapCode != null && currentMapCode == 0) {
                String mapData = object.getString(TopicConstants.DATA);
                JSONObject mapObject = JSON.parseObject(mapData);
                logInfo.setMapName(mapObject.getString(TopicConstants.MAP_NAME));
                logInfo.setSceneName(mapObject.getString(TopicConstants.SCENE_NAME));
            }
        }

        LogInfoService logInfoService = applicationContext.getBean(LogInfoService.class);
        logInfoService.save(logInfo);
        return logInfo.getId();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LogInfoUtils.applicationContext = applicationContext;
    }
}
