package cn.muye.assets.roadpath.service;

import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.service.BaseService;

import java.util.List;

public interface RoadPathLockService extends BaseService<RoadPathLock> {

    /**
     * 上锁
     * @param id
     * @return
     * @throws Exception
     */
    @Deprecated
    boolean lockInner(Long id, String robotCode) throws Exception;

    /**
     * 给路径加锁
     * @param id 锁ID
     * @param robotCode
     * @return
     * @throws Exception
     */
    boolean lock(Long id, String robotCode) throws Exception;

    /**
     * 给路径加方向锁
     * @param id 锁ID
     * @param robotCode
     * @param direction 路径ID
     * @return
     * @throws Exception
     */
    boolean lockInner(Long id, String robotCode, Long direction) throws Exception;

    /**
     * 给路径加方向锁
     * @param id 锁ID
     * @param robotCode
     * @param direction 路径ID
     * @return
     * @throws Exception
     */
    boolean lockDirection(Long id, String robotCode, Long direction) throws Exception;

    /**
     *
     * @param id
     * @param robotCode
     * @param direction 目前暂定锁方向定义为 ：最开始进入的路径 ID
     * @return
     * @throws Exception
     */
    boolean lock(Long id, String robotCode, Long direction) throws Exception;

    /**
     * 释放锁
     * @param id
     * @return
     * @throws Exception
     */
    @Deprecated
    boolean unlockInner(Long id, String robotCode) throws Exception;

    boolean unlock(Long id, String robotCode) throws Exception;

    boolean unlockInnerNewVersion(Long id, String robotCode) throws Exception;

    boolean unlockNewVersion(Long id, String robotCode) throws Exception;

    /**
     * 分页查询路径锁信息
     * @param whereRequest
     * @return
     * @throws Exception
     */
    List<RoadPathLock> listRoadPathLocks(WhereRequest whereRequest) throws Exception;

    boolean cloudReleaseRoadPathLock(String robotCode) throws Exception;

    void schuleReleaseRoadpathLock() throws Exception;

}