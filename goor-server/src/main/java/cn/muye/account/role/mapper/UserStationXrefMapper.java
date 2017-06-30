package cn.muye.account.role.mapper;

import cn.mrobot.bean.account.UserStationXref;
import cn.muye.util.MyMapper;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
public interface UserStationXrefMapper extends MyMapper<UserStationXref> {

    UserStationXref getByUserId(Long roleId);
}
