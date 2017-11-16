package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.service.missiontask.MissionFuncsServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by abel on 17-7-14.
 */
@Service
@Transactional
public class MissionItemTaskServiceImpl
        extends BaseServiceImpl<MissionItemTask>
        implements MissionItemTaskService {
    @Override
    public List<MissionItemTask> findByListIdAndMissionId(Long listId, Long missionId) {
        if (listId == null ||
                missionId == null){
            return null;
        }
        Example example = new Example(MissionItemTask.class);
        example.createCriteria()
                .andCondition("MISSION_LIST_ID=", listId)
                .andCondition("MISSION_ID=", missionId);
        example.setOrderByClause("ID ASC");
        return myMapper.selectByExample(example);
    }

    @Override
    public List<MissionItemTask> findByListId(Long listId) {
        if (listId == null){
            return null;
        }
        Example example = new Example(MissionItemTask.class);
        example.createCriteria()
                .andCondition("MISSION_LIST_ID=", listId);
        example.setOrderByClause("ID ASC");
        return myMapper.selectByExample(example);
    }

    @Override
    public List<MissionItemTask> findByListIdAndItemName(Long listId) {
        if (listId == null){
            return null;
        }
        Example example = new Example(MissionItemTask.class);
        List<String> itemNames = Lists.newArrayList();
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_unload);
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_load);
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_loadNoShelf);
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_finalUnload);
        example.createCriteria()
                .andCondition("MISSION_LIST_ID=", listId)
                .andIn("name",itemNames);
        example.setOrderByClause("ID ASC");
        return myMapper.selectByExample(example);
    }

    @Override
    public List<MissionItemTask> findByListIdAndItemNameEqualToUnlock(Long listId) {
        if (listId == null){
            return null;
        }
        Example example = new Example(MissionItemTask.class);
        List<String> itemNames = Lists.newArrayList();
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_elevator_unlock);
        itemNames.add(MissionFuncsServiceImpl.MissionItemName_roadpath_unlock);
        example.createCriteria()
                .andCondition("MISSION_LIST_ID=", listId)
                .andIn("name",itemNames);
        example.setOrderByClause("ID ASC");
        return myMapper.selectByExample(example);
    }
}
