package com.mercu.bricklink;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.bricklink.repository.PartCategoryRepository;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkLoginService;
import com.mercu.bricklink.service.BrickLinkMyService;
import com.mercu.bricklink.service.BrickLinkService;
import com.mercu.config.AppConfig;
import com.mercu.http.HttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkTest {
    @Autowired
    private BrickLinkService brickLinkService;
    @Autowired
    private BrickLinkLoginService brickLinkLoginService;
    @Autowired
    private BrickLinkMyService brickLinkMyService;
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private HttpService httpService;
    @Autowired
    private PartCategoryRepository partCategoryRepository;

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
    public void wantedList() {
        brickLinkLoginService.loginIfNotLoggedin();
        brickLinkMyService.wantedList();
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
        List<PartCategory> partCategoryList = brickLinkCatalogService.partCategoryList();
        System.out.println(partCategoryList);

//        for (PartCategory partCategory : partCategoryList) {
//            partCategoryRepository.save(partCategory);
//        }
    }
}
