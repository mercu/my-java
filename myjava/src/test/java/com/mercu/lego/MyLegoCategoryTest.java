package com.mercu.lego;

import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.repository.info.PartInfoRepository;
import com.mercu.bricklink.service.BrickLinkCategoryService;
import com.mercu.config.AppConfig;
import com.mercu.lego.model.MyPartCategory;
import com.mercu.lego.service.MyCategoryService;
import com.mercu.log.LogService;
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
public class MyLegoCategoryTest {
    private static final Logger logger = LoggerFactory.getLogger(MyLegoCategoryTest.class);

    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;
    @Autowired
    private MyCategoryService myCategoryService;

    @Autowired
    private PartInfoRepository partInfoRepository;

    @Autowired
    private LogService logService;

    /**
     * BrickLink 부품 카테고리 목록을 이관한다.
     */
    @Test
    public void migrateBrickLinkPartCategories() {
        logService.log("migrateBrickLinkPartCategories", "=== start !");
        List<PartCategory> blPartCategories = brickLinkCategoryService.findPartCategoriesAll();

        for (PartCategory partCategory : blPartCategories) {
            MyPartCategory myPartCategory = new MyPartCategory();
            myPartCategory.setBlCategoryId(partCategory.getId());
            myPartCategory.setType(partCategory.getType());
            myPartCategory.setName(partCategory.getName());
            myPartCategory.setDepth(partCategory.getDepth());
            myPartCategory.setRepImgs(partCategory.getRepImgs());
            myPartCategory.setSetQty(partCategory.getSetQty());
            logService.log("migrateBrickLinkPartCategories", "myPartCategory : " + myPartCategory);

            myCategoryService.save(myPartCategory);
        }

        logService.log("migrateBrickLinkPartCategories", "=== finish !");
    }

    /**
     * BrickLink 부품 카테고리 목록의 대표이미지들을 이관한다.
     */
    @Test
    public void migrateBrickLinkPartCategoryRepImgs() {
        logService.log("migrateBrickLinkPartCategoryRepImgs", "=== start !");
        List<PartCategory> blPartCategories = brickLinkCategoryService.findPartCategoriesAll();

        for (PartCategory partCategory : blPartCategories) {
            MyPartCategory myPartCategory = myCategoryService.findByBlCategoryId(partCategory.getId());
            myPartCategory.setRepImgs(partCategory.getRepImgs());
            logService.log("migrateBrickLinkPartCategoryRepImgs", "myPartCategory : " + myPartCategory);

            myCategoryService.save(myPartCategory);
        }

        logService.log("migrateBrickLinkPartCategoryRepImgs", "=== finish !");
    }

    /**
     * BrickLink 부품 카테고리의 부품 수를 계산한다.
     */
    @Test
    public void updatePartCategoryPartsCount() {
        List<MyPartCategory> myPartCategories = myCategoryService.findPartCategoriesAll();
        for (MyPartCategory myPartCategory : myPartCategories) {
            myPartCategory.setParts(partInfoRepository.countPartsByCategoryId(myPartCategory.getBlCategoryId()));
            myCategoryService.save(myPartCategory);
        }
    }

}
