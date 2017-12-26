package cn.muye.assets.scene.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.util.UserUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/7/3.
 */
@RestController
public class SceneController {

    private static final Logger log = LoggerFactory.getLogger(SceneController.class);
    @Autowired
    private SceneService sceneService;

    /**
     * 传入一个 session ID，将场景对应的信息存入到 session
     *
     * @param sceneId
     * @param request
     * @return
     */
    @RequestMapping(value = "/assets/scene/session/{sceneId}", method = RequestMethod.POST)
    public AjaxResult storeSceneInfoToSession(@PathVariable("sceneId") String sceneId, HttpServletRequest request) {
        try {
            Scene scene = this.sceneService.storeSceneInfoToSession(Constant.RECORD_SCENE_SOURCE_PC, sceneId, UserUtil.getUserTokenValue());
            return AjaxResult.success(scene, "保存场景信息到用户会话中成功!");
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 创建新的场景
     *
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.POST)
    public AjaxResult createScene(@RequestBody Scene scene) {
        // TODO: 21/07/2017 创建新场景
        try {
            Object taskResult = sceneService.saveScene(scene);
            return AjaxResult.success(taskResult, "新增场景信息成功!");
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage(), "新增场景信息失败");
        }
    }

    /**
     * 更新指定的场景信息
     *
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.PUT)
    public AjaxResult updateScene(@RequestBody Scene scene) {
        // TODO: 21/07/2017 更新指定的场景信息
        try {
            log.info("更新指定的场景信息");
            Object taskResult = sceneService.updateScene(scene);
            if (taskResult instanceof AjaxResult){
                return (AjaxResult)taskResult;
            }
            return AjaxResult.success(taskResult, "修改场景信息成功！");
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage(), "修改场景信息失败！");
        }
    }

    /**
     * 删除指定场景
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/assets/scene/{id}", method = RequestMethod.DELETE)
    public AjaxResult deletecene(@PathVariable("id") String id) {
        // TODO: 21/07/2017 根据传入的场景 ID 编号，删除对应的场景与绑定的机器人和地图信息
        try {
            sceneService.deleteSceneById(Long.parseLong(id));
            return AjaxResult.success(id, "删除场景信息成功！");
        } catch (Exception e) {
            return AjaxResult.failed(e, "删除场景信息失败！");
        }
    }

    /**
     * 分页查询场景信息
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.GET)
    public AjaxResult sceneList(WhereRequest whereRequest) {
        // TODO: 21/07/2017 分页列表展示场景数据信息
        try {
            List<Scene> list = sceneService.listScenes(whereRequest);
            PageInfo<Scene> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询成功");
        } catch (Exception e) {
            return AjaxResult.failed(e, "查询失败");
        }
    }

    /**
     * 同步地图与机器人之间的信息
     *
     * @param sceneId
     * @return
     */
    @RequestMapping(value = "/assets/scene/sync/{sceneId}", method = RequestMethod.GET)
    public AjaxResult sendSyncMapMessageToRobots(@PathVariable("sceneId") String sceneId) {
        try {
            Object taskResult = sceneService.sendSyncMapMessageToRobots(Long.parseLong(sceneId));
            return AjaxResult.success(taskResult, "操作成功");
        } catch (Exception e) {
            return AjaxResult.failed();
        }

    }

    /**
     * 同步地图与机器人之间的信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/scene/sync/specialRobots", method = RequestMethod.POST)
    public AjaxResult sendSyncMapMessageToSpecialRobots(@RequestBody Map<String, Object> body) {
        try {
            //sceneId
            //robotIds
            log.info(String.format("接收到的参数为：%s", body.toString()));
            Object taskResult = sceneService.sendSyncMapMessageToSpecialRobots(body);
            return AjaxResult.success(taskResult, "操作成功");
        } catch (Exception e) {
            return AjaxResult.failed();
        }

    }

    @RequestMapping(value = "/assets/scene/checkSceneIsNeedToBeUpdated", method = RequestMethod.GET)
    public Object checkSceneIsNeedToBeUpdated(String mapSceneName, String storeId) throws Exception {
        this.sceneService.checkSceneIsNeedToBeUpdated(mapSceneName, storeId);
        return Arrays.asList("ok", "jay");
    }

    @GetMapping("/getRobotStartAssets/{robotCode}")
    public Object getRobotStartAssets(@PathVariable("robotCode") String robotCode) throws Exception {
        // Noah_A005


//        // 当前指定机器人的 ID 编号信息
//        Long robotId = currentRobot.getId();
//        // 新选择的场景 ID 编号（sceneId、stationIds、chargerMapPointIds）
//        Long sceneId = latestRobotAssets.getLong("sceneId");
//        // 新选择的站 ID 编号数组信息
//        JSONArray stationIds = latestRobotAssets.getJSONArray("stationIds");
//        // 新选择的充电桩 ID 编号数组信息
//        JSONArray chargerMapPointIds = latestRobotAssets.getJSONArray("chargerMapPointIds");


        JSONObject test = new JSONObject();
        test.put("sceneId", 85);
        JSONArray stationIds = new JSONArray();
        stationIds.add(37);stationIds.add(38);
        JSONArray chargerMapPointIds = new JSONArray();
        chargerMapPointIds.add(1589);chargerMapPointIds.add(1590);
        test.put("stationIds", stationIds);
        test.put("chargerMapPointIds", chargerMapPointIds);

//        sceneService.updateGetRobotStartAssets(robotCode,test);
        return sceneService.getRobotStartAssets(robotCode);
    }
}