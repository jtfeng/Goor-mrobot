package cn.mrobot.bean.area.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jelynn on 2017/7/24.
 */
public enum  IndustrialControlPointType {

    INITIAL(0, "goor_domain_src_main_java_cn_mrobot_bean_area_point_IndustrialControlPointType_java_CSD"),//工控 0初始点
    CHARGER_STAND_BY(1, "goor_domain_src_main_java_cn_mrobot_bean_area_point_IndustrialControlPointType_java_CDD"),//工控 1充电点;
    GENERAL(2, "goor_domain_src_main_java_cn_mrobot_bean_area_point_IndustrialControlPointType_java_PTMBD"); //工控 1充电点


    private String value;

    private int caption;

    private IndustrialControlPointType(int caption, String value) {
        this.value = value;
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCaption() {
        return caption;
    }

    public void setCaption(int caption) {
        this.caption = caption;
    }

    public static IndustrialControlPointType getType(int caption) {
        for (IndustrialControlPointType c : IndustrialControlPointType.values()) {
            if (c.getCaption() == caption) {
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
