package cn.muye.account.employee.service.impl;

import cn.mrobot.bean.account.EmployeeStationXref;
import cn.muye.account.employee.mapper.EmployeeMapper;
import cn.muye.account.employee.mapper.EmployeeStationXrefMapper;
import cn.muye.account.employee.service.EmployeeStationXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ray.fu on 2017/8/11.
 */
@Service
@Transactional
public class EmployeeStationXrefServiceImpl implements EmployeeStationXrefService {

    @Autowired
    private EmployeeStationXrefMapper employeeStationXrefMapper;

    @Override
    public void save(EmployeeStationXref xref) {
        employeeStationXrefMapper.insert(xref);
    }
}
