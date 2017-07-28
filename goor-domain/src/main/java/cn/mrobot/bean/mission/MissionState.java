package cn.mrobot.bean.mission;

/**
 * Created by Jelynn on 2017/7/28.
 */
public enum MissionState {

    STATE_INIT("init", "待执行"),
    STATE_WAITING("waiting", "等待中"),
    STATE_FINISHED("finished", "已经完成"),
    STATE_EXECUTING("executing", "正在执行"),
    STATE_PAUSED("paused", "暂停中"),
    STATE_CANCELED("canceled", "被取消");

    private String code;

    private String name;

    MissionState(java.lang.String code, java.lang.String name) {
        this.code = code;
        this.name = name;
    }

    public java.lang.String getCode() {
        return code;
    }

    public void setCode(java.lang.String code) {
        this.code = code;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public static MissionState getMissionState(String code){
        MissionState[] missionStates = MissionState.values();
        for(MissionState missionState : missionStates){
            if(missionState.getCode().equals(code)){
                return missionState;
            }
        }
        return null;
    }
}
