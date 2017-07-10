package cn.muye.area.map.controller;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.AjaxResult;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Jelynn on 2017/7/6.
 */
@Controller
public class MapInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapInfoController.class);

    @Autowired
    private MapInfoService mapInfoService;

    @RequestMapping(value = "area/mapinfo", method = {RequestMethod.POST})
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult setMapAlias(@RequestBody MapInfo mapInfo) {
        try {
            Long id = mapInfo.getId();
            if(null == id){
                return AjaxResult.failed("地图的主键ID不能为空");
            }
            MapInfo mapInfoDB = mapInfoService.getMapInfo(id);
            if(null == mapInfoDB){
                return AjaxResult.failed("地图数据不存在");
            }
            mapInfoDB.setMapAlias(mapInfo.getMapAlias());
            mapInfoService.update(mapInfoDB);
            return AjaxResult.success(mapInfoDB);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }

    @RequestMapping(value = "area/mapinfo", method = {RequestMethod.GET})
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult getMapInfo(WhereRequest whereRequest) {
        try {
            List<MapInfo> mapZipList = mapInfoService.getMapInfo(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success(mapZipList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }

    @RequestMapping(value = "area/mapinfo/getPosition", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult testRabbitMQ(HttpServletRequest request, @RequestParam("deviceId") String deviceId) {
        try {
            return AjaxResult.success(CacheInfoManager.getMessageCache(deviceId));
        } catch (Exception e) {
            LOGGER.error("getPosition exception", e);
            return AjaxResult.failed();
        }
    }
}
