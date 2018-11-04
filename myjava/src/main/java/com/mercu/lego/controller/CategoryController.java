package com.mercu.lego.controller;

import com.mercu.lego.model.MyPartCategory;
import com.mercu.lego.service.MyCategoryService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class CategoryController {
    @Autowired
    private MyCategoryService myCategoryService;

    @RequestMapping("/partCategories")
    @ResponseBody
    public String partCategories(@RequestParam(value = "parentId", required = false) Integer parentId) {
        if (Objects.isNull(parentId)) parentId = 0;

        List<MyPartCategory> partCategories = myCategoryService.findPartCategoriesByParentId(parentId);
        return JsonUtils.toJson(partCategories);
    }

    @RequestMapping(path = "/partCategory/new", method = RequestMethod.POST)
    @ResponseBody
    public String newPartCategory(@RequestParam("parentId") Integer parentId, @RequestParam("name") String name) {
        myCategoryService.newPartCatergory(name, parentId);
        return "success";
    }

    @RequestMapping(path = "/partCategory/move", method = RequestMethod.POST)
    @ResponseBody
    public String movePartCategory(@RequestParam("categoryIdFrom") Integer categoryIdFrom, @RequestParam("parentIdTo") Integer parentIdTo) {
        try {
            myCategoryService.movePartCatergory(categoryIdFrom, parentIdTo);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed! - " + e.getMessage();
        }
    }

    @RequestMapping(path = "/partCategory/addRepresentImage", method = RequestMethod.POST)
    @ResponseBody
    public String addPartCategoryRepresentImage(@RequestParam("categoryId") Integer categoryId, @RequestParam("representImageUrl") String representImageUrl) {
//        brickLinkCategoryService.addPartCategoryRepresentImage(categoryId, representImageUrl);
        return "success";
    }
}
