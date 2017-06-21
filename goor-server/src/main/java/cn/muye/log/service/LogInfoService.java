package cn.muye.log.service;

import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.WhereRequest;
import cn.muye.bean.SearchConstants;
import cn.muye.log.mapper.LogInfoMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional
public class LogInfoService {

	@Autowired
	private LogInfoMapper logInfoMapper;

	public void save(LogInfo logInfo) {
		logInfoMapper.save(logInfo);
	}

	public List<LogInfo> lists(WhereRequest whereRequest) {
		List<LogInfo> logInfoList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object logLevel = map.get(SearchConstants.SEARCH_LOG_LEVEL);
			Object logType = map.get(SearchConstants.SEARCH_LOG_TYPE);
			Object deviceId = map.get(SearchConstants.SEARCH_DEVICE_ID);

			logInfoList = logInfoMapper.lists(deviceId, logLevel, logType, beginDate, endDate);
		}
		return logInfoList;

	}
}
