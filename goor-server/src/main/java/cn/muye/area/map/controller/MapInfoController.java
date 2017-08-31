package cn.muye.area.map.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.bean.constant.TopicConstants;
import cn.mrobot.bean.websocket.WSMessage;
import cn.mrobot.bean.websocket.WSMessageType;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.bean.CurrentInfo;
import cn.muye.area.map.service.MapInfoService;
import cn.muye.base.bean.MessageInfo;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.websoket.WebSocketInit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 级联查询目标点，场景名 -》 地图名
     * 如果传入sceneName，则查询该场景下的地图
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "area/mapinfo/cascade", method = RequestMethod.GET)
    @ResponseBody
//	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
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
            return AjaxResult.failed("系统错误");
        }
    }


//    @Autowired
//    private WebSocketInit webSocketInit;
//    /**
//     * 级联查询目标点，场景名 -》 地图名
//     * 如果传入sceneName，则查询该场景下的地图
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "area/test/websocket", method = RequestMethod.GET)
//    @ResponseBody
////	@PreAuthorize("hasAuthority('mrc_missionnode_r')")
//    public AjaxResult testWebsocket(@RequestParam("message") String message) {
//        try {
//            webSocketInit.sendAll(new WSMessage("低电量警告", WSMessageType.WARNING,message,"server"));
//            return AjaxResult.success();
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return AjaxResult.failed("系统错误");
//        }
//    }

    @RequestMapping(value = "area/mapinfo/getCurrentInfo", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult getCurrentInfo(HttpServletRequest request, @RequestParam("code") String code) {
        try {
           CurrentInfo currentInfo = mapInfoService.getCurrentInfo(code);
           if (null == currentInfo){
               LOGGER.error("未获取到当前位置信息");
               return AjaxResult.failed("未获取到当前位置信息");
           }
            return AjaxResult.success(currentInfo, "获取成功");
        } catch (Exception e) {
            LOGGER.error("getCurrentInfo exception", e);
            return AjaxResult.failed("获取机器人状态信息出错");
        }
    }
}
