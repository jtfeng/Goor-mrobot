package cn.muye.assets.roadpath.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author wlkfec
 */
@RestController
@RequestMapping("roadPathLock")
public class RoadPathLockController {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockController.class);

    @Autowired
    private RoadPathLockService roadPathLockService;
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    @GetMapping("/asset/roadpathlock")
    public AjaxResult testMethod(){
        log.info(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        RoadPathLock roadPathLock = new RoadPathLock();
        roadPathLock.setName("LOCK - 1");
        roadPathLock.setLockAction(RoadPathLock.LockAction.LOCK);
        roadPathLockService.save(roadPathLock);
        roadPathLock.setName("LOCK - 2");
        roadPathLock.setLockAction(RoadPathLock.LockAction.UNLOCK);
        roadPathLockService.save(roadPathLock);

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

    /**
     * 新增个 路径锁对象
     * @param roadPathLock
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public AjaxResult saveRoadPathLock(@RequestBody RoadPathLock roadPathLock){
        try {
            roadPathLockService.save(roadPathLock);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_XZCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_NBCC"));
        }
    }

    /**
     * 修改 路径锁对象
     * @param roadPathLock
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public AjaxResult updateRoadPathLock(@RequestBody RoadPathLock roadPathLock){
        try {
            roadPathLockService.updateSelectiveByStoreId(roadPathLock);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_XGCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_NBCC"));
        }
    }

    /**
     * 添加 路径锁对象
     * @param roadPathLock 添加的锁内容
     * @return 添加结果
     */
    @PostMapping("/assets/roadPathLock")
    public AjaxResult insertRoadPathLock(@RequestBody RoadPathLock roadPathLock){
        try {
            roadPathLock.setLockState(0);//初始状态是未上锁状态
            roadPathLock.setCurrentPasscount(0L);
            roadPathLock.setStoreId(100L);
            roadPathLock.setCreateTime(new Date());
            roadPathLockService.save(roadPathLock);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_TJCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_NBCC"));
        }
    }

    /**
     * 路径锁列表查询
     * @param whereRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public AjaxResult updateRoadPathLock(WhereRequest whereRequest){
        try {
            List<RoadPathLock> roadPathLockList = roadPathLockService.listQueryPageByStoreIdAndOrder(whereRequest.getPage(),whereRequest.getPageSize(),new RoadPathLock(),null);
            PageInfo<RoadPathLock> pathLockPageInfo = new PageInfo<>(roadPathLockList);
            return AjaxResult.success(pathLockPageInfo, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_NBCC"));
        }
    }

    /**
     * 删除 路径锁对象
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public AjaxResult deleteRoadPathLock(@RequestParam("id")Long id){
        try {
            Boolean bool = roadPathService.hasRelatedRoadPath(id);
            if(bool){
                return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CZXGLDLJWFSC"));
            }
            roadPathLockService.deleteById(id);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_SCCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_NBCC"));
        }
    }

    @GetMapping("/testT")
    public AjaxResult testT() throws Exception{
        System.out.println("CCCCCC");
        roadPathLockService.lock(1L,"xx");
        return AjaxResult.success();
    }

    /**
     * 分页查询路径锁对象信息
     * @param whereRequest
     * @return
     */
    @GetMapping("/assets/roadPathLock")
    public AjaxResult roadPathLockList(WhereRequest whereRequest) {
        try {
            List<RoadPathLock> list = roadPathLockService.listRoadPathLocks(whereRequest);
            PageInfo<RoadPathLock> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CXSB"));
        }
    }


    /**
     * 查询全部的逻辑锁路径信息
     * @return
     */
    @GetMapping("/assets/roadPathLockList")
    public AjaxResult roadPathLockList() {
        try {
            List<RoadPathLock> list = roadPathLockService.listAll();
            return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_CXSB"));
        }
    }

    /**
     * 手动释放路径锁  -  roadPathLock/cloudReleaseRoadPathLock/{robotId}
     * @return
     */
    @GetMapping("/cloudReleaseRoadPathLock/{robotId}")
    public AjaxResult cloudReleaseRoadPathLock(@PathVariable("robotId") String robotId) {
        boolean releaseResult = false;
        try {
            releaseResult = roadPathLockService.cloudReleaseRoadPathLock(robotId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return releaseResult ? AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_SDSFLJSCG")) : AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_roadpath_controller_RoadPathLockController_java_SDSFLJSSB"));
    }
}