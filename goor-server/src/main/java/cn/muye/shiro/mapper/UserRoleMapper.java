package cn.muye.shiro.mapper;

import cn.muye.shiro.bean.UserRole;

import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/5/16.
 */
public interface UserRoleMapper {

    List<UserRole> listUserRolesByUserId(Long id);

    void bindUserRole(Map map);

    void deleteByUserId(Long userId);
}
