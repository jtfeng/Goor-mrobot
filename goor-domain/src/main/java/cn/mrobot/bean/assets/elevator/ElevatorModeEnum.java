package cn.mrobot.bean.assets.elevator;

/**
 * 电梯模式定义
 */
public enum ElevatorMode {
    FULL_AUTOMATIC("全自动"),
    HALF_AUTOMATIC("半自动");
    String mode;
    private ElevatorMode(String mode){
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
