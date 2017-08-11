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
import java.util.Map;

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

    @Autowired
    private RobotService robotService;

    @Autowired
    private MapSyncService mapSyncService;

    @Autowired
    private SceneMapZipXREFService sceneMapZipXREFService;

    @RequestMapping(value = "area/mapzip", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult saveMapZip(@RequestBody MapZip mapZip) {
        try {
            MapZip mapZipCondition = new MapZip();
            mapZipCondition.setFileName(mapZip.getFileName());
            List<MapZip> mapZipList = mapZipService.list(mapZipCondition);
            if (mapZipList.size() > 0 && mapZipList.get(0).getId() != mapZip.getId()) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "文件名重复");
            }
            if (mapZip.getId() != null && mapZip.getId() > 0) {
                mapZipService.update(mapZip);
                return AjaxResult.success(mapZip, "修改成功");
            } else {
                mapZipService.save(mapZip);
                return AjaxResult.success(mapZip, "保存成功");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }


    @RequestMapping(value = "area/mapzip", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult listMapZip(WhereRequest whereRequest) {
        try {

            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();

            pageNo = pageNo == null ? 1 : pageNo;
            pageSize = pageSize == null ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            List<MapZip> mapZipList = mapZipService.list(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<MapZip> page = new PageInfo<>(mapZipList);
            return AjaxResult.success(page, "查询成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }

    @RequestMapping(value = "area/mapzip/{id}", method = RequestMethod.DELETE)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
    public AjaxResult deleteMapZip(@PathVariable Long id) {
        try {
            if (null == id) {
                return AjaxResult.failed(2, "参数错误(id不能为空)");
            }

            MapZip mapZip = mapZipService.getMapZip(id);
            if (null == mapZip) {
                return AjaxResult.failed("删除对象不存在");
            }
            mapZipService.delete(mapZip);
            return AjaxResult.success("删除成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
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
                return AjaxResult.failed("地图压缩包不存在");
            }
            List<Long> deviceIds = new ArrayList<>();
            if (!StringUtil.isNullOrEmpty(bindStr)) {
                deviceIds = JSONArray.parseArray(bindStr, Long.class);
            }
            List<Robot> robotList = new ArrayList<>();
            for (int i = 0; i < deviceIds.size(); i++) {
                robotList.add(robotService.getById(deviceIds.get(i)));
            }
            Map<String, AjaxResult> result;
            if (robotList.size() <= 0 || robotList.isEmpty()) {
                result = mapSyncService.syncMap(mapZip, SearchConstants.FAKE_MERCHANT_STORE_ID);
            } else {
                result = mapSyncService.syncMap(mapZip, robotList);
            }
            return AjaxResult.success(result, "地图同步请求发送成功");
        } catch (Exception e) {
            LOGGER.error("地图同步出错", e);
            return AjaxResult.failed("系统错误");
        }
    }

    @Autowired
    private MapAnalysisService mapAnalysisService;

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
            return AjaxResult.success("地图压缩包解压成功");
        } catch (Exception e) {
            LOGGER.error("地图同步出错", e);
            return AjaxResult.failed("系统错误");
        }
    }
}
