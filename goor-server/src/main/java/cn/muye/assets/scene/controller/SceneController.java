package cn.muye.assets.scene.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.service.MapSyncService;
import cn.muye.assets.rfidbracelet.service.RfidBraceletService;
import cn.muye.assets.scene.service.SceneService;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by admin on 2017/7/3.
 */
@RestController
public class SceneController {

    private static final Logger log = LoggerFactory.getLogger(SceneController.class);
    @Autowired
    private SceneService sceneService;

    /**
     * 修改场景别名（Alias Name）
     * @param sceneId
     * @param aliasName
     * @return
     */
    @RequestMapping(value = "/assets/scene/{sceneId}/{aliasName}", method = RequestMethod.POST)
    public AjaxResult updateAliasName(@PathVariable("sceneId") String sceneId, @PathVariable("aliasName") String aliasName) {
        try {
            if (aliasName == null || "".equals(aliasName.trim())){
                return AjaxResult.failed("别名内容不允许为空");
            }
            Scene scene = sceneService.updateAliasName(Long.parseLong(sceneId), aliasName);
            return AjaxResult.success(scene, "更改场景信息别名成功!");
        }catch (Exception e){
            return AjaxResult.failed(e, "更改场景信息别名失败");
        }
    }

    /**
     * 传入一个 session ID，将场景对应的信息存入到 session
     * @param sceneId
     * @param request
     * @return
     */
    @RequestMapping(value = "/assets/scene/session/{sceneId}", method = RequestMethod.POST)
    public AjaxResult storeSceneInfoToSession(@PathVariable("sceneId") String sceneId, HttpServletRequest request) {
        try {
            if (sceneId == null || "".equals(sceneId.trim())){
                return AjaxResult.failed("请传入合法的 sceneId 值");
            }
            log.info("传入的场景 ID 编号为 ：" + sceneId);
            Scene scene = sceneService.getById(Long.parseLong(sceneId));
            if (scene == null){
                return AjaxResult.failed("指定场景编号的场景信息不存在，保存 session 信息失败");
            }
            request.getSession().setAttribute(Constant.SCENE_SESSION_TAG, scene);
            Scene sceneTemp = (Scene) request.getSession().getAttribute(Constant.SCENE_SESSION_TAG);
            log.info("传入 session 中的场景信息为：" + sceneTemp);
            return AjaxResult.success(scene, "保存场景信息到 session 中成功!");
        }catch (Exception e){
            return AjaxResult.failed(e, "保存场景信息到 session 中失败");
        }
    }

    /**
     * 创建新的场景
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.POST)
    public AjaxResult createScene(@RequestBody Scene scene) {
        try {
            log.info(scene.toString());
            sceneService.save(scene);
            return AjaxResult.success(scene, "新增场景信息成功!");
        }catch (Exception e){
            return AjaxResult.failed(e, "新增场景信息失败");
        }
    }

    /**
     * 删除指定场景
     * @param id
     * @return
     */
    @RequestMapping(value = "/assets/scene/{id}", method = RequestMethod.DELETE)
    public AjaxResult deletecene(@PathVariable("id") String id){
        try {
            sceneService.deleteById(Long.parseLong(id));
            return AjaxResult.success(id,"删除场景信息成功！");
        }catch (Exception e){
            return AjaxResult.failed(e,  "删除场景信息失败！");
        }
    }

    /**
     * 更新指定的场景信息
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.PUT)
    public AjaxResult updateScene(@RequestBody Scene scene){
        try {
            sceneService.update(scene);
            return AjaxResult.success(scene,"修改场景信息成功！");
        }catch (Exception e){
            return AjaxResult.failed(e, "修改场景信息失败！");
        }
    }

    /**
     * 分页查询场景信息
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "/assets/scene", method = RequestMethod.GET)
    public AjaxResult sceneList(WhereRequest whereRequest) {
        try {
            List<Scene> list = sceneService.listScenes(whereRequest);
            PageInfo<Scene> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询成功");
        }catch (Exception e){
            Example example = new Example(RfidBracelet.class);
            return AjaxResult.failed(e,"查询失败");
        }
    }

    /**
     * 绑定场景信息与地图信息的多对多关系
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene/bindSceneAndMapRelations", method = RequestMethod.POST)
    public AjaxResult bindSceneAndMapRelations(@RequestBody Scene scene) {
        try {
            Long sceneId = Preconditions.checkNotNull(scene.getId());
            List<MapInfo> mapInfos = Preconditions.checkNotNull(scene.getMapInfos());
            List<Long> ids = new ArrayList<>();
            for (MapInfo mapInfo : mapInfos){
                ids.add(mapInfo.getId());
            }
            //删除历史关系数据
            sceneService.deleteMapAndSceneRelations(sceneId);
            int updateCount = 0;
            if (ids.size() != 0) {
                updateCount = sceneService.insertSceneAndMapRelations(scene.getId(), ids);
            }
            return AjaxResult.success("修改记录的行数为：" + updateCount, "绑定场景与地图信息多对多关系成功");
        }catch (Exception e){
            Example example = new Example(RfidBracelet.class);
            return AjaxResult.failed(e,"绑定场景与地图信息多对多关系失败");
        }
    }

    /**
     * 绑定场景信息与机器人信息的多对多关系
     * @param scene
     * @return
     */
    @RequestMapping(value = "/assets/scene/bindSceneAndRobotRelations", method = RequestMethod.POST)
    public AjaxResult bindSceneAndRobotRelations(@RequestBody Scene scene) {
        try {
            Long sceneId = Preconditions.checkNotNull(scene.getId());
            List<Robot> robots = Preconditions.checkNotNull(scene.getRobots());
            List<Long> ids = new ArrayList<>();
            for (Robot robot : robots){
                ids.add(robot.getId());
            }
            //删除历史关系数据
            sceneService.deleteRobotAndSceneRelations(sceneId);
            int updateCount = 0;
            if (ids.size() != 0) {
                updateCount = sceneService.insertSceneAndRobotRelations(scene.getId(), ids);
            }
            return AjaxResult.success("修改记录的行数为：" + updateCount, "绑定场景与机器人信息多对多关系成功");
        }catch (Exception e){
            Example example = new Example(RfidBracelet.class);
            return AjaxResult.failed(e,"绑定场景与机器人信息多对多关系失败");
        }
    }

    /**
     * 同步地图与机器人之间的信息
     * @param sceneId
     * @return
     */
    @RequestMapping(value = "/assets/scene/sync/{sceneId}", method = RequestMethod.GET)
    public AjaxResult sendSyncMapMessageToRobots(@PathVariable("sceneId") String sceneId) {
        // TODO: 20/07/2017 需要测试地图与机器人之间的同步
        try {
            sceneService.sendSyncMapMessageToRobots(Long.parseLong(sceneId));
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed();
        }
    }
}