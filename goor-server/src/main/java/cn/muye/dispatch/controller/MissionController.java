package cn.muye.dispatch.controller;

import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.enums.MessageType;
import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.mission.*;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.RabbitMqBean;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.dispatch.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
	private RabbitTemplate rabbitTemplate;

	private int TWO_MINUTES_MILLISECOND = 120000;

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
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);

			List<MissionItem> missionItemList = missionItemService.list(whereRequest);
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
	public AjaxResult saveOrUpdateMissionFull(@RequestBody Mission mission, HttpServletRequest request) throws Exception {
		AjaxResult resp;
		try {
			String missionName = mission.getName();
			Mission missionDB = missionService.findByName(missionName);
			if (missionDB != null && !missionDB.getId().equals(mission.getId())) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "已存在相同名称的任务串！");
			}
			String msg = "";
			if (mission.getId() != null) {
				missionService.updateFull(mission,missionDB);
				msg = "修改成功";
			} else {
				missionService.updateFull(mission,missionDB);
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
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			List<MissionList> missionListList = missionListService.list(whereRequest);

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

	/**
	 * 发送调度任务，由多个导航点组成
	 * @param productUsedIdList
	 * @param missionNodeIdList
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"dispatch/navigation/sendNavigation","api/dispatch/navigation/sendNavigation"}, method = RequestMethod.POST)
	@ResponseBody
//    @PreAuthorize("hasAuthority('mrc_navigation_u')")
	public AjaxResult updateSendNavigation(
			@RequestParam Long[] productUsedIdList,
			@RequestParam Long[] missionNodeIdList,
			HttpServletRequest request) {
		AjaxResult resp = AjaxResult.success();
//		try {
//			if(productUsedIdList.length <= 0 || missionNodeIdList.length <= 0) {
//				return AjaxResult.failed(-1,"参数错误");
//			}
//
//			String[] productUsedCodeArray = getProductUsedCodeArrayByIdList(productUsedIdList);
//			if(productUsedCodeArray == null) {
//				return AjaxResult.failed(-1,"未找到设备组");
//			}
//
//			String uuid = UUID.randomUUID().toString();
//
//			//从对照表找延迟时间
//			Long delayTime = Constants.DEFAULT_DELAY_TIME;
//			for(String[] filter : Constants.MRC_MISSION_ORDER_LIST) {
//				if(MRCConstants.MRC_NAME_NAVIGATION.indexOf(filter[0]) > -1  ) {
//					delayTime = Long.parseLong(filter[1]);
//					break;
//				}
//			}
//
//			//获取语音的featureItem
//			FeatureItem featureItem = featureItemService.findById(Constants.VOICE_FEATURE_ITEM_ID);
//			FeatureItemType featureItemType = featureItemTypeService.findById(Constants.VOICE_FEATURE_ITEM_TYPE_ID);
//
//			//通过总任务ID列表得到总任务
//			List<MissionNode> missionNodeList = new ArrayList<MissionNode>();
//			for( Long id : missionNodeIdList ) {
//				MissionNode missionNode = missionNodeService.findById(id);
//				missionNodeList.add(missionNode);
//
//				//增加到站提示语node
//				MissionNode missionNode1 = new MissionNode();
//				missionNode1.setData("{\"voiceContent\":\"我已经到达"+ missionNode.getName() +"\"}");
//				missionNode1.setFeatureItem(featureItem);
//				missionNode1.setFeatureItemType(featureItemType);
//				missionNodeList.add(missionNode1);
//			}
//
//			if(missionNodeList.size() <= 0) {
//				return AjaxResponse.failed(-1,"未找到任务节点");
//			}
//
//			MissionChain missionChain = new MissionChain();
//			missionChain.setMissionNodeList(missionNodeList);
//			List<MissionChain> missionChainList = new ArrayList<MissionChain>();
//			missionChainList.add(missionChain);
//
//			MissionMain missionMain = new MissionMain();
//			missionMain.setMissionChainList(missionChainList);
//
//			//当前时间延后
//			long startTime = new Date().getTime();
//			startTime += delayTime;
//			missionMain.setStartTime(new Date(startTime));
//
//			Map result = mrcSendOperationsService.sendMission(uuid,productUsedCodeArray,missionMain);
//			resp.addDataEntry(result);
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(),e);
//			resp = AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE,"出错");
//		}
		return resp;
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
				List<Mission> missionList = missionService.list(whereRequest);
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
	@RequestMapping(value = {"dispatch/command"}, method = RequestMethod.GET)
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
		MessageInfo info = new MessageInfo();//TODO 具体发送消息内容统一封装在此bean里
		info.setUuId(UUID.randomUUID().toString().replace("-", ""));
		info.setSendTime(new Date());
		info.setSenderId("goor-server");
		info.setReceiverId("daBit");
		info.setMessageType(MessageType.EXECUTOR_COMMAND);//TODO 如果发送资源,注释此行，将此行下面第一行注释去掉
//        info.setMessageType(MessageType.EXECUTOR_RESOURCE);//TODO 如果发送资源,将此行注释去掉，注释此行上面第一行
//        info.setMessageType(MessageType.EXECUTOR_LOG);//TODO 针对 x86 agent 业务逻辑,不接收发送到ros的信息，如：发送命令要求上传log等
//                info.setMessageText(JSON.toJSONString(robotDb));//TODO 发送资源及rostopic命令
		info.setMessageText("{\"topicName\":\""+ command + "\",\"topicType\":\"std_msgs/String\",\"publishMessage\":{\"command\":\"pause\",\"sendTime\":\"2017-07-14 11:50:53\"}}");//TODO 发送资源及rostopic命令

		String backResultCommandRoutingKey = RabbitMqBean.getRoutingKey("daBit",true, MessageType.EXECUTOR_COMMAND.name());
		//单机器命令发送（带回执）
		AjaxResult ajaxCommandResult = (AjaxResult) rabbitTemplate.convertSendAndReceive(TopicConstants.TOPIC_EXCHANGE, backResultCommandRoutingKey, info);
		if (ajaxCommandResult.isSuccess()) {
			return true;
		}
		return flag;
	}

}
