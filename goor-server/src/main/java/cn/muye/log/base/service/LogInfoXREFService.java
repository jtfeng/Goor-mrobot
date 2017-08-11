package cn.muye.log.base.service;

import cn.mrobot.bean.log.LogInfoXREF;
import cn.mrobot.bean.state.enums.ModuleEnums;

/**
 * Created by Jelynn on 2017/8/11.
 */
public interface LogInfoXREFService{

    void save(ModuleEnums module, Long id, Long logInfoId);

    LogInfoXREF getByKey(ModuleEnums module, Long id);
}
