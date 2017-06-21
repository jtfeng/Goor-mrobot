package cn.muye.area.point.service;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.slam.SlamResponseBody;
import cn.mrobot.utils.WhereRequest;

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
public interface PointService {

	long save(MapPoint mapPoint);

	void delete(MapPoint mapPoint);

	void update(MapPoint mapPoint);

	MapPoint findById(long id);

	List<MapPoint> findByName(String pointName, String sceneName);

	List<MapPoint> findBySceneName(String sceneName);

	List<MapPoint> list(WhereRequest whereRequest);

	void handle(SlamResponseBody slamResponseBody);
}
