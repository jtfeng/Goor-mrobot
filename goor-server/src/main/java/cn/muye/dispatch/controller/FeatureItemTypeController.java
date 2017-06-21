package cn.muye.dispatch.controller;

import cn.mrobot.bean.misssion.FeatureItem;
import cn.mrobot.bean.misssion.FeatureItemType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.bean.AjaxResult;
import cn.muye.dispatch.service.FeatureItemService;
import cn.muye.dispatch.service.FeatureItemTypeService;
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

	@RequestMapping(value = "dispatch/feature/itemtype", method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult saveFeatureItemType(@RequestBody FeatureItemType featureItemType, HttpServletRequest request) throws Exception {
		try {
			if (featureItemType.getName() == null) {
				return AjaxResult.failed("功能类型名称不能为空");
			}
			if (featureItemType.getValue() == null) {
				return AjaxResult.failed("功能类型值不能为空");
			}
			if (featureItemType.getFeatureItemId() != null) {
				if(featureItemService.get(featureItemType.getFeatureItemId()) == null){
					return AjaxResult.failed("功能不存在");
				}
			}

			List<FeatureItemType> featureItemTypeList = featureItemTypeService.validate(featureItemType.getName(), featureItemType.getValue());
			if(featureItemTypeList.size() >= 1){
				return AjaxResult.failed("功能类型名称或者值已经存在");
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
