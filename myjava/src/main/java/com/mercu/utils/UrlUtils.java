package com.mercu.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UrlUtils {
    public static Map<String, String> urlParametersMap(String url) {
        try {
            return UrlUtils.quriesToMap(new URL(url).getQuery());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> quriesToMap(String urlQuery) {
        Map<String, String> map = new HashMap<>();

        String[] queries = urlQuery.split("&");
        for (String query : queries) {
            String[] querySplit = query.split("=");
            map.put(querySplit[0], querySplit[1]);
        }

        return map;
    }

    public static String replaceLastPath(String url, String targetPath) {
        if (StringUtils.isBlank(url)) return null;

        int lastIndex = url.lastIndexOf("/");
        if (lastIndex < 0) return null;

        int lastBeforeIndex = url.lastIndexOf("/", lastIndex - 1);
        if (lastBeforeIndex < 0) return null;

        return url.substring(0, lastBeforeIndex + 1) + targetPath + url.substring(lastIndex);
    }

    public static void main(String args[]) {
        System.out.println(UrlUtils.replaceLastPath("http://img.bricklink.com/ItemImage/PT/98/15303.t1.png", "55"));
    }
}
