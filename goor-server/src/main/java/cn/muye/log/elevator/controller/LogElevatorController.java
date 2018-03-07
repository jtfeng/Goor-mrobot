package cn.muye.log.elevator.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.log.elevator.LogElevator;
import cn.mrobot.bean.log.elevator.LogElevatorDetail;
import cn.mrobot.utils.WhereRequest;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.elevator.service.LogElevatorService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LogElevatorController.class);
    @Autowired
    LogElevatorService logElevatorService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

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
                return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_elevator_controller_LogElevatorController_java_DTRZXZCG"));
            }else{
                throw new Exception(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_elevator_controller_LogElevatorController_java_DTRZXZSB"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
//                detail.parseMessage();
                logElevators.set(i, parseMessage(detail));
                i++;
            }
            PageInfo<LogElevator> pageList = new PageInfo<>(logElevators);
            return AjaxResult.success(pageList, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_elevator_controller_LogElevatorController_java_CXDTCZRZXXCG"));
        }catch (Exception e){
            return AjaxResult.failed(e, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_elevator_controller_LogElevatorController_java_CXDTCZRZXXSB"));
        }
    }

    private LogElevatorDetail parseMessage(LogElevatorDetail logElevatorDetail){
        // AA 55 (   01      02     00     00      00    ) 02
        //       (出发楼层、目标楼层、命令字、电梯编号、机器人编号)
        String valueString = logElevatorDetail.getValue();
        logElevatorDetail.setDepartureFloor(valueString.substring(4,6));
        logElevatorDetail.setTargetFloor(valueString.substring(6,8));
        String commandExp = localeMessageSourceService.getMessage(LogElevatorDetail.COMMAND_WORD_MAPPING.get(valueString.substring(8,10)));
        String realExp = "";
        if (commandExp != null){
            realExp = commandExp;
        }else {
            realExp = valueString.substring(8,10);
        }
        logElevatorDetail.setCommandWord(realExp);
        logElevatorDetail.setElevatorNumber(valueString.substring(10,12));
        logElevatorDetail.setRobotNumber(valueString.substring(12,14));
        return logElevatorDetail;
    }
}
