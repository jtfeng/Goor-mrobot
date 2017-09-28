package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.muye.assets.roadpath.mapper.RoadPathLockMapper;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RoadPathLockServiceImpl extends BaseServiceImpl<RoadPathLock> implements RoadPathLockService {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockServiceImpl.class);
    @Autowired
    private RoadPathLockMapper roadPathLockMapper;

    @Override
    public synchronized boolean lock(Long id, String robotCode) throws Exception {
        return lockInner(id, robotCode);
    }

    /**
     * 上锁
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean lockInner(Long id, String robotCode) throws Exception {
        boolean flag = false;
        try {
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id),
                    String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
            Integer lock = roadPathLock.getLockState();
            if (lock != null && lock == 1) {
                //已上锁，不能重复上锁
                if (robotCode.equals(roadPathLock.getRobotCode())) {
                    return true;
                }else {
                    flag = false;
                }
            } else {
                //没有上锁，可以加锁
                roadPathLock.setLockAction(RoadPathLock.LockAction.LOCK);
                roadPathLock.setCreateTime(new Date());
                roadPathLock.setRobotCode(robotCode);
                updateSelective(roadPathLock);
                flag = true;
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public synchronized boolean unlock(Long id, String robotCode) throws Exception {
        return unlockInner(id, robotCode);
    }

    /**
     * 解锁
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unlockInner(Long id, String robotCode) throws Exception {
        boolean flag = false;
        try {
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id),
                    String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
            if (robotCode.equals(roadPathLock.getRobotCode())) {
                Integer lock = roadPathLock.getLockState();
                if (lock == null || lock == 0) {
                    //当前没有上锁，无锁状态
                    flag = true;
                } else {
                    //已经上锁，需要解锁
                    roadPathLock.setLockAction(RoadPathLock.LockAction.UNLOCK);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setRobotCode(robotCode);
                    updateSelective(roadPathLock);
                    flag = true;
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }
}