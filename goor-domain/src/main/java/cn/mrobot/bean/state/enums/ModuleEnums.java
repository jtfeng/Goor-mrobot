package cn.mrobot.bean.state.enums;

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
}
