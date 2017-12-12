package cn.muye.erp.password.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.erp.password.OperationPadPasswordXREF;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.erp.password.service.OperationPadPasswordXREFService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/7
 */
@RestController
public class OperationPakPasswordXREFController {

    @Autowired
    private OperationPadPasswordXREFService operationPakPasswordXREFService;

    /**
     * 保存
     *
     * @param operaXREF
     * @return
     */
    @RequestMapping(value = "services/opera/password", method = RequestMethod.POST)
    public AjaxResult save(@RequestBody OperationPadPasswordXREF operaXREF) {
        if (StringUtil.isBlank(operaXREF.getMac()) ||
                StringUtil.isBlank(operaXREF.getPassword()) ||
                operaXREF.getStationId() == null ||
                operaXREF.getType() == 0) {
            return AjaxResult.failed("密码，站Id，平板MAC不能为空");
        }
        OperationPadPasswordXREF operaXREFDB = operationPakPasswordXREFService.findOne(operaXREF);
        if (operaXREFDB != null) {
            return AjaxResult.failed("绑定失败，已存在相同数据");
        }
//        List<OperationPadPasswordXREF> operaXREFDBList = operationPakPasswordXREFService.findByStationId(operaXREF.getStationId());
//        if (null != operaXREFDBList && operaXREFDBList.size() > 0){
//            return AjaxResult.failed("绑定失败，手术室已绑定平板，请先通知管理员解绑");
//        }
        operationPakPasswordXREFService.save(operaXREF);
        operationPakPasswordXREFService.addStation(operaXREF);
        return AjaxResult.success(operaXREF, "绑定成功");
    }

    /**
     * 解除平板和手术室的绑定关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "opera/password/{id}", method = RequestMethod.DELETE)
    public AjaxResult delete(@PathVariable Long id) {
        if (null == id) {
            return AjaxResult.failed("Id不能为空");
        }
        operationPakPasswordXREFService.deleteById(id);
        return AjaxResult.success("解除成功");
    }

    /**
     * 查询
     *
     * @param mac
     * @return
     */
    @RequestMapping(value = "services/opera/password/query", method = RequestMethod.GET)
    public AjaxResult query(@RequestParam("mac") String mac) {
        if (StringUtil.isBlank(mac)) {
            return AjaxResult.failed("平板MAC不能为空");
        }
        OperationPadPasswordXREF operationPadPasswordXREF = operationPakPasswordXREFService.findByMacAndPassword(mac, null);
        if (null == operationPadPasswordXREF) {
            return AjaxResult.failed("未查询到绑定信息");
        }
        return AjaxResult.success(operationPadPasswordXREF, "查询成功");
    }

    /**
     * 查询，校验
     *
     * @param mac
     * @param password
     * @return
     */
    @RequestMapping(value = "services/opera/password/validate", method = RequestMethod.GET)
    public AjaxResult get(@RequestParam("mac") String mac, @RequestParam("password") String password) {
        if (StringUtil.isBlank(mac) ||
                StringUtil.isBlank(password)) {
            return AjaxResult.failed("密码，平板MAC不能为空");
        }
        OperationPadPasswordXREF operationPadPasswordXREF = operationPakPasswordXREFService.findByMacAndPassword(mac, password);
        if (null == operationPadPasswordXREF) {
            return AjaxResult.failed("密码不存在或不匹配");
        }
        return AjaxResult.success(operationPadPasswordXREF, "查询成功");
    }

    /**
     * 列表
     *
     * @param whereRequest
     * @return
     */
    @RequestMapping(value = "opera/password/list", method = RequestMethod.GET)
    public AjaxResult list(WhereRequest whereRequest, HttpServletRequest request) {
        Integer pageNo = whereRequest.getPage();
        Integer pageSize = whereRequest.getPageSize();

        pageNo = (pageNo == null || pageNo == 0) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<OperationPadPasswordXREF> operationPadPasswordXREFList = operationPakPasswordXREFService.list(whereRequest);
        PageInfo<OperationPadPasswordXREF> page = new PageInfo<OperationPadPasswordXREF>(operationPadPasswordXREFList);
        return AjaxResult.success(page, "查询成功");
    }
}
