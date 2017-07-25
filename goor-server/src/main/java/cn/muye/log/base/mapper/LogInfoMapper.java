package cn.muye.log.base.mapper;

import cn.mrobot.bean.log.LogInfo;
import cn.muye.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-server
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 10:36
 * Describe:
 * Version:1.0
 */
@Component
public interface LogInfoMapper extends MyMapper<LogInfo>{

}
