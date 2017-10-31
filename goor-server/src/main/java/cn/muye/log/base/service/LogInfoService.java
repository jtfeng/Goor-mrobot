package cn.muye.log.base.service;

import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.WhereRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 10:36
 * Describe:
 * Version:1.0
 */
public interface LogInfoService {

    LogInfo findById(Long id);

    void save(LogInfo logInfo);

    List<LogInfo> lists(WhereRequest whereRequest, Long storeId);

    int update(LogInfo logInfo);

    void delete(List<LogInfo> logInfoList);
}
