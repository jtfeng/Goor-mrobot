package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
@Slf4j
public class RunStatusCheckController {

	@RequestMapping(value = "checkOnLine")
	@ResponseBody
	public int checkOnLine(HttpServletRequest request) {
		log.info("goor-server运行正常");
		return 1;
	}

	@RequestMapping(value = "checkRosBridge")
	@ResponseBody
	public int checkRosBridge(HttpServletRequest request) {
//		if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_ROS_BRIDGE_MAX) {
//			return "1";
//		}
		return 1;
	}

	@RequestMapping(value = "getServerVersion")
	@ResponseBody
	public AjaxResult getVersion(HttpServletRequest request) {
		return AjaxResult.success("Ver0.0.0.1003");
	}

	@RequestMapping(value = "checkRosBridge111")
	@ResponseBody
	public int checkRosBridge111(HttpServletRequest request) {
//		if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_ROS_BRIDGE_MAX) {
//			return "1";
//		}
		return 0;
	}

}
