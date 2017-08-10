package cn.muye.log.state.service;

import cn.mrobot.bean.mission.MissionState;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.state.*;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.mrobot.bean.state.enums.NavigationType;
import cn.mrobot.bean.state.enums.StateFieldEnums;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.StringUtil;
import cn.muye.area.map.bean.StateDetail;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.service.missiontask.MissionFuncsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析状态机数据类
 * Created by Jelynn on 2017/7/18.
 */
public interface StateCollectorService {


    void handleStateCollector(StateCollectorResponse stateCollectorResponse) throws Exception;

    /**
     * 获取当前状态，只保留触发的状态
     *
     * @param code
     * @return
     */
    List<StateDetail> getCurrentTriggeredState(String code) throws IllegalAccessException;

    /**
     * 获取底盘当前状态，只保留触发的状态
     *
     * @param code
     * @return
     */
    List<StateDetail> getCurrentBaseState(boolean toDatabase, String code) throws IllegalAccessException;


    /**
     * 添加自动导航状态
     *
     * @param code
     * @return
     */
    List<StateDetail> getCurrentNavigationState(String code);

    /**
     * 添加任务状态日志
     *
     * @param code
     * @return
     */
    List<StateDetail> collectTaskLog(String code);
}
