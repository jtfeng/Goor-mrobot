package cn.muye.dispatch.service;


import cn.mrobot.bean.misssion.FeatureItem;
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
public interface FeatureItemService {

	long save(FeatureItem featureItem);

	FeatureItem get(long id);

	List<FeatureItem> validate(String name, String value);

	List<FeatureItem> list(WhereRequest whereRequest);
}

