package cn.muye.i18n.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.muye.i18n.service.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2018/2/24
 * Time: 09:24
 * Describe:
 * Version:1.0
 */
@RestController
public class I18nController {

	private static final Logger LOGGER = LoggerFactory.getLogger(I18nController.class);

//	@Autowired
//	private SessionLocaleResolver sessionLocaleResolver;

	//系统支持的语言类型
	private static final List<String> SUPPORT_LANG = new ArrayList<String>(
		Arrays.asList("zh","en")
	);

	@RequestMapping("/services/changeLanguage")
	public AjaxResult changeSessionLanguage(HttpServletRequest request, HttpServletResponse response, String lang, String country){
		LOGGER.info("切换语音,lang={},国家={}",lang,country);
		if(lang == null || !SUPPORT_LANG.contains(lang.toLowerCase())) {
			LOGGER.info("未识别的语言，使用默认语言。");
			lang = Constant.DEFAULT_LANG;
		}
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		//代码中即可通过以下方法进行语言设置
		localeResolver.setLocale(request, response, new Locale(lang, country.toUpperCase()));
		return AjaxResult.success();
	}

	@Autowired
	private LocaleMessageSourceService localeMessageSourceService;

	@RequestMapping("/services/testLanguage")
	public AjaxResult testLanguage(HttpServletRequest request){
		String message = localeMessageSourceService.getMessage("test");
		LOGGER.info(message);
		return AjaxResult.success(message);
	}

}
