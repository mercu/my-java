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
public class CandidateController {
    @Autowired
//    private BrickLinkMyService brickLinkMyService;

    @RequestMapping("/admin/candidate")
    @ResponseBody
    public String candidate() {
//        List<MyItemGroup> myItemGroupList = brickLinkMyService.findMyItemsGroup();
//        return JsonUtils.toJson(myItemGroupList);
        return null;
    }

}
