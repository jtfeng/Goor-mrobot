package cn.muye.dispatch.controller;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.DateTimeUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.area.point.service.PointService;
import cn.muye.dispatch.service.*;
import cn.muye.service.missiontask.MissionFuncsService;
import cn.muye.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

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
	@Autowired
	private PointService pointService;
	@Autowired
	private RobotService robotService;
	@Autowired
	MissionFuncsService missionFuncsService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private int TWO_MINUTES_MILLISECOND = 120000;

	//missionItem-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/missionItem"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionitem_u')")
	public AjaxResult saveOrUpdateMissionItem(@RequestBody MissionItem missionItem, HttpServletRequest request) throws Exception {
		try {
			//TODO 从session取当前切换的门店ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

			String missionItemName = missionItem.getName();
			MissionItem missionItemDB = missionItemService.findByName(missionItemName,storeId);
			if (missionItemDB != null && !missionItemDB.getId().equals(missionItem.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务节点！");
			}

			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			missionItem.setSceneId(scene.getId());

			String msg = "";

			if (missionItem.getId() != null) {
				missionItem.setUpdateTime(new Date());
				missionItemService.update(missionItem);
				msg = "修改成功";
			} else {
				missionItem.setStoreId(storeId);
				missionItem.setCreateTime(new Date());
				missionItemService.save(missionItem);
				msg = "新增成功";
			}
			return AjaxResult.success(missionItem,msg);
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
			//TODO 从session取当前切换的门店ID
			MissionItem missionItemDB = missionItemService.get(id,SearchConstants.FAKE_MERCHANT_STORE_ID);
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
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);

			//TODO 从session获取切换的门店ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			List<MissionItem> missionItemList = missionItemService.list(whereRequest,storeId);
			PageInfo<MissionItem> page = new PageInfo<MissionItem>(missionItemList);

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
			//TODO 从session取当前切换门店的ID
			return missionItemService.list(SearchConstants.FAKE_MERCHANT_STORE_ID);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			String missionName = mission.getName();
			Mission missionDB = missionService.findByName(missionName,storeId);
			if (missionDB != null && !missionDB.getId().equals(mission.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}

			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			mission.setSceneId(scene.getId());

			String msg = "";
			if (mission.getId() != null) {
				mission.setUpdateTime(new Date());
				missionService.update(mission);
				msg = "修改成功";
			} else {
				mission.setStoreId(storeId);
				//从session取当前切换的sceneId


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

	/**
	 *创建任务，同时创建并关联子任务
	 * @param mission
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"dispatch/mission/full"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_u')")
	public AjaxResult saveOrUpdateMissionFull(@RequestBody Mission mission,HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			String missionName = mission.getName();
			Mission missionDB = missionService.findByName(missionName,storeId);
			if (missionDB != null && !missionDB.getId().equals(mission.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}

			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			mission.setSceneId(scene.getId());

			//校验点是否存在
			Set<MissionItem> missionItemSet = mission.getMissionItemSet();
			if(missionItemSet == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "不能创建空任务！");
			}
			for(MissionItem missionItem : missionItemSet) {
				//跟点相关的指令，需要校验点是否存在
				if(Constant.ORDER_MAP_POINT_RELATE_LIST.contains(missionItem.getFeatureItemId())) {
					String data = missionItem.getData();
					try {
						Long pointId = JSON.parseObject(data).getLong(Constant.ID);
						MapPoint mapPoint = pointService.findById(pointId);
						if(mapPoint == null) {
							return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，点不存在！");
						}
						missionItem.setData(JSON.toJSONString(mapPoint));
					}
					catch (Exception e) {
						return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，数据格式不正确！");
					}
				}
			}
			String msg = "";
			if (mission.getId() != null) {
				msg = "修改成功";
			} else {
				msg = "新增成功";
			}

			missionService.updateFull(mission,missionDB,storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

			Mission missionDB = missionService.get(missionId,storeId);
			if (missionDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "任务串不存在！");
			}

			List<Long> bindList = JSON.parseArray(bindString, Long.class);
			missionDB.setUpdateTime(new Date());

			missionService.update(missionDB, bindList,storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			Mission missionDB = missionService.get(id,storeId);
			if (missionDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要删除的对象不存在");
			}

			missionService.delete(missionDB,storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			List<Mission> missionList = missionService.list(whereRequest,storeId);

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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			List<Mission> missionList = missionService.list(storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			String missionListName = missionList.getName();
			MissionList missionListDB = missionListService.findByName(missionListName,storeId);
			if (missionListDB != null && !missionListDB.getId().equals(missionList.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}

			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			missionList.setSceneId(scene.getId());

			String msg = "";
			if (missionList.getId() != null) {
				missionList.setUpdateTime(new Date());
				missionListService.update(missionList);
				msg = "修改成功";
			} else {
				missionList.setStoreId(storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			MissionList missionListDB = missionListService.get(id,storeId);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要删除的对象不存在");
			}

			missionListService.delete(missionListDB,storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			//如果场景后台切换过，传入场景ID。
			// 当whereRequest含场景ID条件时，则取该场景ID作为过滤条件；若whereRequest不含场景ID条件时，则取出入的场景ID作为过滤条件。
			if(scene != null) {
				whereRequest = getSceneWhereRequest(scene.getId(),whereRequest);
			}

			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			List<MissionList> missionListList = missionListService.list(whereRequest,storeId);

			PageInfo<MissionList> page = new PageInfo<>(missionListList);
			return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	/**
	 * 根据切换的场景构建查询条件
	 * @param whereRequest
	 * @param sceneId 当whereRequest里面sceneId为空时，若为null，则无场景过滤；若有值，使用该值场景过滤；
	 * @return
	 */
	private WhereRequest getSceneWhereRequest(Long sceneId, WhereRequest whereRequest) throws Exception{
		JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
		Object o = jsonObject.get(SearchConstants.SEARCH_SCENE_ID);
		if(o == null && sceneId != null) {
			jsonObject.put(SearchConstants.SEARCH_SCENE_ID, sceneId);
		}
		return whereRequest;
	}

	@RequestMapping(value = {"dispatch/missionList/list"}, method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult listMissionList(HttpServletRequest request) {
		try {
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			List<MissionList> missionListList = missionListService.list(storeId);
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
			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
			MissionList missionListDB = missionListService.get(missionListId,storeId);
			if (missionListDB == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "总任务不存在！");
			}

			List<Long> bindList = JSON.parseArray(bindString, Long.class);

			missionListDB.setUpdateTime(new Date());
			missionListService.update(missionListDB, bindList,storeId);
//			missionListService.update(missionListDB, missions);
			return AjaxResult.success("成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
	}

	/**
	 *创建任务列表，同时创建并关联任务、子任务
	 * @param missionList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"dispatch/missionList/full"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_mission_u')")
	public AjaxResult saveOrUpdateMissionListFull(@RequestBody MissionList missionList, HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			missionList.setSceneId(scene.getId());

			//设置重复次数是1，执行1次
			missionList.setRepeatCount(1);

			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

			//校验任务类型是否正确
			String missionListType = missionList.getMissionListType();
			if(missionListType == null
					|| (!missionListType.equals(Constant.MISSION_LIST_TYPE_PATROL)
					&& !missionListType.equals(Constant.MISSION_LIST_TYPE_CHARGE))){
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,任务类型不正确");
			}

			MissionList missionListDB = null;

			//校验数据库是否重名
			String missionListName = missionList.getName();
			missionListDB = missionListService.findByName(missionListName,storeId);
			if (missionListDB != null && !missionListDB.getId().equals(missionList.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务列表！");
			}

			//取第一个Mission做导航、语音、充电任务参数校验---------------------------------------
			Mission mission = missionList.getMissionList().get(0);

			//校验点是否存在
			Set<MissionItem> missionItemSet = mission.getMissionItemSet();
			if(missionItemSet == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "不能创建空任务！");
			}
			for(MissionItem missionItem : missionItemSet) {
				Long featureItemId = missionItem.getFeatureItemId();
				String data = missionItem.getData();
				//跟点相关的指令，需要校验点是否存在
				if(Constant.ORDER_MAP_POINT_RELATE_LIST.contains(featureItemId)) {
					try {
						Long pointId = JSON.parseObject(data).getLong(Constant.ID);
						MapPoint mapPoint = pointService.findById(pointId);
						if(mapPoint == null) {
							return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，点不存在！");
						}
						missionItem.setData(JSON.toJSONString(mapPoint));
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(),e);
						return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，数据格式不正确！");
					}
				}
				//按时间长度充电任务，点数据校验
				else if(featureItemId.equals(Constant.ORDER_TIME_CHARGE_ID)) {
					JSONObject jsonObject = JSONObject.parseObject(data);
					try {
						JSONObject mapPointTemp =(JSONObject) jsonObject.get(Constant.ORDER_TIME_CHARGE_POINT);
						MapPoint mapPoint = pointService.findById(Long.parseLong(mapPointTemp.get(Constant.ID).toString()));
						if(mapPoint == null) {
							return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，点不存在！");
						}
						missionItem.setData(JSON.toJSONString(mapPoint));
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(),e);
						return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，数据格式不正确！");
					}
				}
			}

			//巡逻任务：还需增加等待任务
			if(missionListType.equals(Constant.MISSION_LIST_TYPE_PATROL)) {
				//TODO 美亚调度写死两个Mission，第一个是导航和语音的mission，第二个是到目标点后等待任务和语音任务（可以没有等待任务）
				if(missionList.getMissionList() == null || missionList.getMissionList().size() > 2) {
					return  AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "数据格式错误，只能一个导航和一个等待任务！");
				}

				//取第二个mission做等待任务业务参数校验，如果第二个任务存在，再判断
				if(missionList.getMissionList().size() ==2) {
					Mission missionWait = missionList.getMissionList().get(1);
					Set<MissionItem> missionWaitItemSet = missionWait.getMissionItemSet();
					if(missionWaitItemSet == null
							|| missionWaitItemSet.size() > 2) {
						return  AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数错误，等待任务数据格式不正确！");
					}
				}
			}
			//充电任务
			else if(missionListType.equals(Constant.MISSION_LIST_TYPE_CHARGE)) {
				//TODO 美亚调度充电写死一个Mission
				if(missionList.getMissionList() == null || missionList.getMissionList().size() != 1) {
					return  AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "数据格式错误，充电任务只能有一个！");
				}
			}

			String msg = "";
			if (missionList.getId() != null) {
				msg = "修改成功";
			} else {
				msg = "新增成功";
			}
			missionListService.updateFull(missionList,missionListDB,storeId);

			resp = AjaxResult.success(missionList,msg);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "出错");
		}
		return resp;
	}

	/**
	 * 发送调度任务，由多个任务列表拼接组成
	 * @param robotIds
	 * @param missionListIds
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"dispatch/missionList/sendDispatch"}, method = RequestMethod.POST)
	@ResponseBody
//    @PreAuthorize("hasAuthority('mrc_navigation_u')")
	public AjaxResult updateSendNavigation(
			@RequestBody MissionList missionList,
			@RequestParam Long[] robotIds,
			@RequestParam Long[] missionListIds,
			@RequestParam(required = false) String name,
			HttpServletRequest request) {
		AjaxResult resp = AjaxResult.success();
		try {
			//从session取当前切换的场景
			Scene scene = SessionUtil.getScene(request);
			if(scene == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
			}
			missionList.setSceneId(scene.getId());

			if(robotIds.length <= 0 || robotIds.length <= 0) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误");
			}

			String[] robotCodesArray = getRobotCodesArrayByIdList(robotIds);
			//TODO 暂时限定只能发送到1台机器人
			if(robotCodesArray == null
					|| robotCodesArray.length != 1) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,未找到机器人或选择的机器人多于1台");
			}

			//TODO 机器人低电量不接收任务下单

			//从对照表找延迟时间
//			Long delayTime = Constant.DEFAULT_DELAY_TIME;

			//TODO 从session取当前切换门店的ID
			Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

			//校验字段不为空
			if(missionList.getSceneId() == null
					|| missionList.getStartTime() == null
					|| missionList.getStopTime() == null
					|| missionList.getRepeatCount() == null
					|| missionList.getIntervalTime() == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,开始时间、结束时间、重复次数、间隔时间、场景不能为空！");
			}

			//校验任务类型是否正确
			String missionListType = missionList.getMissionListType();
			if(missionListType == null
					|| (!missionListType.equals(Constant.MISSION_LIST_TYPE_PATROL)
							&& !missionListType.equals(Constant.MISSION_LIST_TYPE_CHARGE))){
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,任务类型不正确");
			}

			List<MissionList> missionLists = new ArrayList<MissionList>();
			//如果任务没有名称，则我们根据类型自定义名称
			if(name == null || "".equals(name)) {
				name = MissionListTypeEnum.getValue(missionListType);
			}
			String currentDateTimeString = DateTimeUtils.getCurrentDateTimeString();
			/**
			 * 执行巡逻任务的业务逻辑
			 */
			if(missionListType.equals(Constant.MISSION_LIST_TYPE_PATROL)) {
				//通过总任务ID列表得到总任务
				missionList.setMissionList(new ArrayList<Mission>());
				missionList.setName(name + currentDateTimeString);
				List<Mission> missions = missionList.getMissionList();
				for( Long id : missionListIds ) {
					MissionList missionListTemp = missionListService.get(id,storeId);
					//校验missionList是否存在
					if(missionListTemp == null) {
						return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,未找到任务");
					}

					//对missionList做一些处理，主要是拼接上面missionListTemp里面的任务到一个任务列表
					List<Mission> missionsTemp = missionListTemp.getMissionList();
					if(missionsTemp == null) {
						return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,任务内容异常");
					}
					missions.addAll(missionsTemp);
				}
				missionLists.add(missionList);
			}
			/**
			 * 执行充电任务的业务逻辑
			 * 充电任务比较特殊，需要拆分成两个子missionList计划任务：第一个是去充电任务，第二个是离开充电桩任务
			 */
			else if(missionListType.equals(Constant.MISSION_LIST_TYPE_CHARGE)) {
				//去充电任务列表
				MissionList goToCharge = new MissionList();
				//离开充电桩任务列表
				MissionList leaveCharge = new MissionList();

				goToCharge.setName(name + Constant.START + currentDateTimeString);
				goToCharge.setStartTime(missionList.getStartTime());
				goToCharge.setStopTime(missionList.getStopTime());
				goToCharge.setRepeatCount(missionList.getRepeatCount());
				goToCharge.setIntervalTime(missionList.getIntervalTime());
				goToCharge.setSceneId(missionList.getSceneId());
				goToCharge.setMissionListType(missionListType);
				goToCharge.setMissionList(new ArrayList<Mission>());

				leaveCharge.setName(name + Constant.STOP + currentDateTimeString);
				leaveCharge.setStartTime(missionList.getStopTime());
				leaveCharge.setStopTime(missionList.getStopTime() + Constant.LEAVE_CHARGER_DELAY_TIME );
				leaveCharge.setRepeatCount(missionList.getRepeatCount());
				leaveCharge.setIntervalTime(missionList.getIntervalTime());
				leaveCharge.setSceneId(missionList.getSceneId());
				leaveCharge.setMissionListType(missionListType);
				leaveCharge.setMissionList(new ArrayList<Mission>());

				//todo 拼接两个定时任务下发
				//通过总任务ID列表得到总任务
				List<Mission> missions = goToCharge.getMissionList();
				if(missionListIds.length != 1) {
					return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,数量不正确,只能选择一个充电点");
				}
				Long id = missionListIds[0];
				MissionList missionListTemp = missionListService.get(id,storeId);
				//校验missionList是否存在
				if(missionListTemp == null) {
					return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,未找到任务");
				}

				//对missionList做一些处理，主要是拼接上面missionListTemp里面的任务到一个任务列表
				List<Mission> missionsTemp = missionListTemp.getMissionList();
				//充电任务列表只能有一个任务：充电任务
				if(missionsTemp == null && missionsTemp.size() != 1) {
					return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,任务内容异常");
				}
				missions.addAll(missionsTemp);

				Mission missionTemp = missionsTemp.get(0);
				Set<MissionItem> missionItemTemps = missionTemp.getMissionItemSet();
				//校验missionItemTemps只能有一个充电任务
				if(missionItemTemps == null || missionItemTemps.size() == 0) {
					return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"参数错误,任务单元内容异常");
				}

				//拼接离开充电桩任务
				List<Mission> leaveChargeMissions = new ArrayList<Mission>();
				Mission leaveChargeMission = new Mission();
				leaveChargeMission.setSceneId(missionTemp.getSceneId());
				leaveChargeMission.setSceneId(missionTemp.getStoreId());
				leaveChargeMission.setCreateTime(missionListTemp.getCreateTime());
				leaveChargeMission.setUpdateTime(missionListTemp.getUpdateTime());
				leaveChargeMission.setRepeatCount(missionTemp.getRepeatCount());
				leaveChargeMission.setIntervalTime(missionTemp.getIntervalTime());
				leaveChargeMission.setName(missionTemp.getName());
				leaveChargeMission.setDescription(missionTemp.getDescription());
				leaveChargeMission.setMissionItemSet( new HashSet<MissionItem>() );
				for(MissionItem missionItem : missionItemTemps) {
					MissionItem missionItem1 = new MissionItem();
					//设置离开充电桩任务ID
					missionItem1.setFeatureItemId(Constant.ORDER_LEAVE_CHARGE_ID);
					missionItem1.setName(missionItem.getName());
					missionItem1.setData(missionItem.getData());
					missionItem1.setStoreId(missionItem.getStoreId());
					missionItem1.setCreateTime(missionItem.getCreateTime());
					missionItem1.setUpdateTime(missionItem.getUpdateTime());
					missionItem1.setSceneId(missionItem.getSceneId());
					missionItem1.setCreatedBy(missionItem.getCreatedBy());
					leaveChargeMission.getMissionItemSet().add(missionItem1);
				}
				leaveChargeMissions.add(leaveChargeMission);
				leaveCharge.setMissionList(leaveChargeMissions);

				missionLists.add(goToCharge);
				missionLists.add(leaveCharge);
			}

			//遍历发送机器人消息
			for(String robotCode : robotCodesArray) {
				AjaxResult ajaxResult = missionFuncsService.createMissionListTasksByMissionLists(robotCode,missionLists);
				//TODO 加延时判断，ajaxResult为空也是报错
				if(ajaxResult == null) {
					return AjaxResult.failed(AjaxResult.CODE_FAILED,"消息发送失败");
				}
				else if(ajaxResult.getCode() != AjaxResult.CODE_SUCCESS) {
					//TODO 现在限定是一台机器人，将来多台，返回结果还需要Map形式
					return ajaxResult;
				}
			}

			resp = AjaxResult.success("消息发送成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			resp = AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"出错");
		}
		return resp;
	}

	//通过robotCodeIdList得到对应主设备的主板编号
	private String[] getRobotCodesArrayByIdList(Long[] robotIds) {
		ArrayList<String> robotCodeList = new ArrayList<String>();
		for( Long id: robotIds) {
			Robot robot = robotService.findById(id);
			if( robot == null ) {
				continue;
			}
			robotCodeList.add(robot.getCode());
		}
		int size = robotCodeList.size();
		if(size <= 0) {
			return null;
		}
		return (String[])robotCodeList.toArray(new String[size]);
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

	/**
	 * 根据机器人code去Redis查询最后一条机器人位置信息，查出场景名：
	 * 如果该信息超过2分钟，则提示“机器人已经离线太久，无法调度”；
	 * 如果在2分钟内，则根据场景名去寻找可供调度的任务列表返回给前端展示。
	 * @param robotCode
	 * @return
	 */
	@RequestMapping(value = {"dispatch/mission/availableMissionList"}, method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult getAvailableMissionList(@RequestParam(value = "robotCode") String robotCode) {
		MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(robotCode);
		if (currentMap != null) {
			Long sendTimeMilSeconds = currentMap.getSendTime().getTime();
			if (new Date().getTime() - sendTimeMilSeconds > TWO_MINUTES_MILLISECOND) {
				return AjaxResult.failed("机器人已经离线太久，无法调度");
			} else {
				JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
				String data = jsonObject.getString(TopicConstants.DATA);
				JSONObject object = JSON.parseObject(data);
				String mapData = object.getString(TopicConstants.DATA);
				JSONObject mapDataObject = JSON.parseObject(mapData);
				String sceneName = mapDataObject.getString(TopicConstants.SCENE_NAME);
				WhereRequest whereRequest = new WhereRequest();
				String queryObj = "{\"" + SearchConstants.SEARCH_MISSION_SCENE_NAME + "\": \""+ sceneName +"\"}" ;
				whereRequest.setQueryObj(queryObj);
				//TODO 从session取当前切换门店的ID
				Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;
				List<Mission> missionList = missionService.list(whereRequest,storeId);
				return AjaxResult.success(missionList, "查询成功");
			}
		} else {
			return AjaxResult.failed("机器人已经离线太久，无法调度");
		}
	}


	/**
	 * 暂停，回复，取消当前任务，取消所有任务实时接口
	 * @param command
	 * @return
	 */
	/*@RequestMapping(value = {"dispatch/command"}, method = RequestMethod.GET)
	@ResponseBody
	public AjaxResult sendCommand(String command) {
		boolean flag = false;
		switch (command) {
			case "pause":
				flag = sendMissionCommand("pause");
				break;
			case "resume":
				flag = sendMissionCommand("resume");
				break;
			case "skip":
				flag = sendMissionCommand("skip");
				break;
			case "cancel":
				flag = sendMissionCommand("cancel");
				break;
			case "default":
				break;
		}
		if (flag == true) {
			return AjaxResult.success();
		} else {
			return AjaxResult.failed();
		}
	}

	private boolean sendMissionCommand(String command) {
		boolean flag = false;
		MessageInfo info = new MessageInfo();
		info.setUuId(UUID.randomUUID().toString().replace("-", ""));
		info.setSendTime(new Date());
		info.setSenderId("goor-server");
		info.setReceiverId("daBit");
		info.setMessageType(MessageType.EXECUTOR_COMMAND);
		info.setMessageText("{\"topicName\":\""+ command + "\",\"topicType\":\"std_msgs/String\",\"publishMessage\":{\"command\":\""+ command + "\",\"sendTime\":\"2017-07-14 11:50:53\"}}");

		String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey("daBit",true, MessageType.EXECUTOR_COMMAND.name());
		//单机器命令发送（带回执）
		AjaxResult ajaxCommandResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultCommandRoutingKey, info);
		if (ajaxCommandResult.isSuccess()) {
			return true;
		}
		return flag;
	}*/

}
