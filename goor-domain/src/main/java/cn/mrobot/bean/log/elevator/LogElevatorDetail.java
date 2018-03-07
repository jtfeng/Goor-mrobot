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
        put("00", "TestEleCmd {机器人发送}【测试状态切换】");
        put("01", "CallEleENQ {机器人发送}【呼梯问询】");
        put("02", "CallEleACK {呼梯发送}【呼梯应答】");
        put("03", "TakeEleENQ {内呼发送}【乘梯问询】");
        put("04", "TakeEleACK {机器人发送}【乘梯应答】");
        put("05", "IntoEleENQ {内呼发送}【进入指令】");
        put("06", "IntoEleACK {机器人发送}【进入电梯应答】");
        put("0D", "IntoEleSet {内呼发送}【进入应答确认】");
        put("07", "LeftEleENQ {内呼发送}【到达指令】");
        put("0B", "LeftEleCMD {机器人发送}【离开指令】");
        put("0E", "LeftCMDSet {内呼发送}【离开指令确认】");
        put("08", "LeftEleACK {机器人发送}【离开应答】");
        put("0F", "LeftEleSet {内呼发送}【离开应答确认】");
        put("09", "FullEleENQ {内呼发送}【梯内占用指令】");
        put("0A", "FullEleACK {机器人发送}【占用应答】");
        put("0C", "FloorElErr  {内呼发送}【楼层错误指令】");
        put("10", "InitEleENQ  {机器人发送}【复位电梯状态】");
        put("11", "InitEleACK  {内呼发送}【复位电梯确认】");
        put("12", "StaEleENQ {机器人发送}【电梯状态问询】");
        put("13", "StaEleACK {内呼发送}【电梯状态应答】");
        put("14", "EleLightON {机器人发送}【打开电梯照明】");
        put("15", "EleLitOnSet {内呼发送}【打开电梯照明确认】");
        put("16", "EleLightOFF {机器人发送}【关闭电梯照明】");
        put("17", "EleLitOffSet {内呼发送}【关闭电梯照明确认】");
        put("18", "ACOpenENQ {机器人发送}【门禁开门】");
        put("19", "ACOpenACK {无线门控}【门禁开门确认】");
        put("1A", "ACCloseENQ {机器人发送}【门禁关门】");
        put("1B", "ACCloseACK {无线门控}【门禁关门确认】");
        put("20", "ExComENQ {机器人发送}【外部通信测试】");
        put("21", "ExComACK {无线呼梯发送}【外部通信确认】");
        put("22", "InComENQ {机器人发送}【内部通信测试】");
        put("23", "InComENQ {内呼发送}【内部通信确认】");
    }};
}
