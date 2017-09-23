package cn.mrobot.bean.assets.elevator;

/**
 * 电梯模式定义
 */
public enum ElevatorModeEnum {
    FULL_AUTOMATIC("全自动"),
    HALF_AUTOMATIC("半自动");
    String mode;
    private ElevatorModeEnum(String mode){
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
