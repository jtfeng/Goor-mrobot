package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.muye.base.service.imp.BaseServiceImpl;
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
}
