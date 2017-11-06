package cn.mrobot.bean.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/7
 * Time: 9:47
 * Describe:
 * Version:1.0
 */
public enum LogLevel {

    INFO("INFO", "普通日志"),
    WARNING("WARNING", "警告"),
    ERROR("ERROR", "错误");

    private String name;
    private String value;


    private LogLevel(String name, String value) {
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

    public static LogLevel getLogLevel(String name) {
        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.getName().endsWith(name)) {
                return logLevel;
            }
        }
        return null;
    }

    public static List list() {
        List<Map> resultList = new ArrayList<Map>();
        for (LogLevel c : LogLevel.values()) {
            resultList.add(toDTO(c));
        }
        return resultList;
    }

    private static Map toDTO(LogLevel c) {
        Map result = new HashMap<String, Object>();
        result.put("name", c);
        result.put("value", c.getValue());
        result.put("caption", c.getName());
        return result;
    }
}
