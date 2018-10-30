package com.mercu.bricklink;

import com.mercu.bricklink.service.BrickLinkLoginService;
import com.mercu.bricklink.service.BrickLinkMyService;
import com.mercu.bricklink.service.BrickLinkService;
import com.mercu.config.AppConfig;
import com.mercu.http.HttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkTest.class);

    @Autowired
    private HttpService httpService;

    @Autowired
    private BrickLinkService brickLinkService;
    @Autowired
    private BrickLinkLoginService brickLinkLoginService;
    @Autowired
    private BrickLinkMyService brickLinkMyService;

    @Test
    public void home() {
        System.out.println(httpService.toStringHttpReponse(
            httpService.get("http://bricklink.com")));
    }

    @Test
    public void login() {
        brickLinkLoginService.loginIfNotLoggedin();
    }

    @Test
    public void crawlWantedList() {
        brickLinkLoginService.loginIfNotLoggedin();
        brickLinkMyService.crawlWantedList();
    }

    @Test
    public void ajaxFindSetId() {
        System.out.println(brickLinkService.ajaxFindSetId("70403"));
    }

}
