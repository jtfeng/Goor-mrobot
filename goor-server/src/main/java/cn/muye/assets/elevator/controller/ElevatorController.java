package cn.muye.assets.elevator.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.MapPointType;
import cn.mrobot.bean.assets.elevator.*;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.elevator.mapper.ElevatorModeMapper;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.elevator.service.*;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.util.SessionUtil;
import cn.muye.util.UserUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
public class ElevatorController {

    @Autowired
    private MapPointMapper mapPointMapper;
    @Autowired
    private ElevatorService elevatorService;
    @Autowired
    private ElevatorShaftService elevatorShaftService;
    @Autowired
    private ElevatorPointCombinationService elevatorPointCombinationService;
    @Autowired
    private ElevatorModeMapper elevatorModeMapper;
    @Autowired
    private ElevatorNoticeService elevatorNoticeService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;
    private static final Logger log = LoggerFactory.getLogger(ElevatorController.class);

    /**
     * 分页查询电梯井信息
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevatorShafts", method = RequestMethod.GET)
    public AjaxResult listElevatorShafts(WhereRequest whereRequest) {
        try {
            List<ElevatorShaft> list = elevatorShaftService.listElevatorShafts(whereRequest);
            PageInfo<ElevatorShaft> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTJXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTJXXSB"));
        }
    }

    /**
     * 查询全部电梯井信息
     *
     * @return
     */
    @RequestMapping(value = "listAllElevatorShaft", method = RequestMethod.GET)
    public AjaxResult listAllElevatorShaft() {
        try {
            List<ElevatorShaft> list = elevatorShaftService.listAll();
            return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTJXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTJXXSB"));
        }
    }

    /**
     * 查询全部电梯信息
     *
     * @return
     */
    @RequestMapping(value = "listAllElevator", method = RequestMethod.GET)
    public AjaxResult listAllElevator() {
        try {
            List<Elevator> list = elevatorService.listAll();
            return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTXXSB"));
        }
    }

    /**
     * 根据场景查询对应的电梯信息(供前端使用)
     * @return
     */
    @RequestMapping(value = "listElevatorByScene/{sceneId}", method = RequestMethod.GET)
    public AjaxResult listElevatorByScene(@PathVariable("sceneId") Long sceneId){
        try {
            List<Elevator> list = elevatorService.listElevatorByScene(sceneId);
            return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTXXCG"));
        }catch (Exception e){
            return AjaxResult.failed(e,     localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTXXSB"));
        }
    }

    @Autowired
    private PointService pointService;

    /**
     * 查询全部的地图点信息
     *
     * @return
     */
    @RequestMapping(value = "listAllMapPoints", method = RequestMethod.GET)
    public AjaxResult listAllMapPoints(@RequestParam(value = "sceneName", required = false) String sceneName) {
        try {
//            List<MapPoint> mapPoints = this.mapPointMapper.selectAll();
            List<MapPoint> mapPoints = pointService.listByMapSceneNameAndPointType(sceneName, null, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success(mapPoints, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTDXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBDTDXXSB"));
        }
    }

    /**
     * 查询全部的四点组合信息
     *
     * @return
     */
    @RequestMapping(value = "listAllElevatorPointCombinations", method = RequestMethod.GET)
    public AjaxResult listAllElevatorPointCombinations() {
        try {
            List<ElevatorPointCombination> combinations = this.elevatorPointCombinationService.listAll();
            return AjaxResult.success(combinations, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBSDZHXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXQBSDZHXXSB"));
        }
    }

    /**
     * 分页查询电梯信息
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevators", method = RequestMethod.GET)
    public AjaxResult listElevators(WhereRequest whereRequest) {
        try {
            List<Elevator> list = elevatorService.listElevators(whereRequest);
            PageInfo<Elevator> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTXXSB"));
        }
    }

    /**
     * 分页查询地图四点组合对象信息
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevatorPointCombinations", method = RequestMethod.GET)
    public AjaxResult listElevatorPointCombinations(WhereRequest whereRequest) {
        try {
            List<ElevatorPointCombination> list = elevatorPointCombinationService.listElevatorPointCombinations(whereRequest);
            PageInfo<ElevatorPointCombination> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXZHDXXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXZHDXXSB"));
        }
    }

    /**
     * 创建四个点的对应关系实体信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination", method = RequestMethod.POST)
    public AjaxResult createElevatorPointCombination(@RequestBody ElevatorPointCombination combination) {
        try {
            checkNotNull(combination.getWaitPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_DDDBHBNWK"));
            checkNotNull(combination.getGoPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_JRDBHBNWK"));
            checkNotNull(combination.getOutPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CQDBHBNWK"));
            checkNotNull(combination.getInnerPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_NBDBHBNWK"));
            //判断是否四个点存在于一张地图上
            elevatorPointCombinationService.checkCreateCondition(Lists.newArrayList(
                    combination.getWaitPoint(), combination.getGoPoint(), combination.getOutPoint(), combination.getInnerPoint()
            ));

            checkWaitPoint(combination);
            Scene scene = SessionUtil.SCENE_LOADING_CACHE.getIfPresent(UserUtil.getUserTokenValue() +
                    ":" + Constant.SCENE_SESSION_TAG_PC);
            if (scene != null) {
                combination.setSceneId(scene.getId());
            }
            elevatorPointCombinationService.save(combination);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCSDZHXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCSDZHXXSB"));
        }
    }

    /**
     * 查找等待点是否已经是复制的电梯等待点，如果不是则复制并新增一个
     *
     * @param combination
     * @return
     */
    private ElevatorPointCombination checkWaitPoint(ElevatorPointCombination combination) {
        //查找等待点是否已经是复制的电梯等待点，如果不是则复制并新增一个
        MapPoint oldPoint = pointService.findById(combination.getWaitPoint());
        //我们定义站的点明必须包含station,所以未找到的时候，就新建一个
        if (oldPoint.getPointAlias().indexOf(Constant.ELEVATOR_WAIT) <= -1
                && oldPoint.getCloudMapPointTypeId() != MapPointType.ELEVATOR_WAIT.getCaption()) {
            MapPoint newPoint = new MapPoint();
            MapPoint.copyValue(newPoint, oldPoint);
            newPoint.setPointAlias(newPoint.getPointName() + "_" + Constant.ELEVATOR_WAIT + "_" + combination.getName() + "_" + oldPoint.getMapName() + "_" + oldPoint.getSceneName());
            newPoint.setId(null);
            newPoint.setCloudMapPointTypeId(MapPointType.ELEVATOR_WAIT.getCaption());
            pointService.save(newPoint);
            combination.setWaitPoint(newPoint.getId());
        }
        return combination;
    }

    /**
     * 更新四个点的对应关系实体信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination", method = RequestMethod.PUT)
    public AjaxResult updateElevatorPointCombination(@RequestBody ElevatorPointCombination combination) {
        try {
            checkNotNull(combination.getWaitPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_DDDBHBNWK"));
            checkNotNull(combination.getGoPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_JRDBHBNWK"));
            checkNotNull(combination.getOutPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CQDBHBNWK"));
            checkNotNull(combination.getInnerPoint(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_NBDBHBNWK"));
            //判断是否四个点存在于一张地图上
            elevatorPointCombinationService.checkCreateCondition(Lists.newArrayList(
                    combination.getWaitPoint(), combination.getGoPoint(), combination.getOutPoint(), combination.getInnerPoint()
            ));

            checkWaitPoint(combination);

            elevatorPointCombinationService.updateSelective(combination);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_GXSDZHXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除四个点的对应关系实体信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination/{pointCombinationId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevatorPointCombination(@PathVariable("pointCombinationId") String pointCombinationId) {
        try {
            elevatorPointCombinationService.deleteById(Long.parseLong(pointCombinationId));
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCSDZHXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCSDZHXXSB"));
        }
    }

    /**
     * 根据 ID 查询信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination/{pointCombinationId}", method = RequestMethod.GET)
    public AjaxResult getElevatorPointCombination(@PathVariable("pointCombinationId") String pointCombinationId) {
        try {
            return AjaxResult.success(elevatorPointCombinationService.findById(Long.parseLong(pointCombinationId)));
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 创建电梯对象信息
     *
     * @param elevator
     * @return
     */
    @RequestMapping(value = "/assets/elevator", method = RequestMethod.POST)
    public AjaxResult createElevator(@RequestBody Elevator elevator) {
        try {
            List<Long> combinationIds = Lists.newArrayList();
            checkNotNull(elevator.getElevatorshaftId(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_DTBXBDDTJQZXXZ"));
            for (ElevatorPointCombination combination : elevator.getElevatorPointCombinations()) {
                combinationIds.add(checkNotNull(combination.getId(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_IDBHBXCZQZXJC")));
            }
            String ipElevatorId = elevator.getIpElevatorId();
            /*if (!StringUtil.isNullOrEmpty(ipElevatorId)) {
                String regex = "^[10]{8}";
                boolean flag = ipElevatorId.matches(regex);
                if (!flag) {
                    return AjaxResult.failed(AjaxResult.CODE_FAILED, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_GKDTIDBXW8WEJZ"));
                }
            }*/
            //保存电梯信息以及电梯与点组合的对应关系
            elevatorService.createElevator(elevator, combinationIds);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCDTXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCDTXXSB"));
        }
    }

    /**
     * 更新电梯对象信息
     *
     * @param elevator
     * @return
     */
    @RequestMapping(value = "/assets/elevator", method = RequestMethod.PUT)
    public AjaxResult updateElevator(@RequestBody Elevator elevator) {
        try {
            List<Long> combinationIds = Lists.newArrayList();
            checkNotNull(elevator.getElevatorshaftId(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_DTBXBDDTJQZXXZ"));
            for (ElevatorPointCombination combination : elevator.getElevatorPointCombinations()) {
                combinationIds.add(checkNotNull(combination.getId(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_IDBHBXCZQZXJC")));
            }
            //更新电梯信息以及电梯与点组合的对应关系
            elevatorService.updateElevator(elevator, combinationIds);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_GXDTXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_GXDTXXSB"));
        }
    }

    /**
     * 删除电梯对象信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevator/{elevatorId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevator(@PathVariable("elevatorId") String elevatorId) {
        try {
            elevatorService.deleteById(Long.parseLong(elevatorId));
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTXXSB"));
        }
    }

    /**
     * 根据 ID查询对象信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevator/{elevatorId}", method = RequestMethod.GET)
    public AjaxResult getElevator(@PathVariable("elevatorId") String elevatorId) {
        try {
            return AjaxResult.success(elevatorService.findById(Long.parseLong(elevatorId)));
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 创建电梯井信息
     *
     * @param elevatorShaft
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft", method = RequestMethod.POST)
    public AjaxResult createElevatorShaft(@RequestBody ElevatorShaft elevatorShaft) {
        try {
            elevatorShaftService.save(elevatorShaft);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CJXDDTJCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CJXDDTJSB"));
        }
    }

    /**
     * 更新电梯井信息
     *
     * @param elevatorShaft
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft", method = RequestMethod.PUT)
    public AjaxResult updateElevatorShaft(@RequestBody ElevatorShaft elevatorShaft) {
        try {
            elevatorShaftService.updateSelective(elevatorShaft);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_XGDTJXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_XGDTJXXSB"));
        }
    }

    /**
     * 删除电梯井信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft/{elevatorShaftId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevatorShaft(@PathVariable("elevatorShaftId") String elevatorShaftId) {
        try {
            elevatorShaftService.deleteById(Long.parseLong(elevatorShaftId));
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTJXXCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTJXXSB"));
        }
    }

    /**
     * 根据 ID 查询电梯井信息
     *
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft/{elevatorShaftId}", method = RequestMethod.GET)
    public AjaxResult getElevatorShaft(@PathVariable("elevatorShaftId") String elevatorShaftId) {
        try {
            return AjaxResult.success(elevatorShaftService.findById(Long.parseLong(elevatorShaftId)));
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 更新电梯锁定状态
     *
     * @param elevatorId
     * @param state
     * @return
     */
    @RequestMapping(value = "/assets/updateElevatorLockState/{elevatorId}/{state}", method = RequestMethod.GET)
    public AjaxResult updateElevatorLockState(
            @PathVariable("elevatorId") String elevatorId,
            @PathVariable("state") String state
    ) {
        //Long elevatorId, Integer state
        try {
            Elevator.ELEVATOR_ACTION action = null;
            if ("1".equals(state)) {
                action = Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK;
            }
            if ("0".equals(state)) {
                action = Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK;
            }
            elevatorService.updateElevatorLockState(Long.parseLong(elevatorId), action);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/assets/findByMapFloor/{mapInfoId}/{floor}", method = RequestMethod.GET)
    public AjaxResult findByMapFloor(
            @PathVariable("mapInfoId") String mapInfoId,
            @PathVariable("floor") String floor
    ) {
        try {
            List<Elevator> elevators = elevatorService.findByMapFloor(Long.parseLong(mapInfoId), Integer.parseInt(floor));
            return AjaxResult.success(elevators);
        } catch (Exception e) {
            return AjaxResult.failed(e.getMessage());
        }
    }

    @Autowired
    private ElevatorModeService elevatorModeService;

    @PostMapping(value = "/assets/createElevatorMode")
    public AjaxResult createElevatorMode(@RequestBody ElevatorMode elevatorMode) {
        if (elevatorModeService.save(elevatorMode) > 0) {
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCZTCG"));
        } else {
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCZTSB"));
        }
    }

    @GetMapping(value = "/assets/elevatorMode/{elevatorId}")
    public Object fetchElevatorState(@PathVariable("elevatorId") Long elevatorId) {
        try {
            return AjaxResult.success(elevatorService.determineCurrentElevatorMode(elevatorId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 添加一份新的时间段电梯模式
     *
     * @return
     */
    @RequestMapping(value = "/status/elevatorMode", method = RequestMethod.POST)
    public AjaxResult elevatorMode(@RequestBody ElevatorMode elevatorMode) {
        try {
            Preconditions.checkNotNull(elevatorMode.getStart(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_KSSJBYXWK"));
            Preconditions.checkNotNull(elevatorMode.getEnd(), localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_JSSJBYXWK"));
            Preconditions.checkArgument(elevatorMode.getState() != null && elevatorMode.getState() != -1, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_QXZDTMS"));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.parse("2017-01-01 " + elevatorMode.getStart());
            format.parse("2017-01-01 " + elevatorMode.getEnd());
            Preconditions.checkState(elevatorMode.getStart().compareTo(elevatorMode.getEnd()) < 0,
                    localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_KSSJBXXYJSSJQJC"));
            Long ck = this.elevatorModeMapper.checkLegalRangeDate(elevatorMode.getStart(), elevatorMode.getEnd(),
                    elevatorMode.getElevatorId());
            Preconditions.checkState(ck == 0, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_KSSJHJSSJYJZBDSJFWNQZXXZ"));
            this.elevatorModeMapper.insert(elevatorMode);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_BCDTMSCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除指定的电梯模式
     *
     * @return
     */
    @DeleteMapping("/status/elevatorMode/{id}")
    public AjaxResult deleteElevatorMode(@PathVariable("id") Long id) {
        try {
            this.elevatorModeService.deleteById(id);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTMSCG"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_SCDTMSSB"));
        }
    }

    /**
     * 根据电梯 ID 查询电梯模式
     *
     * @return
     */
    @RequestMapping(value = "/status/elevatorMode/{id}", method = RequestMethod.GET)
    public AjaxResult elevatorModeSearch(@PathVariable("id") Long id) {
        try {
            ElevatorModeEnum modeEnum = this.elevatorService.determineCurrentElevatorMode(id);
            return AjaxResult.success(modeEnum);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.failed(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXDTMSSB"));
        }
    }

    /**
     * 根据电梯编号查询所配置的所有电梯模式信息
     *
     * @return
     */
    @GetMapping("/elevatorMode/{elevatorId}")
    public AjaxResult elevatorModeList(@PathVariable("elevatorId") Long elevatorId) {
        try {
            List<ElevatorMode> list = elevatorModeService.listElevatorModesByElevatorId(elevatorId);
            return AjaxResult.success(list, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CXSB"));
        }
    }

    /**
     * websocket收到电梯pad消息通知反馈
     *
     * @return
     */
    @GetMapping("/elevatorNotice/{id}")
    public AjaxResult elevatorNotice(@PathVariable("id") Long id) {
        try {
            ElevatorNotice elevatorNotice = elevatorNoticeService.findById(id);
            //更新数据库状态
            elevatorNotice.setState(ElevatorNotice.State.RECEIVED.getCode());
            elevatorNoticeService.updateSelective(elevatorNotice);
            CacheInfoManager.removeElevatorNoticeCache(id);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZSB"));
        }
    }

    /**
     * websocket收到电梯pad消息通知反馈
     *
     * @return
     */
    @GetMapping("/elevatorNotice")
    public AjaxResult listElevatorNotice(@RequestParam("stationId") Long stationId,
                                         @RequestParam(value = "state",required = false) Integer state,
                                         @RequestParam(value = "type", required = false) Integer type ) {
        try {
            List<ElevatorNotice> elevatorNoticeList = elevatorNoticeService.listElevatorNotice(stationId, state, type);
            return AjaxResult.success(elevatorNoticeList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZSB"));
        }
    }

    /**
     * websocket收到电梯pad消息通知反馈
     *
     * @return
     */
    @GetMapping("/elevatorNotice/{orderDetailId}")
    public AjaxResult removeElevatorNotice(@PathVariable("orderDetailId") Long orderDetailId) {
        try {
            CacheInfoManager.removeArrivalStationNoticeCacheByOrderDetailId(orderDetailId);
            return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZCG"));
        } catch (Exception e) {
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_assets_elevator_controller_ElevatorController_java_CZSB"));
        }
    }
}
