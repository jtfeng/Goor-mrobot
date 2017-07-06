package cn.muye.account.role.service.impl;

import cn.mrobot.bean.account.UserStationXref;
import cn.muye.account.role.mapper.UserStationXrefMapper;
import cn.muye.account.role.service.UserStationXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/28.
 */
@Service
@Transactional
public class UserStationXrefServiceImpl implements UserStationXrefService {

    @Autowired
    private UserStationXrefMapper userStationXrefMapper;

    @Override
    public void save(UserStationXref userStationXref) {
        userStationXrefMapper.insert(userStationXref);
    }

    @Override
    public Integer deleteByUserId(Long userId) {
        Example example = new Example(UserStationXref.class);
        example.createCriteria().andCondition("USER_ID =", userId);
        return userStationXrefMapper.deleteByExample(example);
    }

    @Override
    public List<UserStationXref> getByUserId(Long userId) {
        return userStationXrefMapper.getByUserId(userId);
    }
}
