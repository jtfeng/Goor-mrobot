package cn.muye.mission.service.impl;

import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.mission.MissionWarning;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.bean.order.OrderDetail;
import cn.mrobot.utils.DateTimeUtils;
import cn.muye.area.station.service.StationService;
import cn.muye.mission.mapper.MissionWarningMapper;
import cn.muye.mission.service.MissionItemTaskService;
import cn.muye.mission.service.MissionListTaskService;
import cn.muye.mission.service.MissionTaskService;
import cn.muye.mission.service.MissionWarningService;
import cn.muye.order.service.OrderDetailService;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Selim on 2017/10/23.
 */
@Service
@Transactional
public class MissionWarningServiceImpl implements MissionWarningService {

    @Autowired
    private MissionWarningMapper missionWarningMapper;
    @Autowired
    private MissionListTaskService missionListTaskService;
    @Autowired
    private MissionTaskService missionTaskService;
    @Autowired
    private MissionItemTaskService missionItemTaskService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private StationService stationService;

    @Override
    public boolean hasExistWarning(MissionWarning missionWarning) {
        Example example = new Example(MissionWarning.class);
        Example.Criteria criteria = example.createCriteria();
        if(missionWarning.getId()!= null){
            criteria.andNotEqualTo("id",missionWarning.getId());
        }
        criteria.andEqualTo("startStationId",missionWarning.getStartStationId())
                .andEqualTo("endStationId", missionWarning.getEndStationId());
        List<MissionWarning> warningList = missionWarningMapper.selectByExample(example);
        if(warningList.size() >= 1) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void save(MissionWarning missionWarning) {
        missionWarning.setCreatedTime(new Date());
        missionWarning.setUpdatedTime(missionWarning.getCreatedTime());
        missionWarningMapper.insert(missionWarning);
    }

    @Override
    public void update(MissionWarning missionWarning) {
        missionWarning.setUpdatedTime(new Date());
        missionWarningMapper.updateByPrimaryKeySelective(missionWarning);
    }

    @Override
    public List<MissionWarning> pageListMissionWarnings(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(MissionWarning.class);
        example.setOrderByClause("UPDATED_TIME DESC");
        List<MissionWarning> missionWarnings = missionWarningMapper.selectByExample(example);
        missionWarnings.forEach(missionWarning -> {
            missionWarning.setStartStation(stationService.findById(missionWarning.getStartStationId()));
            missionWarning.setEndStation(stationService.findById(missionWarning.getEndStationId()));
        });
        return missionWarnings;
    }

    @Override
    public void dailyUpdateWarningData() {
        List<MissionListTask> missionListTaskList = missionListTaskService.findTodayList();
        for (MissionListTask missionListTask : missionListTaskList) {
            List<MissionItemTask> missionItemTaskList = missionItemTaskService.findByListId(missionListTask.getId());
            Date lastFinishDate = null;
            Long lastStationId = null;
            for (MissionItemTask itemTask : missionItemTaskList) {
                //状态为分别为 装货 中途卸货 终点卸货
                if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_load)
                        ||itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_loadNoShelf)){
                    lastFinishDate = itemTask.getFinishDate();
                    lastStationId = getStationIdByMissionId(itemTask.getMissionId());
                }else if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_unload)){
                    Date finishDate = itemTask.getFinishDate();
                    Long stationId = getStationIdByMissionId(itemTask.getMissionId());
                    if(lastFinishDate != null && lastStationId != null && finishDate != null && stationId != null ){
                        //判定添加至warningList
                        addMissionWarningData(lastStationId, stationId,lastFinishDate, finishDate);
                    }
                    //修改为上次记录
                    lastFinishDate = finishDate;
                    lastStationId = stationId;
                }else if(itemTask.getName().equals(MissionFuncsServiceImpl.MissionItemName_finalUnload)){
                    Date finishDate = itemTask.getFinishDate();
                    Long stationId = getStationIdByMissionId(itemTask.getMissionId());
                    if(lastFinishDate != null && lastStationId != null && finishDate != null && stationId != null ){
                        //判定添加至warningList
                        addMissionWarningData(lastStationId, stationId,lastFinishDate, finishDate);
                    }
                }
            }
        }
    }

    @Override
    public Long getWarningTime(Long startStationId, Long endStationId) {
        MissionWarning missionWarning = new MissionWarning();
        missionWarning.setStartStationId(startStationId);
        missionWarning.setEndStationId(endStationId);
        MissionWarning sqlMissionWarning = missionWarningMapper.selectOne(missionWarning);
        if(sqlMissionWarning != null){
            if(sqlMissionWarning.getUseArtificalTime()){
                return sqlMissionWarning.getArtificalTime();
            }else {
                if(sqlMissionWarning.getActionTime()!=null && sqlMissionWarning.getActionTime()>= Constant.USE_CALCULATE_ACTION_TIME){
                    return sqlMissionWarning.getCalculateTime();
                };
            }
        }
        return null;
    }

    //通过missionId 获取对应站id
    public Long getStationIdByMissionId(Long missionId){
        MissionTask missionTask = missionTaskService.findById(missionId);
        if(missionTask == null){
            return null;
        }else {
            OrderDetail orderDetail = orderDetailService.findById(Long.parseLong(missionTask.getOrderDetailMission()));
            return orderDetail == null ? null: orderDetail.getStationId();
        }
    }

    //添加记录
    public void addMissionWarningData(Long startStationId, Long endStationId, Date lastFinishDate, Date finishDate){
        MissionWarning missionWarning = new MissionWarning();
        missionWarning.setStartStationId(startStationId);
        missionWarning.setEndStationId(endStationId);
        long timeGap = DateTimeUtils.getTimeGapSecond(finishDate, lastFinishDate);
        MissionWarning sqlMissionWarning = missionWarningMapper.selectOne(missionWarning);
        if(sqlMissionWarning == null){
            //新增
            missionWarning.setActionTime(1);
            missionWarning.setCreatedTime(new Date());
            missionWarning.setUpdatedTime(missionWarning.getCreatedTime());
            missionWarning.setCalculateTime(timeGap);
            missionWarningMapper.insert(missionWarning);
        }else{
            //修改
            sqlMissionWarning.setUpdatedTime(new Date());
            int actionTime = sqlMissionWarning.getActionTime();
            long calculateTime = sqlMissionWarning.getCalculateTime();
            long newCalculateTime = (calculateTime * actionTime + timeGap)/(actionTime + 1);
            sqlMissionWarning.setActionTime(actionTime + 1);
            sqlMissionWarning.setCalculateTime(newCalculateTime);
            missionWarningMapper.updateByPrimaryKey(sqlMissionWarning);
        }
    }


}
