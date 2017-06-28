package cn.mrobot.bean.assets.robot;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;

/**
 * Created by Selim on 2017/6/21.
 */
@Table(name = "AS_ROBOT_PASSWORD")
public class RobotPassword extends BaseBean{

    private Long robotId; //对应的机器人

    private Integer boxNum; //箱子编号

    private String password; //密码

    public RobotPassword() {
    }

    public RobotPassword(Long id) {
        super(id);
    }

    public RobotPassword(Long id, Long robotId) {
        super(id);
        this.robotId = robotId;
    }



    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
