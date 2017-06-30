package cn.muye.account.role.service;

import cn.mrobot.bean.account.UserStationXref;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
public interface UserStationXrefService {

    void save(UserStationXref userStationXref);

    Integer deleteByUserId(Long userId);
}
