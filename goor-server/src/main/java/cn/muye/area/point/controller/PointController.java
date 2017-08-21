package cn.muye.area.point.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.IndustrialControlPointType;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.base.bean.SearchConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    /**
     * 修改。目前只能修改别名和添加云端导航点类型
     *
     * @param request
     * @param mapPoint
     * @return
     */
    @RequestMapping(value = "area/point", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult saveOrUpdate(HttpServletRequest request, @RequestBody MapPoint mapPoint) {
        try {

            List<MapPoint> pointListDB = pointService.findByName(mapPoint.getPointName(), mapPoint.getSceneName(), mapPoint.getMapName(), SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (pointListDB.size() > 0 && !pointListDB.get(0).getId().equals(mapPoint.getId())) {
                return AjaxResult.failed("已存在相同名称的导航点");
            }

            if (mapPoint.getId() != null) {
                MapPoint mapPointDB = pointService.findById(mapPoint.getId());
                if (null == mapPointDB) {
                    return AjaxResult.failed(1, "导航目标点不存在");
                }
                mapPointDB.setX(mapPoint.getX());
                mapPointDB.setY(mapPoint.getY());
                mapPointDB.setTh(mapPoint.getTh());
                mapPointDB.setPointAlias(mapPoint.getPointAlias());
                mapPointDB.setCloudMapPointTypeId(mapPoint.getCloudMapPointTypeId());
                pointService.update(mapPointDB);
                return AjaxResult.success(mapPointDB);
            } else {
                pointService.save(mapPoint);
                return AjaxResult.success(mapPoint);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1, "系统错误");
        }
    }

    @RequestMapping(value = "area/point", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult listMapPoint(WhereRequest whereRequest, HttpServletRequest request) {
        try {
            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();

            pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
            pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            //用PageInfo对结果进行包装
            List<MapPoint> pointListDB = pointService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<MapPoint> page = new PageInfo<MapPoint>(pointListDB);
            return AjaxResult.success(page);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1);
        }
    }

    /**
     * 查询机器人所绑定的场景下的充电桩点用
     * @param whereRequest
     * @param request
     * @return
     */
    @RequestMapping(value = "area/point/robot", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult listMapPointBySceneId(WhereRequest whereRequest, HttpServletRequest request) {
        try {
            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();
            pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
            pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            //用PageInfo对结果进行包装
            List<MapPoint> pointListDB = pointService.listBySceneId(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<MapPoint> page = new PageInfo<MapPoint>(pointListDB);
            return AjaxResult.success(page);
        } catch (Exception e) {
            LOGGER.error("PointController类的listMapPointBySceneId方法报错{}", e);
            return AjaxResult.failed(1);
        } finally {
        }
    }

    @RequestMapping(value = "area/point/{id}", method = RequestMethod.DELETE)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult deleteMapPoint(@PathVariable long id) {
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
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/point/cascade", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult cascadeMapPoint(@RequestParam("level") int level, @RequestParam(value = "sceneName", required = false) String sceneName) {
        try {
            List<CascadePoint> cascadeMapPointList = pointService.cascadeMapPoint(level, sceneName);
            if (null == cascadeMapPointList && !StringUtil.isNullOrEmpty(sceneName)) {
                LOGGER.info("指定场景名sceneName = " + sceneName + "不存在");
                return AjaxResult.success(new ArrayList<>());
            }
            return AjaxResult.success(cascadeMapPointList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }

    //TODO  测试添加导航点
    /**
     * 级联查询目标点，地图名->目标点类型->目标点
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/point/test", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult test(@RequestParam("count") int count) {
        try {
            MapPoint mapPoint = new MapPoint();
            mapPoint.setMapName("jelynn");
            mapPoint.setSceneName("jelynn");
            mapPoint.setMapZipId(318L);
            mapPoint.setCreateTime(new Date());
            mapPoint.setMapPointTypeId(2);
            mapPoint.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
            mapPoint.setICPointType(IndustrialControlPointType.getType(mapPoint.getMapPointTypeId()).getName());

            double x = 1.15461245;
            int num = 10;
            double y = 1.1781545;
            Random random = new Random(2);
            for (int i = 0; i < count; i++) {
                mapPoint.setX(x);
                for (int j = 0; j < count; j++) {
                    mapPoint.setPointName("G" + num);
                    mapPoint.setPointAlias("G" + num);

                    mapPoint.setY(y);
                    mapPoint.setTh(random.nextDouble());
                    mapPoint.setId(null);
                    pointService.save(mapPoint);
                    num ++;
                    y = y + 0.020000000;
                }
                x += 0.020000000;
            }

            return AjaxResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }
}
