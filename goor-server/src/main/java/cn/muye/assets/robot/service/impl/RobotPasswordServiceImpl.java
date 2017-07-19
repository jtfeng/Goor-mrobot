package cn.muye.assets.robot.service.impl;

import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.robot.RobotType;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.utils.AutoNumUtil;
import cn.mrobot.utils.StringUtil;
import cn.muye.assets.robot.mapper.RobotTypeMapper;
import cn.muye.assets.robot.service.RobotPasswordService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Selim on 2017/6/21.
 */
@Service
@Transactional
public class RobotPasswordServiceImpl extends BaseServiceImpl<RobotPassword> implements RobotPasswordService {

    @Autowired
    private RobotTypeMapper robotTypeMapper;

    @Override
    public void saveRobotPassword(Robot robot) {
        RobotType robotType = robotTypeMapper.selectByPrimaryKey(robot.getTypeId());
        Integer boxCount = robotType.getBoxCount();
        List<RobotPassword> addList = Lists.newArrayList();
        for (int i = 1; i <= boxCount ; i++){
            RobotPassword robotPassWord = new RobotPassword();
            robotPassWord.setCreateTime(robot.getCreateTime());
            robotPassWord.setCreatedBy(robot.getCreatedBy());
            robotPassWord.setStoreId(robot.getStoreId());
            robotPassWord.setBoxNum(i);
            robotPassWord.setRobotId(robot.getId());
            robotPassWord.setPassword(AutoNumUtil.createRandomVcode());
            addList.add(robotPassWord);
        }
        myMapper.insertList(addList);

    }

    @Override
    public List<RobotPassword> listRobotPassword(Long robotId) {
        Example example = new Example(RobotPassword.class);
        example.createCriteria().andCondition("ROBOT_ID =" + robotId);
        example.setOrderByClause("BOX_NUM ASC");
        return myMapper.selectByExample(example);
    }

    @Override
    public void batchUpdateRobotPwdList(List<RobotPassword> robotPasswordList) {
        for (RobotPassword robotPassword : robotPasswordList) {
            super.updateSelectiveByStoreId(robotPassword);
        }
    }

    @Override
    public RobotPassword findByRobotIdAndBoxNumAndPwd(RobotPassword robotPassword) {
        if (robotPassword == null ||
                robotPassword.getBoxNum() == null ||
                robotPassword.getRobotId() == null ||
                StringUtil.isEmpty(robotPassword.getPassword())){
            return null;
        }
        return myMapper.selectOne(robotPassword);
    }

    @Override
    public String getPwdByRobotIdAndBoxNum(Long robotId, Integer boxNum) {
        RobotPassword robotPassword = new RobotPassword();
        robotPassword.setRobotId(robotId);
        robotPassword.setBoxNum(boxNum);
        RobotPassword pwd = myMapper.selectOne(robotPassword);
        return pwd == null ? "" : pwd.getPassword();
    }

    @Override
    public String getPwdByRobotId(Long robotId) {
        RobotPassword robotPassword = new RobotPassword();
        robotPassword.setRobotId(robotId);
        RobotPassword pwd = myMapper.selectOne(robotPassword);
        return pwd == null ? "" : pwd.getPassword();
    }


}
