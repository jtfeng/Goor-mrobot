package cn.muye.area.point.controller;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.point.cascade.CascadeMapPoint;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.SearchConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
 * Date: 2017/6/15
 * Time: 16:37
 * Describe:
 * Version:1.0
 */
@Controller
public class PointController {

	private static Logger LOGGER = LoggerFactory.getLogger(PointController.class);
	@Autowired
	private PointService pointService;

	@RequestMapping(value = "area/point", method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult saveOrUpdate(HttpServletRequest request, @RequestBody MapPoint mapPoint){
		try {

			List<MapPoint> pointListDB = pointService.findByName(mapPoint.getPointName(), mapPoint.getSceneName(),mapPoint.getMapName(),SearchConstants.FAKE_MERCHANT_STORE_ID);
			if (pointListDB.size() > 0 && !pointListDB.get(0).getId().equals(mapPoint.getId())){
				return AjaxResult.failed("已存在相同名称的导航点");
			}

			int mapPointTypeId = mapPoint.getMapPointTypeId();
			if (mapPointTypeId <= 0 ||  MapPointType.getType(mapPointTypeId) == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "点类型有误");
			}

			if (mapPoint.getId() != null) {
				MapPoint mapPointDB = pointService.findById(mapPoint.getId());
				if(null == mapPointDB){
					mapPointDB = new MapPoint();
				}
				mapPointDB.setMapName(mapPoint.getMapName());
				mapPointDB.setMapPointTypeId(mapPoint.getMapPointTypeId());
				mapPointDB.setPointAlias(mapPoint.getPointAlias());
				mapPointDB.setTh(mapPoint.getTh());
				mapPointDB.setX(mapPoint.getX());
				mapPointDB.setY(mapPoint.getY());
				mapPointDB.setPointLevel(mapPoint.getPointLevel());
				pointService.update(mapPointDB);
				return AjaxResult.success(mapPointDB);
			} else {
				pointService.save(mapPoint);
				return AjaxResult.success(mapPoint);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(1,"系统错误" );
		}
	}

	@RequestMapping(value = "area/point", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult listMapPoint(WhereRequest whereRequest, HttpServletRequest request) {
		try {
			List<MapPoint> pointListDB = pointService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
			Integer pageNo = whereRequest.getPage();
			Integer pageSize = whereRequest.getPageSize();

			pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
			pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
			PageHelper.startPage(pageNo, pageSize);
			//用PageInfo对结果进行包装
			PageInfo<MapPoint> page = new PageInfo<MapPoint>(pointListDB);
			return AjaxResult.success(page);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed(1);
		}
	}

	@RequestMapping(value = "area/point/{id}", method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult deleteMapPoint(@PathVariable long id){
		try {
			MapPoint pointDB = pointService.findById(id);
			if (pointDB == null) {
				return AjaxResult.failed("删除对象不存在");
			}
			pointService.delete(pointDB);
			return AjaxResult.success("删除成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}

	/**
	 * 级联查询目标点，地图名->目标点类型->目标点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "area/point/cascade", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult cascadeMapPoint(){
		try {
			List<CascadeMapPoint> cascadeMapPointList = pointService.cascadeMapPoint();
			return AjaxResult.success(cascadeMapPointList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}

//	/**
//	 * 向机器人发送指令
//	 * 载入地图和场景的导航点
//	 *
//	 * @param sceneName
//	 * @param mapName
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "area/point/load", method = RequestMethod.GET)
//	@ResponseBody
////	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
//	public AjaxResult loadMapPoint(@RequestParam("sceneName") String sceneName,
//								   @RequestParam("mapName") String mapName) throws Exception {
//		try {
//			//封装数据
//			JSONObject pointDataJsonObject = new JSONObject();
//			pointDataJsonObject.put(TopicConstants.SCENE_NAME, sceneName);
//			pointDataJsonObject.put(TopicConstants.MAP_NAME, mapName);
//
//			SlamRequestBody slamRequestBody = new SlamRequestBody(TopicConstants.POINT_LOAD);
//			slamRequestBody.setData(pointDataJsonObject);
//			JSONObject messageObject = new JSONObject();
//			messageObject.put(TopicConstants.DATA, JSON.toJSONString(slamRequestBody));
//
//			CommonInfo commonInfo = new CommonInfo();
//			commonInfo.setTopicName(TopicConstants.APP_PUB);
//			commonInfo.setTopicType(TopicConstants.TOPIC_TYPE_STRING);
//			commonInfo.setPublishMessage(JSON.toJSONString(messageObject));
//
//			String text = JSON.toJSONString(commonInfo);
//			byte[] b = text.getBytes();
//			MessageInfo info = new MessageInfo(MessageType.EXECUTOR_COMMAND, text, b);
//			info.setMessageStatusType(MessageStatusType.INIT);
////		info.setReceiptWebSocket(true);
////		info.setWebSocketId("user-9");
//			info.setSendDeviceType(DeviceType.GOOR_SERVER);
//			info.setReceiverDeviceType(DeviceType.GOOR);
//			info.setMessageKind(0);
//			info.setSendTime(new Date());
//			info.setUpdateTime(new Date());
//			info.setSendCount(0);
//
//			messageSendService.sendNoStatusMessage("cookyPlus1301_jelynn", info);
//			return AjaxResult.success();
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(), e);
//			return AjaxResult.failed();
//		}
//	}
}
