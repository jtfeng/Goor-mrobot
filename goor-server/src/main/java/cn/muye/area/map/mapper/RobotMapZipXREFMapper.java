package cn.muye.area.map.mapper;

import cn.mrobot.bean.area.map.RobotMapZipXREF;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Created by Jelynn on 2017/8/15.
 */
public interface RobotMapZipXREFMapper extends MyMapper<RobotMapZipXREF> {

    void removeBySceneId(@Param("sceneId") Long sceneId,
                         @Param("deleteFlag") int deleteFlag,
                         @Param("deleteTime") Date deleteTime);
}
