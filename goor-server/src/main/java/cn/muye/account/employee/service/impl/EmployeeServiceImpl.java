package cn.muye.account.employee.service.impl;

import cn.mrobot.bean.account.Employee;
import cn.mrobot.bean.account.EmployeeStationXref;
import cn.mrobot.bean.area.station.Station;
import cn.mrobot.utils.WhereRequest;
import cn.muye.account.employee.mapper.EmployeeMapper;
import cn.muye.account.employee.mapper.EmployeeStationXrefMapper;
import cn.muye.account.employee.service.EmployeeService;
import cn.muye.area.station.service.StationService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.util.UserUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;
import java.util.List;

/**
 * Created by ray.fu on 2017/8/11.
 */
@Service
@Transactional
public class EmployeeServiceImpl extends BaseServiceImpl<Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private EmployeeStationXrefMapper employeeStationXrefMapper;
    @Autowired
    private StationService stationService;


    @Autowired
    private UserUtil userUtil;

    @Override
    public void addEmployee(Employee employee) throws RuntimeException {
        employee.setCreatedBy(userUtil.getCurrentUserId());
        employee.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        employee.setCreateTime(new Date());
        employeeMapper.insert(employee);
        List<Station> list = employee.getStationList();
        List<Station> stationListNew = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            for (Station station : list) {
                EmployeeStationXref xref = new EmployeeStationXref();
                xref.setEmployeeId(employee.getId());
                xref.setStationId(station.getId());
                employeeStationXrefMapper.insert(xref);
                Station stationDb = stationService.findById(station.getId());
                stationListNew.add(stationDb);
            }
        }
        employee.setStationList(stationListNew);
    }

    @Override
    public void updateEmployee(Employee employee) throws RuntimeException {
        super.updateSelectiveByStoreId(employee);
        Long empId = employee.getId();
        Example example = new Example(EmployeeStationXref.class);
        example.createCriteria().andCondition("EMPLOYEE_ID=", empId);
        employeeStationXrefMapper.deleteByExample(example);
        List<Station> stationList = employee.getStationList();
        List<Station> stationListNew = Lists.newArrayList();
        if (stationList != null && stationList.size() > 0) {
            for (Station station : stationList) {
                EmployeeStationXref xref = new EmployeeStationXref();
                xref.setStationId(station.getId());
                xref.setEmployeeId(empId);
                employeeStationXrefMapper.insert(xref);
                Station stationDb = stationService.findById(station.getId());
                stationListNew.add(stationDb);
            }
        }
        employee.setStationList(stationListNew);
    }

    @Override
    public Employee getByCode(String code) throws RuntimeException {
//        Example example = new Example(Employee.class);
//        Example.Criteria criteria = example.createCriteria().andCondition("CODE = ", code);
//        criteria.andCondition("STORE_ID = ", SearchConstants.FAKE_MERCHANT_STORE_ID);
        Employee employee = new Employee();
        employee.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        employee.setCode(code);
//        List<Employee> list = employeeMapper.selectByExample(example);
        Employee employeeDb = employeeMapper.selectOne(employee);
//        if (list != null && list.size() > 0) {
//            return list.get(0);
//        } else {
//            return null;
//        }
        return employeeDb;
    }

    @Override
    public List<Employee> list(WhereRequest whereRequest) {
        List<Employee> employeeDbList = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Employee.class, "ID DESC");
        if (employeeDbList != null && employeeDbList.size() > 0) {
            for (Employee employee : employeeDbList) {
                Example example = new Example(Employee.class);
                example.createCriteria().andCondition("EMPLOYEE_ID=", employee.getId());
                List<EmployeeStationXref> listDb = employeeStationXrefMapper.selectByExample(example);
                List<Station> stationList = Lists.newArrayList();
                if (listDb != null && listDb.size() > 0) {
                    for (EmployeeStationXref xref : listDb) {
                        Station stationDb = stationService.findById(xref.getStationId());
                        stationList.add(stationDb);
                    }
                }
                employee.setStationList(stationList);
            }
        }
        return employeeDbList;
    }

}
