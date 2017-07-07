package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotConfig;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotConfigService;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl extends BaseServiceImpl<Robot> implements RobotService {

    @Autowired
    private RobotPasswordService robotPasswordService;

    @Autowired
    private RobotConfigService robotConfigService;

    public void updateRobot(Robot robot) {
        //更新机器人信息
        updateByStoreId(robot);
        //更新机器人配置信息
        RobotConfig robotConfig = robotConfigService.getByRobotId(robot.getId());
        if (robotConfig != null) {
            robotConfig.setBatteryThreshold(robot.getBatteryThreshold());
        }
        robotConfigService.update(robotConfig);
    }

    private List<Robot> listPageByStoreIdAndOrder(int page, int pageSize, String name, Integer type, Class<Robot> clazz, String order) {
        PageHelper.startPage(page, pageSize);
        Example example = new Example(clazz);
        Example.Criteria criteria = example.createCriteria();
        criteria = criteria.andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if (name != null) {
            criteria.andCondition("NAME like", "%" + name + "%");
        }
        if (type != null) {
            criteria.andCondition("TYPE_ID =", type);
        }
        if (!StringUtil.isNullOrEmpty(order)) {
            example.setOrderByClause(order);
        }
        return myMapper.selectByExample(example);
    }

    public List<Robot> listRobot(WhereRequest whereRequest) {
        List<Robot> list = null;
        if (whereRequest != null && !StringUtil.isNullOrEmpty(whereRequest.getQueryObj())) {
            JSONObject jsonObject = JSONObject.parseObject(whereRequest.getQueryObj());
            String name = (String) jsonObject.get(SearchConstants.SEARCH_NAME);
            int type = Integer.valueOf((String) jsonObject.get(SearchConstants.SEARCH_TYPE));
            list = listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), name, type, Robot.class, "ID DESC");
        } else {
            list = super.listPageByStoreIdAndOrder(whereRequest.getPage(), whereRequest.getPageSize(), Robot.class, "ID DESC");
        }
        list.forEach(robot -> {
            robot.setBatteryThreshold(robotConfigService.getByRobotId(robot.getId()).getBatteryThreshold());
            robot.setPasswords(robotPasswordService.listRobotPassword(robot.getId()));
        });
        return list;
    }

    public Robot getById(Long id) {
        return myMapper.selectByPrimaryKey(id);
    }


    public void saveRobot(Robot robot) {
        super.save(robot);
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setBatteryThreshold(robot.getBatteryThreshold());
        robotConfig.setRobotId(robot.getId());
        robotConfig.setStoreId(SearchConstants.FAKE_MERCHANT_STORE_ID);
        robotConfig.setCreateTime(new Date());
        robotConfig.setCreatedBy(1L);
        robotConfigService.add(robotConfig);
        robotPasswordService.saveRobotPassword(robot);
    }

    public void deleteRobotById(Long id) {
        myMapper.deleteByPrimaryKey(id);
        robotPasswordService.delete(new RobotPassword(null, id));
    }

    public Robot getByName(String name) {
        Robot robot = new Robot();
        robot.setName(name);
        return myMapper.selectOne(robot);
    }

    public Robot getByCode(String code) {
        Robot robot = new Robot();
        robot.setCode(code);
        return myMapper.selectOne(robot);
    }
}
