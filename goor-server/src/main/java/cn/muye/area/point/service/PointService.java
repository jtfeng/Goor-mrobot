package cn.muye.area.point.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.area.point.cascade.CascadeMapPoint;
import cn.mrobot.bean.area.point.cascade.CascadePoint;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.WhereRequest;
import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/15
 * Time: 16:15
 * Describe:
 * Version:1.0
 */
public interface PointService {

	long save(MapPoint mapPoint);

	int save(List<MapPoint> mapPointList);

	void delete(MapPoint mapPoint);

	void delete(String sceneName, String mapName, long storeId);

	void update(MapPoint mapPoint);

	MapPoint findById(long id);

	List<MapPoint> findByName(String pointName, String sceneName,String mapName,long storeId );

	@Deprecated
	List<MapPoint> findBySceneName(String sceneName);

	List<MapPoint> list(WhereRequest whereRequest, long storeId);

	@Deprecated
	void handle(SlamResponseBody slamResponseBody);

	List<CascadePoint> cascadeMapPoint(int level, String sceneName);

	void delete(long storeId, int deleteFlag);

	void updateDeleteFlag(long storeId, int deleteFlag);
}
