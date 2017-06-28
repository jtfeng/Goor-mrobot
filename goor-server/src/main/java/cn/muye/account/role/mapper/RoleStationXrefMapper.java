package cn.muye.account.role.mapper;

import cn.mrobot.bean.account.RoleStationXref;
import cn.muye.util.MyMapper;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
public interface RoleStationXrefMapper extends MyMapper<RoleStationXref> {

    RoleStationXref getByRoleId(Long roleId);
}
