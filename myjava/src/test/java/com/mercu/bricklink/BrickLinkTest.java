package com.mercu.bricklink;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.bricklink.model.SetCategory;
import com.mercu.bricklink.repository.PartCategoryRepository;
import com.mercu.bricklink.repository.SetCategoryRepository;
import com.mercu.bricklink.service.BrickLinkCatalogService;
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

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkTest.class);

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
    @Autowired
    private SetCategoryRepository setCategoryRepository;

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

    @Test
    public void setCategories() {
        List<SetCategory> setCategoryList = brickLinkCatalogService.setCategoryList();
        System.out.println(setCategoryList);

//        for (SetCategory setCategory : setCategoryList) {
//            setCategoryRepository.save(setCategory);
//        }
    }
}
