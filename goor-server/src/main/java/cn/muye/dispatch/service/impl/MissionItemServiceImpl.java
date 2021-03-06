package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.mission.MissionItem;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionItemMapper;
import cn.muye.dispatch.service.MissionItemService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/9
 * Time: 11:36
 * Describe:
 * Version:1.0
 */
@Service
@Transactional
public class MissionItemServiceImpl implements MissionItemService {

	@Autowired
	protected MissionItemMapper missionItemMapper;

	@Override
	public long save(MissionItem missionItem) {
		return missionItemMapper.save(missionItem);
	}

	@Override
	public MissionItem findByName(String name,long storeId) {
		return missionItemMapper.findByName(name,storeId);
	}

	@Override
	public void update(MissionItem missionItem) {
		missionItemMapper.update(missionItem);
	}

	@Override
	public void delete(MissionItem missionItem) {
		missionItemMapper.delete(missionItem);
	}

	@Override
	public MissionItem get(long id,long storeId) {
		return missionItemMapper.get(id,storeId);
	}

	@Override
	public List<MissionItem> list(WhereRequest whereRequest,Long storeId) {
		List<MissionItem> missionChainList = new ArrayList<>();
		if (whereRequest != null && whereRequest.getQueryObj() != null && JSON.parseObject(whereRequest.getQueryObj()) != null ) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
//			Object missionChainId = map.get(SearchConstants.SEARCH_MISSION_CHAIN_ID);
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object sceneId = map.get(SearchConstants.SEARCH_SCENE_ID);
//			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionChainList = missionItemMapper.list(name, /*missionChainId,*/ beginDate, endDate/*, priority*/,storeId,sceneId);
		}else {
			missionChainList = missionItemMapper.listAll(storeId);
		}
		return missionChainList;
	}

	@Override
	public List<MissionItem> list(Long storeId) {
		return missionItemMapper.listAll(storeId);
	}

}

