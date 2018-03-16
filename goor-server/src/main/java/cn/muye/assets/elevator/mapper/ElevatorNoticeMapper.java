package cn.muye.assets.elevator.mapper;

import cn.mrobot.bean.assets.elevator.ElevatorNotice;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Jelynn
 * @date 2018/1/8
 */
public interface ElevatorNoticeMapper extends MyMapper<ElevatorNotice> {

    void updateState(@Param("id") Long id, @Param("state") int state);
}
