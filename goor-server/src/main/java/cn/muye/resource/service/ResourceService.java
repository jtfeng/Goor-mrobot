package cn.muye.resource.service;

import cn.mrobot.bean.resource.Resource;
import cn.muye.base.service.BaseService;

import java.util.List;

/**
 * Created by Selim on 2017/6/13.
 */
public interface ResourceService extends BaseService<Resource> {

    List<Resource> listByType(Integer resourceType, int page, int pageSize);

}
