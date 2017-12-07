package cn.muye.area.map.mapper;

import cn.mrobot.bean.area.map.MapInfo;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/7/5
 * Time: 15:06
 * Describe:
 * Version:1.0
 */
@Component
public interface MapInfoMapper extends MyMapper<MapInfo> {

    void updateDeleteFlag(@Param("storeId") long storeId, @Param("mapZipId") long mapZipId, @Param("deleteFlag") int deleteFlag);

    List<String> selectSceneName(long storeId);

    List<String> selectMapNameBySceneName(@Param("sceneName") String sceneName,
                                          @Param("storeId") long storeId);
}
