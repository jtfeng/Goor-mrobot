package cn.mrobot.bean.message.data;

import java.io.Serializable;

/**
 * Created by abel on 17-7-5.
 */
public class PickUpPswdVerifyBean implements Serializable {

    private static final long serialVersionUID = 2517028450403546518L;

    public static final String RET_CODE_SUCCESS = "success";//验证成功
    public static final String RET_CODE_ERROR_PARA = "error_para";//参数验证不合法
    public static final String RET_CODE_ERROR_ROBOT = "error_robot";//没有找到对应的机器人记录
    public static final String RET_CODE_ERROR_NO_RECORD = "error_no_record";//没有查询到对应的密码记录，验证失败

    /**
     * 机器人主板编号
     */
    private String robotCode;

    /**
     *箱子id
     */
    private Integer boxNum;

    /**
     *箱子取货密码
     */
    private String pswd;

    /**
     * 返回验证结果代码
     */
    private String retCode;

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }
}
