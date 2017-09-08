package cn.muye.order.bean;

/**
 * Created by Selim on 2017/8/31.
 */
public enum WSOrderNotificationType {

    SEND_STATION("SEND_STATION", "发货站提醒"),
    RECEIVE_STATION("RECEIVE_STATION", "取货站提醒");

    private String name;
    private String value;


    WSOrderNotificationType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static WSOrderNotificationType getWSOrderNotificationType(String name){
        for(WSOrderNotificationType logType : WSOrderNotificationType.values()){
            if(logType.getName().endsWith(name)){
                return logType;
            }
        }
        return null;
    }
}
