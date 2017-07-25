package cn.muye.log.base.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.log.LogInfo;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.log.base.service.LogInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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

    @Autowired
    private LogInfoService logInfoService;

    @RequestMapping(value = "log/list", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult list(WhereRequest whereRequest) {
        List<LogInfo> logInfoList = logInfoService.lists(whereRequest, SearchConstants.FAKE_MERCHANT_STORE_ID);

        Integer pageNo = whereRequest.getPage() ;
        Integer pageSize = whereRequest.getPageSize();

        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<LogInfo> lists = new ArrayList<>();
        //用PageInfo对结果进行包装
        PageInfo<LogInfo> page = new PageInfo<LogInfo>(lists);
        return AjaxResult.success(page);
    }
}
