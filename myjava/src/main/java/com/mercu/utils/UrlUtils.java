package com.mercu.utils;

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

}
