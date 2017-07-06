package cn.muye.dispatch.controller;

import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.WhereRequest;
import cn.mrobot.utils.ajax.AjaxResponse;
import cn.muye.base.bean.AjaxResult;
import cn.muye.dispatch.service.*;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by Jelynn on 2017/6/14.
 */
@Controller
public class MissionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MissionController.class);

	@Autowired
	private MissionItemService missionItemService;
	@Autowired
	private MissionService missionService;
	@Autowired
	private MissionListService missionListService;

	//missionItem-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/node"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_u')")
	public AjaxResponse saveOrUpdateMissionNode(@RequestBody MissionItem missionItem, HttpServletRequest request) throws Exception {
		try {
			String missionNodeName = missionItem.getName();
			MissionItem missionItemDB = missionItemService.findByName(missionNodeName);
			if (missionItemDB != null && !missionItemDB.getId().equals(missionItem.getId())) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务节点！");
			}

			if (missionItem.getId() != null) {
				missionItem.setUpdateTime(new Date());
				missionItemService.update(missionItem);
			} else {
				missionItemService.save(missionItem);
			}
			return AjaxResponse.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/mission/node/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_d')")
	public AjaxResponse deleteMissionNode(@PathVariable Long id, HttpServletRequest request) throws Exception {
		AjaxResponse resp;
		try {
			if (id == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "参数错误");
			}
			MissionItem missionItemDB = missionItemService.get(id);
			if (missionItemDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionItemService.delete(missionItemDB);
			return AjaxResponse.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/mission/node"}, method = RequestMethod.GET)
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	@ResponseBody
	public AjaxResult pageMissionNode(HttpServletRequest request, WhereRequest whereRequest) {
		try {
			List<MissionItem> missionItemList = missionItemService.list(whereRequest);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);

			PageInfo<MissionItem> page = new PageInfo<MissionItem>();
			PageHelper.startPage(pageNo, pageSize);
			//用PageInfo对结果进行包装
			page.setList(missionItemList);
			return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = {"dispatch/mission/node/list"}, method = RequestMethod.GET)
	@ResponseBody
	public List listMissionNode(HttpServletRequest request) {
		try {
			return missionItemService.list();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//mission-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/chain"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_u')")
	public AjaxResponse saveOrUpdateMissionChain(@RequestBody Mission mission, HttpServletRequest request) throws Exception {
		AjaxResponse resp;
		try {
			String missionChainName = mission.getName();
			Mission missionDB = missionService.findByName(missionChainName);
			if (missionDB != null && !missionDB.getId().equals(mission.getId())) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务串！");
			}

			if (mission.getId() != null) {
				mission.setUpdateTime(new Date());
				missionService.update(mission);
			} else {
				missionService.save(mission);
			}
			resp = AjaxResponse.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
		return resp;
	}

	@RequestMapping(value = {"dispatch/mission/chain/bindNode"}, method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_u')")
	public AjaxResponse bindMissionNode(@RequestParam("missionChainId") Long missionChainId,
										@RequestParam("bingString") String bingString,
										HttpServletRequest request) throws Exception {
		AjaxResponse resp;
		try {
			Mission missionDB = missionService.get(missionChainId);
			if (missionDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "任务串不存在！");
			}

			List<Long> bindList = JSON.parseArray(bingString, Long.class);

			missionDB.setUpdateTime(new Date());
			missionService.update(missionDB, bindList);
			resp = AjaxResponse.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
		return resp;
	}

	@RequestMapping(value = {"dispatch/mission/chain/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_d')")
	public AjaxResponse deleteMissionChain(@PathVariable long id, HttpServletRequest request) throws Exception {
		AjaxResponse resp;
		try {
			Mission missionDB = missionService.get(id);
			if (missionDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionService.delete(missionDB);
			return AjaxResponse.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/mission/chain"}, method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_r')")
	public AjaxResult pageMissionChain(HttpServletRequest request, WhereRequest whereRequest) {
		try {
			List<Mission> missionList = missionService.list(whereRequest);

			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			//用PageInfo对结果进行包装
			PageInfo<Mission> page = new PageInfo<Mission>(missionList);
			return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = {"dispatch/mission/chain/list"}, method = RequestMethod.GET)
	@ResponseBody
	public List listMissionChain(HttpServletRequest request) {
		try {
			List<Mission> missionList = missionService.list();
			return missionList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//missionList-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/main"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionmain_u')")
	public AjaxResult saveOrUpdateMissionMain(@RequestBody MissionList missionList, HttpServletRequest request) throws Exception {
		try {
			String missionMainName = missionList.getName();
			MissionList missionListDB = missionListService.findByName(missionMainName);
			if (missionListDB != null && !missionListDB.getId().equals(missionList.getId())) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务串！");
			}

			if (missionList.getId() != null) {
				missionList.setUpdateTime(new Date());
				missionListService.update(missionList);
			} else {
				missionListService.save(missionList);
			}
			return AjaxResult.success();

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/mission/main/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionmain_d')")
	public AjaxResult deleteMissionMain(@PathVariable long id, HttpServletRequest request) throws Exception {
		try {
			MissionList missionListDB = missionListService.get(id);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionListService.delete(missionListDB);
			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/mission/main/test"}, method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionmain_r')")
	public AjaxResult pageMissionMain(HttpServletRequest request, WhereRequest whereRequest) {
		try {
			List<MissionList> missionListList = missionListService.list(whereRequest);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			PageInfo<MissionList> page = new PageInfo<>(missionListList);
			return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	@RequestMapping(value = {"dispatch/mission/main/list"}, method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult listMissionMain(HttpServletRequest request) {
		try {
			List<MissionList> missionListList = missionListService.list();
			return AjaxResult.success(missionListList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(-1, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/mission/main/bindChain"}, method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_u')")
	public AjaxResult bindMissionChain(@RequestParam("missionMainId") Long missionMainId,
									   @RequestParam("bingString") String bingString,
									   HttpServletRequest request) throws Exception {
		try {
			MissionList missionListDB = missionListService.get(missionMainId);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "总任务不存在！");
			}

			List<Long> bindList = JSON.parseArray(bingString, Long.class);

			missionListDB.setUpdateTime(new Date());
			missionListService.update(missionListDB, bindList);
			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
	}

//	//把类转换成entry返回给前端，解耦和
//	private Map objectToEntry(MissionItem missionNode) {
//		Map entry = new HashMap();
//		entry.put("id", missionNode.getId());
//		entry.put("name", missionNode.getName());
//		entry.put("description", missionNode.getDescription());
//		entry.put("featureItem", objectToEntry(missionNode.getFeatureItem()));
//		entry.put("featureItemType", objectToEntry(missionNode.getFeatureItemType()));
//		entry.put("repeatCount", missionNode.getRepeatCount());
//		entry.put("intervalTime", missionNode.getIntervalTime());
//		entry.put("createTime", missionNode.getCreateTime());
//		entry.put("updateTime", missionNode.getUpdateTime());
//		entry.put("priority", missionNode.getPriority());
//		entry.put("data", missionNode.getData());
//		return entry;
//	}
//
//	//把类转换成entry返回给前端，解耦和
//	private Map objectToEntry(FeatureItem featureItem) {
//		Map entry = new HashMap();
//		entry.put("id", featureItem.getId());
//		entry.put("name", featureItem.getName());
//		entry.put("value", featureItem.getValue());
//		entry.put("description", featureItem.getDescription());
//		return entry;
//	}
//
//	//把类转换成entry返回给前端，解耦和
//	private Map objectToEntry(FeatureItemType featureItemType) {
//		Map entry = new HashMap();
//		entry.put("id", featureItemType.getId());
//		entry.put("name", featureItemType.getName());
//		entry.put("value", featureItemType.getValue());
//		entry.put("description", featureItemType.getDescription());
//		entry.put("dataModel", featureItemType.getDataModel());
//		return entry;
//	}

}
