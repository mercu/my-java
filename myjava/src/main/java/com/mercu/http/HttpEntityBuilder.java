package com.mercu.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpEntityBuilder {
    private List<NameValuePair> params = new ArrayList<>();

    public static HttpEntityBuilder create() {
        return new HttpEntityBuilder();
    }

    public HttpEntityBuilder addParameter(String k, String v) {
        params.add(new BasicNameValuePair(k, v));
        return this;
    }

    public HttpEntity build() {
        try {
            return new UrlEncodedFormEntity(params);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("build failed!", e);
        }
    }
}
