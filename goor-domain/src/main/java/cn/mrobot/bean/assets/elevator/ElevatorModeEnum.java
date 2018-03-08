package cn.mrobot.bean.assets.elevator;

/**
 * 电梯模式定义
 */
public enum ElevatorModeEnum {

    FULL_AUTOMATIC(1, "goor_domain_src_main_java_cn_mrobot_bean_assets_elevator_ElevatorModeEnum_java_QZD"),
    HALF_AUTOMATIC(0, "goor_domain_src_main_java_cn_mrobot_bean_assets_elevator_ElevatorModeEnum_java_BZD");
    private int modelCode;
    private String mode;

    private ElevatorModeEnum(int modelCode, String mode) {
        this.modelCode = modelCode;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public int getModelCode() {
        return modelCode;
    }

    public static ElevatorModeEnum getElevatorModeEnum(int modelCode){
        ElevatorModeEnum[] values = ElevatorModeEnum.values();
        for (ElevatorModeEnum elevatorModeEnum : values){
            if (elevatorModeEnum.getModelCode() == modelCode){
                return elevatorModeEnum;
            }
        }
        return null;
    }
}
