package cn.muye.area.map.service.impl;

import cn.mrobot.bean.area.map.MapZip;
import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.map.mapper.MapZipMapper;
import cn.muye.area.map.service.MapZipService;
import cn.muye.base.bean.SearchConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.io.File;
import java.util.Date;
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
@Service
public class MapZipServiceImpl implements MapZipService {

	@Autowired
	private MapZipMapper mapZipMapper;

	@Override
	public void update(MapZip mapZip) {
		mapZipMapper.updateByPrimaryKey(mapZip);
	}

	@Override
	public MapZip getMapZip(long id) {
		return mapZipMapper.selectByPrimaryKey(id);
	}

	@Override
	public long save(MapZip mapZip) {
		mapZip.setCreateTime(new Date());
		return mapZipMapper.insert(mapZip);
	}

	@Override
	public void delete(MapZip mapZip) {
		//删除文件
		String filePath = mapZip.getFilePath();
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		mapZipMapper.delete(mapZip);
	}

	@Override
	public List<MapZip> list(MapZip mapZip) {
		return mapZipMapper.select(mapZip);
	}

	@Override
	public List<MapZip> list(WhereRequest whereRequest, long storeId) {
		Condition condition = new Condition(MapPoint.class);
		if (whereRequest.getQueryObj() != null) {
			JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
			Object mapName = jsonObject.get(SearchConstants.SEARCH_MAP_NAME);
			if (mapName != null) {
				condition.createCriteria().andCondition("MAP_NAME like '%" + mapName + "%'");
			}
		}
		condition.createCriteria().andCondition("STORE_ID =" + storeId);
		condition.setOrderByClause("CREATE_TIME desc");
		List<MapZip> mapZipList = mapZipMapper.selectByExample(condition);
		return mapZipList;
	}
}
