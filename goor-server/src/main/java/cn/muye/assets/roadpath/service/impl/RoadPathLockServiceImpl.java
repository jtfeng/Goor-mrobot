package cn.muye.assets.roadpath.service.impl;

import cn.mrobot.bean.assets.roadpath.RoadPath;
import cn.mrobot.bean.assets.roadpath.RoadPathLock;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.roadpath.mapper.RoadPathLockMapper;
import cn.muye.assets.roadpath.mapper.RoadPathMapper;
import cn.muye.assets.roadpath.service.RoadPathLockService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RoadPathLockServiceImpl extends BaseServiceImpl<RoadPathLock> implements RoadPathLockService {

    private static final Logger log = LoggerFactory.getLogger(RoadPathLockServiceImpl.class);
    @Autowired
    private RoadPathMapper roadPathMapper;
    @Autowired
    private RoadPathLockMapper roadPathLockMapper;

    @Override
    public synchronized boolean lock(Long id, String robotCode) throws Exception {
        return lockInner(id, robotCode);
        // 加入了方向以及控制排队机器人数量重新修改加解锁方法
//        return lockInner(id, robotCode, id);
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


    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -----
    /**
     * 加入了锁方向进行加锁和释放锁操作
     * @param id
     * @param robotCode
     * @param direction
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean lockInner(Long id, String robotCode, Long direction) throws Exception {
        boolean flag = false;
        try {
            RoadPath roadPath = Preconditions.checkNotNull(roadPathMapper.selectByPrimaryKey(id),
                    String.format("不存在编号为 %s 的路径对象!", String.valueOf(id)));
            Long pathLockId = Preconditions.checkNotNull(roadPath.getPathLock(),
                    String.format("编号为 %s 路径还未绑定逻辑对象锁!", String.valueOf(id)));
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(pathLockId),
                    String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(pathLockId)));
            Preconditions.checkNotNull(direction, "方向信息不允许为空!");
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            Long passCount = roadPathLock.getPassCount();//允许通过的最大机器人数量
            Long currentPasscount = roadPathLock.getCurrentPasscount();//当前路径中排队的机器人数量
            String robotCodes = roadPathLock.getRobotCodes();//路径中所有排队的机器人编号信息
            Long  pathDirection = roadPathLock.getDirection();//路径锁绑定的方向信息，加锁的时候需要验证此方向
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++

            // 首先需要判断是不是 第一个机器人 进入执行加锁操作
            if (pathDirection == null) {
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
                //后续机器人依次进入，需要根据方向判断是否能够进行加锁
                if (!direction.equals(pathDirection)) {
                    //传入的方向与绑定的锁方向不一致，加锁失败
                    flag = false;
                } else {
                    //方向一致此时需要判断当前排队数量是否已经达到阈值，若达到阈值，则加锁失败
                    if (currentPasscount.equals(passCount)) {
                        flag = false;//加锁失败
                    }else {
                        //没有达到阈值，执行加锁操作
                        roadPathLock.setCurrentPasscount(currentPasscount + 1L);
                        roadPathLock.setCreateTime(new Date());
                        roadPathLock.setRobotCode(robotCode);//将传入的机器人设置为拥有锁对象的持有者
                        roadPathLock.setRobotCodes(robotCodes + "," + robotCode);//将此机器人信息加入到路径的冗余信息中
                        updateSelective(roadPathLock);
                        flag = true;
                    }
                }
            }
        }catch (Exception e){
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

    /**
     * 加入了锁方向，执行锁解锁操作
     * @param id
     * @param robotCode
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unlockInnerNewVersion(Long id, String robotCode) throws Exception {
        boolean flag = false;
        try {
            RoadPath roadPath = Preconditions.checkNotNull(roadPathMapper.selectByPrimaryKey(id),
                    String.format("不存在编号为 %s 的路径对象!", String.valueOf(id)));
            Long pathLockId = Preconditions.checkNotNull(roadPath.getPathLock(),
                    String.format("编号为 %s 路径还未绑定逻辑对象锁!", String.valueOf(id)));
            RoadPathLock roadPathLock = Preconditions.checkNotNull(roadPathLockMapper.selectByPrimaryKey(pathLockId),
                    String.format("不存在编号为 %s 的逻辑锁对象!", String.valueOf(pathLockId)));
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            Long passCount = roadPathLock.getPassCount();//允许通过的最大机器人数量
            Long currentPasscount = roadPathLock.getCurrentPasscount();//当前路径中排队的机器人数量
            String robotCodes = roadPathLock.getRobotCodes();//路径中所有排队的机器人编号信息
            Long  pathDirection = roadPathLock.getDirection();//路径锁绑定的方向信息，加锁的时候需要验证此方向
            // +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++ +++++
            List<String> containerRobots = Lists.newArrayList();
            if (robotCodes != null) {
                Arrays.stream(robotCodes.split(",")).forEach( e -> containerRobots.add(e));
            }
            //解锁需要先判断，对应的路径所绑定的机器人信息中是否包含有发出解锁指令的机器人
            if (containerRobots.indexOf(robotCode) == -1) {
                //发出指令的机器人当前不在路径排队对象中，解锁失败
                flag = false;
            }else {
                //执行解锁 （真实解锁 与 修改排队数量 - 两类操作）
                if (currentPasscount != 1) {
                    //表示当前排队中有多个机器人，这时修改数量
                    containerRobots.remove(robotCode);
                    StringBuilder builder = new StringBuilder();
                    containerRobots.stream().forEach( e -> builder.append(e).append(","));
                    String currentStr = builder.toString().substring(0, builder.toString().length() - 1);//最新的排队机器人信息
                    roadPathLock.setRobotCodes(currentStr);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setCurrentPasscount(currentPasscount - 1L);
                    updateSelective(roadPathLock);
                    flag = true;
                }else {
                    //真正的执行解锁操作
                    roadPathLock.setLockAction(RoadPathLock.LockAction.UNLOCK);
                    roadPathLock.setRobotCode(null);
                    roadPathLock.setCreateTime(new Date());
                    roadPathLock.setCurrentPasscount(null);
                    roadPathLock.setRobotCodes(null);
                    roadPathLock.setDirection(null);
                    updateSelective(roadPathLock);
                    flag = true;
                }
            }
        }catch (Exception e){
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
                whereRequest.getPageSize(), RoadPathLock.class,"ID DESC");
        return roadPathLockList;
    }
}