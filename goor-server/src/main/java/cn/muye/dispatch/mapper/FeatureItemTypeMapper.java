package cn.muye.dispatch.mapper;


import cn.mrobot.bean.misssion.FeatureItemType;
import org.apache.ibatis.annotations.Param;

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
public interface FeatureItemTypeMapper {

	long save(FeatureItemType featureItemType);

	FeatureItemType get(long id);

	List<FeatureItemType> validate(@Param("name") String name,
								   @Param("value") String value);

	List<FeatureItemType> list(@Param("name") Object name,
							   @Param("featureItemId") Object featureItemId);
}

