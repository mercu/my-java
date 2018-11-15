package com.mercu.utils;

import java.util.Objects;

public class NumberUtils {
    public static int intValueDefault(Integer intObj, int defaultValue) {
        if (Objects.isNull(intObj)) return defaultValue;
        return intObj.intValue();
    }
}
