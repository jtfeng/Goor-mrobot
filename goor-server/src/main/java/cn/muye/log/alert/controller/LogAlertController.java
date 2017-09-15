package cn.muye.log.alert.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.alert.LogAlert;
import cn.mrobot.utils.WhereRequest;
import cn.muye.log.alert.service.LogAlertService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
           PageInfo<LogAlert> pageInfo = new PageInfo<>(list);
           return AjaxResult.success(pageInfo, "查询成功");
       } else {
           return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR, "参数有误");
       }
    }
}
