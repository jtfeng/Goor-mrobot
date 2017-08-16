package cn.muye.area.map.mapper;

import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jelynn on 2017/8/15.
 */
public interface RobotMapZipXREFMapper extends MyMapper<RobotMapZipXREF> {

    void updateByRobotId(RobotMapZipXREF robotMapZipXREF);

    List<RobotMapZipXREF> findByRobotId(Long robotId);

}
