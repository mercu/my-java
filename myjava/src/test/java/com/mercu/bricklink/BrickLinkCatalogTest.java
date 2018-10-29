package com.mercu.bricklink;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.bricklink.model.PartInfo;
import com.mercu.bricklink.model.SetCategory;
import com.mercu.bricklink.model.SetInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.config.AppConfig;
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
public class BrickLinkCatalogTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkCatalogTest.class);

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @Test
    public void crawlPartCategories() {
        List<PartCategory> partCategoryList = brickLinkCatalogService.crawlPartCategoryList();
        System.out.println(partCategoryList);

//        brickLinkCatalogService.savePartCategoryList(partCategoryList);
    }

    @Test
    public void crawlSetCategories() {
        List<SetCategory> setCategoryList = brickLinkCatalogService.crawlSetCategoryList();
        System.out.println(setCategoryList);

//        brickLinkCatalogService.saveSetCategoryList(setCategoryList);
    }

    @Test
    public void findSetCategoriesAll() {
        System.out.println(
                brickLinkCatalogService.findSetCategoriesAll());
    }

    @Test
    public void crawlSetInfoListOfCategory() {
        brickLinkCatalogService.crawlSetInfoListOfCategory("143");
    }

    @Test
    public void crawlSetInfoListOfCategoriesAll() {
        List<SetCategory> setCategoryList = brickLinkCatalogService.findSetCategoriesAll();
        for (SetCategory setCategory : setCategoryList) {
            List<SetInfo> setInfoList = brickLinkCatalogService.crawlSetInfoListOfCategory(setCategory.getId());
            brickLinkCatalogService.saveSetInfoList(setInfoList);
        }
    }

    @Test
    public void crawlSetInfoListOfYear() {
        for (int year = 0; year >= 0; year--) {
            List<SetInfo> setInfoList = brickLinkCatalogService.crawlSetInfoListOfYear(String.valueOf(year));
            brickLinkCatalogService.saveSetInfoList(setInfoList);
        }
    }

    @Test
    public void crawlPartInfoListOfCategoriesAll() {
        List<PartCategory> partCategoryList = brickLinkCatalogService.findPartCategoriesAll();
        for (PartCategory partCategory : partCategoryList) {
            List<PartInfo> partInfoList = brickLinkCatalogService.crawlPartInfoListOfCategory(partCategory.getId());
            brickLinkCatalogService.savePartInfoList(partInfoList);
        }
    }

}
