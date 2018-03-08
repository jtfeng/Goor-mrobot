package cn.mrobot.bean.state.enums;

/**
 * Created by Jelynn on 2017/7/18.
 */
public enum  NavigationType {

    NAV_READY(0,"goor_domain_src_main_java_cn_mrobot_bean_state_enums_NavigationType_java_DHZB"), //导航准备
    NAV_MOVING(1,"goor_domain_src_main_java_cn_mrobot_bean_state_enums_NavigationType_java_DHZ"),
    NAV_PAUSE(2,"goor_domain_src_main_java_cn_mrobot_bean_state_enums_NavigationType_java_DHZT");

    private int code;

    private String name;

    NavigationType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static NavigationType getType(int code){
        for(NavigationType type : NavigationType.values()){
            if (code == type.getCode()){
                return type;
            }
        }
        return null;
    }
}
