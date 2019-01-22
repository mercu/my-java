package com.mercu.utils;

import java.util.Map;
import java.util.Objects;

public class MapUtils {
    public static void increase(Map<String, Integer> countMap, String key) {
        Integer count = countMap.get(key);
        if (Objects.isNull(count)) {
            countMap.put(key, 1);
        } else {
            countMap.put(key, count + 1);
        }
    }
}
