package cn.muye.log.base.service.impl;

import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.log.base.mapper.LogInfoMapper;
import cn.muye.log.base.service.LogInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Jelynn on 2017/7/24.
 */
@Service
@Transactional
public class LogInfoServiceImpl implements LogInfoService {

    @Autowired
    private LogInfoMapper logInfoMapper;

    @Override
    public LogInfo findById(Long id) {
        return logInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(LogInfo logInfo) {
        logInfoMapper.insert(logInfo);
    }

    @Override
    public List<LogInfo> lists(WhereRequest whereRequest, Long storeId) {
        Example example = new Example(LogInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (whereRequest.getQueryObj() != null) {
            JSONObject object = JSON.parseObject(whereRequest.getQueryObj());
            String deviceId = object.getString(SearchConstants.SEARCH_DEVICE_ID);
            String logType = object.getString(SearchConstants.SEARCH_LOG_TYPE);
            String logLevel = object.getString(SearchConstants.SEARCH_LOG_LEVEL);
            Integer module = object.getInteger(SearchConstants.SEARCH_MODULE);
            String mapName = object.getString(SearchConstants.SEARCH_MAP_NAME);
            String sceneName = object.getString(SearchConstants.SEARCH_SCENE_NAME);

            if (!StringUtil.isNullOrEmpty(deviceId)) {
                criteria.andCondition("DEVICE_ID = '" + deviceId + "'");
            }
            if (!StringUtil.isNullOrEmpty(logType)) {
                criteria.andCondition("LOG_TYPE = '" + logType + "'");
            }
            if (!StringUtil.isNullOrEmpty(logLevel)) {
                criteria.andCondition("LOG_LEVEL ='" + logLevel + "'");
            }
            if (null != module) {
                criteria.andCondition("MODULE = " + module);
            }
            if (!StringUtil.isNullOrEmpty(mapName)) {
                criteria.andCondition("MAP_NAME = '" + mapName+"'");
            }
            if (!StringUtil.isNullOrEmpty(sceneName)) {
                criteria.andCondition("SCENE_NAME = '" + sceneName+"'");
            }
        }
        criteria.andCondition("STORE_ID=" + storeId);
        example.setOrderByClause("CREATE_TIME DESC");
        return logInfoMapper.selectByExample(example);
    }

    @Override
    public int update(LogInfo logInfo) {
        return logInfoMapper.updateByPrimaryKeySelective(logInfo);
    }
}
