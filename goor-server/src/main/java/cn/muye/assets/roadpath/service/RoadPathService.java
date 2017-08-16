package cn.muye.assets.roadpath.service;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.muye.base.service.BaseService;

import java.util.Map;

public interface RoadPathService extends BaseService<RoadPath> {

    void createRoadPath(Map<String, Object> body) throws Exception;

}