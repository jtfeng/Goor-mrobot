package cn.muye.log.alert.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.alert.AlertTypeEnum;
import cn.mrobot.bean.log.alert.LogAlert;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.utils.WhereRequest;
import cn.muye.i18n.service.LocaleMessageSourceService;
import cn.muye.log.alert.service.LogAlertService;
import cn.muye.mission.service.MissionItemTaskService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by ray.fu on 2017/9/11.
 */
@RestController
@RequestMapping("log/alert")
@Api(
        value = "报警日志功能",
        description = "报警日志功能")
public class LogAlertController {

    @Autowired
    private LogAlertService logAlertService;

    @Autowired
    private MissionItemTaskService missionItemTaskService;

    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 查看报警日志
     * @param whereRequest
     * @return
     */
    @GetMapping()
    @ApiOperation(
            value = "报警日志列表",
            notes = "报警日志列表")
    public AjaxResult list(WhereRequest whereRequest) {
       if (whereRequest != null) {
           int page = whereRequest.getPage();
           int pageSize = whereRequest.getPageSize();
           List<LogAlert> list = logAlertService.listPageByStoreIdAndOrder(page, pageSize, LogAlert.class, "ALERT_TIME DESC");
           list.forEach(logAlert -> {
               logAlert.setAlertName(localeMessageSourceService.getMessage(AlertTypeEnum.getValue(logAlert.getAlertCode())));
               if(logAlert.getMissionItemId()!= null){
                   MissionItemTask missionItemDb = missionItemTaskService.findById(logAlert.getMissionItemId());
                   if (missionItemDb != null) {
                       logAlert.setMissionItemDescription(missionItemDb.getDescription());
                   }
               }
           });
           PageInfo<LogAlert> pageInfo = new PageInfo<>(list);
           return AjaxResult.success(pageInfo, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_alert_controller_LogAlertController_java_CXCG"));
       } else {
           return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_log_alert_controller_LogAlertController_java_CSYW"));
       }
    }
}
