package cn.muye.account.role.service.impl;

import cn.mrobot.bean.account.RoleStationXref;
import cn.muye.account.role.mapper.RoleStationXrefMapper;
import cn.muye.account.role.service.RoleStationXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
@Service
@Transactional
public class RoleStationXrefServiceImpl implements RoleStationXrefService {

    @Autowired
    private RoleStationXrefMapper roleStationXrefMapper;

    @Override
    public void save(Long roleId, Long stationId) {
        RoleStationXref roleStationXrefDb = roleStationXrefMapper.getByRoleId(roleId);
        if (roleStationXrefDb != null) {
            roleStationXrefDb.setStationId(stationId);
            roleStationXrefMapper.updateByPrimaryKey(roleStationXrefDb);
        } else {
            RoleStationXref roleStationXref = new RoleStationXref();
            roleStationXref.setRoleId(roleId);
            roleStationXref.setStationId(stationId);
            roleStationXrefMapper.insert(roleStationXref);
        }
    }
}
