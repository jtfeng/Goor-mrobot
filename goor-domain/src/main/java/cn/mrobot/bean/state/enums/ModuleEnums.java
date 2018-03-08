package cn.mrobot.bean.state.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jelynn on 2017/7/24.
 */
public enum ModuleEnums {

    NAVIGATION(100, "goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_ZDDH"),
    CHARGE(200, "goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_DC"),
    BASE(300, "goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_DP"),
    MISSION(400, "goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_DDRW"),
    SCENE(500, "goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_CJ"),
    PAD_INFO(600,"goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_HSZTX"),
    BOOT(700,"goor_domain_src_main_java_cn_mrobot_bean_state_enums_ModuleEnums_java_KJGL");

    private int moduleId;

    private String moduleName;

    ModuleEnums(int moduleId, String moduleName) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (ModuleEnums c : ModuleEnums.values()) {
            resultList.add(toDTO(c)) ;
        }
        return resultList;
    }

    public static ModuleEnums getModuleEnums(int moduleId){
        for(ModuleEnums moduleEnums : ModuleEnums.values()){
            if(moduleEnums.getModuleId() == moduleId){
                return moduleEnums;
            }
        }
        return null;
    }

    private static Map toDTO(ModuleEnums c) {
        Map result = new HashMap<String,Object>();
        result.put("name",c);
        result.put("moduleId",c.getModuleId());
        result.put("moduleName",c.getModuleName());
        return result;
    }
}
