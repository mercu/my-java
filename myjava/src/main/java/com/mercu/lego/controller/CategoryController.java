package com.mercu.lego.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.service.BrickLinkCategoryService;
import com.mercu.utils.JsonUtils;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class CategoryController {
    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;

    @RequestMapping("/partCategories")
    @ResponseBody
    public String partCategories() {
        List<PartCategory> partCategories = brickLinkCategoryService.findPartCategoriesAll();
        return JsonUtils.toJson(partCategories);
    }
}
