package cn.muye.area.map.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.CurrentPose;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

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
            if (null == id) {
                return AjaxResult.failed("地图的主键ID不能为空");
            }
            MapInfo mapInfoDB = mapInfoService.getMapInfo(id);
            if (null == mapInfoDB) {
                return AjaxResult.failed("地图数据不存在");
            }
            mapInfoDB.setMapAlias(mapInfo.getMapAlias());
            mapInfoService.update(mapInfoDB);
            return AjaxResult.success(mapInfoDB, "修改成功");
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
            return AjaxResult.success(mapZipList, "查询成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed("系统错误");
        }
    }

    @RequestMapping(value = "area/mapinfo/getPosition", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getPosition(HttpServletRequest request, @RequestParam("code") String code) {
        try {
            //TODO 从redis中获取当前机器的坐标
            CurrentPose currentPose = new CurrentPose();
            MessageInfo currentPoseInfo = CacheInfoManager.getMessageCache(code);
            if (null == currentPoseInfo) {
                return AjaxResult.failed("未获取到当前机器人（" + code + "）实时坐标");
            }
            if (StringUtils.isEmpty(currentPoseInfo.getMessageText())) {
                LOGGER.error("currentPoseInfo 的 MessageText为空");
                return AjaxResult.failed("获取到的当前机器人（" + code + "）实时坐标数据有误");
            }
            parsePoseData(currentPoseInfo);
            currentPose.setPose(parsePoseData(currentPoseInfo));
            //TODO 根据场景名和地图名获取地图信息
            MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
            if (null == currentMap) {
                return AjaxResult.failed("未获取到当前机器人（" + code + "）实时地图");
            }
            if (StringUtils.isEmpty(currentMap.getMessageText())) {
                LOGGER.error("currentMap的MessageText为空");
                return AjaxResult.failed("获取到的当前机器人（" + code + "）实时地图数据有误");
            }
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            String mapData = object.getString(TopicConstants.DATA);
            JSONObject mapDataObject = JSON.parseObject(mapData);
            String mapName = mapDataObject.getString(TopicConstants.MAP_NAME);
            String sceneName = mapDataObject.getString(TopicConstants.SCENE_NAME);

            MapInfo mapInfo = mapInfoService.getMapInfo(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            if (mapInfo == null) {
                return AjaxResult.failed("未找到地图信息 name=" + mapName + "，sceneName=" + sceneName);
            }
            //TODO 按照数据格式封装给前端
            currentPose.setMapInfo(mapInfo);
            return AjaxResult.success(currentPose, "获取当前位置信息成功");
        } catch (Exception e) {
            LOGGER.error("getPosition exception", e);
            return AjaxResult.failed("获取机器人位置信息出错");
        }
    }

    private String parsePoseData(MessageInfo messageInfo) {
        JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
        String poseData = jsonObject.getString("pose");
        JSONObject poseObject = JSON.parseObject(poseData);
        return poseObject.getString("pose");
    }
}
