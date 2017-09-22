package cn.muye.area.fixpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.muye.area.fixpath.service.FixPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jelynn on 2017/9/18.
 */
@RestController
public class FixPathController {

    private static final Logger logger = LoggerFactory.getLogger(FixPathController.class);

    @Autowired
    private FixPathService fixPathService;

    @RequestMapping(value = "area/fixpath/{sceneId}", method = RequestMethod.GET)
    public AjaxResult sendFixPathQuery(@PathVariable("sceneId") Long sceneId) {
        try {
           return fixPathService.sendFixpathQuery(sceneId);
        } catch (Exception e) {
            logger.error(" 获取工控固定路径出错", e);
            return AjaxResult.failed("请求失败");
        }
    }
}
