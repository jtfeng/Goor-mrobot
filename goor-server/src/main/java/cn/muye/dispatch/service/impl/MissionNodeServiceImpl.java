package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.MissionNode;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionNodeMapper;
import cn.muye.dispatch.service.MissionNodeService;
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
public class MissionNodeServiceImpl implements MissionNodeService {

	@Autowired
	protected MissionNodeMapper missionNodeMapper;

	@Override
	public long save(MissionNode missionNode) {
		return missionNodeMapper.save(missionNode);
	}

	@Override
	public MissionNode findByName(String name) {
		return missionNodeMapper.findByName(name);
	}

	@Override
	public void update(MissionNode missionNode) {
		missionNodeMapper.update(missionNode);
	}

	@Override
	public void delete(MissionNode missionNode) {
		missionNodeMapper.delete(missionNode);
	}

	@Override
	public MissionNode get(long id) {
		return missionNodeMapper.get(id);
	}

	@Override
	public List<MissionNode> list(WhereRequest whereRequest) {

		List<MissionNode> missionChainList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object missionChainId = map.get(SearchConstants.SEARCH_MISSION_CHAIN_ID);
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionChainList = missionNodeMapper.list(name, missionChainId, beginDate, endDate, priority);
		}else {
			missionChainList = missionNodeMapper.listAll();
		}
		return missionChainList;
	}

	@Override
	public List<MissionNode> list() {
		return missionNodeMapper.listAll();
	}

}

