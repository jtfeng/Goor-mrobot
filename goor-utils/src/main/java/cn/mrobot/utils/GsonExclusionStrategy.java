package cn.mrobot.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by Spring Boot Code Gen.
 * Author: Abel Deng, Mail: abeldeng@qq.com.
 */
public class GsonExclusionStrategy implements ExclusionStrategy {
    private final Class<?> typeToSkip;

    public GsonExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(JsonIgnore.class) != null;
    }
}

