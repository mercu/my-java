package com.mercu.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static List<String> toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
