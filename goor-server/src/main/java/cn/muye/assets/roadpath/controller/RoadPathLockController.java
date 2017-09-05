package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.roadpath.RoadPathDetail;
import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.assets.roadpath.service.RoadPathService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class RoadPathLockController {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockController.class);

    @Autowired
    private RoadPathLockService roadPathLockService;

    @GetMapping("/asset/roadpathlock")
    public AjaxResult testMethod(HttpServletRequest request){
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        RoadPathLock roadPathLock = new RoadPathLock();
        roadPathLock.setName("LOCK - 1");
        roadPathLock.setLockAction(RoadPathLock.LockAction.LOCK);
        roadPathLockService.save(roadPathLock, request);
        roadPathLock.setName("LOCK - 2");
        roadPathLock.setLockAction(RoadPathLock.LockAction.UNLOCK);
        roadPathLockService.save(roadPathLock, request);

        Long id = roadPathLock.getId();
        RoadPathLock chaxunPathLock = roadPathLockService.findById(id);
        System.out.println(chaxunPathLock);

        chaxunPathLock.setName("TestLockAction");
        chaxunPathLock.setLockAction(RoadPathLock.LockAction.LOCK);
        roadPathLockService.updateSelective(roadPathLock);

        List<RoadPathLock> roadPathLocks = roadPathLockService.listAll();
        System.out.println(roadPathLocks.size());
        System.out.println(roadPathLocks);

        roadPathLockService.deleteById(id);

        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        return AjaxResult.success();
    }
}