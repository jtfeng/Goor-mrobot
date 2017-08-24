package cn.muye.dispatch.service.impl;

import cn.mrobot.bean.mission.FeatureItem;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.dispatch.mapper.FeatureItemMapper;
import cn.muye.dispatch.service.FeatureItemService;
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
@Transactional
@Service
public class FeatureItemServiceImpl implements FeatureItemService {

    @Autowired
    protected FeatureItemMapper featureItemMapper;

    @Override
    public long save(FeatureItem featureItem) {
        return featureItemMapper.save(featureItem);
    }

    @Override
    public FeatureItem get(long id) {
        return featureItemMapper.get(id);
    }

    @Override
    public List<FeatureItem> validate(String name, String value) {
        return featureItemMapper.validate(name, value);
    }

    @Override
    public FeatureItem findByValue(String value) {
        List<FeatureItem> featureItemList = featureItemMapper.findByValue(value);
        return (featureItemList == null || featureItemList.size() <= 0) ? null : featureItemList.get(0);
    }

    @Override
    public List<FeatureItem> list(WhereRequest whereRequest) {
        List<FeatureItem> featureItemList = new ArrayList<>();
        if (whereRequest != null && whereRequest.getQueryObj() != null && JSON.parseObject(whereRequest.getQueryObj()) != null) {
            JSONObject map = JSON.parseObject(whereRequest.getQueryObj());
            Object name = map.get(SearchConstants.SEARCH_NAME);
            featureItemList = featureItemMapper.list(name);
        } else {
            featureItemList = featureItemMapper.list(null);
        }

        return featureItemList;
    }
}

