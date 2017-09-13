package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.service.RoadPathService;
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
            return AjaxResult.success();
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
     * 分页列表查询路径信息
     * @param whereRequest
     * @return
     */
    @GetMapping("/asset/roadPath")
    public AjaxResult roadPathList(WhereRequest whereRequest) {
        try {
            List<RoadPath> list = roadPathService.listRoadPaths(whereRequest);
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
}