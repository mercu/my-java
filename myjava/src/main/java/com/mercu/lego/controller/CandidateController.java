package com.mercu.lego.controller;

import com.mercu.lego.service.MatchMyItemService;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@RestController
public class CandidateController {
    @Autowired
    private MatchMyItemService matchMyItemService;

    @RequestMapping("/admin/candidate")
    @ResponseBody
    public String candidate() {
        return JsonUtils.toJson(matchMyItemService.findMatchIds());
    }

}
