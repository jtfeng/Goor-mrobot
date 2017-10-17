package cn.muye.assets.elevator.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.*;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.assets.elevator.mapper.ElevatorModeMapper;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.elevator.service.ElevatorModeService;
import cn.muye.assets.elevator.service.ElevatorPointCombinationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.elevator.service.ElevatorShaftService;
import cn.muye.base.bean.SearchConstants;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

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
    private static final Logger log = LoggerFactory.getLogger(ElevatorController.class);

    /**
     * 分页查询电梯井信息
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevatorShafts", method = RequestMethod.GET)
    public AjaxResult listElevatorShafts(WhereRequest whereRequest){
        try {
            List<ElevatorShaft> list = elevatorShaftService.listElevatorShafts(whereRequest);
            PageInfo<ElevatorShaft> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询电梯井信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,         "查询电梯井信息失败");
        }
    }

    /**
     * 查询全部电梯井信息
     * @return
     */
    @RequestMapping(value = "listAllElevatorShaft", method = RequestMethod.GET)
    public AjaxResult listAllElevatorShaft(){
        try {
            List<ElevatorShaft> list = elevatorShaftService.listAll();
            return AjaxResult.success(list, "查询全部电梯井信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,     "查询全部电梯井信息失败");
        }
    }

    /**
     * 查询全部电梯信息
     * @return
     */
    @RequestMapping(value = "listAllElevator", method = RequestMethod.GET)
    public AjaxResult listAllElevator(){
        try {
            List<Elevator> list = elevatorService.listAll();
            return AjaxResult.success(list, "查询全部电梯信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,     "查询全部电梯信息失败");
        }
    }

    @Autowired
    private PointService pointService;
    /**
     * 查询全部的地图点信息
     * @return
     */
    @RequestMapping(value = "listAllMapPoints", method = RequestMethod.GET)
    public AjaxResult listAllMapPoints(@RequestParam(value = "sceneName", required = false) String sceneName){
        try {
//            List<MapPoint> mapPoints = this.mapPointMapper.selectAll();
            List<MapPoint> mapPoints = pointService.listByMapSceneNameAndPointType(sceneName,null, SearchConstants.FAKE_MERCHANT_STORE_ID);
            return AjaxResult.success(mapPoints, "查询全部地图点信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,          "查询全部地图点信息失败");
        }
    }

    /**
     * 查询全部的四点组合信息
     * @return
     */
    @RequestMapping(value = "listAllElevatorPointCombinations", method = RequestMethod.GET)
    public AjaxResult listAllElevatorPointCombinations(){
        try {
            List< ElevatorPointCombination> combinations = this.elevatorPointCombinationService.listAll();
            return AjaxResult.success(combinations, "查询全部四点组合信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,             "查询全部四点组合信息失败");
        }
    }

    /**
     * 分页查询电梯信息
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevators", method = RequestMethod.GET)
    public AjaxResult listElevators(WhereRequest whereRequest){
        try {
            List<Elevator> list = elevatorService.listElevators(whereRequest);
            PageInfo<Elevator> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询电梯信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,         "查询电梯信息失败");
        }
    }

    /**
     * 分页查询地图四点组合对象信息
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "listElevatorPointCombinations", method = RequestMethod.GET)
    public AjaxResult listElevatorPointCombinations(WhereRequest whereRequest){
        try {
            List<ElevatorPointCombination> list = elevatorPointCombinationService.listElevatorPointCombinations(whereRequest);
            PageInfo<ElevatorPointCombination> pageList = new PageInfo<>(list);
            return AjaxResult.success(pageList, "查询组合点信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e,         "查询组合点信息失败");
        }
    }

    /**
     * 创建四个点的对应关系实体信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination", method = RequestMethod.POST)
    public AjaxResult createElevatorPointCombination(@RequestBody ElevatorPointCombination combination){
        try {
            checkNotNull(combination.getWaitPoint(),"等待点编号不能为空!");
            checkNotNull(combination.getGoPoint(),  "进入点编号不能为空!");
            checkNotNull(combination.getOutPoint(), "出去点编号不能为空!");
            checkNotNull(combination.getInnerPoint(),"内部点编号不能为空!");
            //判断是否四个点存在于一张地图上
            elevatorPointCombinationService.checkCreateCondition(Lists.newArrayList(
                    combination.getWaitPoint(), combination.getGoPoint(), combination.getOutPoint(), combination.getInnerPoint()
            ));
            elevatorPointCombinationService.save(combination);
            return AjaxResult.success("保存四点组合信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "保存四点组合信息失败");
        }
    }

    /**
     * 更新四个点的对应关系实体信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination", method = RequestMethod.PUT)
    public AjaxResult updateElevatorPointCombination(@RequestBody ElevatorPointCombination combination){
        try {
            checkNotNull(combination.getWaitPoint(),"等待点编号不能为空!");
            checkNotNull(combination.getGoPoint(),  "进入点编号不能为空!");
            checkNotNull(combination.getOutPoint(), "出去点编号不能为空!");
            checkNotNull(combination.getInnerPoint(),"内部点编号不能为空!");
            //判断是否四个点存在于一张地图上
            elevatorPointCombinationService.checkCreateCondition(Lists.newArrayList(
                    combination.getWaitPoint(), combination.getGoPoint(), combination.getOutPoint(), combination.getInnerPoint()
            ));
            elevatorPointCombinationService.updateSelective(combination);
            return AjaxResult.success("更新四点组合信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 删除四个点的对应关系实体信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination/{pointCombinationId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevatorPointCombination(@PathVariable("pointCombinationId") String pointCombinationId){
        try {
            elevatorPointCombinationService.deleteById(Long.parseLong(pointCombinationId));
            return AjaxResult.success("删除四点组合信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "删除四点组合信息失败");
        }
    }

    /**
     * 根据 ID 查询信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorPointCombination/{pointCombinationId}", method = RequestMethod.GET)
    public AjaxResult getElevatorPointCombination(@PathVariable("pointCombinationId") String pointCombinationId){
        try {
            return AjaxResult.success(elevatorPointCombinationService.findById(Long.parseLong(pointCombinationId)));
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 创建电梯对象信息
     * @param elevator
     * @return
     */
    @RequestMapping(value = "/assets/elevator", method = RequestMethod.POST)
    public AjaxResult createElevator(@RequestBody Elevator elevator){
        try {
            List<Long> combinationIds = Lists.newArrayList();
            checkNotNull(elevator.getElevatorshaftId(),"电梯必须绑定电梯井，请重新选择!");
            for (ElevatorPointCombination combination : elevator.getElevatorPointCombinations()) {
                combinationIds.add(checkNotNull(combination.getId(), "ID编号必须存在，请重新检查!"));
            }
            String ipElevatorId = elevator.getIpElevatorId();
            /*if (!StringUtil.isNullOrEmpty(ipElevatorId)) {
                String regex = "^[10]{8}";
                boolean flag = ipElevatorId.matches(regex);
                if (!flag) {
                    return AjaxResult.failed(AjaxResult.CODE_FAILED, "工控电梯ID必须为8位二进制");
                }
            }*/
            //保存电梯信息以及电梯与点组合的对应关系
            elevatorService.createElevator(elevator, combinationIds);
            return AjaxResult.success("保存电梯信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "保存电梯信息失败");
        }
    }

    /**
     * 更新电梯对象信息
     * @param elevator
     * @return
     */
    @RequestMapping(value = "/assets/elevator", method = RequestMethod.PUT)
    public AjaxResult updateElevator(@RequestBody Elevator elevator){
        try {
            List<Long> combinationIds = Lists.newArrayList();
            checkNotNull(elevator.getElevatorshaftId(),"电梯必须绑定电梯井，请重新选择!");
            for (ElevatorPointCombination combination:elevator.getElevatorPointCombinations()){
                combinationIds.add(checkNotNull(combination.getId(), "ID编号必须存在，请重新检查!"));
            }
            //更新电梯信息以及电梯与点组合的对应关系
            elevatorService.updateElevator(elevator, combinationIds);
            return AjaxResult.success("更新电梯信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "更新电梯信息失败");
        }
    }

    /**
     * 删除电梯对象信息
     * @return
     */
    @RequestMapping(value = "/assets/elevator/{elevatorId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevator(@PathVariable("elevatorId") String elevatorId){
        try {
            elevatorService.deleteById(Long.parseLong(elevatorId));
            return AjaxResult.success("删除电梯信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "删除电梯信息失败");
        }
    }

    /**
     * 根据 ID查询对象信息
     * @return
     */
    @RequestMapping(value = "/assets/elevator/{elevatorId}", method = RequestMethod.GET)
    public AjaxResult getElevator(@PathVariable("elevatorId") String elevatorId){
        try {
            return AjaxResult.success(elevatorService.findById(Long.parseLong(elevatorId)));
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 创建电梯井信息
     * @param elevatorShaft
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft", method = RequestMethod.POST)
    public AjaxResult createElevatorShaft(@RequestBody ElevatorShaft elevatorShaft){
        try {
            elevatorShaftService.save(elevatorShaft);
            return AjaxResult.success("创建新的电梯井成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "创建新的电梯井失败");
        }
    }

    /**
     * 更新电梯井信息
     * @param elevatorShaft
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft", method = RequestMethod.PUT)
    public AjaxResult updateElevatorShaft(@RequestBody ElevatorShaft elevatorShaft){
        try {
            elevatorShaftService.updateSelective(elevatorShaft);
            return AjaxResult.success("修改电梯井信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "修改电梯井信息失败");
        }
    }

    /**
     * 删除电梯井信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft/{elevatorShaftId}", method = RequestMethod.DELETE)
    public AjaxResult deleteElevatorShaft(@PathVariable("elevatorShaftId") String elevatorShaftId){
        try {
            elevatorShaftService.deleteById(Long.parseLong(elevatorShaftId));
            return AjaxResult.success("删除电梯井信息成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "删除电梯井信息失败");
        }
    }

    /**
     * 根据 ID 查询电梯井信息
     * @return
     */
    @RequestMapping(value = "/assets/elevatorShaft/{elevatorShaftId}", method = RequestMethod.GET)
    public AjaxResult getElevatorShaft(@PathVariable("elevatorShaftId") String elevatorShaftId){
        try {
            return AjaxResult.success(elevatorShaftService.findById(Long.parseLong(elevatorShaftId)));
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 更新电梯锁定状态
     * @param elevatorId
     * @param state
     * @return
     */
    @RequestMapping(value = "/assets/updateElevatorLockState/{elevatorId}/{state}", method = RequestMethod.GET)
    public AjaxResult updateElevatorLockState(
            @PathVariable("elevatorId") String elevatorId,
            @PathVariable("state") String state
    ){
        //Long elevatorId, Integer state
        try {
            Elevator.ELEVATOR_ACTION action = null;
            if ("1".equals(state)){action = Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK;}
            if ("0".equals(state)){action = Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK;}
            elevatorService.updateElevatorLockState(Long.parseLong(elevatorId), action);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/assets/findByMapFloor/{mapInfoId}/{floor}", method = RequestMethod.GET)
    public AjaxResult findByMapFloor(
            @PathVariable("mapInfoId") String mapInfoId,
            @PathVariable("floor") String floor
    ){
        try {
            List<Elevator> elevators = elevatorService.findByMapFloor(Long.parseLong(mapInfoId), Integer.parseInt(floor));
            return AjaxResult.success(elevators);
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
        }
    }

    @Autowired
    private ElevatorModeService elevatorModeService;
    @PostMapping(value = "/assets/createElevatorMode")
    public AjaxResult createElevatorMode(@RequestBody ElevatorMode elevatorMode){
        if (elevatorModeService.save(elevatorMode) > 0) {
            return AjaxResult.success("保存状态成功");
        }else {
            return AjaxResult.failed("保存状态失败");
        }
    }

    @GetMapping(value = "/assets/elevatorMode/{elevatorId}")
    public Object fetchElevatorState(@PathVariable("elevatorId") Long elevatorId){
        try {
            return AjaxResult.success(elevatorService.determineCurrentElevatorMode(elevatorId));
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 添加一份新的时间段电梯模式
     * @return
     */
    @RequestMapping(value = "/status/elevatorMode", method = RequestMethod.POST)
    public AjaxResult elevatorMode(@RequestBody ElevatorMode elevatorMode){
        try {
            Preconditions.checkNotNull(elevatorMode.getStart(), "开始时间不允许为空！");
            Preconditions.checkNotNull(elevatorMode.getEnd(),   "结束时间不允许为空！");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.parse("2017-01-01 " + elevatorMode.getStart());
            format.parse("2017-01-01 " + elevatorMode.getEnd());
            Preconditions.checkState(elevatorMode.getStart().compareTo(elevatorMode.getEnd()) < 0,
                    "开始时间必须小于结束时间，请检查！");
            Long ck = this.elevatorModeMapper.checkLegalRangeDate(elevatorMode.getStart(), elevatorMode.getEnd(),
                    elevatorMode.getElevatorId());
            Preconditions.checkState(ck == 0, "开始时间或结束时间已经在别的时间范围内，请重新选择！");
            this.elevatorModeMapper.insert(elevatorMode);
            return AjaxResult.success("保存电梯模式成功");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "保存电梯模式失败");
        }
    }

    /**
     * 根据电梯 ID 查询电梯模式
     * @return
     */
    @RequestMapping(value = "/status/elevatorMode/{id}", method = RequestMethod.GET)
    public AjaxResult elevatorModeSearch(@PathVariable("id") Long id){
        try {
            ElevatorModeEnum modeEnum = this.elevatorService.determineCurrentElevatorMode(id);
            return AjaxResult.success(modeEnum);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.failed( "查询电梯模式失败");
        }
    }

}