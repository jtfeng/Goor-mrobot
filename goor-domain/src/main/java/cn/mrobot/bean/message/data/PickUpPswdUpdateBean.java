package cn.mrobot.bean.message.data;

import cn.mrobot.bean.assets.robot.RobotPassword;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-5.
 */
public class PickUpPswdUpdateBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息的唯一id
     */
    private String uuid;
    /**
     *
     */
    List<RobotPassword> list;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<RobotPassword> getList() {
        return list;
    }

    public void setList(List<RobotPassword> list) {
        this.list = list;
    }
}
