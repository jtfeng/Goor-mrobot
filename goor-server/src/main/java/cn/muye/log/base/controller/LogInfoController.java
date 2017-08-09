package cn.muye.log.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.log.base.service.LogInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 16:38
 * Describe:
 * Version:1.0
 */
@Controller
public class LogInfoController {

    private static Logger LOGGER = LoggerFactory.getLogger(LogInfoController.class);

    @Autowired
    private LogInfoService logInfoService;

    @RequestMapping(value = "loginfo/list", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult listLogInfo(HttpServletRequest request, WhereRequest whereRequest) {
        try {
            Integer pageNo = whereRequest.getPage();
            Integer pageSize = whereRequest.getPageSize();

            pageNo = pageNo == null ? 1 : pageNo;
            pageSize = pageSize == null ? 10 : pageSize;
            PageHelper.startPage(pageNo, pageSize);
            //用PageInfo对结果进行包装
            List<LogInfo> logInfoList = logInfoService.lists(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);
            PageInfo<LogInfo> page = new PageInfo<LogInfo>(logInfoList);
            return AjaxResult.success(page);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return AjaxResult.failed(1,"日志查询错误");
        }
    }
}
