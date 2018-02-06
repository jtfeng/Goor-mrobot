package cn.mrobot.bean.version;

/**
 * @author Created by chay on 2018/1/11.
 */
public class RequestData {
    private String name;
    private String value;

    public RequestData() {

    }

    public RequestData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
