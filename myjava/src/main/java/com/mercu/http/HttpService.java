package com.mercu.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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

        try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            System.out.println("=== response body ===");
            br.lines().forEach(line -> System.out.println(line));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
