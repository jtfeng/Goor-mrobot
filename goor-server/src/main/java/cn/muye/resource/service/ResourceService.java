package cn.muye.resource.service;

import cn.mrobot.bean.resource.Resource;

import java.util.List;

/**
 * Created by Selim on 2017/6/13.
 */
public interface ResourceService {
    void save(Resource resource);

    List<Resource> list();

    List<Resource> listByType(Integer resourceBase);

    Resource getById(Long resourceId);
}
