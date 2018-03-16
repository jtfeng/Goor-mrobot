package cn.muye.mission.mapper;

import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by abel on 17-7-7.
 */
public interface MissionItemTaskMapper extends MyMapper<MissionItemTask> {
    List<MissionItemTask> listByOrderDetailId(@Param("orderDetailId")Long orderDetailId);
}
