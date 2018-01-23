package cn.muye.log.alert.service.impl;

import cn.mrobot.bean.log.alert.LogAlert;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.log.alert.service.LogAlertService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by ray.fu on 2017/9/11.
 */
@Service
public class LogAlertServiceImpl extends BaseServiceImpl<LogAlert> implements LogAlertService {


    @Override
    public LogAlert findLastSameAlert(String robotCode, String message, String alertCode) {
        Example example = new Example(LogAlert.class);
        example.createCriteria().andEqualTo("robotCode", robotCode)
                .andEqualTo("description", message)
                .andEqualTo("alertCode",alertCode);
        example.setOrderByClause("CREATE_TIME DESC");
        List<LogAlert> logAlertList = myMapper.selectByExample(example);
        return logAlertList.size() > 0 ? logAlertList.get(0) : null;
    }
}
