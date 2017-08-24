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

    private String name;

    private IndustrialControlPointType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static IndustrialControlPointType getType(int caption) {
        for (IndustrialControlPointType c : IndustrialControlPointType.values()) {
            if (c.getValue() == caption) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (IndustrialControlPointType c : IndustrialControlPointType.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c.getName());
            result.put("value",c.getValue());
            resultList.add(result) ;
        }
        return resultList;
    }
}
