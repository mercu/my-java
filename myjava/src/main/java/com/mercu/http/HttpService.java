package com.mercu.http;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@Service
public class HttpService {
    private HttpClient httpClient;

    @PostConstruct
    public void initialize() {
        httpClient = HttpClientBuilder.create().build();
    }

    public HttpResponse get(String url) {
        HttpGet request = new HttpGet(url);
        //        request.addHeader("User-Agent", "");

        HttpResponse response = executeHttpClient(request);
        return response;
    }

    public HttpResponse post(String url) {
        return post(url, null);

    }
    public HttpResponse post(String url, HttpEntity httpEntity) {
        HttpPost post = new HttpPost(url);
//        post.setHeader("User-Agent", "");
        if (Objects.nonNull(httpEntity)) post.setEntity(httpEntity);

        HttpResponse response = executeHttpClient(post);
        return response;
    }

    public String getAsString(String url) {
        return toStringHttpReponse(get(url));
    }

    public String toStringHttpReponse(HttpResponse response) {
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("toStringHttpReponse failed!", e);
        }
    }

    private HttpResponse executeHttpClient(HttpUriRequest request) {
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("executeHttpClient failed!", e);
        }
    }

}
