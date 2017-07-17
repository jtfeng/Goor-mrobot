package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionTask;
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
public class MissionTaskServiceImpl
        extends BaseServiceImpl<MissionTask>
        implements MissionTaskService {
    @Override
    public List<MissionTask> findByListId(Long listId) {
        if (listId == null){
            return null;
        }
        Example example = new Example(MissionTask.class);
        example.createCriteria()
                .andCondition("MISSION_LIST_ID=", listId);
        example.setOrderByClause("ID ASC");
        return myMapper.selectByExample(example);
    }
}
