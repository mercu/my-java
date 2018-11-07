package com.mercu.bricklink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkCategoryService;
import com.mercu.config.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkCatalogTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkCatalogTest.class);

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;

    @Test
    public void findSetCategoriesAll() {
        System.out.println(
                brickLinkCategoryService.findSetCategoriesAll());
    }

    @Test
    public void findMinifigCategoriesRoot() {
        System.out.println(brickLinkCategoryService.findMinifigCategoriesRoot());
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
