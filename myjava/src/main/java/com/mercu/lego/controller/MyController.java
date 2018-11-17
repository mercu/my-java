package com.mercu.lego.controller;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.my.MyItem;
import com.mercu.bricklink.model.my.MyItemGroup;
import com.mercu.bricklink.service.BrickLinkMyService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class MyController {
    @Autowired
    private BrickLinkMyService brickLinkMyService;

    @RequestMapping("/admin/myParts")
    @ResponseBody
    public String myParts() {
        List<MyItemGroup> myItemGroupList = brickLinkMyService.findMyItemsGroup();
        return JsonUtils.toJson(myItemGroupList);
    }

    @RequestMapping("/admin/myPartWhereInfos")
    @ResponseBody
    public String myPartWhereInfos(@RequestParam String partNo, @RequestParam String colorId) {
        List<MyItem> myPartWhereInfos = brickLinkMyService.findMyItemWheres(CategoryType.P.getCode(), partNo, colorId);
        return JsonUtils.toJson(myPartWhereInfos);
    }

    @RequestMapping(value = "/admin/myPartQty", method = RequestMethod.POST)
    @ResponseBody
    public String myPartQty(@RequestParam String partNo, @RequestParam String colorId, @RequestParam String whereCode, @RequestParam String whereMore, @RequestParam Integer qty) {
        MyItem saved = brickLinkMyService.addMyItem(CategoryType.P.getCode(), partNo, colorId, whereCode, whereMore, qty);
        return JsonUtils.toJson(saved);
    }

}
