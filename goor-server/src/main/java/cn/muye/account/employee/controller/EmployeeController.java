package cn.muye.account.employee.controller;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Employee;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.employee.service.EmployeeService;
import cn.muye.i18n.service.LocaleMessageSourceService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Created by ray.fu on 2017/8/11.
 */
@Controller
public class EmployeeController {

    private static Logger LOGGER = Logger.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private LocaleMessageSourceService localeMessageSourceService;

    /**
     * 测试员工工号校验，返回值
     * @param code
     * @param missionItemId
     * @param subName
     * @return
     */
    @RequestMapping(value = {"account/testEmployee"}, method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult testEmployeeNumber(@RequestParam("code") String code, @RequestParam(value = "missionItemId",required = false) Long missionItemId, @RequestParam("subName") String subName) {
        try {
            AjaxResult ajaxResult = employeeService.verifyEmplyeeNumber(code, missionItemId, subName);
            return ajaxResult;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


    @RequestMapping(value = {"account/employee"}, method = RequestMethod.POST)
//    @PreAuthorize("hasAuthority('account_employee_u')")
    @ResponseBody
    public AjaxResult addEmployee(@RequestBody Employee employee) {
        try{
            //校验员工工号是否重复
            String code = employee.getCode();
            String name = employee.getName();
            if (StringUtil.isNullOrEmpty(code)) {
                return AjaxResult.failed(employee, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_YGGHBNWK"));
            }
            if (StringUtil.isNullOrEmpty(name)) {
                return AjaxResult.failed(employee, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_YGMCBNWK"));
            }
            Employee employeeDb = employeeService.getByCodeType(code, employee.getType());
            //同类型下的员工编号不允许重复
            if (employeeDb != null && employeeDb.getCode() != null
                    && employeeDb.getCode().equals(code) && employeeDb.getType().equals(employee.getType())) {
                return AjaxResult.failed(employee, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_TLXYGBHZF"));
            }
            employeeService.addEmployee(employee);
        } catch (RuntimeException e) {
            LOGGER.error("{}", e);
            return AjaxResult.failed(employee,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_XZSB"));
        } catch (Exception e) {
            LOGGER.error("{}", e);
            return AjaxResult.failed(employee,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_XZSB"));
        } finally {
        }
        return AjaxResult.success(employee,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_XZCG"));
    }

    @RequestMapping(value = {"account/employee"}, method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('account_employee_r')")
    @ResponseBody
    public AjaxResult listEmployee(WhereRequest whereRequest) {
        List<Employee> listEmployee = employeeService.list(whereRequest);
        PageInfo<Employee> userPageInfo = new PageInfo(listEmployee);
        return AjaxResult.success(userPageInfo,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_CXCG"));
    }

    @RequestMapping(value = {"account/employee"}, method = RequestMethod.PUT)
//    @PreAuthorize("hasAuthority('account_employee_u')")
    @ResponseBody
    public AjaxResult updateEmployee(@RequestBody Employee employee) {
        try {
            String code = employee.getCode();
            Long id = employee.getId();
            String name = employee.getName();
            if (id == null || StringUtil.isNullOrEmpty(code) || StringUtil.isNullOrEmpty(name)) {
                return AjaxResult.failed(AjaxResult.CODE_PARAM_ERROR,localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_YGCSYW"));
            }
            Employee employeeDb = employeeService.getByCodeType(code, employee.getType());
            //同类型下的员工编号不允许重复
            if (employeeDb != null && employeeDb.getCode() != null
                    && employeeDb.getCode().equals(code)
                    && employeeDb.getType().equals(employee.getType())
                    && !employeeDb.getId().equals(id)) {
                return AjaxResult.failed(employee, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_TLXYGBHZF"));
            }
            employeeService.updateEmployee(employee);
        } catch (Exception e) {
            LOGGER.error("修改报错===>{}", e);
        } finally {
        }
        return AjaxResult.success(employee, localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_XGCG"));
    }

    @RequestMapping(value = {"account/employee/{id}"}, method = RequestMethod.DELETE)
//    @PreAuthorize("hasAuthority('account_employee_d')")
    @ResponseBody
    public AjaxResult deleteEmployee(@PathVariable Long id) {
        employeeService.deleteById(id);
        return AjaxResult.success(localeMessageSourceService.getMessage("goor_server_src_main_java_cn_muye_account_employee_controller_EmployeeController_java_SCCG"));
    }

}
