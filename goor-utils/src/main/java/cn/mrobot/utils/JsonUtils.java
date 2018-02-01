package cn.mrobot.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Spring Boot Code Gen.
 * Author: Abel Deng, Mail: abeldeng@qq.com.
 */
public class JsonUtils {

    private static Gson gson;
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    static {
        gson = new GsonBuilder()
                .setExclusionStrategies(new GsonExclusionStrategy(Collection.class))
                .create();
    }

    private JsonUtils() {

    }

    public static Object fromJson(String str, Type t){
        Object ret = null;
        try {
            if (gson != null) {
                ret = gson.fromJson(str, t);
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ret;
    }

    public static String toJson(Object object, Type t){
        String ret = null;
        try {
            if (gson != null) {
                ret = gson.toJson(object, t);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ret;
    }
}


