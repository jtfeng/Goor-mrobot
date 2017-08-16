package cn.mrobot.bean.state.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jelynn on 2017/7/24.
 */
public enum ModuleEnums {

    NAVIGATION(100, "自动导航"),
    CHARGE(200, "电池"),
    BASE(300, "底盘"),
    MISSION(400, "调度任务"),
    SCENE(500, "场景");

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
