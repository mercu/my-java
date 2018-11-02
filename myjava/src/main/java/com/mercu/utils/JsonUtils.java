package com.mercu.utils;

import com.google.gson.Gson;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
