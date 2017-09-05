package cn.muye.assets.roadpath.service;

import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.muye.base.service.BaseService;

public interface RoadPathLockService extends BaseService<RoadPathLock> {

    /**
     * 上锁
     * @param id
     * @return
     * @throws Exception
     */
    boolean lock(Long id) throws Exception;

    /**
     * 释放锁
     * @param id
     * @return
     * @throws Exception
     */
    boolean unlock(Long id) throws Exception;

}