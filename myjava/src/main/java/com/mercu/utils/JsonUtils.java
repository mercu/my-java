package com.mercu.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        if (Objects.isNull(object)) return gson.toJson(new Object());
        return gson.toJson(object);
    }

    public static List<String> toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
