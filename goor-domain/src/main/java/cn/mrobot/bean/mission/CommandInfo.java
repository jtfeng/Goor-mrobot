package cn.mrobot.bean.mission;

import java.io.Serializable;

/**
 * Created by admin on 2017/7/26.
 */
public class CommandInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid; //唯一标识名

    private String command; //命令名

    private Long sendTime; //发送时间

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }
}
