package cn.muye.log.elevator.service;

import cn.mrobot.bean.log.elevator.LogElevator;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by abel on 17-7-15.
 */
@Service
@Transactional
public class LogElevatorServiceImpl
        extends BaseServiceImpl<LogElevator>
        implements LogElevatorService {
    @Override
    public List<LogElevator> listPageByTimeDesc(int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        Example example = new Example(LogElevator.class);
        example.setOrderByClause("CREATE_TIME DESC");
        return myMapper.selectByExample(example);
    }
}
