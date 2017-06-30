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

    TRAILER(1, "拖车式"),
    CABINET(2, "柜式"),
    DRAWER(3, "抽屉式");

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

    public static Map list() {
        Map map = new HashMap();
        List<Map> resultList = new ArrayList<Map>();
        for (RobotTypeEnum c : RobotTypeEnum.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        map.put("robotType", resultList);
        return map;
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
