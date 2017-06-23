package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.robot.RobotPassword;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.assets.robot.service.RobotService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ray.Fu on 2017/6/12.
 */
@Service
@Transactional
public class RobotServiceImpl extends BaseServiceImpl<Robot> implements RobotService {

    @Autowired
    private RobotPasswordService robotPasswordService;

    public List<Robot> listRobot(WhereRequest whereRequest) {
        List<Robot> list = super.listPageByStoreIdAndOrder(whereRequest.getPage(),whereRequest.getPageSize(),Robot.class,"ID DESC");
        for (Robot robot : list) {
            robot.setPasswords(robotPasswordService.listRobotPassword(robot.getId()));
        }
        return list;
    }

    public Robot getById(Long id) {
        return myMapper.selectByPrimaryKey(id);
    }


    public void saveRobot(Robot robot) {
        super.save(robot);
        robotPasswordService.saveRobotPassword(robot);
    }

    public void deleteRobotById(Long id) {
        myMapper.deleteByPrimaryKey(id);
        robotPasswordService.delete(new RobotPassword(null,id));
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
