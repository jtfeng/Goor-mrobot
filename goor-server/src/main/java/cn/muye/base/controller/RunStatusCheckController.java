package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.VersionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping(value = "checkRosBridge111")
	@ResponseBody
	public int checkRosBridge111(HttpServletRequest request) {
//		if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_ROS_BRIDGE_MAX) {
//			return "1";
//		}
		return 0;
	}

	/**
	 * 获取server版本号
	 *
	 * @return
	 */
	@RequestMapping(value = "services/getServerV", method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult getGoorServerVersion() {
		return AjaxResult.success(VersionConstants.VERSION_NOAH_GOOR_SERVER);
	}
}
