package com.mercu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class HtmlUtils {
    public static String findLineOfStringContains(String html, String str) {
        String findLine = null;

        try (BufferedReader br = new BufferedReader(new StringReader(html))) {
            findLine = br.lines()
                    .filter(line -> line.contains(str))
                    .findFirst()
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return findLine;
    }
}
