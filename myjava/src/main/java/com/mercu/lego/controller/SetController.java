package com.mercu.lego.controller;

import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class SetController {
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @RequestMapping("/setIdBySetNo")
    @ResponseBody
    public String setIdBySetNo(@RequestParam("setNo") String setNo) {
        return JsonUtils.toJson(brickLinkCatalogService.setIdBySetNoCached(setNo));
    }

}
