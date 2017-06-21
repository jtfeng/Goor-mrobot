package cn.muye.log.mapper;

import cn.mrobot.bean.log.LogInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 10:36
 * Describe:
 * Version:1.0
 */
public interface LogInfoMapper {

	long save(LogInfo logInfo);

	List<LogInfo> lists(@Param("deviceId") Object deviceId,
						@Param("logLevelName") Object logLevelName,
						@Param("logTypeName") Object logTypeName,
						@Param("beginDate") Object beginDate,
						@Param("endDate") Object endDate);

}
