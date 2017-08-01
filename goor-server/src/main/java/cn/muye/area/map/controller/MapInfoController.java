package cn.muye.area.map.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.charge.ChargeInfo;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.CurrentInfo;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.log.state.StateCollectorService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    @Autowired
    private StateCollectorService stateCollectorService;

    @Autowired
    private RobotService robotService;


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
            mapInfoDB.setPngDesigned(mapInfo.getPngDesigned());
            mapInfoDB.setFloor(mapInfo.getFloor());
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

    @RequestMapping(value = "area/mapinfo/getCurrentInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getCurrentInfo(HttpServletRequest request, @RequestParam("code") String code) {
        try {
            //从缓存中获取当前机器的坐标
            CurrentInfo currentInfo = new CurrentInfo();

            //获取开机状态
            Robot robot = robotService.getByCode(code, SearchConstants.FAKE_MERCHANT_STORE_ID);
            currentInfo.setOnline(robot.getOnline());
            if(!robot.getOnline()){
                LOGGER.info("机器人（" + code + "）不在线");
                return AjaxResult.success(currentInfo,"获取当前信息成功");
            }

            MessageInfo currentPoseInfo = CacheInfoManager.getMessageCache(code);
            if (null != currentPoseInfo) {
                parsePoseData(currentPoseInfo);
                currentInfo.setPose(parsePoseData(currentPoseInfo));
            } else {
                LOGGER.info("未获取到当前机器人（" + code + "）实时坐标");
            }

            //根据机器人code获取地图信息
            currentInfo.setMapInfo(getCurrentMapInfo(code));

            //获取当前电量信息
            ChargeInfo chargeInfo = CacheInfoManager.getRobotChargeInfoCache(code);
            if (chargeInfo != null) {
                currentInfo.setChargeInfo(chargeInfo);
            }

            //过滤状态。封装成List<StateDetail> 返回
            currentInfo.setList(stateCollectorService.getCurrentTriggeredState(code));

            //添加当前任务状态
            currentInfo.setMission(stateCollectorService.collectTaskLog(code));

            return AjaxResult.success(currentInfo, "获取当前信息成功");
        } catch (IllegalAccessException e) {
            LOGGER.error("getCurrentInfo exception", e);
            return AjaxResult.failed("获取机器人状态信息出错");
        } catch (Exception e) {
            LOGGER.error("getCurrentInfo exception", e);
            return AjaxResult.failed("获取机器人状态信息出错");
        }
    }

    private String parsePoseData(MessageInfo messageInfo) {
        JSONObject jsonObject = JSON.parseObject(messageInfo.getMessageText());
        String poseData = jsonObject.getString("pose");
        JSONObject poseObject = JSON.parseObject(poseData);
        return poseObject.getString("pose");
    }

    /**
     * 获取机器人当前地图信息
     * @param code
     * @return
     */
    private MapInfo getCurrentMapInfo(String code) {
        //根据场景名和地图名获取地图信息
        MessageInfo currentMap = CacheInfoManager.getMapCurrentCache(code);
        if (null != currentMap) {
            JSONObject jsonObject = JSON.parseObject(currentMap.getMessageText());
            String data = jsonObject.getString(TopicConstants.DATA);
            JSONObject object = JSON.parseObject(data);
            Integer errorCode = object.getInteger(SearchConstants.SEARCH_ERROR_CODE);
            if (errorCode != null && errorCode == 0) {
                String mapData = object.getString(TopicConstants.DATA);
                JSONObject mapObject = JSON.parseObject(mapData);
                String mapName = mapObject.getString(TopicConstants.MAP_NAME);
                String sceneName = mapObject.getString(TopicConstants.SCENE_NAME);
                MapInfo mapInfo = CacheInfoManager.getMapOriginalCache(FileUtils.parseMapAndSceneName(mapName, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID));
                if (mapInfo != null) {
                   return mapInfo;
                } else {
                    LOGGER.info("未找到地图信息 name=" + mapName + "，sceneName=" + sceneName);
                }
            }
        } else {
            LOGGER.info("未获取到当前机器人（" + code + "）实时地图");
        }
        return null;
    }

}
