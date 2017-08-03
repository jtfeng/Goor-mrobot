package cn.mrobot.bean.state.enums;

import cn.mrobot.bean.mission.MissionListTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jelynn on 2017/7/24.
 */
public enum ModuleEnums {

    NAVIGATION(100, "自动导航"),
    CHARGE(200, "电量"),
    BASE(300, "底盘");

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

    private static Map toDTO(ModuleEnums c) {
        Map result = new HashMap<String,Object>();
        result.put("name",c);
        result.put("moduleId",c.getModuleId());
        result.put("moduleName",c.getModuleName());
        return result;
    }
}
