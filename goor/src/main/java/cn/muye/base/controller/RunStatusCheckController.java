package cn.muye.base.controller;

import cn.mrobot.bean.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin
@Slf4j
public class RunStatusCheckController {

	@RequestMapping(value = "checkOnLine")
	@ResponseBody
	public AjaxResult checkOnLine(HttpServletRequest request) {
		log.info("goor运行正常");
		return AjaxResult.success();
	}
}
