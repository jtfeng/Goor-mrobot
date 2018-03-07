package cn.muye.dispatch.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.mission.FeatureItemType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.dispatch.service.FeatureItemService;
import cn.muye.dispatch.service.FeatureItemTypeService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/10
 * Time: 13:02
 * Describe:
 * Version:1.0
 */
@Controller
public class FeatureItemTypeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureItemTypeController.class);

	@Autowired
	private FeatureItemTypeService featureItemTypeService;
	@Autowired
	private FeatureItemService featureItemService;
	@Autowired
	private LocaleMessageSourceService localeMessageSourceService;

	@RequestMapping(value = "dispatch/feature/itemtype", method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult saveFeatureItemType(@RequestBody FeatureItemType featureItemType, HttpServletRequest request) throws Exception {
		try {
			if (featureItemType.getName() == null) {
				return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_dispatch_controller_FeatureItemTypeController_java_GNLXMCBNWK"));
			}
			if (featureItemType.getValue() == null) {
				return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_dispatch_controller_FeatureItemTypeController_java_GNLXZBNWK"));
			}
			if (featureItemType.getFeatureItemId() != null) {
				if(featureItemService.get(featureItemType.getFeatureItemId()) == null){
					return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_dispatch_controller_FeatureItemTypeController_java_GNBCZ"));
				}
			}

			List<FeatureItemType> featureItemTypeList = featureItemTypeService.validate(featureItemType.getName(), featureItemType.getValue());
			if(featureItemTypeList.size() >= 1){
				return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_dispatch_controller_FeatureItemTypeController_java_GNLXMCHZZYJCZ"));
			}

			featureItemTypeService.save(featureItemType);
			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = "dispatch/feature/itemtype/{id}", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult getFeatureItemType(@PathVariable long id, HttpServletRequest request) throws Exception {
		try {
			FeatureItemType featureItemType = featureItemTypeService.get(id);
			return AjaxResult.success(featureItemType);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = "dispatch/feature/itemtype", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult listFeatureItemType(HttpServletRequest request, WhereRequest whereRequest) throws Exception {
		try {
			List<FeatureItemType> list = featureItemTypeService.list(whereRequest);
			return AjaxResult.success(list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}
}
