package com.mercu.utils;

import org.apache.commons.lang3.StringUtils;

public class SubstringUtils {
    /**
     * from, to 문자열을 제외한 내부 문자열을 반환
     * @param line
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWithout(String line, String from, String to) {
        if (StringUtils.isBlank(line)) return null;

        int fromIdx = line.indexOf(from);
        int toIdx = line.lastIndexOf(to);
        if (fromIdx == -1 || toIdx == -1 || fromIdx >= toIdx) return null;

        return line.substring(fromIdx + from.length(), toIdx);
    }

    /**
     * from, to 값을 포함한 문자열을 반환
     * @param line
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWith(String line, String from, String to) {
        if (StringUtils.isBlank(line)) return null;

        int fromIdx = line.indexOf(from);
        int toIdx = line.lastIndexOf(to);
        if (fromIdx == -1 || toIdx == -1 || fromIdx >= toIdx) return null;

        return line.substring(fromIdx, toIdx + to.length());
    }
}
