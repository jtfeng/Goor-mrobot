package cn.mrobot.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Spring Boot Code Gen.
 * Author: Abel Deng, Mail: abeldeng@qq.com.
 */
public class JsonUtils {

    private static Gson gson;

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
            if (gson != null)
                ret = gson.fromJson(str, t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String toJson(Object object, Type t){
        String ret = null;
        try {
            if (gson != null)
                ret = gson.toJson(object, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}


