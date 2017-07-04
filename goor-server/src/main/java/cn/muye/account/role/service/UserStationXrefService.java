package cn.muye.account.role.service;

import cn.mrobot.bean.account.UserStationXref;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
public interface UserStationXrefService {

    void save(UserStationXref userStationXref);

    Integer deleteByUserId(Long userId);

    List<UserStationXref> getByUserId(Long userId);
}
