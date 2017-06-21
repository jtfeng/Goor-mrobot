package cn.mrobot.bean.slam;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 命令返回结果
 * Created by jelynn on 2017/6/3
 */

public class SlamResponseBody<T> {
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

    @Override
    public String toString() {
        return "SlamResponseBody{" +
               "subName='" + subName + '\'' +
               ", data=" + data +
               ", msg='" + msg + '\'' +
               ", errorCode='" + errorCode + '\'' +
               '}';
    }

}
