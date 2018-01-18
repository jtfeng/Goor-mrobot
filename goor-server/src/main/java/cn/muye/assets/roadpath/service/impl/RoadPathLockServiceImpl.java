package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.mapper.RoadPathLockMapper;
import cn.muye.assets.roadpath.mapper.RoadPathMapper;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author wlkfec
 */
@Service
public class RoadPathLockServiceImpl extends BaseServiceImpl<RoadPathLock> implements RoadPathLockService {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockServiceImpl.class);
    @Autowired
    private RoadPathMapper roadPathMapper;
    @Autowired
    private RoadPathLockMapper roadPathLockMapper;
    @Autowired
    private RobotMapper robotMapper;

    @Override
    public synchronized boolean lock(Long id, String robotCode) throws Exception {
        log.info(" ***** ----- ----- ----- ----- 加锁开始 ----- ----- ----- ----- ***** ");
        Preconditions.checkNotNull(id, "RoadPath 路径编号不允许为空！");
        Preconditions.checkNotNull(robotCode, "robotCode 机器人编号不允许为空！");
        boolean result = lockInner(id, robotCode, id);
        log.info(" ***** ----- ----- ----- ----- 加锁结束 ----- ----- ----- ----- ***** ");
        return result;
    }

    @Override
    public synchronized boolean lockDirection(Long id, String robotCode, Long direction) throws Exception {
        log.info(" ***** ----- ----- ----- ----- 加锁开始 ----- ----- ----- ----- ***** ");
        Preconditions.checkNotNull(id, "RoadPath 路径编号不允许为空！");
        Preconditions.checkNotNull(robotCode, "robotCode 机器人编号不允许为空！");
        boolean result = lockInner(id, robotCode, direction);
        log.info(" ***** ----- ----- ----- ----- 加锁结束 ----- ----- ----- ----- ***** ");
        return result;
    }

    /**
     * 上锁
     *
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
                } else {
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return flag;
    }


    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -----

    /**
     * 加入了锁方向进行加锁和释放锁操作
     *
     * @param id
     * @param robotCode
     * @param direction
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean lockInner(Long id, String robotCode, Long direction) throws Exception {
        log.info("路径锁编号：" + id + "、机器人编号为：" + robotCode + "、加锁方向为(按照定义即为路径编号)：" + direction);
        boolean flag = false;
        try {
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id), String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
            Preconditions.checkNotNull(direction, "方向信息不允许为空!");
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            //允许通过的最大机器人数量
            Long passCount = roadPathLock.getPassCount();
            //当前路径中排队的机器人数量
            Long currentPasscount = roadPathLock.getCurrentPasscount();
            //路径中所有排队的机器人编号信息
            String robotCodes = roadPathLock.getRobotCodes();
            //路径锁绑定的方向信息，加锁的时候需要验证此方向
            Long pathDirection = roadPathLock.getDirection();
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            log.info("【当前状态】允许通过的最大机器人数量：" + passCount +
                    "、当前路径中排队的机器人数量：" + currentPasscount +
                    "、路径中所有排队的机器人编号信息：" + robotCodes +
                    "、路径锁绑定的方向：" + pathDirection);

            if (robotCodes != null && robotCodes.contains(robotCode)) {
                // 此时的锁成为可重入锁
                log.info("编号为：" + robotCode + " 的机器人重复请求执行加锁操作，上一次已经成功获取锁，所以直接返回加锁成功。");
                flag = true;
            } else {
                // 首先需要判断是不是 第一个机器人 进入执行加锁操作
                if (pathDirection == null) {
                    log.info("该机器人首次对路径锁进行加锁");
                    //(第一个)机器人进入
                    roadPathLock.setLockAction(RoadPathLock.LockAction.LOCK);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setRobotCode(robotCode);
                    roadPathLock.setDirection(direction);
                    roadPathLock.setCurrentPasscount(1L);
                    roadPathLock.setRobotCodes(robotCode);
                    updateSelective(roadPathLock);
                    flag = true;
                } else {
                    log.info("路径锁已经被某个机器人进行加锁操作，后续机器人进行判断确定是否可以执行加锁操作");
                    //后续机器人依次进入，需要根据方向判断是否能够进行加锁
                    if (!direction.equals(pathDirection)) {
                        log.info("加锁方向与最初的加锁方向不一致，加锁失败");
                        //传入的方向与绑定的锁方向不一致，加锁失败
                        flag = false;
                    } else {
                        //方向一致此时需要判断当前排队数量是否已经达到阈值，若达到阈值，则加锁失败
                        if (currentPasscount.equals(passCount)) {
                            log.info("加锁方向与最初方向一致，但路径队列中的机器人数量已经达到最大阈值，加锁失败");
                            //加锁失败
                            flag = false;
                        } else {
                            log.info("满足各项检查条件，加锁成功！");
                            //没有达到阈值，执行加锁操作
                            roadPathLock.setCurrentPasscount(currentPasscount + 1L);
                            roadPathLock.setCreateTime(new Date());
                            //将传入的机器人设置为拥有锁对象的持有者
                            roadPathLock.setRobotCode(robotCode);
                            //将此机器人信息加入到路径的冗余信息中
                            roadPathLock.setRobotCodes(robotCodes + "," + robotCode);
                            updateSelective(roadPathLock);
                            flag = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public synchronized boolean lock(Long id, String robotCode, Long direction) throws Exception {
        return lockInner(id, robotCode, direction);
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -----

    @Override
    public synchronized boolean unlock(Long id, String robotCode) throws Exception {
        log.info(" ***** ----- ----- ----- ----- 解锁开始 ----- ----- ----- ----- ***** ");
        Preconditions.checkNotNull(id, "RoadPath 路径编号不允许为空！");
        Preconditions.checkNotNull(robotCode, "robotCode 机器人编号不允许为空！");
        boolean result = unlockInnerNewVersion(id, robotCode);
        log.info(" ***** ----- ----- ----- ----- 解锁结束 ----- ----- ----- ----- ***** ");
        return result;
    }

    /**
     * 解锁
     *
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 加入了锁方向，执行锁解锁操作
     *
     * @param id        锁ID
     * @param robotCode
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unlockInnerNewVersion(Long id, String robotCode) throws Exception {
        log.info("路径锁编号：" + id + "、机器人编号为：" + robotCode);
        boolean flag = false;
        try {
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(id), String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(id)));
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            //允许通过的最大机器人数量
            Long passCount = roadPathLock.getPassCount();
            //当前路径中排队的机器人数量
            Long currentPasscountDB = roadPathLock.getCurrentPasscount();
            Long currentPasscount = (null != currentPasscountDB) ? currentPasscountDB : 0;
            //路径中所有排队的机器人编号信息
            String robotCodes = roadPathLock.getRobotCodes();
            //路径锁绑定的方向信息，加锁的时候需要验证此方向
            Long pathDirection = roadPathLock.getDirection();
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            log.info("【当前状态】允许通过的最大机器人数量：" + passCount +
                    "、当前路径中排队的机器人数量：" + currentPasscount +
                    "、路径中所有排队的机器人编号信息：" + robotCodes +
                    "、路径锁绑定的方向：" + pathDirection);
            List<String> containerRobots = Lists.newArrayList();
            if (robotCodes != null) {
                Arrays.stream(robotCodes.split(",")).forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        containerRobots.add(s);
                    }
                });
            }
            //解锁需要先判断，对应的路径所绑定的机器人信息中是否包含有发出解锁指令的机器人
            if (containerRobots.indexOf(robotCode) == -1) {
                log.info("发出解锁指令的机器人不在当前路径队列中，直接返回解锁成功的状态！");
                //发出指令的机器人当前不在路径排队对象中，解锁失败
                flag = true;
            } else {
                //执行解锁 （真实解锁 与 修改排队数量 - 两类操作）
                if (currentPasscount != 1) {
                    log.info("满足解锁条件，但当前路径队列中机器人数量大于1，故此时的解锁只修改排队机器人数量，解锁成功");
                    //表示当前排队中有多个机器人，这时修改数量
                    containerRobots.remove(robotCode);
                    StringBuilder builder = new StringBuilder();
                    containerRobots.stream().forEach(e -> builder.append(e).append(","));
                    //最新的排队机器人信息
                    String currentStr = builder.toString().substring(0, builder.toString().length() - 1);
                    roadPathLock.setRobotCodes(currentStr);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setCurrentPasscount(currentPasscount - 1L);
                    updateSelective(roadPathLock);
                    flag = true;
                } else {
                    log.info("满足解锁条件，并且当前发出解锁指令的机器人为路径队列中唯一的机器人，此时执行真真的解锁操作，将相关属性进行重置，解锁成功");
                    //真正的执行解锁操作
                    roadPathLock.setLockAction(RoadPathLock.LockAction.UNLOCK);
                    roadPathLock.setRobotCode(null);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setCurrentPasscount(null);
                    roadPathLock.setRobotCodes(null);
                    roadPathLock.setDirection(null);
                    update(roadPathLock);
                    flag = true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public synchronized boolean unlockNewVersion(Long id, String robotCode) throws Exception {
        return unlockInnerNewVersion(id, robotCode);
    }

    @Override
    public List<RoadPathLock> listRoadPathLocks(WhereRequest whereRequest) throws Exception {
        List<RoadPathLock> roadPathLockList = listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), RoadPathLock.class, "ID DESC");
        return roadPathLockList;
    }

    /**
     * 云端主动释放(手动释放)指定机器人的路径锁
     *
     * @param robotCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean cloudReleaseRoadPathLock(String robotCode) throws Exception {
        Example example = new Example(RoadPathLock.class);
        example.createCriteria().andCondition(" ROBOT_CODES like ", "%" + robotCode + "%");
        // 一般只有一个元素(每台机器应该单一时刻只能请求一个路径锁)
        List<RoadPathLock> locks = this.roadPathLockMapper.selectByExample(example);
        // 表示最后的解锁结果
        boolean result = true;
        for (RoadPathLock roadPathLock : locks) {
            // 手动解锁机器人路径锁，调用同步方法完成
            result = result && unlock(roadPathLock.getId(), robotCode);
        }
        return result;
    }

    @Override
    public void schuleReleaseRoadpathLock() throws Exception {
        List<Robot> robotList = robotMapper.selectAll();
        if (null == robotList || robotList.size() <= 0) {
            return;
        }
        for (Robot robot : robotList) {
            String robotCode = robot.getCode();
            if (robot.getBusy() == null || !robot.getBusy()){
                cloudReleaseRoadPathLock(robotCode);
            }else if(robot.getBusy()){
               // 非空闲，查看机器人在线状态，如果机器人不在线，则清除所有的锁
                Boolean isOnline = CacheInfoManager.getRobotOnlineCache(robotCode);
                if (isOnline== null || !isOnline){
                    cloudReleaseRoadPathLock(robotCode);
                }
            }
        }
    }
}