package cn.muye.dispatch.controller;

import cn.mrobot.bean.misssion.*;
import cn.mrobot.utils.WhereRequest;
import cn.mrobot.utils.ajax.AjaxResponse;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.SearchConstants;
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
	private MissionNodeService missionNodeService;
	@Autowired
	private MissionChainService missionChainService;
	@Autowired
	private MissionMainService missionMainService;

	//missionNode-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/node"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_u')")
	public AjaxResponse saveOrUpdateMissionNode(@RequestBody MissionNode missionNode, HttpServletRequest request) throws Exception {
		try {
			String missionNodeName = missionNode.getName();
			MissionNode missionNodeDB = missionNodeService.findByName(missionNodeName);
			if (missionNodeDB != null && !missionNodeDB.getId().equals(missionNode.getId())) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务节点！");
			}

			if (missionNode.getId() != null) {
				missionNode.setUpdateTime(new Date());
				missionNodeService.update(missionNode);
			} else {
				missionNodeService.save(missionNode);
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
			MissionNode missionNodeDB = missionNodeService.get(id);
			if (missionNodeDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionNodeService.delete(missionNodeDB);
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
			List<MissionNode> missionNodeList = missionNodeService.list(whereRequest);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);

			PageInfo<MissionNode> page = new PageInfo<MissionNode>();
			PageHelper.startPage(pageNo, pageSize);
			//用PageInfo对结果进行包装
			page.setList(missionNodeList);
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
			return missionNodeService.list();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//missionChain-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/chain"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionchain_u')")
	public AjaxResponse saveOrUpdateMissionChain(@RequestBody MissionChain missionChain, HttpServletRequest request) throws Exception {
		AjaxResponse resp;
		try {
			String missionChainName = missionChain.getName();
			MissionChain missionChainDB = missionChainService.findByName(missionChainName);
			if (missionChainDB != null && !missionChainDB.getId().equals(missionChain.getId())) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务串！");
			}

			if (missionChain.getId() != null) {
				missionChain.setUpdateTime(new Date());
				missionChainService.update(missionChain);
			} else {
				missionChainService.save(missionChain);
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
			MissionChain missionChainDB = missionChainService.get(missionChainId);
			if (missionChainDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "任务串不存在！");
			}

			List<Long> bindList = JSON.parseArray(bingString, Long.class);

			missionChainDB.setUpdateTime(new Date());
			missionChainService.update(missionChainDB, bindList);
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
			MissionChain missionChainDB = missionChainService.get(id);
			if (missionChainDB == null) {
				return AjaxResponse.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionChainService.delete(missionChainDB);
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
			List<MissionChain> missionChainList = missionChainService.list(whereRequest);

			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			//用PageInfo对结果进行包装
			PageInfo<MissionChain> page = new PageInfo<MissionChain>(missionChainList);
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
			List<MissionChain> missionChainList = missionChainService.list();
			return missionChainList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	//missionMain-----------------------------------------------------------------------
	@RequestMapping(value = {"dispatch/mission/main"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionmain_u')")
	public AjaxResult saveOrUpdateMissionMain(@RequestBody MissionMain missionMain, HttpServletRequest request) throws Exception {
		try {
			String missionMainName = missionMain.getName();
			MissionMain missionMainDB = missionMainService.findByName(missionMainName);
			if (missionMainDB != null && !missionMainDB.getId().equals(missionMain.getId())) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "已存在相同名称的任务串！");
			}

			if (missionMain.getId() != null) {
				missionMain.setUpdateTime(new Date());
				missionMainService.update(missionMain);
			} else {
				missionMainService.save(missionMain);
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
			MissionMain missionMainDB = missionMainService.get(id);
			if (missionMainDB == null) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "要删除的对象不存在");
			}

			missionMainService.delete(missionMainDB);
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
			List<MissionMain> missionMainList = missionMainService.list(whereRequest);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			PageInfo<MissionMain> page = new PageInfo<>(missionMainList);
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
			List<MissionMain> missionMainList = missionMainService.list();
			return AjaxResult.success(missionMainList);
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
			MissionMain missionMainDB = missionMainService.get(missionMainId);
			if (missionMainDB == null) {
				return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "总任务不存在！");
			}

			List<Long> bindList = JSON.parseArray(bingString, Long.class);

			missionMainDB.setUpdateTime(new Date());
			missionMainService.update(missionMainDB, bindList);
			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(AjaxResponse.RESPONSE_STATUS_FAIURE, "出错");
		}
	}

//	//把类转换成entry返回给前端，解耦和
//	private Map objectToEntry(MissionNode missionNode) {
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
