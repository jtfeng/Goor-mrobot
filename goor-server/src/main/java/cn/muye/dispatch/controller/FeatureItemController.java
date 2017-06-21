package cn.muye.dispatch.controller;

import cn.mrobot.bean.misssion.FeatureItem;
import cn.mrobot.utils.WhereRequest;
import cn.muye.bean.AjaxResult;
import cn.muye.bean.SearchConstants;
import cn.muye.dispatch.service.FeatureItemService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class FeatureItemController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureItemTypeController.class);

	@Autowired
	private FeatureItemService featureItemService;

	/**
	 * 获取featureItem所有列表
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "dispatch/feature/item", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult listFeatureItem(HttpServletRequest request, WhereRequest whereRequest) throws Exception {
		try {
			List<FeatureItem> featureItemList = featureItemService.list(whereRequest);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;

			PageHelper.startPage(pageNo, pageSize);
				PageHelper.startPage(pageNo, pageSize);
				//用PageInfo对结果进行包装
				PageInfo<FeatureItem> page = new PageInfo<FeatureItem>(featureItemList);
				return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = "dispatch/feature/item", method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult saveFeatureItem(@RequestBody FeatureItem featureItem, HttpServletRequest request) throws Exception {
		try {
			if (featureItem.getName() == null) {
				return AjaxResult.failed("功能名不能为空");
			}
			if (featureItem.getValue() == null) {
				return AjaxResult.failed("功能值不能为空");
			}
			List<FeatureItem> featureItemList = featureItemService.validate(featureItem.getName(), featureItem.getValue());
			if(featureItemList.size() >= 1){
				return AjaxResult.failed("功能名称或者值已经存在");
			}

			featureItemService.save(featureItem);
			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}
}
