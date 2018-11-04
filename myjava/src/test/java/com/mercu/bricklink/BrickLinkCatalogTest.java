package com.mercu.bricklink;

import com.mercu.bricklink.crawler.BrickLinkCatalogCrawler;
import com.mercu.bricklink.crawler.BrickLinkCategoryCrawler;
import com.mercu.bricklink.model.category.MinifigCategory;
import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.model.category.SetCategory;
import com.mercu.bricklink.model.info.ColorInfo;
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
    private BrickLinkCatalogCrawler brickLinkCatalogCrawler;
    @Autowired
    private BrickLinkCategoryCrawler brickLinkCategoryCrawler;
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;

    @Test
    public void crawlPartCategories() {
        List<PartCategory> partCategoryList = brickLinkCategoryCrawler.crawlPartCategoryList();
        System.out.println(partCategoryList);

//        brickLinkCatalogService.savePartCategoryList(partCategoryList);
    }

    @Test
    public void crawlSetCategories() {
        List<SetCategory> setCategoryList = brickLinkCategoryCrawler.crawlSetCategoryList();
        System.out.println(setCategoryList);

//        brickLinkCatalogService.saveSetCategoryList(setCategoryList);
    }

    @Test
    public void crawlMinifigCategories() {
        List<MinifigCategory> minifigCategoryList = brickLinkCategoryCrawler.crawlMinifigCategoryList();
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
        brickLinkCatalogCrawler.crawlSetInfoListOfCategory("143");
    }

    @Test
    public void crawlMinifigInfoListOfCategoriesRoot() {
        List<MinifigCategory> minifigCategoryList = brickLinkCategoryService.findMinifigCategoriesRoot();
        for (MinifigCategory minifigCategory : minifigCategoryList) {
            if (StringUtils.equals(minifigCategory.getParts(), "(1)")) continue;


            List<MinifigInfo> minifigInfoList = brickLinkCatalogCrawler.crawlMinifigInfoListOfCategory(minifigCategory.getId());
            brickLinkCatalogService.saveMinifigInfoList(minifigInfoList);
        }
    }

    @Test
    public void crawlSetInfoListOfCategoriesAll() {
        List<SetCategory> setCategoryList = brickLinkCategoryService.findSetCategoriesAll();
        for (SetCategory setCategory : setCategoryList) {
            List<SetInfo> setInfoList = brickLinkCatalogCrawler.crawlSetInfoListOfCategory(setCategory.getId());
            brickLinkCatalogService.saveSetInfoList(setInfoList);
        }
    }

    @Test
    public void crawlSetInfoListOfYear() {
        for (int year = 0; year >= 0; year--) {
            List<SetInfo> setInfoList = brickLinkCatalogCrawler.crawlSetInfoListOfYear(String.valueOf(year));
            brickLinkCatalogService.saveSetInfoList(setInfoList);
        }
    }

    @Test
    public void crawlPartInfoListOfCategoriesAll() {
        List<PartCategory> partCategoryList = brickLinkCategoryService.findPartCategoriesAll();
        for (PartCategory partCategory : partCategoryList) {
            List<PartInfo> partInfoList = brickLinkCatalogCrawler.crawlPartInfoListOfCategory(String.valueOf(partCategory.getId()));
            brickLinkCatalogService.savePartInfoList(partInfoList);
        }
    }

    @Test
    public void crawlColorInfoList() {
        List<ColorInfo> colorInfoList = brickLinkCatalogCrawler.crawlColorInfoList();
        brickLinkCatalogService.saveColorInfoList(colorInfoList);
    }

    /**
     * 부품 카테고리별 대표 이미지를 최대 5개 까지 추출한다.
     */
    @Test
    public void partCategoryRepresentImages() {
        brickLinkCategoryService.autoUpdatePartCategoryRepresentImagesAll();
    }

    /**
     * 부품 카테고리의 세트내 총 수량 개수를 업데이트 한다.
     */
    @Test
    public void updatePartCategorySetQty() {
        brickLinkCategoryService.updatePartCategorySetQty();
    }

    /**
     * 부품별 세트내 총 수량 개수를 업데이트 한다.
     */
    @Test
    public void updatePartInfoSetQty() {
        brickLinkCatalogService.updatePartInfoSetQty();
    }

}
