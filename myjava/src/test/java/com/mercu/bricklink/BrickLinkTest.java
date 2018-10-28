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
        brickLinkService.loginIfNotLoggedin();
    }

    @Test
    public void wantedList() {
        brickLinkService.loginIfNotLoggedin();
        brickLinkService.wantedList();
    }

    @Test
    public void findSetId() {
        System.out.println(brickLinkService.findSetId("70403"));
    }

    @Test
    public void setInventory() {
//        brickLinkService.setInventory("70403");
//        brickLinkService.setInventory("10706");
        brickLinkService.setInventory("75055");
    }

    @Test
    public void partCategories() {
        brickLinkService.partCategories();
    }
}