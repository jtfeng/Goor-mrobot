package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.misssion.FeatureItemType;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.FeatureItemTypeMapper;
import cn.muye.dispatch.service.FeatureItemTypeService;
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
public class FeatureItemTypeServiceImpl implements FeatureItemTypeService {

	@Autowired
	protected FeatureItemTypeMapper featureItemTypeMapper;

	@Override
	public FeatureItemType get(long id) {
		return featureItemTypeMapper.get(id);
	}

	@Override
	public long save(FeatureItemType featureItemType) {
		return featureItemTypeMapper.save(featureItemType);
	}

	@Override
	public List<FeatureItemType> validate(String name, String value) {
		return featureItemTypeMapper.validate(name, value);
	}

	@Override
	public List<FeatureItemType> list(WhereRequest whereRequest) {
		List<FeatureItemType> featureItemTypeList = new ArrayList<>();
		if (whereRequest.getQueryObj() != null) {
			JSONObject jsonObject = JSON.parseObject(whereRequest.getQueryObj());
			Object name = jsonObject.get(SearchConstants.SEARCH_NAME);
			Object featureItemId = jsonObject.get(SearchConstants.SEARCH_FEATURE_ITEM_ID);
			featureItemTypeList = featureItemTypeMapper.list(name, featureItemId);
		} else {
			featureItemTypeList = featureItemTypeMapper.list(null, null);
		}

		return featureItemTypeList;
	}

}

