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
@Transactional(rollbackFor = Exception.class)
public class RoadPathLockServiceImpl extends BaseServiceImpl<RoadPathLock> implements RoadPathLockService {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockServiceImpl.class);
    @Autowired
    private RoadPathLockMapper roadPathLockMapper;
    private ReentrantLock lock1 = new ReentrantLock();
    private ReentrantLock lock2 = new ReentrantLock();

    /**
     * 上锁
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public boolean lock(Long id, String robotCode) throws Exception {
        boolean flag = false;
        try {
            if (lock1.tryLock(5, TimeUnit.SECONDS)) {
                RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id),
                        String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
                Integer lock = roadPathLock.getLock();
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
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronizationAdapter() {
                                @Override
                                public void afterCompletion(int status) {
                                    lock1.unlock();
                                }
                            });
                } else {
                    lock1.unlock();
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 解锁
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public synchronized boolean unlock(Long id, String robotCode) throws Exception {
        boolean flag = false;
        try {
            if (lock2.tryLock(5, TimeUnit.SECONDS)) {
                RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id),
                        String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
                if (robotCode.equals(roadPathLock.getRobotCode())) {
                    Integer lock = roadPathLock.getLock();
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
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronizationAdapter() {
                                @Override
                                public void afterCompletion(int status) {
                                    lock2.unlock();
                                }
                            });
                } else {
                    lock2.unlock();
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }
}