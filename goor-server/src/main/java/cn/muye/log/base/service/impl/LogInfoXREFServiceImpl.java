package cn.muye.log.base.service.impl;

import cn.mrobot.bean.log.LogInfoXREF;
import cn.mrobot.bean.state.enums.ModuleEnums;
import cn.muye.log.base.mapper.LogInfoXREFMapper;
import cn.muye.log.base.service.LogInfoXREFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/11.
 */
@Service
public class LogInfoXREFServiceImpl implements LogInfoXREFService {

    @Autowired
    private LogInfoXREFMapper logInfoXREFMapper;

    @Override
    public void save(ModuleEnums module, Long id, Long logInfoId) {
        LogInfoXREF logInfoXREF = new LogInfoXREF();
        logInfoXREF.setForeignKey(module.getModuleName() + "_" + id);
        logInfoXREF.setLogInfoId(logInfoId);
        logInfoXREFMapper.insert(logInfoXREF);
    }

    @Override
    public LogInfoXREF getByKey(ModuleEnums module, Long id) {
        String key = module.getModuleName() + "_" + id;
        Example example = new Example(LogInfoXREF.class);
        example.createCriteria().andCondition("FOREIGN_KEY='" + key + "'");
        List<LogInfoXREF> logInfoXREFList = logInfoXREFMapper.selectByExample(example);
        if (logInfoXREFList == null || logInfoXREFList.size() <= 0)
            return null;
        return logInfoXREFList.get(0);
    }
}
