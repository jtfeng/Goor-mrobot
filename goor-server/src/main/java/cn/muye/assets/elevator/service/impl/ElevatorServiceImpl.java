package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.*;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorModeMapper;
import cn.muye.assets.elevator.mapper.ElevatorPointCombinationMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.service.ElevatorPointCombinationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
public class ElevatorServiceImpl extends BaseServiceImpl<Elevator> implements ElevatorService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorServiceImpl.class);
    @Autowired
    private ElevatorMapper elevatorMapper;
    @Autowired
    private ElevatorShaftMapper elevatorShaftMapper;
    @Autowired
    private ElevatorModeMapper elevatorModeMapper;
    @Autowired
    private ElevatorPointCombinationService elevatorPointCombinationService;
    private ReentrantLock lock;
    private ReentrantLock lock1;

    @Override
    public List<Elevator> listElevators(WhereRequest whereRequest) throws Exception {
        List<Elevator> elevators = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), Elevator.class,"ID DESC");
        return elevators;
    }

    @Override
    public List<Elevator> findByMapFloor(Long mapInfoId, Integer floor){
        log.info("###################  mapinfoid: " + mapInfoId + ", floor: " + floor);
        Long storeId = 100L;
        List<Elevator> elevators = this.elevatorMapper.selectAllFlushCache();
        bindElevatorShaft(elevators);
        bindElevatorPointCombination(elevators);
        elevators = elevators.stream().filter(new Predicate<Elevator>() {
            @Override
            public boolean test(Elevator elevator) {
                return storeId.equals(elevator.getStoreId());
            }
        }).collect(Collectors.toList());
        System.out.println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        for (Elevator elevator:elevators) {
            List<ElevatorPointCombination> combinations = elevator.getElevatorPointCombinations();
            elevator.setElevatorPointCombinations(
                    combinations.stream().filter(new Predicate<ElevatorPointCombination>() {
                        @Override
                        public boolean test(ElevatorPointCombination combination) {
                            boolean isPassed = false;
                            try {
                                // 四点集合必须在同一张地图上
                                MapInfo mapInfo = findByMapNameAndStoreId(
                                        combination.getgPoint().getMapName(),
                                        combination.getStoreId(),
                                        combination.getgPoint().getSceneName()
                                );
                                isPassed = mapInfoId.equals(mapInfo.getId()) && floor.equals(mapInfo.getFloor());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return isPassed;
                        }
                    }).collect(Collectors.toList())
            );
        }
        System.out.println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
        return elevators.stream().filter(new Predicate<Elevator>() {
            @Override
            public boolean test(Elevator elevator) {
                return elevator.getElevatorPointCombinations().size() != 0;
            }
        }).collect(Collectors.toList());

    }

    @Autowired
    private ElevatorPointCombinationMapper elevatorPointCombinationMapper;
    @Override
    public List<Elevator> findByMapFloor(Long mapInfoId, Integer floor, MapPoint waitPoint) {//传入的点类型肯定为等待点
        Long elevatorId = elevatorPointCombinationMapper.findElevatorByWaitPoint(waitPoint.getId());
        if (elevatorId != null) {
            Elevator elevator = elevatorMapper.selectByPrimaryKey(elevatorId);
            bindElevatorShaft(Collections.singletonList(elevator));//绑定修饰电梯，赋值实体类的其余属性
            if (elevator.getElevatorShaft() != null) {
                Long elevatorShaftId = elevator.getElevatorShaft().getId();//当前绑定点对应的电梯井
                List<Elevator> elevators = findByMapFloor(mapInfoId, floor);
                return elevators.stream().filter(new Predicate<Elevator>() {
                    @Override
                    public boolean test(Elevator elevator) {
                        return elevatorShaftId.equals(elevator.getElevatorShaft().getId());
                    }
                }).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public MapInfo findByMapNameAndStoreId(String mapName, Long storeId, String sceneName) throws Exception {
        List<MapInfo> mapInfos = this.elevatorMapper.findByMapNameAndStoreId(mapName, storeId, sceneName);
        checkArgument(mapInfos != null && mapInfos.size() == 1, "同一门店下不能有重名地图，请检查!");
        return mapInfos.get(0);
    }

    @Override
    public boolean updateElevatorLockState(Long elevatorId, Elevator.ELEVATOR_ACTION action){
        boolean flag = false;
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {// 限制超时时间为5秒
                Elevator elevator = super.findById(elevatorId);
                if (Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK.equals(action)) {
                    //上锁
                    if ("1".equals(elevator.getLockState())) {// 1表示上锁
                        flag = false;
                    } else {
                        this.elevatorMapper.updateElevatorLockState(elevatorId, 1);
                        flag = true;
                    }
                }
                if (Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK.equals(action)) {
                    //解锁
                    if ("1".equals(elevator.getLockState())) {// 1表示上锁
                        this.elevatorMapper.updateElevatorLockState(elevatorId, 0);
                    }
                    flag = true;
                }
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronizationAdapter() {
                                @Override
                                public void afterCompletion(int status) {
                                    lock.unlock();//释放锁
                                }
                            });
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 电梯上锁与加锁
     * @param elevatorId
     * @param action
     * @param robotCode
     * @return
     */
    @Override
    public boolean updateElevatorLockStateWithRobotCode(Long elevatorId, Elevator.ELEVATOR_ACTION action, String robotCode) {
        checkArgument(robotCode != null && !"".equals(robotCode.trim()), "机器人编号 robotCode 不允许为空!");
        boolean flag = false;
        try {
            if (lock1.tryLock(5, TimeUnit.SECONDS)) {
                Elevator elevator = super.findById(elevatorId);
                if (Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK.equals(action)) {
                    if ("1".equals(elevator.getLockState())) {// 1表示当前的电梯状态为"上锁状态"
                        if (robotCode.equals(elevator.getRobotCode())) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } else {
                        elevator.setLockState("1");
                        elevator.setRobotCode(robotCode);
                        this.update(elevator);
                        flag = true;
                    }
                }
                if (Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK.equals(action)) {
                    if ("1".equals(elevator.getLockState())) {// 0表示解锁
                        if (robotCode.equals(elevator.getRobotCode())) {
                            elevator.setLockState("0");
                            elevator.setRobotCode(null);
                            updateSelective(elevator);
                            flag = true;
                        }else {
                            flag = false;
                        }
                    }else {
                        flag = true;
                    }
                }
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronizationAdapter() {
                                @Override
                                public void afterCompletion(int status) {
                                    lock1.unlock();//释放锁
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

    @Override
    public void createElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //保存电梯信息
        save(elevator);
        //删除旧的关系
        elevatorMapper.deleteRelationsByElevatorId(elevator.getId());
        if (combinationIds.size() != 0) {
            //添加新的关系
            elevatorMapper.insertRelationsByElevatorId(elevator.getId(), combinationIds);
        }
    }

    @Override
    public void updateElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //更新电梯信息
        this.updateSelective(elevator);
        //删除旧的关系
        elevatorMapper.deleteRelationsByElevatorId(elevator.getId());
        if (combinationIds.size() != 0) {
            //添加新的关系
            elevatorMapper.insertRelationsByElevatorId(elevator.getId(), combinationIds);
        }
    }

    @Override
    public List<Elevator> findByElevatorPointCombinationId(Long elevatorPointCombinationId) {
        List<Elevator> elevators = elevatorMapper.findByElevatorPointCombinationId(elevatorPointCombinationId);
        bindElevatorShaft(elevators);
        return elevators;
    }

    @Override
    public List<Elevator> listPageByStoreIdAndOrder(int page, int pageSize, Class<Elevator> clazz, String order) {
        List<Elevator> elevators = super.listPageByStoreIdAndOrder(page, pageSize, clazz, order);
        bindElevatorShaft(elevators);
        bindElevatorPointCombination(elevators);
        return elevators;
    }

    private void bindElevatorShaft(List<Elevator> elevators){
        for (Elevator elevator:elevators){
            try {
                checkNotNull(elevator.getElevatorshaftId());
                log.info(" - - - - - ");
                log.info(String.format("所属电梯井的编号为：%d", elevator.getElevatorshaftId()));
                ElevatorShaft elevatorShaft = this.elevatorShaftMapper.selectByPrimaryKey(elevator.getElevatorshaftId());
                log.info(String.format("所属电梯井的信息为：%s",elevatorShaft.toString()));
                elevator.setElevatorShaft(elevatorShaft);
                log.info(" - - - - - ");
            }catch (Exception e){
                elevator.setElevatorShaft(null);
            }
        }
    }
    private void bindElevatorPointCombination(List<Elevator> elevators){
        for (Elevator elevator:elevators){
            try {
                checkNotNull(elevator.getId());
                log.info(" - - - - - ");
                log.info(String.format("当前电梯的编号信息为：%d", elevator.getId()));
                List<ElevatorPointCombination> combinations = this.elevatorPointCombinationService.findByElevatorId(elevator.getId());
                log.info(String.format("当前该电梯所绑定的点组合信息为：%s",combinations.toString()));
                elevator.setElevatorPointCombinations(combinations);
                log.info(" - - - - - ");
            }catch (Exception e){
                elevator.setElevatorPointCombinations(null);
            }
        }
    }

    @Override
    public Elevator findById(Long id) {
        Elevator elevator = super.findById(id);
        bindElevatorShaft(Lists.newArrayList(elevator));
        bindElevatorPointCombination(Lists.newArrayList(elevator));
        return elevator;
    }

    @Override
    public ElevatorModeEnum determineCurrentElevatorMode(Long elevatorId) throws Exception {
        Date currentDate = new Date();
        Example example = new Example(ElevatorMode.class);
        example.createCriteria()
                .andCondition("ELEVATOR_ID =", elevatorId)
                .andCondition("START_TIME <=", currentDate)
                .andCondition("END_TIME >=", currentDate);
        List<ElevatorMode> elevatorModes = elevatorModeMapper.selectByExample(example);
        if (elevatorModes != null && elevatorModes.size() != 0) {
            ElevatorMode mode = elevatorModes.get(0);
            if (0 == mode.getState()) {
                return ElevatorModeEnum.FULL_AUTOMATIC;
            }
            if (1 == mode.getState()) {
                return ElevatorModeEnum.HALF_AUTOMATIC;
            }
        }
        return null;
    }
}
