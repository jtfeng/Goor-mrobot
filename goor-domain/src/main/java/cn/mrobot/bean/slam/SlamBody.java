package cn.mrobot.bean.slam;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 请求和返回的topic body
 * Created by Jelynn on 2017/7/17.
 */
public class SlamBody <T> {

    //方法名
    @JSONField(name = "pub_name")
    private String pubName;

    //方法名
    @JSONField(name = "sub_name")
    private String subName;


    @JSONField(name = "data")
    private T data;

    //失败原因
    @JSONField(name = "msg")
    private String msg;

    // 非 0 即失败
    @JSONField(name = "error_code")
    private String errorCode;

    @JSONField(name = "uuid")
    private String uuid;

    @JSONField(name = "target_robot_code")
    private String targetRobotCode;

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTargetRobotCode() {
        return targetRobotCode;
    }

    public void setTargetRobotCode(String targetRobotCode) {
        this.targetRobotCode = targetRobotCode;
    }
}
