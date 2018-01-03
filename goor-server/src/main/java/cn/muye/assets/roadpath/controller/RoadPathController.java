package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.assets.scene.service.SceneService;
import cn.muye.base.bean.SearchConstants;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RoadPathController {

    private static final Logger log = LoggerFactory.getLogger(RoadPathController.class);

    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private SceneService sceneService;

    /**
     * 创建
     * @param body
     * @return
     */
    @PostMapping("/asset/roadPath")
    public AjaxResult createRoadPath(@RequestBody Map<String, Object> body){
        // 拟合方式现假定有三种 （1：直线；2：曲线；3.云端定义）
        // String -> pathName
        // String -> pattern
        // String -> data
        // List<Long> -> points -> 前端传递格式为 一个数组
        // Long cloudSceneId
        try {
            roadPathService.createRoadPath(body);
            return AjaxResult.success("路径添加成功");
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 更新
     * @param body
     * @return
     */
    @RequestMapping(value = "/asset/roadPath", method = RequestMethod.PUT)
    public AjaxResult updateRoadPath(@RequestBody Map<String, Object> body){
        try {
            roadPathService.updateRoadPath(body);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除指定的道路路径编号
     * @param id
     * @return
     */
    @ RequestMapping(value = "/asset/roadPath/{id}", method = RequestMethod.DELETE)
    public AjaxResult deleteRoadPath(@PathVariable("id") String id){
        try {
            this.roadPathService.deleteById(Long.parseLong(id));
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除指定场景的所有路径，包括云端路径和工控路径
     * @param sceneId
     * @return
     */
    @RequestMapping(value = "/asset/roadPath/deleteBySceneId/{sceneId}", method = RequestMethod.DELETE)
    public AjaxResult deleteRoadPathBySceneId(@PathVariable("sceneId") Long sceneId){
        try {
            Scene scene = sceneService.getSceneById(sceneId);
            String sceneName = scene.getMapSceneName();
            if(StringUtil.isNullOrEmpty(sceneName)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,sceneId + "云端场景未绑定有效的工控场景");
            }
            this.roadPathService.deleteBySceneName(sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success("操作成功");
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除指定场景的所有路径，包括云端路径和工控路径
     * @param sceneId
     * @return
     */
    @RequestMapping(value = "/asset/roadPath/deleteBySceneIdType", method = RequestMethod.DELETE)
    public AjaxResult deleteRoadPathBySceneIdType(@RequestParam(value = "sceneId", required = true) Long sceneId,
                                                  @RequestParam(value = "pathType", required = false) Integer pathType,
                                                  @RequestParam(required = false) String mapName){
        try {
            Scene scene = sceneService.getSceneById(sceneId);
            String sceneName = scene.getMapSceneName();
            if(StringUtil.isNullOrEmpty(sceneName)) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,sceneId + "云端场景未绑定有效的工控场景");
            }
            this.roadPathService.deleteBySceneMapNameType(sceneName, pathType, mapName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success("操作成功");
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 分页列表查询路径信息
     * @param whereRequest
     * @return
     */
    @GetMapping("/asset/roadPath")
    public AjaxResult roadPathList(WhereRequest whereRequest) {
        try {
            List<RoadPath> list = roadPathService.listRoadPaths(whereRequest , SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<RoadPath> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询成功");
        } catch (Exception e) {
            return AjaxResult.failed(e,         "查询失败");
        }
    }

    /**
     * 根据 开始点 和 结束点 查询路径
     * @return
     */
    @GetMapping("/asset/roadPath/{startPoint}/{endPoint}/{sceneName}/{mapName}")
    public AjaxResult findRoadPathByStartAndEndPoint(
            @PathVariable("startPoint") Long startPoint,
            @PathVariable("endPoint") Long endPoint,
            @PathVariable("sceneName") String sceneName,
            @PathVariable("mapName") String mapName){
        try {
            List<RoadPathDetail> roadPathDetails = this.roadPathService.findRoadPathByStartAndEndPoint(startPoint, endPoint, sceneName, mapName);
            return AjaxResult.success(roadPathDetails);
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 未指定的路径绑定指定的逻辑锁对象
     * @return
     */
    @GetMapping("/asset/roadPath/bind/{pathId}/{lockId}")
    public AjaxResult findRoadPathByStartAndEndPoint(
            @PathVariable("pathId") Long pathId,
            @PathVariable("lockId") Long lockId){
        try {
            RoadPath roadPath = this.roadPathService.findById(pathId);
            if (lockId != 0L) {
                roadPath.setPathLock(lockId);
            }else {
                roadPath.setPathLock(null);
            }
            roadPathService.update(roadPath);
            if (lockId != 0L) {
                return AjaxResult.success("路径绑定逻辑锁对象成功");
            }else {
                return AjaxResult.success("解除路径锁对象成功");
            }
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

}