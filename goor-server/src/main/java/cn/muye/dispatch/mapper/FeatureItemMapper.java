package cn.muye.dispatch.mapper;


import cn.mrobot.bean.mission.FeatureItem;
import cn.muye.util.MyMapper;
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
public interface FeatureItemMapper extends MyMapper<FeatureItem>{

	long save(FeatureItem featureItem);

	FeatureItem get(long id);

	List<FeatureItem> validate(@Param("name") String name, @Param("value") String value);

	List<FeatureItem> findByValue(@Param("value") String value);

	List<FeatureItem> list(@Param("name") Object name);
}

