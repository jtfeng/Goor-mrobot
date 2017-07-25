package cn.muye.log.base.service;

import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.muye.log.base.mapper.LogInfoMapper;

import java.util.ArrayList;
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

    void save(LogInfo logInfo);

    List<LogInfo> lists(WhereRequest whereRequest, Long storeId);
}
