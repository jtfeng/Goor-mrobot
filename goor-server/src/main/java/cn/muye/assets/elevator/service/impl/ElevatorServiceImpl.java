package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
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
import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class ElevatorServiceImpl extends BaseServiceImpl<Elevator> implements ElevatorService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorServiceImpl.class);
    @Autowired
    private ElevatorMapper elevatorMapper;
    @Autowired
    private ElevatorShaftMapper elevatorShaftMapper;
    @Autowired
    private ElevatorPointCombinationService elevatorPointCombinationService;

    @Override
    public List<Elevator> listElevators(WhereRequest whereRequest) throws Exception {
        List<Elevator> elevators = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), Elevator.class,"ID DESC");
        return elevators;
    }

    @Override
    public List<Elevator> findByMapFloor(Long mapInfoId, Integer floor){
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
                                MapInfo mapInfo = findByMapNameAndStoreId(combination.getgPoint().getMapName(), combination.getStoreId());
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

    @Override
    public MapInfo findByMapNameAndStoreId(String mapName, Long storeId) throws Exception {
        List<MapInfo> mapInfos = this.elevatorMapper.findByMapNameAndStoreId(mapName, storeId);
        checkArgument(mapInfos != null && mapInfos.size() == 1, "同一门店下不能有重名地图，请检查!");
        return mapInfos.get(0);
    }

    @Override
    public synchronized boolean updateElevatorLockState(Long elevatorId, Elevator.ELEVATOR_ACTION action){
        Elevator elevator = super.findById(elevatorId);
        if (Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK.equals(action)){
            //上锁
            if ("1".equals(elevator.getLockState())){// 1表示上锁
                return false;
            }else {
                this.elevatorMapper.updateElevatorLockState(elevatorId, 1);
                return true;
            }
        }
        if (Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK.equals(action)){
            //解锁
            if ("1".equals(elevator.getLockState())) {// 1表示上锁
                this.elevatorMapper.updateElevatorLockState(elevatorId, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean updateElevatorLockStateWithRobotCode(Long elevatorId, Elevator.ELEVATOR_ACTION action, String robotCode) {
        checkArgument(robotCode != null && !"".equals(robotCode.trim()), "机器人编号 robotCode 不允许为空!");
        Elevator elevator = super.findById(elevatorId);
        if (Elevator.ELEVATOR_ACTION.ELEVATOR_LOCK.equals(action)){
            if ("1".equals(elevator.getLockState())){// 1表示上锁
                return false;
            }else {
                elevator.setLockState("1");
                elevator.setRobotCode(robotCode);
                updateSelective(elevator);
                return true;
            }
        }
        if (Elevator.ELEVATOR_ACTION.ELEVATOR_UNLOCK.equals(action)){
            if (("1".equals(elevator.getLockState()))  && (robotCode.equals(elevator.getRobotCode()))  ) {// 0表示解锁
                elevator.setLockState("0");
                elevator.setRobotCode(null);
                updateSelective(elevator);
            }
            return true;
        }
        return false;
    }

    @Override
    public void createElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //保存电梯信息
        save(elevator);
        //删除旧的关系
        elevatorMapper.deleteRelationsByElevatorId(elevator.getId());
        //添加新的关系
        elevatorMapper.insertRelationsByElevatorId(elevator.getId(), combinationIds);
    }

    @Override
    public void updateElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //更新电梯信息
         update(elevator);
        //删除旧的关系
        elevatorMapper.deleteRelationsByElevatorId(elevator.getId());
        //添加新的关系
        elevatorMapper.insertRelationsByElevatorId(elevator.getId(), combinationIds);
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
}
