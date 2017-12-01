package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.elevator.service.ElevatorShaftService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.AssertTrue;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ElevatorShaftServiceImpl extends BaseServiceImpl<ElevatorShaft> implements ElevatorShaftService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorShaftServiceImpl.class);
    @Autowired
    private ElevatorMapper elevatorMapper;
    @Autowired
    private ElevatorShaftMapper elevatorShaftMapper;
    @Autowired
    private ElevatorService elevatorService;

    @Transactional
    @Override
    public List<ElevatorShaft> listElevatorShafts(WhereRequest whereRequest) throws Exception {
        List<ElevatorShaft> elevatorShafts = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), ElevatorShaft.class,"ID DESC");
        return elevatorShafts;
    }
    @Transactional
    @Override
    public List<ElevatorShaft> listPageByStoreIdAndOrder(int page, int pageSize, Class<ElevatorShaft> clazz, String order) {
        List<ElevatorShaft> elevatorShafts = super.listPageByStoreIdAndOrder(page, pageSize, clazz, order);
        packageDateBindelevator(elevatorShafts);
        return elevatorShafts;
    }

    private void packageDateBindelevator(List<ElevatorShaft> elevatorShafts){
        for (ElevatorShaft elevatorShaft:elevatorShafts){
            try {
                checkNotNull(elevatorShaft.getId());
                log.info(" - - - - - ");
                log.info(String.format("电梯井的 ID 编号为：%s", elevatorShaft.getId()));
                List<Elevator> elevators = elevatorService.listByShaftId(elevatorShaft.getId());
                //TODO 20171201以下方法查不到，不知道为什么——wlkfec之前测试的是可以的吗？
                /*List<Elevator> elevators = this.elevatorMapper.select(new Elevator(){{
                    setElevatorshaftId(elevatorShaft.getId());
                }});*/
                log.info(String.format("该电梯井下属的电梯有：%s", elevators.toString()));
                log.info(" - - - - - ");
                elevatorShaft.setElevators(elevators);
            }catch (Exception e){
                elevatorShaft.setElevators(Lists.newArrayList());
            }
        }
    }
    @Transactional
    @Override
    public ElevatorShaft findById(Long id) {
        ElevatorShaft shaft = super.findById(id);
        packageDateBindelevator(Lists.newArrayList(shaft));
        return shaft;
    }

    @Override
    public synchronized boolean updateElevatorShaftLockState(Long elevatorShaftId, ElevatorShaft.ELEVATORSHAFT_ACTION action) {
        return updateElevatorShaftLockStateInner(elevatorShaftId, action);
    }

    @Transactional
    @Override
    public boolean updateElevatorShaftLockStateInner(Long elevatorShaftId, ElevatorShaft.ELEVATORSHAFT_ACTION action){
        boolean flag = false;
        try {
            ElevatorShaft elevatorShaft = super.findById(elevatorShaftId);//先获取电梯井对象
            if (ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_LOCK.equals(action)) {
                //上锁
                if ("1".equals(elevatorShaft.getLockState())) {// 1表示上锁
                    flag = false;
                } else {
                    this.elevatorShaftMapper.updateElevatorShaftLockState(elevatorShaftId, 1);
                    flag = true;
                }
            }
            if (ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_UNLOCK.equals(action)) {
                //解锁
                if ("1".equals(elevatorShaft.getLockState())) {// 1表示上锁
                    this.elevatorShaftMapper.updateElevatorShaftLockState(elevatorShaftId, 0);
                }
                flag = true;
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public synchronized boolean updateElevatorShaftLockStateWithRobotCode(Long elevatorShaftId, ElevatorShaft.ELEVATORSHAFT_ACTION action, String robotCode) {
        return updateElevatorShaftLockStateWithRobotCodeInner(elevatorShaftId, action, robotCode);
    }

    /**
     * 上锁与加锁
     * @param elevatorShaftId
     * @param action
     * @param robotCode
     * @return
     */
    @Transactional
    @Override
    public boolean updateElevatorShaftLockStateWithRobotCodeInner(Long elevatorShaftId, ElevatorShaft.ELEVATORSHAFT_ACTION action, String robotCode) {
        checkArgument(robotCode != null && !"".equals(robotCode.trim()), "机器人编号 robotCode 不允许为空!");
        boolean flag = false;
        try {
            ElevatorShaft elevatorShaft = super.findById(elevatorShaftId);
            if (ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_LOCK.equals(action)) {
                if ("1".equals(elevatorShaft.getLockState())) {// 1表示当前状态为"上锁状态"
                    if (robotCode.equals(elevatorShaft.getRobotCode())) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } else {
                    elevatorShaft.setLockState("1");
                    elevatorShaft.setRobotCode(robotCode);
                    this.update(elevatorShaft);
                    flag = true;
                }
            }
            if (ElevatorShaft.ELEVATORSHAFT_ACTION.ELEVATORSHAFT_UNLOCK.equals(action)) {
                //UNLOCK 表示解锁动作，机器人发出解锁请求
                if ("1".equals(elevatorShaft.getLockState())) {// 如果当前为上锁状态
                    if (robotCode.equals(elevatorShaft.getRobotCode())) {
                        elevatorShaft.setLockState("0");
                        elevatorShaft.setRobotCode(null);
                        updateSelective(elevatorShaft);
                        flag = true;
                    }else {
                        flag = false;
                    }
                } else {
                    // 当前状态已经是解锁状态
                    flag = true;
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }
}
