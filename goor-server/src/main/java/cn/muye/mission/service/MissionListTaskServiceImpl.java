package cn.muye.mission.service;

import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MissionTaskService missionTaskService;

    @Autowired
    MissionItemTaskService missionItemTaskService;

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

    @Override
    public MissionListTask findLastByRobotCode(String robotCode) {
        if (StringUtil.isEmpty(robotCode)){
            return null;
        }
        Example example = new Example(MissionListTask.class);
        example.createCriteria()
                .andCondition("ROBOT_CODE=", robotCode);
        example.setOrderByClause("CREATE_TIME DESC");
        List<MissionListTask> tasks = myMapper.selectByExample(example);
        if (tasks != null && !tasks.isEmpty()){
            return tasks.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<MissionListTask> listPageByStoreIdAndOrder(
            int page,
            int pageSize,
            Long sceneId,
            String state,
            String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(MissionListTask.class);
        Example.Criteria criteria = example.createCriteria()
                .andCondition("SCENE_ID =", sceneId)
                .andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (!StringUtil.isNullOrEmpty(state)){
            criteria.andCondition("STATE =", state);
        }
        if(!StringUtil.isNullOrEmpty(order)){
            example.setOrderByClause(order);
        }
        return myMapper.selectByExample(example);
    }

    @Override
    public List<MissionListTask> tasksList(WhereRequest whereRequest) {
        List<MissionListTask> list = null;
        if (whereRequest == null ||
                StringUtil.isNullOrEmpty(whereRequest.getQueryObj())){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
        Long sceneId = jsonObject.getLong("sceneId");
        String state = jsonObject.getString("state");
        list = listPageByStoreIdAndOrder(
                whereRequest.getPage(),
                whereRequest.getPageSize(),
                sceneId, state, "CREATE_TIME DESC");

        //填充子数据
        if (list != null){
            list.forEach(missionListTask -> {

            });
        }
        return list;
    }
}
