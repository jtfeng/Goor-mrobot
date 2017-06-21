package cn.muye.area.station.controller;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.AjaxResult;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Chay
 * Date: 2017/6/17
 * Time: 13:02
 * Describe:
 * Version:1.0
 */
@Controller
public class StationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StationController.class);

	@Autowired
	private StationService stationService;
	@Autowired
	private PointService pointService;

	/**
	 * 分页查询资源
	 * @param whereRequest
	 * @return
	 */
	@RequestMapping(value = {"area/station"}, method = RequestMethod.GET)
	@ApiOperation(value = "查询站列表", httpMethod = "GET", notes = "查询站列表")
	@ResponseBody
	private AjaxResult pageStation(WhereRequest whereRequest){
		try {
			PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
			List<Station> stationList = stationService.list(whereRequest);
			List<Station> returnList = new ArrayList<Station>();
			if(stationList != null && stationList.size() > 0) {
				for(Station station:stationList) {
					station = toEntity(station);
					returnList.add(station);
				}
			}

			PageInfo<Station> pageList = new PageInfo<Station>(returnList);
			return AjaxResult.success(pageList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			return AjaxResult.failed("系统内部查询出错");
		}

	}

	/**
	 * 查询单个站接口
	 * @param id
	 * @return
	 */
	@RequestMapping(value = {"area/station/{id}"}, method = RequestMethod.GET)
	@ApiOperation(value = "查询站详情", httpMethod = "GET", notes = "查询站详情")
	@ResponseBody
	public AjaxResult getStation(@ApiParam(value = "站ID") @PathVariable String id) {
		if (id == null) {
			return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "查询失败");
		}
		Station station = null;
		try {
			station = stationService.findById(Long.valueOf(id));
		} catch (NumberFormatException e) {
			LOGGER.error(e.getMessage(),e);
			return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "查询失败");
		}
		return AjaxResult.success(toEntity(station), "查询成功");
	}

	/**
	 * 删除单个站接口
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "area/station/{id}", method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult deleteMapPoint(@PathVariable long id) throws Exception {
		try {
			Station stationDB = stationService.findById(id);
			if (stationDB == null) {
				return AjaxResult.failed("删除对象不存在");
			}
			stationService.delete(stationDB);

			return AjaxResult.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed();
		}
	}

	/**
	 * 新增或修改站
	 * @param station
	 * @return
	 */
	@RequestMapping(value = {"area/station"}, method = RequestMethod.POST)
	@ApiOperation(value = "新增或修改站", httpMethod = "POST", notes = "新增或修改站")
	@ResponseBody
	public AjaxResult saveOrUpdateStation(@ApiParam(value = "站")@RequestBody Station station) {
		Integer stationTypeId = station.getStationTypeId();
		if (stationTypeId == null || stationTypeId <= 0 || StationType.getType(stationTypeId) == null) {
			return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站类型有误");
		}
		String name = station.getName();
		if (StringUtil.isNullOrEmpty(name)) {
			return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站名称不能为空");
		}
		//判断是否有重复的名称,且查找到的对象ID不是提交的对象ID
		List<Station> stationDbList = stationService.listByName(name);
		int size = stationDbList.size();
		Long id = station.getId();
		if (stationDbList != null
				&& (size > 1
					|| (size == 1 && id != null && !stationDbList.get(0).getId().equals(id)))) {
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "站名称重复");
		}

		//校验点都是数据库里的点
		if(!isPointExist(station)) {
			return AjaxResult.failed(AjaxResult.CODE_FAILED, "点参数错误");
		}

		if (station != null && id != null) { //修改
			Station stationDb = stationService.findById(id);
			stationDb.setName(station.getName());
			stationDb.setStationTypeId(station.getStationTypeId());
			stationDb.setDescription(station.getDescription());
			stationDb.setMapPoints(station.getMapPoints());

			stationService.update(stationDb);
			return AjaxResult.success(toEntity(stationDb), "修改成功");
		} else if (station != null && id == null){
			stationService.save(station);
			System.out.println("###########"+station.getId());

			return AjaxResult.success(toEntity(station), "新增成功");
		} else {
			return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
		}
	}

	/**
	 * 校验station绑定的点是否都存在
	 * @param station
	 * @return
	 */
	private boolean isPointExist(Station station) {
		List<MapPoint> pointList = station.getMapPoints();
		//如果没有点，则不校验
		if(pointList == null || pointList.size() <= 0) {
			return true;
		}

		//如果有一个点不存在，则返回假
		for(MapPoint mapPoint : pointList) {
			MapPoint mapPointDB = pointService.findById(mapPoint.getId());
			if(mapPointDB == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 把Station转化成前端显示entity
	 * @param station
	 * @return
	 */
	private Station toEntity(Station station) {
		Integer typeId = station.getStationTypeId();
		if(typeId != null) {
			station.setStationType(StationType.getTypeJson(typeId));
		}

		List<MapPoint> pointList = station.getMapPoints();
		if(pointList != null && pointList.size() > 0) {
			List<MapPoint> resultPointList = new ArrayList<MapPoint>();
			for(MapPoint mapPoint : pointList) {
				MapPoint mapPointDB = pointService.findById(mapPoint.getId());
				if(mapPointDB != null) {
					resultPointList.add(toEntity(mapPointDB));
				}
			}
			station.setMapPoints(resultPointList);
		}

		return station;
	}

	/**
	 * 把MapPoint转化成前端entity
	 * @param mapPoint
	 * @return
	 */
	private MapPoint toEntity(MapPoint mapPoint) {
		Integer typeId = mapPoint.getMapPointTypeId();
		if(typeId != null) {
			//TODO 等待Jelynn添加方法
//			mapPoint.setMapPointType(MapPointType.getTypeJson(typeId));
		}
		return mapPoint;
	}
}
