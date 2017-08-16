package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.muye.assets.roadpath.service.RoadPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RoadPathController {

    private static final Logger log = LoggerFactory.getLogger(RoadPathController.class);

    @Autowired
    private RoadPathService roadPathService;

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

}