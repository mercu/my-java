package com.mercu.lego.controller;

import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class PartController {
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @RequestMapping("/partList")
    @ResponseBody
    public String partCategories(@RequestParam("categoryId") Integer categoryId, @RequestParam(value = "limit", required = false) Integer limit) {
        List<PartInfo> partInfoList = brickLinkCatalogService.findPartInfoListByCategoryIdWithMyItems(categoryId, limit);
        return JsonUtils.toJson(partInfoList);
    }
}
