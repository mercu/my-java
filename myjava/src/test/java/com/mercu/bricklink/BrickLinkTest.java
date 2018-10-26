package com.mercu.bricklink;

import com.mercu.config.AppConfig;
import com.mercu.http.HttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkTest {
    @Autowired
    private BrickLinkService brickLinkService;

    @Autowired
    private HttpService httpService;

    @Test
    public void home() {
        System.out.println(httpService.toStringHttpReponse(
            httpService.get("http://bricklink.com")));
    }

    @Test
    public void login() {
        brickLinkService.loginIfNeed();
    }

    @Test
    public void wantedList() {
        // TODO
    }

}
