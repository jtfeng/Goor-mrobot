package cn.muye.account.employee.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.account.Employee;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by ray on 2017/8/11.
 */
public interface EmployeeService extends BaseService<Employee> {

    void addEmployee(Employee employee);

    void updateEmployee(Employee employee);

    Employee getByCode(String code);

    List<Employee> list(WhereRequest whereRequest);

    AjaxResult verifyEmplyeeNumber(String code, Long missionItemId, String subName) throws Exception;
}
