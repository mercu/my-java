package com.mercu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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

    public void get(String url) {
        HttpGet request = new HttpGet(url);

        //        request.addHeader("User-Agent", "");
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("response.statusLine.statusCode : " + response.getStatusLine().getStatusCode());
        printEntityContent(response);

    }

    private void printEntityContent(HttpResponse response) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            System.out.println("=== response body ===");
            br.lines().forEach(line -> System.out.println(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=== end of body ===");
    }

    public void post(String url) {
        HttpPost post = new HttpPost(url);

//        post.setHeader("User-Agent", "");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("userid", "mercujjang@gmail.com"));
        params.add(new BasicNameValuePair("password", System.getProperty("pass"))); // !!!!!!!!!!!!!
        params.add(new BasicNameValuePair("override", "false"));
        params.add(new BasicNameValuePair("keepme_loggedin", "false"));
        params.add(new BasicNameValuePair("mid", "166afe6283900000-8299106d32c5b932"));
        params.add(new BasicNameValuePair("pageid", "MAIN"));

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse response = httpClient.execute(post);
            System.out.println("response.statusLine.statusCode : " + response.getStatusLine().getStatusCode());
            printEntityContent(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
