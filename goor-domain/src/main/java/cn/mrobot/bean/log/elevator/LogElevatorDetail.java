package cn.mrobot.bean.log.elevator;

import cn.mrobot.bean.base.BaseBean;

import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

public class LogElevatorDetail extends LogElevator {
    private String departureFloor;//出发楼层
    private String targetFloor;//目标楼层
    private String commandWord;//命令字
    private String elevatorNumber;//电梯编号
    private String robotNumber;//机器人编号

    public String getDepartureFloor() {
        return departureFloor;
    }
    public void setDepartureFloor(String departureFloor) {
        this.departureFloor = departureFloor;
    }
    public String getTargetFloor() {
        return targetFloor;
    }
    public void setTargetFloor(String targetFloor) {
        this.targetFloor = targetFloor;
    }
    public String getCommandWord() {
        return commandWord;
    }
    public void setCommandWord(String commandWord) {
        this.commandWord = commandWord;
    }
    public String getElevatorNumber() {
        return elevatorNumber;
    }
    public void setElevatorNumber(String elevatorNumber) {
        this.elevatorNumber = elevatorNumber;
    }
    public String getRobotNumber() {
        return robotNumber;
    }
    public void setRobotNumber(String robotNumber) {
        this.robotNumber = robotNumber;
    }
    public void parseMessage(){
        // AA 55 (   01      02     00     00      00    ) 02
        //       (出发楼层、目标楼层、命令字、电梯编号、机器人编号)
        String valueString = getValue();
        setDepartureFloor(valueString.substring(4,6));
        setTargetFloor(valueString.substring(6,8));
        String commandExp = COMMAND_WORD_MAPPING.get(valueString.substring(8,10));
        String realExp = "";
        if (commandExp != null){
            realExp = commandExp;
        }else {
            realExp = valueString.substring(8,10);
        }
        setCommandWord(realExp);
        setElevatorNumber(valueString.substring(10,12));
        setRobotNumber(valueString.substring(12,14));
    }
    public static final Map<String, String> COMMAND_WORD_MAPPING = new HashMap<String, String>(){{
        put("00", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_TESTELECMDJQRFSCSZTQH");
        put("01", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_CALLELEENQJQRFSHTWX");
        put("02", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_CALLELEACKHTFSHTYD");
        put("03", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_TAKEELEENQNHFSCTWX");
        put("04", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_TAKEELEACKJQRFSCTYD");
        put("05", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INTOELEENQNHFSJRZL");
        put("06", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INTOELEACKJQRFSJRDTYD");
        put("0D", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INTOELESETNHFSJRYDQR");
        put("07", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_LEFTELEENQNHFSDDZL");
        put("0B", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_LEFTELECMDJQRFSLKZL");
        put("0E", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_LEFTCMDSETNHFSLKZLQR");
        put("08", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_LEFTELEACKJQRFSLKYD");
        put("0F", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_LEFTELESETNHFSLKYDQR");
        put("09", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_FULLELEENQNHFSTNZYZL");
        put("0A", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_FULLELEACKJQRFSZYYD");
        put("0C", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_FLOORELERRNHFSLCCWZL");
        put("10", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INITELEENQJQRFSFWDTZT");
        put("11", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INITELEACKNHFSFWDTQR");
        put("12", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_STAELEENQJQRFSDTZTWX");
        put("13", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_STAELEACKNHFSDTZTYD");
        put("14", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ELELIGHTONJQRFSDKDTZM");
        put("15", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ELELITONSETNHFSDKDTZMQR");
        put("16", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ELELIGHTOFFJQRFSGBDTZM");
        put("17", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ELELITOFFSETNHFSGBDTZMQR");
        put("18", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ACOPENENQJQRFSMJKM");
        put("19", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ACOPENACKWXMKMJKMQR");
        put("1A", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ACCLOSEENQJQRFSMJGM");
        put("1B", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_ACCLOSEACKWXMKMJGMQR");
        put("20", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_EXCOMENQJQRFSWBTXCS");
        put("21", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_EXCOMACKWXHTFSWBTXQR");
        put("22", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INCOMENQJQRFSNBTXCS");
        put("23", "goor_domain_src_main_java_cn_mrobot_bean_log_elevator_LogElevatorDetail_java_INCOMENQNHFSNBTXQR");
    }};
}
