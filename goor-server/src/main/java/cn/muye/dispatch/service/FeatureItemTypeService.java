package cn.muye.dispatch.service;

import cn.mrobot.bean.mission.FeatureItemType;
import cn.mrobot.utils.WhereRequest;

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
public interface FeatureItemTypeService{

	FeatureItemType get(long id);

	long save(FeatureItemType featureItemType);

	List<FeatureItemType> validate(String name, String value);

    List<FeatureItemType> list(WhereRequest whereRequest);
}

