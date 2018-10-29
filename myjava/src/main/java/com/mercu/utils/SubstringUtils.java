package com.mercu.utils;

public class SubstringUtils {
    /**
     * from, to 문자열을 제외한 내부 문자열을 반환
     * @param line
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWithout(String line, String from, String to) {
        return line.substring(
                line.indexOf(from) + from.length(),
                line.lastIndexOf(to)
        );
    }

    /**
     * from, to 값을 포함한 문자열을 반환
     * @param line
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWith(String line, String from, String to) {
        return line.substring(
                line.indexOf(from),
                line.lastIndexOf(to) + to.length()
        );
    }
}
