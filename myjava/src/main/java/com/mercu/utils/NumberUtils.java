package com.mercu.utils;

import java.util.Objects;

public class NumberUtils {
    public static int intValueDefault(Integer intObj, int defaultValue) {
        if (Objects.isNull(intObj)) return defaultValue;
        return intObj.intValue();
    }

    public static boolean equals(Integer int1, Integer int2) {
        if (Objects.isNull(int1)) return false;
        return int1.equals(int2);
    }
}
