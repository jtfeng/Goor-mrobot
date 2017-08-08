package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.base.PubData;
import cn.mrobot.bean.base.PubX86HeartBeat;
import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.cache.CacheInfoManager;
import com.alibaba.fastjson.JSON;
import edu.wpi.rail.jrosbridge.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@CrossOrigin
@Slf4j
public class RunStatusCheckController {

	@RequestMapping(value = "checkOnLine")
	@ResponseBody
	public String checkOnLine(HttpServletRequest request) {
		log.info("goor运行正常");
		return "1";
	}

	@RequestMapping(value = "checkRosBridge")
	@ResponseBody
	public String checkRosBridge(HttpServletRequest request) {
		if ((System.currentTimeMillis() - CacheInfoManager.getX86MissionTopicHeartCheckCache()) > TopicConstants.CHECK_ROS_BRIDGE_MAX) {
			return "0";
		}
		return "1";
	}

	@RequestMapping(value = "checkRosBridge111")
	@ResponseBody
	public String checkRosBridge111(HttpServletRequest request) {
		return "0";
	}
}
