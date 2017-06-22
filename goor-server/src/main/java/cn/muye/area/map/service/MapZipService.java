package cn.muye.area.map.service;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.utils.WhereRequest;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/22
 * Time: 9:28
 * Describe:
 * Version:1.0
 */
public interface MapZipService {

	MapZip getMapZip(long id);

	long save(MapZip mapZip);

	void delete(MapZip mapZip);

	List<MapZip> list(WhereRequest whereRequest);
}
