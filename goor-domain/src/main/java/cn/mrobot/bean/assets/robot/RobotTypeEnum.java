package cn.mrobot.bean.assets.robot;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray.Fu on 2017/6/30.
 */
public enum RobotTypeEnum {

    TRAILER(1, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_TCS"),
    CABINET(2, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_JS"),
    DRAWER(3, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_CTS"),
    COOKY(4, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_COOKY"),
    COOKYPLUS(5, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_COOKYPLUS"),
    CARSON(6, "goor_domain_src_main_java_cn_mrobot_bean_assets_robot_RobotTypeEnum_java_CARSON");

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
        for(RobotTypeEnum noticeType : RobotTypeEnum.values()){
            if(caption==noticeType.getCaption()){
                value = noticeType.getValue();
            }
        }
        return value;
    }

    public static RobotTypeEnum getType(int caption){
        for (RobotTypeEnum c : RobotTypeEnum.values()) {
            if (c.getCaption() == caption) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (RobotTypeEnum c : RobotTypeEnum.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        return resultList;
    }

    public static String getTypeJson(int caption){
        for (RobotTypeEnum c : RobotTypeEnum.values()) {
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

    private RobotTypeEnum(int caption, String value) {
        this.caption = caption;
        this.value = value;
    }
}
