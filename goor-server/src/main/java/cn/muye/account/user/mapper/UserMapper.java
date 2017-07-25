package cn.muye.account.user.mapper;

import cn.mrobot.bean.account.User;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/13.
 */
public interface UserMapper extends MyMapper<User> {

    User getByUserName(String username);

    void fakeDeleteById(Long id);

    User getUserByDirectKey(@Param("directLoginKey") Integer directKey,@Param("storeId") Long storeId);

    List<User> selectBySuperAdmin(Map map);

    List<User> selectByHospitalAdmin(Map map);

    List<User> selectByStationAdmin(Map map);

    User getByAccessToken(String accessToken);
}
