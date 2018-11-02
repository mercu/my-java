package com.mercu.bricklink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mercu.bricklink.crawler.BrickLinkMyCrawler;
import com.mercu.bricklink.service.BrickLinkAjaxService;
import com.mercu.bricklink.service.BrickLinkLoginService;
import com.mercu.config.AppConfig;
import com.mercu.http.HttpService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkTest.class);

    @Autowired
    private HttpService httpService;

    @Autowired
    private BrickLinkLoginService brickLinkLoginService;
    @Autowired
    private BrickLinkMyCrawler brickLinkMyCrawler;
    @Autowired
    private BrickLinkAjaxService brickLinkAjaxService;

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
        brickLinkMyCrawler.crawlWantedList();
    }

    @Test
    public void ajaxFindSetId() {
        System.out.println(brickLinkAjaxService.ajaxFindSetId("70403"));
    }

}
