package cn.muye.log.elevator.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.log.elevator.LogElevator;
import cn.mrobot.bean.log.elevator.LogElevatorDetail;
import cn.mrobot.utils.WhereRequest;
import cn.muye.log.elevator.service.LogElevatorService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


/**
 * Created by abel on 17-7-7.
 */
@Controller
@RequestMapping("log/elevator")
@Api(
        value = "电梯服务功能",
        description = "电梯服务功能")
public class LogElevatorController {

    @Autowired
    LogElevatorService logElevatorService;

    @GetMapping("/show")
    @ApiOperation(
            value = "最新电梯日志展示",
            notes = "最新电梯日志展示")
    public ModelAndView elevatorLogShow(
            Map<String, Object> map
    ) {
        List<LogElevator> list =
                logElevatorService.listPageByTimeDesc(1, 100);
        if (list != null && !list.isEmpty()){
            map.put("list", list);
        }
        return new ModelAndView("elevator/show");
    }

    @PostMapping("/addtest")
    @ApiOperation(
            value = "电梯日志新增提交",
            notes = "电梯日志新增提交")
    @ResponseBody
    public AjaxResult addLogElevator() {
        try {
            LogElevator logElevator = new LogElevator();
            logElevator.setAddr("test");
            logElevator.setValue("test value");
            logElevatorService.save(logElevator);
            if (logElevator.getId() != null){
                return AjaxResult.success("电梯日志新增成功");
            }else{
                throw new Exception("电梯日志新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.failed(e.getMessage());
        }
    }

    /**
     * 根据电梯的 ip 信息查询所有的操作日志信息
     * @param whereRequest
     * @return
     */
    @GetMapping("/listLogElevatorsByElevatorIp")
    @ResponseBody
    public AjaxResult listLogElevatorsByElevatorIp(WhereRequest whereRequest){
        try {
            List<LogElevator> logElevators = this.logElevatorService.listLogElevatorsByElevatorIp(whereRequest);
            int i = 0;
            for (LogElevator logElevator:logElevators){
                LogElevatorDetail detail = new LogElevatorDetail();
                BeanUtils.copyProperties(logElevator, detail);
                detail.parseMessage();
                logElevators.set(i, detail);
                i++;
            }
            PageInfo<LogElevator> pageList = new PageInfo<>(logElevators);
            return AjaxResult.success(pageList, "查询电梯操作日志信息成功");
        }catch (Exception e){
            return AjaxResult.failed(e, "查询电梯操作日志信息失败");
        }
    }
}
