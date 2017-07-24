package cn.muye.area.point.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.IndustrialControlPointType;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
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

	/**
	 * 修改。目前只能修改别名和添加云端导航点类型
	 * @param request
	 * @param mapPoint
	 * @return
	 */
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
			if (mapPointTypeId <= 0 ||  IndustrialControlPointType.getType(mapPointTypeId) == null) {
				return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "工控点类型有误");
			}

			if (mapPoint.getId() != null) {
				MapPoint mapPointDB = pointService.findById(mapPoint.getId());
				if(null == mapPointDB){
					mapPointDB = new MapPoint();
				}
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
	public AjaxResult cascadeMapPoint(@RequestParam("level") int level){
		try {
			List<CascadePoint> cascadeMapPointList = pointService.cascadeMapPoint(level);
			return AjaxResult.success(cascadeMapPointList);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}
}
