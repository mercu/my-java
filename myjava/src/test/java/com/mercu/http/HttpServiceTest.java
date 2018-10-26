package com.mercu.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mercu.config.AppConfig;
import spring.TestConfig;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class HttpServiceTest {
    @Autowired
    private HttpService httpService;

    @Test
    public void httpGet() {
        httpService.get("http://www.google.com");
    }
}
