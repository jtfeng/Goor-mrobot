package cn.muye.log.elevator.service;

import cn.mrobot.bean.log.elevator.LogElevator;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import cn.muye.log.elevator.mapper.LogElevatorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageRowBounds;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LogElevatorMapper logElevatorMapper;
    @Override
    public List<LogElevator> listPageByTimeDesc(int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        Example example = new Example(LogElevator.class);
        example.setOrderByClause("CREATE_TIME DESC");
        return myMapper.selectByExample(example);
    }

    @Override
    public List<LogElevator> listLogElevatorsByElevatorIp(WhereRequest whereRequest) {
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        Example example = new Example(LogElevator.class);
        String query = whereRequest.getQueryObj();
        if (query != null && !"".equals(query)){
            example.createCriteria().andCondition("ADDR like '%" + query + "%'");
        }
        example.setOrderByClause("CREATE_TIME DESC");
        return logElevatorMapper.selectByExampleAndRowBounds(example,
                new RowBounds((whereRequest.getPage()-1)*whereRequest.getPageSize(),whereRequest.getPageSize()));
    }
}
