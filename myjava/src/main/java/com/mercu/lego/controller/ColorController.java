package com.mercu.lego.controller;

import com.mercu.bricklink.service.BrickLinkColorService;
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
public class ColorController {
    @Autowired
    private BrickLinkColorService brickLinkColorService;

    @RequestMapping("/allColorPartImgUrlsByPartNo")
    @ResponseBody
    public String allColorPartImgUrlsByPartNo(@RequestParam("partNo") String partNo) {
        return JsonUtils.toJson(brickLinkColorService.findAllColorPartImgUrlsByPartNo(partNo));
    }

}
