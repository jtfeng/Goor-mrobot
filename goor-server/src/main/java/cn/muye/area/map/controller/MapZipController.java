package cn.muye.area.map.controller;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapZipService;
import cn.muye.base.bean.AjaxResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 9:46
 * Describe:
 * Version:1.0
 */
@Controller
public class MapZipController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapZipController.class);

	@Autowired
	private MapZipService mapZipService;

	@RequestMapping(value = "area/mapzip", method = {RequestMethod.POST,RequestMethod.PUT})
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult saveMapZip(@RequestBody MapZip mapZip) throws Exception {
		try {
			mapZipService.save(mapZip);
			return AjaxResult.success(mapZip);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}


	@RequestMapping(value = "area/mapzip", method = RequestMethod.GET)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult listMapZip(WhereRequest whereRequest) throws Exception {
		try {
			List<MapZip> mapZipList = mapZipService.list(whereRequest);
			int pageNo = (whereRequest.getPage() == 0) ? 1 : whereRequest.getPage();
			int pageSize = (whereRequest.getPageSize() == 0) ? 10 : whereRequest.getPage();

			PageHelper.startPage(pageNo, pageSize);
			PageInfo<MapZip> page = new PageInfo<>(mapZipList);
			return AjaxResult.success(page);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}

	@RequestMapping(value = "area/mapzip/{id}", method = RequestMethod.DELETE)
	@ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
	public AjaxResult deleteMapZip(@PathVariable Long id) throws Exception {
		try {
			if(null == id){
				return AjaxResult.failed(2,"参数错误");
			}

			MapZip mapZip = mapZipService.getMapZip(id);
			if(null == mapZip){
				return AjaxResult.failed("删除对象不存在");
			}
			mapZipService.delete(mapZip);
			return AjaxResult.success("删除成功");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return AjaxResult.failed("系统错误");
		}
	}
}
