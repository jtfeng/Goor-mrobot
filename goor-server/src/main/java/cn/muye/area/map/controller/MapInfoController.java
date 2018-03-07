package cn.muye.area.map.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.CurrentInfo;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.i18n.service.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jelynn on 2017/7/6.
 */
@Controller
public class MapInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapInfoController.class);

    @Autowired
    private MapInfoService mapInfoService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = "area/mapinfo", method = {RequestMethod.POST})
//    @PreAuthorize("hasAuthority('area_mapinfo_u')")
    @ResponseBody
    public AjaxResult setMapAlias(@RequestBody MapInfo mapInfo) {
        try {
            Long id = mapInfo.getId();
            if (null == id) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_DTDZJIDBNWK"));
            }
            MapInfo mapInfoDB = mapInfoService.getMapInfo(id);
            if (null == mapInfoDB) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_DTSJBCZ"));
            }
            mapInfoDB.setMapAlias(mapInfo.getMapAlias());
            mapInfoDB.setPngDesigned(mapInfo.getPngDesigned());
            mapInfoDB.setFloor(mapInfo.getFloor());
            mapInfoDB.setLogicFloor(mapInfo.getLogicFloor());
            mapInfoService.update(mapInfoDB);
            return AjaxResult.success(mapInfoDB, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_XGCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_XTCW"));
        }
    }

    @RequestMapping(value = "area/mapinfo", method = {RequestMethod.GET})
//    @PreAuthorize("hasAuthority('area_mapinfo_r')")
    @ResponseBody
    public AjaxResult getMapInfo(WhereRequest whereRequest) {
        try {
            List<MapInfo> mapZipList = mapInfoService.getMapInfo(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success(mapZipList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_CXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_XTCW"));
        }
    }

    /**
     * 级联查询目标点，场景名 -》 地图名
     * 如果传入sceneName，则查询该场景下的地图
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/mapinfo/cascade", method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('area_mapinfo_r')")
    @ResponseBody
    public AjaxResult cascadeMapPoint(@RequestParam("level") int level, @RequestParam(value = "sceneName", required = false) String sceneName) {
        try {
            List<CascadePoint> cascadeSceneMapNameList = mapInfoService.cascadeSceneMapName(level, sceneName);
            if (null == cascadeSceneMapNameList && !StringUtil.isNullOrEmpty(sceneName)) {
                LOGGER.info("指定场景名sceneName = " + sceneName + "不存在");
                return AjaxResult.success(new ArrayList<>());
            }
            return AjaxResult.success(cascadeSceneMapNameList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_XTCW"));
        }
    }

    @RequestMapping(value = "area/mapinfo/getCurrentInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getCurrentInfo(HttpServletRequest request, @RequestParam("code") String code) {
        try {
           CurrentInfo currentInfo = mapInfoService.getCurrentInfo(code);
           if (null == currentInfo){
               LOGGER.error("未获取到当前状态信息");
               return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_WHQDDQZTXX"));
           }
            return AjaxResult.success(currentInfo, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_HQCG"));
        } catch (Exception e) {
            LOGGER.error("getCurrentInfo exception", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapInfoController_java_HQJQRZTXXCC"));
        }
    }
}
