package cn.mrobot.bean.enums;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by chay on 2018/1/20.
 */
public enum X86PatternEnum {

    LINE(0, "直线"),
    BEZIER(1, "三阶贝塞尔曲线"),
    CUSTOM(2, "自定义曲线");

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
        for(X86PatternEnum noticeType : X86PatternEnum.values()){
            if(caption==noticeType.getCaption()){
                value = noticeType.getValue();
            }
        }
        return value;
    }

    public static X86PatternEnum getType(int caption){
        for (X86PatternEnum c : X86PatternEnum.values()) {
            if (c.getCaption() == caption) {
                return c;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (X86PatternEnum c : X86PatternEnum.values()) {
            Map result = new HashMap<String,Object>();
            result.put("name",c);
            result.put("value",c.getValue());
            result.put("caption",c.getCaption());
            resultList.add(result) ;
        }
        return resultList;
    }

    public static String getTypeJson(int caption){
        for (X86PatternEnum c : X86PatternEnum.values()) {
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

    private X86PatternEnum(int caption, String value) {
        this.caption = caption;
        this.value = value;
    }
}
