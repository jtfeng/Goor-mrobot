package cn.muye.area.map.mapper;

import cn.mrobot.bean.area.map.SceneMapZipXREF;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by Jelynn on 2017/8/2.
 */
public interface SceneMapZipXREFMapper extends MyMapper<SceneMapZipXREF> {

    void updateDeleteFlag(@Param("mapZipId") Long mapZipId, @Param("deleteFlag") int deleteFlag);
}
