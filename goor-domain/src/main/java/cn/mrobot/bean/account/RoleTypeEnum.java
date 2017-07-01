package cn.mrobot.bean.account;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/30.
 */
public enum RoleTypeEnum {

    SUPER_ADMIN(1, "超级管理员"),
    HOSPITAL_ADMIN(2, "医院管理员"),
    STATION_ADMIN(3, "站管理员");

    private String value;

    private int caption;

    public String getValue() {
        return value;
    }

    public int getCaption() {
        return caption;
    }

    public static String getValue(int caption) {
        String value = "";
        for(RoleTypeEnum noticeType : RoleTypeEnum.values()){
            if(caption==noticeType.getCaption()){
                value = noticeType.getValue();
            }
        }
        return value;
    }

    public static RoleTypeEnum getType(int caption){
        for (RoleTypeEnum c : RoleTypeEnum.values()) {
            if (c.getCaption() == caption) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (RoleTypeEnum c : RoleTypeEnum.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        return resultList;
    }

    public static String getTypeJson(int caption){
        for (RoleTypeEnum c : RoleTypeEnum.values()) {
            if (c.getCaption() == caption) {
                Map result = new HashMap<String,Object>();
                result.put("name",c);
                result.put("value",c.getValue());
                result.put("caption",c.getCaption());
                return JSON.toJSONString(result);
            }
        }
        return null;
    }

    private RoleTypeEnum(int caption, String value) {
        this.caption = caption;
        this.value = value;
    }
}
