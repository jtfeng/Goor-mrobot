package cn.muye.area.map.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.map.SceneMapZipXREF;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapAnalysisService;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.area.map.service.MapZipService;
import cn.muye.area.map.service.SceneMapZipXREFService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 9:46
 * Describe:
 * Version:1.0
 * @author Jelynn
 */
@Controller
public class MapZipController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapZipController.class);

    @Autowired
    private MapZipService mapZipService;

    @Autowired
    private RobotService robotService;

    @Autowired
    private MapSyncService mapSyncService;

    @Autowired
    private MapAnalysisService mapAnalysisService;

    @Autowired
    private SceneMapZipXREFService sceneMapZipXREFService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @RequestMapping(value = "area/mapzip", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult saveMapZip(@RequestBody MapZip mapZip) {
        try {
            MapZip mapZipCondition = new MapZip();
            mapZipCondition.setFileName(mapZip.getFileName());
            List<MapZip> mapZipList = mapZipService.list(mapZipCondition);
            if (mapZipList.size() > 0 && mapZipList.get(0).getId() != mapZip.getId()) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_WJMZF"));
            }
            if (mapZip.getId() != null && mapZip.getId() > 0) {
                mapZipService.update(mapZip);
                return AjaxResult.success(mapZip, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XGCG"));
            } else {
                mapZipService.save(mapZip);
                return AjaxResult.success(mapZip, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_BCCG"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }


    @RequestMapping(value = "area/mapzip", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult listMapZip(WhereRequest whereRequest) {
        try {

            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();

            pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
            pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            List<MapZip> mapZipList = mapZipService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<MapZip> page = new PageInfo<MapZip>(mapZipList);
            return AjaxResult.success(page, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_CXCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }


    @RequestMapping(value = "area/mapzip/{id}", method = RequestMethod.DELETE)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult deleteMapZip(@PathVariable Long id) {
        try {
            if (null == id) {
                return AjaxResult.failed(2, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_CSCWIDBNWK"));
            }

            MapZip mapZip = mapZipService.getMapZip(id);
            if (null == mapZip) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_SCDXBCZ"));
            }
            mapZipService.delete(mapZip);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_SCCG"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }

    /**
     * 地图同步
     *
     * @param id
     * @param bindStr
     * @return
     */
    @RequestMapping(value = "area/mapZip/sync", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult syncMap(@RequestParam("id") Long id, @RequestParam(value = "deviceIds", required = false) String bindStr) {
        try {

            MapZip mapZip = mapZipService.getMapZip(id);
            if (null == mapZip) {
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_DTYSBBCZ"));
            }
            List<Long> deviceIds = new ArrayList<>();
            if (!StringUtil.isNullOrEmpty(bindStr)) {
                deviceIds = JSONArray.parseArray(bindStr, Long.class);
            }
            List<Robot> robotList = new ArrayList<>();
            for (int i = 0; i < deviceIds.size(); i++) {
                robotList.add(robotService.getById(deviceIds.get(i)));
            }
            Object result;
            if (robotList.size() <= 0 || robotList.isEmpty()) {
                result = mapSyncService.syncMap(mapZip, SearchConstants.FAKE_MERCHANT_STORE_ID);
            } else {
                result = mapSyncService.syncMap(mapZip, robotList);
            }
            return AjaxResult.success(result, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_DTTBQQFSCG"));
        } catch (Exception e) {
            LOGGER.error("地图同步出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }

    /**
     * 解压地图压缩包（仅做还原数据）
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "area/mapZip/unzip", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult unzip(@RequestParam("id") Long id) {
        try {
            MapZip mapZip = mapZipService.getMapZip(id);
            File saveFile = mapAnalysisService.unzipMapZipFile(mapZip);
            List<SceneMapZipXREF> sceneMapZipXREFList = sceneMapZipXREFService.list(id);
            List<String> sceneNames = new ArrayList<>();
            for (int i = 0; i < sceneMapZipXREFList.size(); i++) {
                sceneNames.add(sceneMapZipXREFList.get(i).getSceneName());
            }

            if (saveFile.exists()) {
                mapAnalysisService.analysisFile(saveFile, sceneNames, mapZip);
            }
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_DTYSBJYCG"));
        } catch (Exception e) {
            LOGGER.error("地图同步出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }

    /**
     * 根据场景地图名更新机器人地图
     *
     */
    @RequestMapping(value = "area/mapZip/mapSync", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult mapSync(@RequestParam("mapSceneName") String mapSceneName,
                              @RequestParam("deviceIds") String bindStr,
                              @RequestParam("sceneId") Long sceneId) {
        try {
            List<Long>  deviceIds = new ArrayList<Long>();
            if (!StringUtil.isNullOrEmpty(bindStr)) {
                deviceIds = JSONArray.parseArray(bindStr, Long.class);
            }
            List<Robot> robotList = new ArrayList<>();
            for (int i = 0; i < deviceIds.size(); i++) {
                robotList.add(robotService.getById(deviceIds.get(i)));
            }
            mapSyncService.sendMapSyncMessageNew(robotList, mapSceneName,sceneId);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_DTTBCG"));
        } catch (Exception e) {
            LOGGER.error("地图同步出错", e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_area_map_controller_MapZipController_java_XTCW"));
        }
    }
}
