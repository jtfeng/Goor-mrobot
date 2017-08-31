package cn.muye.area.station.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.bean.area.station.StationType;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.station.service.StationService;
import cn.muye.area.station.service.StationStationXREFService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.SessionUtil;
import com.github.pagehelper.PageInfo;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private StationStationXREFService stationStationXREFService;

    /**
     * 分页查询资源
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = {"area/station"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询站列表", httpMethod = "GET", notes = "查询站列表")
    @ResponseBody
    private AjaxResult pageStation(WhereRequest whereRequest, HttpServletRequest request) {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if (scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            //TODO 从session取切换门店的ID，现在先写死
            List<Station> stationList = stationService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID, scene.getId());
            if (stationList != null && stationList.size() > 0) {
                for (Station station : stationList) {
                    station = toEntity(station);
                }
            }

            PageInfo<Station> pageList = new PageInfo<Station>(stationList);
            return AjaxResult.success(pageList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统内部查询出错");
        }
    }

    /**
     * 查询单个站接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"area/station/{id}"}, method = RequestMethod.GET)
    @ApiOperation(value = "查询站详情", httpMethod = "GET", notes = "查询站详情")
    @ResponseBody
    public AjaxResult getStation(@ApiParam(value = "站ID") @PathVariable Long id, HttpServletRequest request) {
        if (id == null) {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "查询失败");
        }
        Station station = null;
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if (scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            //TODO 从session取切换门店的ID，现在先写死
            station = stationService.findById(Long.valueOf(id), SearchConstants.FAKE_MERCHANT_STORE_ID, scene.getId());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "查询失败");
        }
        return AjaxResult.success(toEntity(station), "查询成功");
    }

    /**
     * 删除单个站接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "area/station/{id}", method = RequestMethod.DELETE)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult deleteMapPoint(@PathVariable long id, HttpServletRequest request) throws Exception {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if (scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            //TODO 从session取切换门店的ID，现在先写死
            Station stationDB = stationService.findById(id, SearchConstants.FAKE_MERCHANT_STORE_ID, scene.getId());
            if (stationDB == null) {
                return AjaxResult.failed("删除对象不存在");
            }
            stationDB.setActive(Constant.DELETE);
//            stationService.delete(stationDB);
            stationService.update(stationDB);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("被使用，无法删除");
        }
    }

    /**
     * 新增或修改站
     *
     * @param station
     * @return
     */
    @RequestMapping(value = {"area/station"}, method = RequestMethod.POST)
    @ApiOperation(value = "新增或修改站", httpMethod = "POST", notes = "新增或修改站")
    @ResponseBody
    public AjaxResult saveOrUpdateStation(@ApiParam(value = "站") @RequestBody Station station, HttpServletRequest request) {
        try {
            //从session取当前切换的场景
            Scene scene = SessionUtil.getScene();
            if (scene == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "请先切换到某场景！");
            }
            Long sceneId = scene.getId();

            Integer stationTypeId = station.getStationTypeId();
            if (stationTypeId == null || stationTypeId <= 0 || StationType.getType(stationTypeId) == null) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站类型有误");
            }
            String name = station.getName();
            if (StringUtil.isNullOrEmpty(name)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "站名称不能为空");
            }
            //TODO 从session取切换门店的ID，现在先写死
            Long storeId = SearchConstants.FAKE_MERCHANT_STORE_ID;

            //判断是否有重复的名称,且查找到的对象ID不是提交的对象ID
            List<Station> stationDbList = stationService.listByName(name, storeId, sceneId);
            int size = stationDbList.size();
            Long id = station.getId();
            if (stationDbList != null
                    && (size > 1
                    || (size == 1 && id == null)
                    || (size == 1 && id != null && !stationDbList.get(0).getId().equals(id)))) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "站名称重复");
            }

            //校验点都是数据库里的点
            if (!isPointExist(station)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "点参数错误");
            }

            if (station != null && id != null) { //修改

                Station stationDb = stationService.findById(id, storeId, sceneId);
                if (stationDb == null) {
                    return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "要修改的对象不存在");
                }
                stationDb.setName(station.getName());
                stationDb.setStationTypeId(station.getStationTypeId());
                stationDb.setDescription(station.getDescription());
                stationDb.setMapPoints(station.getMapPoints());

                stationService.update(stationDb);
                return AjaxResult.success(toEntity(stationDb), "修改成功");
            } else if (station != null && id == null) { //新增
                station.setActive(Constant.NORMAL);
                station.setSceneId(scene.getId());
                station.setStoreId(storeId);
                station.setCreateTime(new Date());
                //TODO 从session取登录用户ID
                station.setCreatedBy(SearchConstants.FAKE_MERCHANT_STORE_ID);

                stationService.save(station);

                return AjaxResult.success(toEntity(station), "新增成功");
            } else {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("出错");
        }
    }

    /**
     * 站绑定机器人
     * @return
     * @author Ray.Fu
     */
    @RequestMapping(value = {"area/station/robot"}, method = RequestMethod.POST)
    @ApiOperation(value = "站绑定机器人", httpMethod = "POST", notes = "站绑定机器人")
    @ResponseBody
    public AjaxResult bindRobots(@ApiParam(value = "站") @RequestBody Station station) {
        Long id = station.getId();
        List<Robot> stationList = station.getRobotList();
        if (station != null && id != null && stationList != null && stationList.size() > 0) {
            Station stationDb = stationService.findById(station.getId());
            if (stationDb != null) {
                stationService.bindRobots(station);
                return AjaxResult.success(station, "绑定成功");
            } else {
                return AjaxResult.failed(AjaxResult.CODE_FAILED, "不存在的站");
            }

        } else {
            return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
        }
    }

    /**
     * 校验station绑定的点是否都存在
     *
     * @param station
     * @return
     */
    private boolean isPointExist(Station station) {
        List<MapPoint> pointList = station.getMapPoints();
        //如果没有点，则不校验
        if (pointList == null || pointList.size() <= 0) {
            return true;
        }

        //如果有一个点不存在，则返回假
        for (MapPoint mapPoint : pointList) {
            MapPoint mapPointDB = pointService.findById(mapPoint.getId());
            if (mapPointDB == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 把Station转化成前端显示entity
     *
     * @param station
     * @return
     */
    private Station toEntity(Station station) {
//		Integer typeId = station.getStationTypeId();
//		if(typeId != null) {
//			station.setStationType(StationType.getTypeJson(typeId));
//		}

        List<MapPoint> pointList = null;
        if (station != null) {
            pointList = station.getMapPoints();
        }
        if (pointList != null && pointList.size() > 0) {
            for (MapPoint mapPoint : pointList) {
                if (mapPoint == null) {
                    continue;
                }
                MapPoint mapPointDB = pointService.findById(mapPoint.getId());
                if (mapPointDB != null) {
                    toEntity(mapPointDB);
                }
            }
        }

        return station;
    }

    /**
     * 把MapPoint转化成前端entity
     *
     * @param mapPoint
     * @return
     */
    private MapPoint toEntity(MapPoint mapPoint) {
//		Integer typeId = mapPoint.getMapPointTypeId();
//		if(typeId != null) {
//			mapPoint.setMapPointType(MapPointType.getTypeJson(typeId));
//		}
        return mapPoint;
    }

    /**
     * 站绑定可到达的站
     * @param station
     * @return
     */
    @RequestMapping(value = {"area/station/bindAccessArriveStation"}, method = RequestMethod.POST)
    @ApiOperation(value = "站绑定可到达的站", httpMethod = "POST", notes = "站绑定可到达的站")
    @ResponseBody
    public AjaxResult bindAccessArrivedStation(@ApiParam(value = "站") @RequestBody Station station) {
        List<Station> accessArriveStationIdList = station.getAccessArriveStationIdList();
        if (station.getId() != null && accessArriveStationIdList != null && accessArriveStationIdList.size() > 0) {
            List<Long> stationIdList = accessArriveStationIdList.stream().map(station1 -> station1.getId()).collect(Collectors.toList());
            if (stationIdList.contains(station.getId())) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,"可到达的站不能绑定自己");
            }
            stationStationXREFService.save(station.getId(), stationIdList);
        }
        return AjaxResult.success(station,"绑定成功");
    }

    //测试用的不要提交
//    @RequestMapping(value = {"area/station/test"}, method = RequestMethod.GET)
//    @ResponseBody
//    public AjaxResult listByStationId(@RequestParam("id") Long stationId) {
//        List<Station> stationList = stationService.listAccessStationByStationId(stationId);
//        return AjaxResult.success(stationList);
//    }

}
