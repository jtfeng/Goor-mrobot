package cn.muye.account.employee.mapper;

import cn.mrobot.bean.account.Employee;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by ray.fu on 2017/8/11.
 */
public interface EmployeeMapper extends MyMapper<Employee> {

    List<Employee> selectEmployeeNumberByMissionItemId(Map map);

    List<String> listAvailableEmployees(@Param("stationId")Long stationId, @Param("employeeType")Integer employeeType);
}
