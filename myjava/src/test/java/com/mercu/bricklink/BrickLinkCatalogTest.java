package com.mercu.bricklink;

import com.mercu.bricklink.model.category.MinifigCategory;
import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.model.category.SetCategory;
import com.mercu.bricklink.model.info.MinifigInfo;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkCategoryService;
import com.mercu.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;

    @Test
    public void crawlPartCategories() {
        List<PartCategory> partCategoryList = brickLinkCategoryService.crawlPartCategoryList();
        System.out.println(partCategoryList);

//        brickLinkCatalogService.savePartCategoryList(partCategoryList);
    }

    @Test
    public void crawlSetCategories() {
        List<SetCategory> setCategoryList = brickLinkCategoryService.crawlSetCategoryList();
        System.out.println(setCategoryList);

//        brickLinkCatalogService.saveSetCategoryList(setCategoryList);
    }

    @Test
    public void crawlMinifigCategories() {
        List<MinifigCategory> minifigCategoryList = brickLinkCategoryService.crawlMinifigCategoryList();
        System.out.println(minifigCategoryList);

        brickLinkCategoryService.saveMinifigCategoryList(minifigCategoryList);
    }

    @Test
    public void findSetCategoriesAll() {
        System.out.println(
                brickLinkCategoryService.findSetCategoriesAll());
    }

    @Test
    public void findMinifigCategoriesRoot() {
        System.out.println(brickLinkCategoryService.findMinifigCategoriesRoot());
    }

    @Test
    public void crawlSetInfoListOfCategory() {
        brickLinkCatalogService.crawlSetInfoListOfCategory("143");
    }

    @Test
    public void crawlMinifigInfoListOfCategoriesRoot() {
        List<MinifigCategory> minifigCategoryList = brickLinkCategoryService.findMinifigCategoriesRoot();
        for (MinifigCategory minifigCategory : minifigCategoryList) {
            if (StringUtils.equals(minifigCategory.getParts(), "(1)")) continue;


            List<MinifigInfo> minifigInfoList = brickLinkCatalogService.crawlMinifigInfoListOfCategory(minifigCategory.getId());
            brickLinkCatalogService.saveMinifigInfoList(minifigInfoList);
        }
    }

    @Test
    public void crawlSetInfoListOfCategoriesAll() {
        List<SetCategory> setCategoryList = brickLinkCategoryService.findSetCategoriesAll();
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
        List<PartCategory> partCategoryList = brickLinkCategoryService.findPartCategoriesAll();
        for (PartCategory partCategory : partCategoryList) {
            List<PartInfo> partInfoList = brickLinkCatalogService.crawlPartInfoListOfCategory(partCategory.getId());
            brickLinkCatalogService.savePartInfoList(partInfoList);
        }
    }

}
