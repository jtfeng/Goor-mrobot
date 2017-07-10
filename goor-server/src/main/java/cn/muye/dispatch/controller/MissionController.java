package cn.muye.dispatch.controller;

import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.WhereRequest;
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
	@RequestMapping(value = {"dispatch/missionItem"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionitem_u')")
	public AjaxResult saveOrUpdateMissionItem(@RequestBody MissionItem missionItem, HttpServletRequest request) throws Exception {
		try {
			String missionItemName = missionItem.getName();
			MissionItem missionItemDB = missionItemService.findByName(missionItemName);
			if (missionItemDB != null && !missionItemDB.getId().equals(missionItem.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务节点！");
			}

			if (missionItem.getId() != null) {
				missionItem.setUpdateTime(new Date());
				missionItemService.update(missionItem);
			} else {
				missionItem.setCreateTime(new Date());
				missionItemService.save(missionItem);
			}
			return AjaxResult.success(missionItem,"成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/missionItem/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionitem_d')")
	public AjaxResult deleteMissionItem(@PathVariable Long id, HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			if (id == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误");
			}
			MissionItem missionItemDB = missionItemService.get(id);
			if (missionItemDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要删除的对象不存在");
			}

			missionItemService.delete(missionItemDB);
			return AjaxResult.success("删除成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/missionItem"}, method = RequestMethod.GET)
//	@PreAuthorize("hasAuthority('mrc_missionitem_r')")
	@ResponseBody
	public AjaxResult pageMissionItem(HttpServletRequest request, WhereRequest whereRequest) {
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

	@RequestMapping(value = {"dispatch/missionItem/list"}, method = RequestMethod.GET)
	@ResponseBody
	public List listMissionItem(HttpServletRequest request) {
		try {
			return missionItemService.list();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//mission-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_u')")
	public AjaxResult saveOrUpdateMission(@RequestBody Mission mission, HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			String missionName = mission.getName();
			Mission missionDB = missionService.findByName(missionName);
			if (missionDB != null && !missionDB.getId().equals(mission.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}

			String msg = "";
			if (mission.getId() != null) {
				mission.setUpdateTime(new Date());
				missionService.update(mission);
				msg = "修改成功";
			} else {
				mission.setCreateTime(new Date());
				missionService.save(mission);
				msg = "新增成功";
			}
			resp = AjaxResult.success(mission,msg);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
		return resp;
	}

	@RequestMapping(value = {"dispatch/mission/bindItem"}, method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_u')")
	public AjaxResult bindMissionItem(@RequestParam("missionId") Long missionId,
										@RequestParam("bindString") String bindString,
//										@RequestParam(value = "missionItems", required = false) List<MissionItem> missionItems,
										HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			Mission missionDB = missionService.get(missionId);
			if (missionDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "任务串不存在！");
			}

			List<Long> bindList = JSON.parseArray(bindString, Long.class);
			missionDB.setUpdateTime(new Date());
			missionService.update(missionDB, bindList);
//			missionService.update(missionDB, missionItems);
			resp = AjaxResult.success("成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
		return resp;
	}

	@RequestMapping(value = {"dispatch/mission/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_d')")
	public AjaxResult deleteMission(@PathVariable long id, HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			Mission missionDB = missionService.get(id);
			if (missionDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要删除的对象不存在");
			}

			missionService.delete(missionDB);
			return AjaxResult.success("删除成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/mission"}, method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_r')")
	public AjaxResult pageMission(HttpServletRequest request, WhereRequest whereRequest) {
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

	@RequestMapping(value = {"dispatch/mission/list"}, method = RequestMethod.GET)
	@ResponseBody
	public List listMission(HttpServletRequest request) {
		try {
			List<Mission> missionList = missionService.list();
			return missionList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//missionList-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/missionList"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionList_u')")
	public AjaxResult saveOrUpdateMissionList(@RequestBody MissionList missionList, HttpServletRequest request) throws Exception {
		try {
			String missionListName = missionList.getName();
			MissionList missionListDB = missionListService.findByName(missionListName);
			if (missionListDB != null && !missionListDB.getId().equals(missionList.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}

			String msg = "";
			if (missionList.getId() != null) {
				missionList.setUpdateTime(new Date());
				missionListService.update(missionList);
				msg = "修改成功";
			} else {
				missionList.setCreateTime(new Date());
				missionListService.save(missionList);
				msg = "新建成功";
			}
			return AjaxResult.success(msg);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/missionList/{id}"}, method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionList_d')")
	public AjaxResult deleteMissionList(@PathVariable long id, HttpServletRequest request) throws Exception {
		try {
			MissionList missionListDB = missionListService.get(id);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要删除的对象不存在");
			}

			missionListService.delete(missionListDB);
			return AjaxResult.success("删除成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "可能在其他地方被使用");
		}
	}

	@RequestMapping(value = {"dispatch/missionList"}, method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionList_r')")
	public AjaxResult pageMissionList(HttpServletRequest request, WhereRequest whereRequest) {
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

	@RequestMapping(value = {"dispatch/missionList/list"}, method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult listMissionList(HttpServletRequest request) {
		try {
			List<MissionList> missionListList = missionListService.list();
			return AjaxResult.success(missionListList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
	}

	@RequestMapping(value = {"dispatch/missionList/bindMission"}, method = RequestMethod.POST)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_u')")
	public AjaxResult bindMission(@RequestParam("missionListId") Long missionListId,
									   @RequestParam("bindString") String bindString,
//									   @RequestParam("missions") List<Mission> missions,
									   HttpServletRequest request) throws Exception {
		try {
			MissionList missionListDB = missionListService.get(missionListId);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "总任务不存在！");
			}

			List<Long> bindList = JSON.parseArray(bindString, Long.class);

			missionListDB.setUpdateTime(new Date());
			missionListService.update(missionListDB, bindList);
//			missionListService.update(missionListDB, missions);
			return AjaxResult.success("成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
	}

//	//把类转换成entry返回给前端，解耦和
//	private Map objectToEntry(MissionItem missionItem) {
//		Map entry = new HashMap();
//		entry.put("id", missionItem.getId());
//		entry.put("name", missionItem.getName());
//		entry.put("description", missionItem.getDescription());
//		entry.put("featureItem", objectToEntry(missionItem.getFeatureItem()));
//		entry.put("featureItemType", objectToEntry(missionItem.getFeatureItemType()));
//		entry.put("repeatCount", missionItem.getRepeatCount());
//		entry.put("intervalTime", missionItem.getIntervalTime());
//		entry.put("createTime", missionItem.getCreateTime());
//		entry.put("updateTime", missionItem.getUpdateTime());
//		entry.put("priority", missionItem.getPriority());
//		entry.put("data", missionItem.getData());
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
