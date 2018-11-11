package com.mercu.lego.controller;

import com.mercu.bricklink.model.my.MyItem;
import com.mercu.bricklink.service.BrickLinkMyService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
        List<MyItem> myItemList = brickLinkMyService.findMyItems();

        return JsonUtils.toJson(myItemList);
    }

}
