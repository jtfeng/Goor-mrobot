package cn.mrobot.bean.mission;

/**
 * Created by Jelynn on 2017/7/28.
 */
public enum MissionState {

    STATE_INIT("init", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_DZX"),
    STATE_WAITING("waiting", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_DDZ"),
    STATE_FINISHED("finished", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_YJWC"),
    STATE_EXECUTING("executing", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_ZZZX"),
    STATE_PAUSED("paused", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_ZTZ"),
    STATE_CANCELED("canceled", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_BQX"),
    STATE_FAILED("failed", "goor_domain_src_main_java_cn_mrobot_bean_mission_MissionState_java_SB");

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
