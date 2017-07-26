package cn.muye.area.map.bean;

/**
 * Created by Jelynn on 2017/7/25.
 */
public class StateDetail {

    //属性英文名
    private String name;

    //属性中文名
    private String CHName;

    //属性值
    private int value;

    //属性值 中文表示
    private String CHValue;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCHName() {
        return CHName;
    }

    public void setCHName(String CHName) {
        this.CHName = CHName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCHValue() {
        return CHValue;
    }

    public void setCHValue(String CHValue) {
        this.CHValue = CHValue;
    }
}
