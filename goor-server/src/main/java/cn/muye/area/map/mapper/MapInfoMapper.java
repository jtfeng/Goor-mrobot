package cn.muye.area.map.mapper;

import cn.mrobot.bean.area.map.MapInfo;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/7/5
 * Time: 15:06
 * Describe:
 * Version:1.0
 */
public interface MapInfoMapper extends MyMapper<MapInfo> {

    void updateDeleteFlag(@Param("storeId") long storeId, @Param("mapZipId") long mapZipId, @Param("deleteFlag") int deleteFlag);
}
