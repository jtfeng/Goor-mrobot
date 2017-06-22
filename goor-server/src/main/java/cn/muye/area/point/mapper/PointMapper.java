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
public interface PointMapper extends MyMapper<MapPoint>{

	List<String> selectMapName();

	List<Integer> selectPointTypeByMapName(String mapName);

	List<MapPoint> selectPointByPointTypeMapName(@Param("mapName")String mapName,
												 @Param("mapPointTypeId") int mapPointTypeId);
}
