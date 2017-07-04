package cn.muye.account.role.mapper;

import cn.mrobot.bean.account.UserStationXref;
import cn.muye.util.MyMapper;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
public interface UserStationXrefMapper extends MyMapper<UserStationXref> {

    List<UserStationXref> getByUserId(Long roleId);
}
