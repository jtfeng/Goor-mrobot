package cn.muye.assets.elevator.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.service.ElevatorPointCombinationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.elevator.service.ElevatorShaftService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.google.common.base.Preconditions.*;

import java.util.Date;
import java.util.List;

@RestController
public class ElevatorController {

    @Autowired
    private ElevatorService elevatorService;
    @Autowired
    private ElevatorShaftService elevatorShaftService;
    @Autowired
    private ElevatorPointCombinationService elevatorPointCombinationService;

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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            elevatorPointCombinationService.update(combination);
            return AjaxResult.success();
        }catch (Exception e){
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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            for (ElevatorPointCombination combination:elevator.getElevatorPointCombinations()){
                combinationIds.add(checkNotNull(combination.getId(), "ID编号必须存在，请重新检查!"));
            }
            //保存电梯信息以及电梯与点组合的对应关系
            elevatorService.createElevator(elevator, combinationIds);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            elevatorShaftService.update(elevatorShaft);
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
            return AjaxResult.success();
        }catch (Exception e){
            return AjaxResult.failed(e.getMessage());
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
}