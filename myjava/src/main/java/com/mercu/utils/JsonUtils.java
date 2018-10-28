package com.mercu.utils;

public class JsonUtils {
    /**
     * from, to 문자열을 제외한 내부 문자열을 반환
     * @param jsonContainedLine
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWithout(String jsonContainedLine, String from, String to) {
        return jsonContainedLine.substring(
                jsonContainedLine.indexOf(from),
                jsonContainedLine.lastIndexOf(to) + 1
        );
    }

    /**
     * from, to 값을 포함한 문자열을 반환
     * @param jsonContainedLine
     * @param from
     * @param to
     * @return
     */
    public static String substringBetweenWith(String jsonContainedLine, String from, String to) {
        return jsonContainedLine.substring(
                jsonContainedLine.indexOf(from) + 1,
                jsonContainedLine.lastIndexOf(to)
        );
    }
}
