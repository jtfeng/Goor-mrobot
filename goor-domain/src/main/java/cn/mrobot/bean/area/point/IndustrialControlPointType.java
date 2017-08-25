package cn.mrobot.bean.area.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jelynn on 2017/7/24.
 */
public enum  IndustrialControlPointType {

    INITIAL(0, "初始点"),//工控 0初始点
    CHARGER_STAND_BY(1, "充电点"),//工控 1充电点;
    GENERAL(2, "普通目标点"); //工控 1充电点


    private int value;

    private String caption;

    private IndustrialControlPointType(int value, String caption) {
        this.value = value;
        this.caption = caption;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public static IndustrialControlPointType getType(int value) {
        for (IndustrialControlPointType c : IndustrialControlPointType.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (IndustrialControlPointType c : IndustrialControlPointType.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        return resultList;
    }
}
