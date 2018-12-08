package com.mercu.lego.controller;

import com.mercu.lego.service.MatchMyItemService;
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
public class CandidateController {
    @Autowired
    private MatchMyItemService matchMyItemService;

    @RequestMapping("/admin/matchIdList")
    @ResponseBody
    public String matchIdList() {
        return JsonUtils.toJson(matchMyItemService.findMatchIds());
    }

    @RequestMapping("/admin/matchSetList")
    @ResponseBody
    public String matchSetList(@RequestParam(value = "matchId", required = false) String matchId) {
        return JsonUtils.toJson(matchMyItemService.findMatchSetList(matchId));
    }

    @RequestMapping("/admin/matchSetParts")
    @ResponseBody
    public String matchSetParts(@RequestParam(value = "setId") String setId) {
        return JsonUtils.toJson(matchMyItemService.findMatchSetParts(setId));
    }

}
