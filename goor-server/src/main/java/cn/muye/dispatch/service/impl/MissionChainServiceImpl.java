package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.MissionChain;
import cn.mrobot.bean.misssion.MissionChainNodeXREF;
import cn.mrobot.bean.misssion.MissionNode;
import cn.mrobot.utils.WhereRequest;
import cn.muye.bean.SearchConstants;
import cn.muye.dispatch.mapper.MissionChainMapper;
import cn.muye.dispatch.mapper.MissionChainNodeXREFMapper;
import cn.muye.dispatch.mapper.MissionNodeMapper;
import cn.muye.dispatch.service.MissionChainService;
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
public class MissionChainServiceImpl implements MissionChainService {

	@Autowired
	protected MissionChainMapper missionChainMapper;

	@Autowired
	protected MissionChainNodeXREFMapper missionChainNodeXREFMapper;

	@Autowired
	protected MissionNodeMapper missionNodeMapper;

	@Override
	public long save(MissionChain missionChain) {
		return missionChainMapper.save(missionChain);
	}

	@Override
	public void update(MissionChain missionChain) {
		missionChainMapper.update(missionChain);
	}

	@Override
	public void update(MissionChain missionChain, List<Long> nodeIdList) {
		Long missionChainId = missionChain.getId();
		MissionChainNodeXREF missionChainNodeXREF = new MissionChainNodeXREF();
		missionChainNodeXREF.setMissionChainId(missionChainId);
		//添加关联关系
		List<MissionChainNodeXREF> missionChainNodeXREFList = missionChainNodeXREFMapper.findByChainId(missionChainId);
		for (int i = 0; i < nodeIdList.size(); i++) {
			missionChainNodeXREF.setMissionNodeId(nodeIdList.get(i));
			if (! missionChainNodeXREFList.contains(missionChainNodeXREF)) {
				missionChainNodeXREFMapper.save(missionChainNodeXREF);
			}
		}
		missionChainMapper.update(missionChain);
	}

	@Override
	public MissionChain get(long id) {
		return missionChainMapper.get(id);
	}

	@Override
	public void delete(MissionChain missionChain) {
		missionChainNodeXREFMapper.deleteByChainId(missionChain.getId());//删除关联关系
		missionChainMapper.delete(missionChain.getId());
	}

	@Override
	public MissionChain findByName(String name) {
		return missionChainMapper.findByName(name);
	}

	@Override
	public List<MissionChain> list(WhereRequest whereRequest) {
		List<MissionChain> missionChainList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
			Object missionMainId = map.get(SearchConstants.SEARCH_MISSION_MAIN_ID);
			Object name = map.get(SearchConstants.SEARCH_NAME);
			Object beginDate = map.get(SearchConstants.SEARCH_BEGIN_DATE);
			Object endDate = map.get(SearchConstants.SEARCH_END_DATE);
			Object priority = map.get(SearchConstants.SEARCH_PRIORITY);

			missionChainList = missionChainMapper.list(missionMainId, name, beginDate, endDate, priority);
		}else {
			missionChainList = missionChainMapper.listAll();
		}
		return bindMissionNodeList(missionChainList);

	}

	@Override
	public List<MissionChain> list() {
		List<MissionChain> missionChainList = missionChainMapper.listAll();
		return bindMissionNodeList(missionChainList);
	}

	private List<MissionChain> bindMissionNodeList(List<MissionChain> missionChainList) {
		MissionChain missionChain;
		List<MissionChain> result = new ArrayList<>();
		for (int i = 0; i < missionChainList.size(); i++) {
			List<MissionNode> missionNodeList = new ArrayList<>();
			missionChain = missionChainList.get(i);
			List<MissionChainNodeXREF> missionChainNodeXREFList = missionChainNodeXREFMapper.findByChainId(missionChain.getId());
			for (int j = 0; j < missionChainNodeXREFList.size(); j++) {
				missionNodeList.add(missionChainNodeXREFList.get(j).getMissionNode());
			}
			missionChain.setMissionNodeList(missionNodeList);
			result.add(missionChain);
		}
		return result;
	}

}

