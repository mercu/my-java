package com.mercu.lego.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercu.bricklink.service.BrickLinkCategoryService;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class CategoryController {
    @Autowired
    private BrickLinkCategoryService brickLinkCategoryService;

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }
}
