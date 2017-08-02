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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
