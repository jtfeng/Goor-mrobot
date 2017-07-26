package cn.muye.area.point.mapper;

import cn.mrobot.bean.area.point.MapPoint;
import cn.muye.util.MyMapper;
import org.apache.catalina.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/15
 * Time: 16:15
 * Describe:
 * Version:1.0
 */
public interface PointMapper extends MyMapper<MapPoint> {

    List<String> selectMapName(long storeId);

    List<String> selectSceneName(long storeId);

    List<String> selectMapNameBySceneName(@Param("sceneName") String sceneName,
                                          @Param("storeId") long storeId);

    List<Integer> selectPointTypeByMapName(@Param("sceneName") String sceneName,
                                           @Param("mapName") String mapName,
                                           @Param("storeId") long storeId);

    List<MapPoint> selectPointByPointTypeMapName(@Param("sceneName") String sceneName,
                                                 @Param("mapName") String mapName,
                                                 @Param("cloudMapPointTypeId") int cloudMapPointTypeId,
                                                 @Param("storeId") long storeId);

    void updateDeleteFlag(@Param("storeId") long storeId, @Param("deleteFlag")int deleteFlag);
}
