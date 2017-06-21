package cn.muye.assets.robot.service;

import cn.mrobot.bean.robot.Robot;
import cn.muye.assets.robot.mapper.RobotMapper;
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
public class RobotService {

    @Autowired
    private RobotMapper robotMapper;

    public List<Robot> listRobot() {
        Example example = new Example(Robot.class);
        example.setOrderByClause("ID DESC");
        List<Robot> list = robotMapper.selectByExample(example);
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
