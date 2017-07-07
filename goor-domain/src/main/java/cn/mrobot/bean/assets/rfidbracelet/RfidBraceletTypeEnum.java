package cn.mrobot.bean.assets.rfidbracelet;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/30.
 */

/**
 * 简单定义手环权限
 */
public enum RfidBraceletTypeEnum {

    //护士长
    HEAD_NURSE(0),
    //普通护士
    NURSE(1);
    private Integer value;
    private RfidBraceletTypeEnum(Integer value) {
        this.value = value;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
}