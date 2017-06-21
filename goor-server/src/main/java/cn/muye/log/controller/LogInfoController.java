package cn.muye.log.controller;

import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.bean.log.LogLevel;
import cn.mrobot.bean.log.LogType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.SearchConstants;
import cn.muye.log.service.LogInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 16:38
 * Describe:
 * Version:1.0
 */
@Controller
public class LogInfoController {

	@Autowired
	private LogInfoService logInfoService;

	@RequestMapping(value = "log/list", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResult list(WhereRequest whereRequest) {
		List<LogInfo> logInfoList = logInfoService.lists(whereRequest);


		if (whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Integer pageNo =Integer.parseInt(map.get(SearchConstants.SEARCH_PAGE_NO).toString());
			Integer pageSize =Integer.parseInt(map.get(SearchConstants.SEARCH_PAGE_SIZE).toString());

			pageNo = pageNo == null ? 1:pageNo;
			pageSize = pageSize == null?10:pageSize;
			PageHelper.startPage(pageNo, pageSize);
			List<LogInfo> lists = new ArrayList<>();
			for(int i=0; i < logInfoList.size(); i ++){
				lists.add(objectToEntry(logInfoList.get(i)));  //处理枚举类型
			}
			//用PageInfo对结果进行包装
			PageInfo<LogInfo> page = new PageInfo<LogInfo>(lists);
			return AjaxResult.success(page);
		}

		return AjaxResult.success(JSONArray.toJSONString(logInfoList));
//		LogInfoUtils.info("dasdf", LogType.INFO_EXECUTE_TASK, "asdfasdf");
//		LogInfoUtils.warn("dasdf", LogType.INFO_EXECUTE_TASK, "asdfasdf");
//		LogInfoUtils.error("dasdf", LogType.INFO_EXECUTE_TASK, "asdfasdf");
//		return AjaxResult.success();
	}

	private LogInfo objectToEntry(LogInfo logInfo) {
		if(LogLevel.getLogLevel(logInfo.getLogLevelName()) != null){
			logInfo.setLogLevelName(LogLevel.getLogLevel(logInfo.getLogLevelName()).getValue());
		}
		if(LogType.getLogType(logInfo.getLogTypeName()) != null){
			logInfo.setLogTypeName(LogType.getLogType(logInfo.getLogTypeName()).getValue());
		}

		return logInfo;
	}
}
