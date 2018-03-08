package cn.mrobot.bean.mission;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/7/14.
 */
public enum MissionTypeEnum {

    DISPATCH_MISSION(1, "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionTypeEnum_java_DDRW"),
    CHARGER_MISSION(2, "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionTypeEnum_java_CDRW");

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
        for(MissionTypeEnum noticeType : MissionTypeEnum.values()){
            if(caption==noticeType.getCaption()){
                value = noticeType.getValue();
            }
        }
        return value;
    }

    public static MissionTypeEnum getType(int caption){
        for (MissionTypeEnum c : MissionTypeEnum.values()) {
            if (c.getCaption() == caption) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (MissionTypeEnum c : MissionTypeEnum.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        return resultList;
    }

    public static String getTypeJson(int caption){
        for (MissionTypeEnum c : MissionTypeEnum.values()) {
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

    private MissionTypeEnum(int caption, String value) {
        this.caption = caption;
        this.value = value;
    }
}
