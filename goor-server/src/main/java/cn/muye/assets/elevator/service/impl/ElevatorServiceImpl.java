package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.area.map.MapInfo;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.*;
import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.point.service.PointService;
import cn.muye.area.point.service.impl.PointServiceImpl;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorModeMapper;
import cn.muye.assets.elevator.mapper.ElevatorPointCombinationMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.service.ElevatorPointCombinationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.roadpath.service.RoadPathService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
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
    @Autowired
    private RoadPathService roadPathService;
    @Autowired
    private PointService pointService;

    @Override
    public List<Elevator> listElevatorByScene(Long id) {
        return elevatorMapper.listElevatorByScene(id);
    }

    @Transactional
    @Override
    public List<Elevator> listElevators(WhereRequest whereRequest) throws Exception {
        Map<String, Long> params = Maps.newHashMap();
        JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
        params.put("sceneId", Long.parseLong(String.valueOf(jsonObject.get("sceneId"))));
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        List<Elevator> elevators =  elevatorMapper.listElevatorsByScene(params);
        bindElevatorShaft(elevators);
        bindElevatorPointCombination(elevators);
        return elevators;
    }

    @Transactional
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
                                log.error(e.getMessage(), e);
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

    @Transactional
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

    @Transactional
    @Override
    public MapInfo findByMapNameAndStoreId(String mapName, Long storeId, String sceneName) throws Exception {
        List<MapInfo> mapInfos = this.elevatorMapper.findByMapNameAndStoreId(mapName, storeId, sceneName);
        checkArgument(mapInfos != null && mapInfos.size() == 1, "同一门店下不能有重名地图，请检查!");
        return mapInfos.get(0);
    }

    @Override
    public synchronized boolean updateElevatorLockState(Long elevatorId, Elevator.ELEVATOR_ACTION action) {
        return updateElevatorLockStateInner(elevatorId, action);
    }

    @Transactional
    @Override
    public boolean updateElevatorLockStateInner(Long elevatorId, Elevator.ELEVATOR_ACTION action){
        boolean flag = false;
        try {
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
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public synchronized boolean updateElevatorLockStateWithRobotCode(Long elevatorId, Elevator.ELEVATOR_ACTION action, String robotCode) {
        return updateElevatorLockStateWithRobotCodeInner(elevatorId,action, robotCode);
    }

    /**
     * 电梯上锁与加锁
     * @param elevatorId
     * @param action
     * @param robotCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateElevatorLockStateWithRobotCodeInner(Long elevatorId, Elevator.ELEVATOR_ACTION action, String robotCode) {
        checkArgument(robotCode != null && !"".equals(robotCode.trim()), "机器人编号 robotCode 不允许为空!");
        boolean flag = false;
        try {
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
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //保存电梯信息
        save(elevator);
        //更新电梯四点对象关联关系，并维护假的地图四点对象路径
        updateElevatorFakePathAndCombination(elevator, combinationIds);
    }

    /**
     * 更新电梯四点对象关联关系，并维护假的地图四点对象路径
     * @param elevator
     * @param combinationIds
     * @throws Exception
     */
    private void updateElevatorFakePathAndCombination (Elevator elevator, List<Long> combinationIds) throws Exception {
        //todo 每次保存电梯，要根据电梯新四点对象删除图缓存，删除原假的电梯的对象路径。生成新电梯四点对象路径，并更新（重新生成）原云端路径缓存。
        updateElevatorFakePath(elevator);

        if (combinationIds.size() != 0) {
            //添加新的关系
            elevatorMapper.insertRelationsByElevatorId(elevator.getId(), combinationIds);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateElevator(Elevator elevator, List<Long> combinationIds) throws Exception {
        //更新电梯信息
        this.updateSelective(elevator);
        //更新电梯四点对象关联关系，并维护假的地图四点对象路径
        updateElevatorFakePathAndCombination(elevator, combinationIds);
    }

    /**
     * 每次保存电梯，要根据电梯新四点对象删除图缓存，删除原假的电梯的对象路径。生成新电梯四点对象路径，并更新（重新生成）原云端路径缓存。
     * @param elevator
     */
    private void updateElevatorFakePath(Elevator elevator) throws Exception {
        String sceneName = elevator.getSceneName();
        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);

        List<ElevatorPointCombination> elevatorCombinationDBOld = this.elevatorPointCombinationService.findByElevatorId(elevator.getId());
        //删除原假的电梯的对象路径
        if(elevatorCombinationDBOld != null && elevatorCombinationDBOld.size() > 0) {
            for(ElevatorPointCombination elevatorPointCombination : elevatorCombinationDBOld) {
                roadPathService.deleteByStartEndPointIdType(elevatorPointCombination.getWaitPoint(), null , Constant.PATH_TYPE_CLOUD, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
                roadPathService.deleteByStartEndPointIdType(null, elevatorPointCombination.getOutPoint(), Constant.PATH_TYPE_CLOUD, sceneName, SearchConstants.FAKE_MERCHANT_STORE_ID);
            }
        }

        //删除旧的四点对象关系关系
        elevatorMapper.deleteRelationsByElevatorId(elevator.getId());

        //生成新的该电梯的假的四点对象路径
        if(elevator.getElevatorPointCombinations() != null && elevator.getElevatorPointCombinations().size() > 0) {
            generateFakePathByElevator(elevator);
        }

        //清空某场景、某门店下的路径相关的缓存
        PathUtil.clearPathCache(SearchConstants.FAKE_MERCHANT_STORE_ID, sceneName);
    }

    @Transactional
    @Override
    public List<Elevator> findByElevatorPointCombinationId(Long elevatorPointCombinationId) {
        List<Elevator> elevators = elevatorMapper.findByElevatorPointCombinationId(elevatorPointCombinationId);
        bindElevatorShaft(elevators);
        return elevators;
    }

    @Transactional
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

    @Transactional
    @Override
    public Elevator findById(Long id) {
        Elevator elevator = super.findById(id);
        bindElevatorShaft(Lists.newArrayList(elevator));
        bindElevatorPointCombination(Lists.newArrayList(elevator));
        return elevator;
    }

    @Transactional
    @Override
    public ElevatorModeEnum determineCurrentElevatorMode(Long elevatorId) throws Exception {
        String currentDateStr = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Example example = new Example(ElevatorMode.class);
        example.createCriteria()
                .andCondition("ELEVATOR_ID =", elevatorId)
                .andCondition("START <=", currentDateStr)
                .andCondition("END >=", currentDateStr);
        List<ElevatorMode> elevatorModes = elevatorModeMapper.selectByExample(example);
        log.info(" ################################################################## ");
        log.info(String.format("查询电梯的id是：" + elevatorId));
        if (elevatorModes != null && elevatorModes.size() != 0) {
            ElevatorMode mode = elevatorModes.get(0);
            log.info(String.format("电梯的模式为：%d", mode.getState()));
            return ElevatorModeEnum.getElevatorModeEnum(mode.getState());
        }else{
            log.info(String.format("没有获取到电梯的模式"));
        }
        log.info(" ################################################################## ");
        //如果没有查询结果，则设置默认返回结果为 "全自动" 模式
        return ElevatorModeEnum.FULL_AUTOMATIC;
    }

    @Transactional
    @Override
    public List<Elevator> listBySceneName(String sceneName) {
        Example example = new Example(Elevator.class);
        example.createCriteria().andCondition("SCENE_NAME =", sceneName);
        List<Elevator> elevators = myMapper.selectByExample(example);
        bindElevatorShaft(elevators);
        bindElevatorPointCombination(elevators);
        return elevators;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult generateFakePathByElevator(Elevator elevator) throws Exception{
        String placeholder = " | ----- | ";
        String sceneName = elevator.getSceneName();
        List<ElevatorPointCombination> elevatorPointCombinations = new ArrayList<>();
        //从数据库查出四点对象
        for(ElevatorPointCombination tempCombination : elevator.getElevatorPointCombinations()) {
            tempCombination = elevatorPointCombinationService.findById(tempCombination.getId());

            if(tempCombination == null) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,"四点对象不存在，参数错误！");
            }
            elevatorPointCombinations.add(tempCombination);
        }

        log.info(placeholder + "elevatorPointCombinations、" + elevatorPointCombinations.toString());
        //遍历同一部电梯的四点对象，生成两两之间的假工控路径
        for(ElevatorPointCombination startCombination : elevatorPointCombinations) {
            log.info(placeholder + "startCombination、" + startCombination.toString());
            MapPoint startCombinePoint = startCombination.getwPoint();
            log.info(placeholder + "startCombinePoint、" + startCombinePoint.toString());
            if(startCombinePoint == null || startCombinePoint.getId() == null) {
                return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'等待点为空，生成错误！");
            }
            for(ElevatorPointCombination endCombination : elevatorPointCombinations) {
                log.info(placeholder + "endCombination、" + endCombination.toString());
                //跳过相同的四点集合
                if(endCombination.getId().equals(startCombination.getId())) {
                    continue;
                }
                MapPoint endCombinePoint = endCombination.getoPoint();
                if(endCombinePoint == null || endCombinePoint.getId() == null) {
                    return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'出去点为空，生成错误！");
                }
                log.info(placeholder + "endCombinePoint、" + endCombinePoint.toString());

                //电梯云端路径起点是出发楼层工控路径终点
                MapPoint startPathPoint = PathUtil.findPathPointByXYTH(sceneName,
                        startCombinePoint.getMapName(),startCombinePoint.getX(),startCombinePoint.getY(),startCombinePoint.getTh(),null, pointService);
                if(startPathPoint == null) {
                    log.info(placeholder + "startPathPoint is null");
                    return AjaxResult.failed(AjaxResult.CODE_FAILED,elevator.getName() + "电梯关联的四点集合'"+ startCombination.getName() +"'等待点相关联的path路径点为空，生成错误！");
                }

                log.info(placeholder + "startPathPoint、" + startPathPoint.toString());
                Long startPathPointId = startPathPoint.getId();
                Long startCombinePointId = startCombinePoint.getId();
                Long endCombinePointId = endCombinePoint.getId();
                //没有则新建
                RoadPath roadPath = new RoadPath();
                roadPath.setWeight(Constant.DEFAULT_ELEVATOR_X86_WEIGHT);
                roadPath.setMapName(startCombinePoint.getMapName());
                roadPath.setSceneName(sceneName);
                roadPath.setData("");
                roadPath.setEndPoint(endCombinePointId);
                roadPath.setStartPoint(startPathPointId);
                roadPath.setPattern("");
                roadPath.setPathName(elevator.getName() + "_"
                        + startCombinePoint.getPointAlias() + "_" + startCombinePoint.getMapName() + "_" + startCombinePoint.getSceneName()
                        + "_to_"
                        + endCombinePoint.getPointAlias() + "_" + endCombinePoint.getMapName() + "_" + endCombinePoint.getSceneName() + "_auto");
                roadPath.setCreateTime(new Date());
                roadPath.setPathType(Constant.PATH_TYPE_CLOUD);
                roadPath.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
                log.info(placeholder + "roadPath、" + roadPath.toString());
                List<Long> pointIds = new ArrayList<>();
                pointIds.add(startPathPointId);
                pointIds.add(startCombinePointId);
                pointIds.add(endCombinePointId);
                log.info(placeholder + "pointIds、" + pointIds.toString());

                roadPathService.createOrUpdateRoadPathByStartAndEndPoint(startPathPointId,
                        endCombinePointId,sceneName,null,Constant.PATH_TYPE_CLOUD,
                        roadPath,pointIds);
            }
        }
        return AjaxResult.success();
    }

    /**
     *
     * @param elevatorList
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult generateFakePathByElevatorList(List<Elevator> elevatorList) throws Exception {
        String placeholder = " | ----- | ";
        /*
        * 插入电梯点之间的路径
        * **/
        //遍历循环电梯列表，建立同一个电梯的电梯四点对象两两间等待点到出电梯的path点的假工控路径(不同电梯之间是没有路径的)
        for(Elevator elevator:elevatorList) {
            log.info(placeholder + "elevator、" + elevator.toString());
            //该电梯至少得有两个四点对象才能建立电梯楼层间云端路径
            if (elevator == null ||
                    elevator.getElevatorPointCombinations() == null ||
                    elevator.getElevatorPointCombinations().size() <= 1) {
                continue;
            }
            AjaxResult ajaxResult = generateFakePathByElevator(elevator);
            if(!ajaxResult.isSuccess()) {
                return ajaxResult;
            }
        }
        return AjaxResult.success();
    }

    @Override
    public List<Elevator> listByShaftId(Long id) {
        Example example = new Example(Elevator.class);
        example.createCriteria().andCondition("ELEVATORSHAFT_ID =", id);
        List<Elevator> elevators = myMapper.selectByExample(example);
        bindElevatorShaft(elevators);
        bindElevatorPointCombination(elevators);
        return elevators;
    }
}
