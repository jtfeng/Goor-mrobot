package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.muye.assets.robot.mapper.RobotMapper;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl implements RobotService {

    @Autowired
    private RobotMapper robotMapper;
    @Autowired
    private RobotPasswordService robotPasswordService;

    public List<Robot> listRobot() {
        Example example = new Example(Robot.class);
        example.setOrderByClause("ID DESC");
        List<Robot> list = robotMapper.selectByExample(example);
        for (Robot robot : list) {
            robot.setPasswords(robotPasswordService.listRobotPassword(robot.getId()));
        }
        return list;
    }

    public Robot getById(Long id) {
        return robotMapper.selectByPrimaryKey(id);
    }

    public void update(Robot robotDb) {
        robotMapper.updateByPrimaryKey(robotDb);
    }

    public void save(Robot robot) {
        robotMapper.insert(robot);
        robotPasswordService.saveRobotPassword(robot);
    }

    public void deleteById(Long id) {
        robotMapper.deleteByPrimaryKey(id);
    }

    public Robot getByName(String name) {
        Robot robot = new Robot();
        robot.setName(name);
        return robotMapper.selectOne(robot);
    }

    public Robot getByCode(String code) {
        Robot robot = new Robot();
        robot.setCode(code);
        return robotMapper.selectOne(robot);
    }
}
