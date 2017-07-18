package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.utils.StringUtil;
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
public class MissionListTaskServiceImpl
        extends BaseServiceImpl<MissionListTask>
        implements MissionListTaskService {
    @Override
    public List<MissionListTask> findByRobotCodeAndState(
            String robotCode,
            String state) {
        if (StringUtil.isEmpty(robotCode) ||
                StringUtil.isEmpty(state)){
            return null;
        }
        Example example = new Example(MissionListTask.class);
        example.createCriteria()
                .andCondition("ROBOT_CODE=", robotCode)
                .andCondition("STATE=", state);
        return myMapper.selectByExample(example);
    }
}
